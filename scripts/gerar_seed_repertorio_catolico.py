# -*- coding: utf-8 -*-
import json
from pathlib import Path

src = Path("/in/repertorio_catolico_enriquecido.json")
out = Path("/out/seed-repertorio-catolico-enriquecido.sql")
json_copy = Path("/out/repertorio_catolico_enriquecido.json")

data = json.loads(src.read_text(encoding="utf-8"))
json_copy.write_text(json.dumps(data, ensure_ascii=False, indent=2), encoding="utf-8")


def esc(v):
    if v is None:
        return "NULL"
    s = str(v).strip()
    if not s:
        return "NULL"
    return "'" + s.replace("'", "''") + "'"


lines = [
    "-- Seed a partir de repertorio_catolico_enriquecido.json (idempotente por titulo).",
    "INSERT INTO musica (",
    "    codigo, titulo, autor, interprete_referencia, tom_padrao, categoria_liturgica,",
    "    letra, cifra, link_referencia, observacao, ativo, data_cadastro, data_atualizacao",
    ")",
    "SELECT gen_random_uuid(), v.titulo, v.autor, v.interprete, v.tom, v.categoria,",
    "       v.letra, v.cifra, v.link, v.observacao, TRUE, NOW(), NOW()",
    "FROM (VALUES",
]

for i, m in enumerate(data):
    comma = "," if i < len(data) - 1 else ""
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

out.write_text("\n".join(lines), encoding="utf-8")
print(f"count={len(data)}")
print(f"wrote={out}")
print(out.read_text(encoding="utf-8")[:450])
