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

## Collection Mappers
There is support for mapping any `Iterable` of type `T` to any of `List`, `Set`, `MutableList`, and `MutableSet` of type `R`.

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

The following floating point mappers are built-in

|              | Float | Double | *BigDecimal*  |
|--------------|-------|--------|---------------|
| Float        | -     | X      | X             |
| Double       |       | -      | X             |

There also exist a mapper for all numeric types to `String`.

## Char Mappers
The following char mappers are built-in

|        | Char | String |
|--------|------|--------|
| Char   | -    | X      | 

## LocalDate Mappers
The following numeric mappers are built-in

|                 | *LocalDateTime* | *LocalTime* | *LocalDate* |
|-----------------|-----------------|-------------|-------------|
| *LocalDateTime* | -               |  X          | X           |

## UUID Mappers
The following UUID mappers are built-in

|        | *UUID* | String |
|--------|--------|--------|
| *UUID* | -      | X      |

## The identity Mapper
The identity mapper named `IdentityMapper` maps any `T` to any `T`.

## kotlinx-datetime Mappers
The following kotlinx-datetime mappers can be included via the dependency `tech.mappie.api:module-kotlinx-datetime`.

The following Period mappers are available

|            | DatePeriod | *Period* |
|------------|------------|----------|
| DatePeriod | -          | X        |
| *Period*   | X          | -        |

The following DayOfWeek mappers are available

|             | DayOfWeek | *DayOfWeek* |
|-------------|-----------|-------------|
| DayOfWeek   | -         | X           |
| *DayOfWeek* | X         | -           |

The following Instant mappers are available

|           | Instant | *Instant* |
|-----------|---------|-----------|
| Instant   | -       | X         |
| *Instant* | X       | -         |

The following LocalDate mappers are available

|             | LocalDate | *LocalDate* |
|-------------|-----------|-------------|
| LocalDate   | -         | X           |
| *LocalDate* | X         | -           |

The following LocalDateTime mappers are available

|                 | LocalDateTime | *LocalDateTime* |
|-----------------|---------------|-----------------|
| LocalDateTime   | -             | X               |
| *LocalDateTime* | X             | -               |

The following LocalTime mappers are available

|             | LocalTime | *LocalTime* |
|-------------|-----------|-------------|
| LocalTime   | -         | X           |
| *LocalTime* | X         | -           |

The following Month mappers are available

|         | Month | *Month* |
|---------|-------|---------|
| Month   | -     | X       |
| *Month* | X     | -       |

The following timezone mappers are available

|                     | TimeZone | FixedOffsetTimeZone | UtcOffset | *ZoneId* | *ZoneOffset* |
|---------------------|----------|---------------------|-----------|----------|--------------|
| TimeZone            | -        |                     |           | X        |              |
| FixedOffsetTimeZone |          | -                   |           |          | X            |
| UtcOffset           |          |                     | -         |          | X            |
| *ZoneId*            | X        | -                   |           | -        |              |
| *ZoneOffset*        | X        | X                   | X         |          | -            |