---
title: "Java Compatibility"
summary: "Compatibility with Java."
eleventyNavigation:
  key: Java Compatibility
  parent: Object Mapping
  order: 10
---

Java classes are different from those of Kotlin. The main difference for Mappie is that Java does not have the concept
of properties. Instead, the convention is to have a field `x`, and a getter `getX()` and setter `setX(Person value)`
method.

Mappie automatically infers such getter methods. They must follow the convention `getX()` for the property `x` to be
inferred. Also note that in Java all types are nullable. Mappie will give a warning if a Java getter is used to assign
to a non-nullable target.