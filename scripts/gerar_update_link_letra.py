#!/usr/bin/env python3
"""Gera SQL para atualizar musica.letra a partir do CSV de links."""
from __future__ import annotations

import csv
from pathlib import Path

SRC = Path("musicas_link_letra_UTF8_BOM_ponto_virgula.csv")
OUT = Path("update-musicas-link-letra.sql")


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

    updates: list[tuple[str, str]] = []
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
        updates.append((titulo, link))

    # Última ocorrência vence em títulos duplicados no CSV.
    unicos: dict[str, tuple[str, str]] = {}
    for titulo, link in updates:
        unicos[titulo.casefold()] = (titulo, link)

    lines = [
        "-- Atualiza link da letra a partir de musicas_link_letra_UTF8_BOM_ponto_virgula.csv",
        "-- Match por LOWER(TRIM(titulo)). Somente linhas com link http(s).",
        "BEGIN;",
    ]
    for titulo, link in sorted(unicos.values(), key=lambda item: item[0].casefold()):
        lines.append(
            "UPDATE musica SET letra = '{link}', data_atualizacao = NOW() "
            "WHERE LOWER(TRIM(titulo)) = LOWER(TRIM('{titulo}'));".format(
                link=esc(link),
                titulo=esc(titulo),
            )
        )
    lines.append("COMMIT;")
    OUT.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(
        f"csv_rows={len(rows)} com_link={len(updates)} "
        f"unicos={len(unicos)} sem_link={sem_link} sql={OUT}"
    )


if __name__ == "__main__":
    main()
