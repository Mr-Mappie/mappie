---
title: "Changelog"
layout: "layouts/changelog.html"
changelog:
    - date: "2023-05-24"
      title: "v1.1.0"
      items:
        - "**This is a test changelog record for demonstration.**"
        - "**Improvement:** add `$btn-font-family` to control the button's font family."
        - "**Improvement:** add `$heading-font-weight` to control the heading's font-weight."
        - "**Improvement:** rename some keys in the `$colors` map (`mark-color` : `mark-foreground`, `code-color` : `code-foreground`)."
        - "**Improvement:** reorganize the recurrent colors into variables."
        - "**Fix:** modify `btn-variant()` mixin: add hover foreground color."
        - "Global switch to `color()` function's fallback value under the [`$settings`](https://sprucecss.com/docs/sass/variables#settings) map."
        - "Modify the [`scrollbar()`](https://sprucecss.com/docs/sass/mixins#scrollbar) mixin to accept hover thumb background-color value."
        - "Make `$breakpoints` overwriteable by key."
        - "Modify `font-size()` and `responsive-font-size()` function to accept optimal size value. The optimal value will fallback to a global settnigs under the `$settings` map."
    - date: "2023-05-23"
      title: "v1.0.0"
      items:
        - "Initial release"
---
