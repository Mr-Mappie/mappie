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
The following integer mappers are built-in

|              | Byte | Short | Int | Long | *BigInteger* | *BigDecimal*  |
|--------------|------|-------|-----|------|--------------|---------------|
| Byte         | -    | X     | X   | X    | X            | X             |
| Short        |      | -     | X   | X    | X            | X             |
| Int          |      |       | -   | X    | X            | X             |
| Long         |      |       |     | -    | X            | X             |
| *BigInteger* |      |       |     |      | -            |               |
| *BigDecimal* |      |       |     |      |              | -             |

{.table-matrix}

The following floating point mappers are built-in

|              | Float | Double | *BigDecimal*  |
|--------------|-------|--------|---------------|
| Float        | -     | X      | X             |
| Double       |       | -      | X             |

{.table-matrix}

There also exist a mapper for all numeric types to `String`.

## Char Mappers
The following char mappers are built-in

|        | Char | String |
|--------|------|--------|
| Char   | -    | X      | 

{.table-matrix}

## LocalDate Mappers
The following numeric mappers are built-in

|                 | *LocalDateTime* | *LocalTime* | *LocalDate* |
|-----------------|-----------------|-------------|-------------|
| *LocalDateTime* | -               |  X          | X           |

{.table-matrix}

## UUID Mappers
The following UUID mappers are built-in

|        | *UUID* | String |
|--------|--------|--------|
| *UUID* | -      | X      |

{.table-matrix}
