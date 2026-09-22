# -*- coding: utf-8 -*-
"""Gera seed SQL idempotente a partir de um JSON de músicas montado em /in/input.json."""
import json
import os
from collections import Counter
from pathlib import Path

SRC = Path(os.environ.get("SEED_SRC", "/in/input.json"))
OUT_SQL = Path(os.environ.get("SEED_SQL", "/out/seed-musicas-novas.sql"))
OUT_JSON = Path(os.environ.get("SEED_JSON", "/out/musicas-novas-normalizadas.json"))

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


raw = json.loads(SRC.read_text(encoding="utf-8"))
if isinstance(raw, dict) and "musicas" in raw:
    raw = raw["musicas"]

vistos = set()
musicas = []
duplicadas = 0
for m in raw:
    titulo = str(m.get("titulo") or "").strip()
    key = normalizar_titulo(titulo)
    if not key:
        continue
    if key in vistos:
        duplicadas += 1
        continue
    vistos.add(key)
    musicas.append(
        {
            "titulo": titulo,
            "autor": m.get("autor"),
            "interpreteReferencia": m.get("interpreteReferencia") or m.get("interprete_referencia"),
            "tomPadrao": m.get("tomPadrao") or m.get("tom_padrao"),
            "categoriaLiturgica": normalizar_categoria(
                m.get("categoriaLiturgica") or m.get("categoria_liturgica")
            ),
            "letra": normalizar_link(m.get("letra")),
            "cifra": normalizar_link(m.get("cifra")),
            "linkReferencia": normalizar_link(m.get("linkReferencia") or m.get("link_referencia")),
            "observacao": m.get("observacao"),
            "ativo": True if m.get("ativo") is None else bool(m.get("ativo")),
        }
    )

OUT_JSON.write_text(json.dumps(musicas, ensure_ascii=False, indent=2), encoding="utf-8")

lines = [
    f"-- Seed a partir de {SRC.name} (idempotente por titulo).",
    "INSERT INTO musica (",
    "    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,",
    "    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao",
    ")",
    "SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,",
    "       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()",
    "FROM (VALUES",
]

if not musicas:
    lines = ["-- Nenhuma musica para inserir.", "SELECT 1 WHERE FALSE;"]
else:
    for i, m in enumerate(musicas):
        comma = "," if i < len(musicas) - 1 else ""
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
print(f"raw={len(raw)} unicas={len(musicas)} duplicadas_no_arquivo={duplicadas}")
print("cats", dict(Counter(m["categoriaLiturgica"] for m in musicas)))
print(f"wrote_sql={OUT_SQL}")
