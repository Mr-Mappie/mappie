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

|               | Byte | Short | Int | Long | Float | Double | *BigInteger* | *BigDecimal* | String |
|---------------|------|-------|-----|------|-------|--------|--------------|--------------|--------|
| Byte          | -    | X     | X   | X    |       |        | X            | X            | X      |
| Short         |      | -     | X   | X    |       |        | X            | X            | X      |
| Int           |      |       | -   | X    |       |        | X            | X            | X      |
| Long          |      |       |     | -    |       |        | X            | X            | X      |
| Float         |      |       |     |      | -     | X      |              | X            | X      |
| Double        |      |       |     |      |       | -      |              | X            | X      |
| *BigInteger*  |      |       |     |      |       |        | -            |              | X      |
| *BigDecimal*  |      |       |     |      |       |        |              | -            | X      |

## Char Mappers
The following char mappers are built-int

|        | Char | String |
|--------|------|--------|
| Char   | -    | X      | 

## LocalDate Mappers
The following numeric mappers are built-int

|                 | *LocalTime* | *LocalTime* | *LocalDate* |
|-----------------|-------------|-------------|-------------|
| *LocalDateTime* | -           |  X          | X           |


## Char Mappers
The following UUID mappers are built-int

|        | *UUID* | String |
|--------|--------|--------|
| *UUID* | -      | X      | 