---
title: "Object Mapping Overview"
summary: "Performing object mapping."
eleventyNavigation:
  key: Object Mapping Overview
  parent: Object Mapping
  order: 4
---

Mappie supports creating object mappers via the base class `ObjectMappie`. Extending this class will instruct Mappie
to generate a mapper which will call a constructor of the target type, based on the implicit- and explicit mappings.

Implicit mappings are resolved are those that can be inferred automatically, and is explained in 
[Inferring Implicit Mappings](/object-mapping/inferring-implicit-mappings/). Explicit mappings need to be defined by
the programmer, for which there are multiple ways: by property, by value, and by expression. These options are
explained in further detail in [Constructing Explicit Mappings](/object-mapping/constructing-explicit-mappings/).