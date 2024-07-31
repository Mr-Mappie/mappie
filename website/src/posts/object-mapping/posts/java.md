---
title: "Java Compatibility"
summary: "Compatibility with Java."
eleventyNavigation:
  key: Java Compatibility
  parent: Object Mapping
  order: 11
---

Java classes are different from those of Kotlin. The main difference for Mappie is that Java does not have the concept
of properties. Instead, the convention is to have a field `x`, and a getter `getX()` and setter `setX(Person value)`
method.

Mappie uses these getter- and setter methods in implicit mappings. They must follow the convention `getX()` and 
`setX(...)` for the property `x` to be inferred. Also note that in Java all types are nullable. Mappie will give a 
warning if a Java type is used to assign to a non-nullable target. The Kotlin compiler uses several nullability 
annotations to determine whether the platform type is nullable or not. See for more information 
[Nullability annotations](https://kotlinlang.org/docs/java-interop.html#nullability-annotations).

For example, suppose we have the Java class `Person`
```java
class Person {
    private String name;
    
    public String getName() {
        return name;
    }
}
```
with the Kotlin data class `PersonDto`
```kotlin
data class PersonDto(val name: String)
```
We can write a mapper in a regular manner
```kotlin
class PersonMapper : ObjectMappie<Person, PersonDto>()
```
Mappie will infer that `Person` has the source property `name` and maps it to the target property `name` of `PersonDto`.

Do note that Java classes often haven public no-arg constructors, and as such, a mapping can always be constructed 
without any explicit mappings.