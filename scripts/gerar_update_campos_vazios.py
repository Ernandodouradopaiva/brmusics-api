#!/usr/bin/env python3
"""Preenche campos em branco de musica a partir da planilha anexada."""
from __future__ import annotations

import csv
from pathlib import Path

SRC = Path("musicas_links_letra_cifra_preenchidos_UTF8_BOM.csv")
OUT = Path("update-musicas-campos-vazios.sql")


def esc(value: str) -> str:
    return value.replace("'", "''")


def is_http(value: str) -> bool:
    v = (value or "").strip().lower()
    return v.startswith("http://") or v.startswith("https://")


def clean(value: str | None) -> str | None:
    if value is None:
        return None
    text = value.strip()
    return text or None


def main() -> None:
    with SRC.open("r", encoding="utf-8-sig", newline="") as fh:
        reader = csv.DictReader(fh, delimiter=";")
        if reader.fieldnames:
            reader.fieldnames = [
                (name or "").lstrip("\ufeff").strip() for name in reader.fieldnames
            ]
        rows = list(reader)

    unicos: dict[str, dict[str, str | None]] = {}
    for row in rows:
        titulo = clean(row.get("Titulo"))
        if not titulo:
            continue
        letra = clean(row.get("Link da letra"))
        cifra = clean(row.get("Link da cifra"))
        link = clean(row.get("Link de referencia"))
        unicos[titulo.casefold()] = {
            "titulo": titulo,
            "autor": clean(row.get("Autor")),
            "interprete": clean(row.get("Interprete")),
            "tom": clean(row.get("Tom")),
            "categoria": clean(row.get("Categoria")),
            "letra": letra if letra and is_http(letra) else None,
            "cifra": cifra if cifra and is_http(cifra) else None,
            "link": link if link and is_http(link) else None,
            "observacao": clean(row.get("Observacao")),
        }

    lines = [
        "-- Preenche somente campos em branco a partir de musicas_links_letra_cifra_preenchidos_UTF8_BOM.csv",
        "BEGIN;",
    ]
    contadores = {
        "letra": 0,
        "cifra": 0,
        "link": 0,
        "autor": 0,
        "interprete": 0,
        "tom": 0,
        "categoria": 0,
        "observacao": 0,
    }

    for item in sorted(unicos.values(), key=lambda x: str(x["titulo"]).casefold()):
        titulo = str(item["titulo"])
        sets: list[str] = []

        if item["letra"]:
            sets.append(
                "letra = CASE WHEN letra IS NULL OR btrim(letra) = '' "
                f"THEN '{esc(str(item['letra']))}' ELSE letra END"
            )
            contadores["letra"] += 1
        if item["cifra"]:
            sets.append(
                "cifra = CASE WHEN cifra IS NULL OR btrim(cifra) = '' "
                f"THEN '{esc(str(item['cifra']))}' ELSE cifra END"
            )
            contadores["cifra"] += 1
        if item["link"]:
            sets.append(
                "link_referencia = CASE WHEN link_referencia IS NULL OR btrim(link_referencia) = '' "
                f"THEN '{esc(str(item['link']))}' ELSE link_referencia END"
            )
            contadores["link"] += 1
        if item["autor"]:
            sets.append(
                "autor = CASE WHEN autor IS NULL OR btrim(autor) = '' "
                f"THEN '{esc(str(item['autor']))}' ELSE autor END"
            )
            contadores["autor"] += 1
        if item["interprete"]:
            sets.append(
                "interprete_referencia = CASE WHEN interprete_referencia IS NULL OR btrim(interprete_referencia) = '' "
                f"THEN '{esc(str(item['interprete']))}' ELSE interprete_referencia END"
            )
            contadores["interprete"] += 1
        if item["tom"]:
            sets.append(
                "tom_padrao = CASE WHEN tom_padrao IS NULL OR btrim(tom_padrao) = '' "
                f"THEN '{esc(str(item['tom']))}' ELSE tom_padrao END"
            )
            contadores["tom"] += 1
        if item["categoria"]:
            sets.append(
                "categoria_liturgica = CASE WHEN categoria_liturgica IS NULL OR btrim(categoria_liturgica) = '' "
                f"THEN '{esc(str(item['categoria']).upper())}' ELSE categoria_liturgica END"
            )
            contadores["categoria"] += 1
        if item["observacao"]:
            sets.append(
                "observacao = CASE WHEN observacao IS NULL OR btrim(observacao) = '' "
                f"THEN '{esc(str(item['observacao']))}' ELSE observacao END"
            )
            contadores["observacao"] += 1

        if not sets:
            continue

        sets.append("data_atualizacao = NOW()")
        lines.append(
            "UPDATE musica SET "
            + ", ".join(sets)
            + f" WHERE LOWER(TRIM(titulo)) = LOWER(TRIM('{esc(titulo)}'));"
        )

    lines.append(
        "SELECT "
        "COUNT(*) FILTER (WHERE letra IS NOT NULL AND btrim(letra) <> '') AS com_letra, "
        "COUNT(*) FILTER (WHERE cifra IS NOT NULL AND btrim(cifra) <> '') AS com_cifra, "
        "COUNT(*) FILTER (WHERE link_referencia IS NOT NULL AND btrim(link_referencia) <> '') AS com_link, "
        "COUNT(*) FILTER (WHERE letra IS NULL OR btrim(letra) = '') AS sem_letra "
        "FROM musica;"
    )
    lines.append("COMMIT;")
    OUT.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"linhas_planilha={len(rows)} unicos={len(unicos)} updates={sum(1 for l in lines if l.startswith('UPDATE'))}")
    print("campos_com_valor_na_planilha", contadores)
    print(f"sql={OUT}")


if __name__ == "__main__":
    main()
