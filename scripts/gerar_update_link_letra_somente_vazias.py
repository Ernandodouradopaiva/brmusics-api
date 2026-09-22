#!/usr/bin/env python3
"""Atualiza musica.letra apenas quando estiver vazia e a planilha tiver link."""
from __future__ import annotations

import csv
from pathlib import Path

SRC = Path("musicas_letra_preenchida_com_cifra_UTF8_BOM.csv")
OUT = Path("update-musicas-link-letra-somente-vazias.sql")


def esc(value: str) -> str:
    return value.replace("'", "''")


def main() -> None:
    with SRC.open("r", encoding="utf-8-sig", newline="") as fh:
        reader = csv.DictReader(fh, delimiter=";")
        if reader.fieldnames:
            reader.fieldnames = [
                (name or "").lstrip("\ufeff").strip() for name in reader.fieldnames
            ]
        rows = list(reader)

    unicos: dict[str, tuple[str, str]] = {}
    sem_link = 0
    for row in rows:
        titulo = (row.get("Titulo") or "").strip()
        link = (row.get("Link da letra") or "").strip()
        if not titulo:
            continue
        if not link:
            sem_link += 1
            continue
        lower = link.lower()
        if not (lower.startswith("http://") or lower.startswith("https://")):
            continue
        unicos[titulo.casefold()] = (titulo, link)

    lines = [
        "-- Preenche letra somente se estiver vazia no banco e houver link na planilha.",
        "-- Fonte: musicas_letra_preenchida_com_cifra_UTF8_BOM.csv",
        "BEGIN;",
    ]
    for titulo, link in sorted(unicos.values(), key=lambda item: item[0].casefold()):
        lines.append(
            "UPDATE musica SET letra = '{link}', data_atualizacao = NOW() "
            "WHERE LOWER(TRIM(titulo)) = LOWER(TRIM('{titulo}')) "
            "AND (letra IS NULL OR btrim(letra) = '');".format(
                link=esc(link),
                titulo=esc(titulo),
            )
        )
    lines.append(
        "SELECT COUNT(*) FILTER (WHERE letra IS NULL OR btrim(letra) = '') AS sem_letra, "
        "COUNT(*) FILTER (WHERE letra IS NOT NULL AND btrim(letra) <> '') AS com_letra, "
        "COUNT(*) AS total FROM musica;"
    )
    lines.append("COMMIT;")
    OUT.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"unicos_com_link={len(unicos)} sem_link_planilha={sem_link} sql={OUT}")


if __name__ == "__main__":
    main()
