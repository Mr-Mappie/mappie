---
title: "Reporting"
summary: "Generating Mappie reports."
eleventyNavigation:
  key: Reporting
  parent: Getting Started
  order: 5
---

Mappie can generate reports containing Kotlin-like representations of the generated mappers.
This must be enabled explicitly, see [Gradle Configuration](/getting-started/gradle-configuration/) or 
[Maven Configuration](/getting-started/maven-configuration/) for more details.

The mappers are generated in the back-end of the compiler, from the so-called Intermediate Representation (IR).
These reports are pretty-printed from the IR, and thus cannot be used as actual Kotlin code. 

When Mappie's pretty-printing fails, the Kotlin compiler's pretty-printer will be used instead. 
However, this produces an abstract representation. Note that this should never happen and should always 
be considered a bug in Mappie.