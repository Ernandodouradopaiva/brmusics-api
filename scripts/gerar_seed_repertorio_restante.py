# -*- coding: utf-8 -*-
"""Gera seed SQL com músicas do JSON completo que ainda não estão no enriquecido/DB (por título)."""
import json
from pathlib import Path

SRC_FULL = Path("/in/repertorio_catolico_300_musicas.json")
SRC_DONE = Path("/in/repertorio_catolico_enriquecido.json")
OUT_SQL = Path("/out/seed-repertorio-catolico-restante.sql")
OUT_JSON = Path("/out/repertorio_catolico_restante.json")

VALID_CATS = {
    "ENTRADA",
    "ATO_PENITENCIAL",
    "GLORIA",
    "SALMO",
    "ACLAMACAO",
    "PRECES",
    "OFERTORIO",
    "SANTO",
    "ORACAO_EUCAISTICA",
    "ELEVACAO",
    "AMEM",
    "CORDEIRO",
    "COMUNHAO",
    "POS_COMUNHAO",
    "FINAL",
    "ADORACAO",
    "MARIANA",
    "ESPIRITO_SANTO",
    "LOUVOR",
    "OUTROS",
}


def esc(v):
    if v is None:
        return "NULL"
    s = str(v).strip()
    if not s:
        return "NULL"
    return "'" + s.replace("'", "''") + "'"


def normalizar_titulo(t):
    return " ".join(str(t or "").strip().lower().split())


def normalizar_link(v):
    if v is None:
        return None
    s = str(v).strip()
    if not s:
        return None
    lower = s.lower()
    if lower.startswith("http://") or lower.startswith("https://"):
        return s
    return None


def normalizar_categoria(v):
    if v is None:
        return "OUTROS"
    s = str(v).strip().upper().replace(" ", "_")
    if s == "OUTRO":
        s = "OUTROS"
    if s in ("COMUNHAO_01", "COMUNHAO_02"):
        s = "COMUNHAO"
    if s not in VALID_CATS:
        return "OUTROS"
    return s


full = json.loads(SRC_FULL.read_text(encoding="utf-8"))
done = json.loads(SRC_DONE.read_text(encoding="utf-8"))
done_titles = {normalizar_titulo(m.get("titulo")) for m in done}

restante = []
skipped_dup = 0
for m in full:
    t = normalizar_titulo(m.get("titulo"))
    if not t:
        continue
    if t in done_titles:
        skipped_dup += 1
        continue
    restante.append(
        {
            "titulo": str(m.get("titulo") or "").strip(),
            "autor": (m.get("autor") or None),
            "interpreteReferencia": m.get("interpreteReferencia") or m.get("interprete_referencia"),
            "tomPadrao": m.get("tomPadrao") or m.get("tom_padrao"),
            "categoriaLiturgica": normalizar_categoria(
                m.get("categoriaLiturgica") or m.get("categoria_liturgica")
            ),
            "letra": normalizar_link(m.get("letra")),
            "cifra": normalizar_link(m.get("cifra")),
            "linkReferencia": normalizar_link(
                m.get("linkReferencia") or m.get("link_referencia")
            ),
            "observacao": m.get("observacao"),
            "ativo": True if m.get("ativo") is None else bool(m.get("ativo")),
        }
    )
    done_titles.add(t)

OUT_JSON.write_text(json.dumps(restante, ensure_ascii=False, indent=2), encoding="utf-8")

lines = [
    "-- Seed restante (300 - enriquecido), idempotente por titulo.",
    "INSERT INTO musica (",
    "    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,",
    "    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao",
    ")",
    "SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,",
    "       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()",
    "FROM (VALUES",
]

if not restante:
    lines = ["-- Nenhuma musica restante para inserir.", "SELECT 1 WHERE FALSE;"]
else:
    for i, m in enumerate(restante):
        comma = "," if i < len(restante) - 1 else ""
        lines.append(
            "    ({}, {}, {}, {}, {}, {}, {}, {}, {}){}".format(
                esc(m.get("titulo")),
                esc(m.get("autor")),
                esc(m.get("interpreteReferencia")),
                esc(m.get("tomPadrao")),
                esc(m.get("categoriaLiturgica")),
                esc(m.get("letra")),
                esc(m.get("cifra")),
                esc(m.get("linkReferencia")),
                esc(m.get("observacao")),
                comma,
            )
        )
    lines += [
        ") AS v(titulo, autor, interprete, tom, categoria, letra, cifra, link, observacao)",
        "WHERE NOT EXISTS (",
        "    SELECT 1 FROM musica m WHERE LOWER(TRIM(m.titulo)) = LOWER(TRIM(v.titulo))",
        ");",
        "",
    ]

OUT_SQL.write_text("\n".join(lines), encoding="utf-8")
print(f"full={len(full)} done={len(done)} skipped_in_enriquecido={skipped_dup} restante={len(restante)}")
from collections import Counter

print("cats", dict(Counter(m["categoriaLiturgica"] for m in restante)))
