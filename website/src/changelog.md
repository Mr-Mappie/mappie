---
title: "Changelog"
layout: "layouts/changelog.html"
changelog:
    - date: "tbd"
      title: "v0.5.0"
      items:
        - "[#57](https://github.com/Mr-Mappie/mappie/issues/57) object mappers can now use property setters."
        - "[#52](https://github.com/Mr-Mappie/mappie/issues/52) the `EnumMappie` target type does not have to be an enum, but can be any."
        - "Several other bug fixes."
    - date: "2024-07-08"
      title: "v0.4.0"
      items:
        - "[#45](https://github.com/Mr-Mappie/mappie/issues/45) mapper can now have multiple from parameters."
        - "[#50](https://github.com/Mr-Mappie/mappie/issues/50) added compatibility with Kotlin `1.9.24` and `2.0.20-Beta1`."
        - "Improved code generation, resulting in less intermediate code being generated."
        - "Removed deprecated `fromConstant`."
        - "Several other bug fixes."  
    - date: "2024-07-03"
      title: "v0.3.0"
      items:
        - "[#17](https://github.com/Mr-Mappie/mappie/issues/17) mappers can now be declared as inner declarations, instead of requiring them to be top-level."
        - "[#31](https://github.com/Mr-Mappie/mappie/issues/31) added the explicit mapping method `thrownByEnumEntry` to throw an exception as a result when mapping an enum entry."
        - "[#28](https://github.com/Mr-Mappie/mappie/issues/28) added implicit mapping inference of mappers with the same name but a different type, but a mapper for those types are defined."
        - "[#42](https://github.com/Mr-Mappie/mappie/issues/42) added a configuration option to disable resolving via default arguments."
        - "[#40](https://github.com/Mr-Mappie/mappie/issues/40) added `to` alias to refer to for target properties as an alternative to the fully written out `TO` type."
        - "[#46](https://github.com/Mr-Mappie/mappie/issues/46) added support for Java get methods."
        - "Several other bug fixes."
    - date: "2024-06-27"
      title: "v0.2.0"
      items:
        - "[#13](https://github.com/Mr-Mappie/mappie/issues/13) added support for declaring a mapper without an implementation of map."
        - "[#16](https://github.com/Mr-Mappie/mappie/issues/16) improved resolution of explicit parameter names."
        - "[#21](https://github.com/Mr-Mappie/mappie/issues/21) added global configuration option to report all warnings as errors."
        - "[#24](https://github.com/Mr-Mappie/mappie/issues/24) added explicit mapping `fromValue` as a replacement of the much more restricting `fromConstant`."
        - "[#26](https://github.com/Mr-Mappie/mappie/issues/26) support for selecting nested properties."
        - "Several other bug fixes."
    - date: "2024-06-22"
      title: "v0.1.0"
      items:
        - "Initial release"
---
