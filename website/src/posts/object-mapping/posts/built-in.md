---
title: "Built-in Mappers"
summary: "Built-in Mappers."
eleventyNavigation:
  key: Built-in Mappers
  parent: Object Mapping
  order: 12
---

Mappie comes with several safe built-in mappers. These mappers are defined in the package `tech.mappie.api.builtin` and
can be applied explicitly, or implicitly.

The types below that are in *cursive* are not available on all platforms. For example, `BigInteger` is only available
on the JVM platform.

## Numeric Mappers
The following numeric mappers are built-int.

|              | Byte | Short | Int | Long | *BigInteger* |
|--------------|------|-------|-----|------|--------------|
| Byte         |      | X     | X   | X    | X            |
| Short        |      |       | X   | X    | X            |
| Int          |      |       |     | X    | X            |
| Long         |      |       |     |      | X            |

## Char Mappers
The following char mappers are built-int

|        | Char | String |
|--------|------|--------|
| Char   |      | X      | 

## LocalDate Mappers
The following numeric mappers are built-int

|                 | *LocalDateTime* | *LocalTime* | *LocalDate* |
|-----------------|-----------------|-------------|-------------|
| *LocalDateTime* |                 | X           | X           |
