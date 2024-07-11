---
title: "Multiple Sources"
summary: "Multiple Sources."
eleventyNavigation:
  key: Multiple Sources
  parent: Object Mapping
  order: 9
---

The standard Mappie `ObjectMappie` base class maps a single source parameter to a single
target parameter. Mappie also supports mapping multiple source parameters to a single 
target parameter. 

Mappie supports up to five source parameters. This can be achieved via the base classes `ObjectMappie2`, 
`ObjectMappie3`, ..., `ObjectMappie5`. 

For example, suppose we want to map the sources `Person`, `Address`, and `ContactInformation` 
to `PersonDto`. We can write this as
```kotlin
object PersonDtoMapper : ObjectMappie3<Person, Address, ContactInformation, PersonDto>() {
    override fun mapping(first: Person, second: Address, third: ContactInformation): PersonDto = mapping {
        // mapping logic
    }
}
```

Note that these multiple source mappers have less built-in functionality, most notable mapping
lists and sets as described in  [List & Sets](/object-mapping/lists-and-sets/).