---
title: "Frequently Asked Questions"
summary: "Have some questions? You may find it here."
displaySummary: true
layout: "layouts/faq.html"
faqs:
-
    title: "FAQ"
    items:
    -   title: "How does Mappie compare to MapStruct?"
        description: "
        Both Mappie and MapStruct run before or during the compilation of your source code. Mappie is
        implemented as a Kotlin compiler Plugin, while MapStruct is based on [kapt](https://kotlinlang.org/docs/kapt.html),
        which is in maintenance mode.
        

        Mappie generates Kotlin intermediate code, while MapStruct generates Java code. Combining Java code with Kotlin
        is a potential risk for nullability issues at run-time. The annotation based approach of MapStruct also has
        usability issues, as there are no syntactical references to the objects being mapped, disallowing refactoring
        support from IDE's.
        

        Both Mappie and MapStruct have excellent run-time performance.
        "
    -   title: "How does Mappie compare to ShapeShift?"
        description: "
        Both Mappie and ShapeShift are built for the Kotlin ecosystem. Mappie generates code at compile-time, while
        ShapeShift resolves it's mappings via reflection. This results in a big performance penalty.
        
        
        The design choices of ShapeShift and Mappie are different. For example, ShapeShift requires the target 
        classes to have a no-arg constructor, and mapping purely on properties, limiting the flexibility of your code 
        base. 
      

        As Mappie in it's early stages, ShapeShift does has more built-in support for advanced use-cases while Mappie
        sometimes needs to fall back on more generic operations.
        "
---
