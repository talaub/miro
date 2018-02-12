# Miro

Meet Miro, the clever CSS-Preprocessor that is designed to help you build websites even faster.

## Syntax changes

In Miro you don't need curly braces. Indentation is used to determine where a block starts and where it ends. Just use four spaces to indent you statements and Miro will add the curly braces for you.

```stylus
div
    color: red;
```

compiles to

```css
div {
    color: red;
}
```

I am a very clumsy person. I always forget semilocons and colons. That's why you don't need to write them in Miro, they are completly optional.

```stylus
div
    color red
```

compiles to

```css
div {
    color: red;
}
```

## Features

But now to the cool stuff. Let's take a look at the new featues Miro introduces to CSS.

### Variables

One obvious feature that is implemented by many CSS preprocessors is variables. You can predefine values and use them in your stylesheet.

```stylus
$color-red = #f00;

div
    color $color-red
```

compiles to

```css
div {
    color: red;
}
```

Variables are only defined for a specific scope. If a variable is defined inside a block it can only used in this very block and blocks that are inside this block.

### Nesting

This is also a classic. Almost every CSS preprocessor allows blocks to be nested inside each other. For me it's still one of the most important features. Ever wanted to specify rules for a `a` in a `div` in a `section`?
If you did, you probably wrote something like this:

```css
section {
    ...
}
section div {
    ...
}
section div a {
    ...
}
section div a:hover {
    ...
}
```

Very long and repetetive. But here's the equivalent Miro code:

```stylus
section
    ...
    div
        ...
        a
            ...
            &:hover
                ...
```

### Calculations

When you store values inside variables you often want to manipulate them afterwards. For example brighten a color, double a margin, you name it.

```stylus
$margin = 50px

div
    margin-top $margin
    margin-left $margin * 2
```

### Manipulating values

Besides calculations you can use functions to change values. Let's take a look at setting the alpha value for a color. Image you have declared your primary color in a variable like this:

```stylus
$primary-color = #73AC21
```

And now you want to have an overlay in that color but with reduced opacity. Colorvalues have a `setAlpha` function that fits perfectly for this. Let's try it!

```stylus
$primary-color = #73AC21

div
    background-color $primary-color
    
    &.overlay
        background-color $primary-color.setAlpha(50%)
```

You can use a percentage value, a floating point number between 0.0 and 1.0 and integers between 1 and 255 to set the value. This code example compiles to

```css
div {
    background-color: #73ac21;
}
div.overlay {
    background-color: rgba(115, 172, 33, 0.5);
}
```

### Code responsibly

Building a responsive website can be a real pain. Media queries are cool but for me it's impractical to redefine every block inside a media query. In Miro and many other CSS preprocessors you can nest the media queries inside of the block. This is way shorter and better to understand than having every block at least two times. Take a look at this simple example:

```stylus
div
    width 80%
   
    @media (max-width: 700px)
        width 95%
```

which compiles to

```css
div {
    width: 80%;
}
@media (max-width: 700px) {
div {
    width: 95%;
}
}
```

You see - not double `div`. And maybe we are on the same page when I say: Media queries are quite difficult to work with because you define a `max-width` and not a condition that has to be met in order to have the underlying rules applied. Because I prefer a "if-statement" way of writing conditional statements I built media-ifs into Miro. This example probably explains it best

```stylus
div
    width 80%

    @if (width <= 700px):
        width 95%
```

This code snippet is equivalent to the media query above.

### Keep things short and simple

Some properties have to be prefixed for different browsers. Like `-webkit-...`. But nobody can remember all of them and remember which prefix has to be used for which property. That's why Miro has an autoprefixer built in. That means that most of the properties that need to be prefixed will receive their prefixes automatically.

Of course `!important` is not a good style of writing CSS code. Still, Miro allows you to reduce it to a simple exclamation point:

```stylus
div
    color red!
```

compiles to

```css
div {
    color: red !important;
}
```

Ever defined the style of a block and found yourself writing the word `font` over and over again? Nested properties can be used to structure you style statements some more. They look like this

```stylus
div
    font--
        family 'Arial'
        weight 400
        size 20px
```

This is self explanatory. All the statemtents get a `font-` attached before the property.

Another way of keeping your code short is using mixins. Mixins are predefined code blocks that can be reused wherever you want.

```stylus
$circle(radius)
    border-radius 50%
    width $radius; height $radius

div
    $circle(10px)
```

compiles to

```css
div {
    border-radius: 50%;
    width: 10px;
    height: 10px;
}
```

Mixins can take as many parameters as you like. You can even set default values for the parameters. If this parameter is not passed on a call the default value will be used. Let's take a look at this example:

```stylus
$circle(radius = 10px)
    border-radius 50%
    width $radius; height $radius

div
    $circle()

    &.large
        $circle(20px)
```

compiles to

```css
div {
    border-radius: 50%;
    width: 10px;
    height: 10px;
}
div.large {
    border-radius: 50%;
    width: 20px;
    height: 20px;
}
```

### Importing other files

I like to have different files for different components and another seperate file containing all declared variables and mixins. You can have your code distributed across many Miro files and import them in your main file. There are two ways of importing Miro files: `@use` and `@import`.
`@import` basically copies the code of the file to the position of the import statement. Much like a CSS import would work. `@use` on the other hand only imports variables, mixins and custom functions from the other file.

### Scripting

When you're coding you often want to execute code when a certrain condition is met. This is done using if-statements. Miro also supports if-statements for example to validate parameter values inside a mixin. You use ifs like this:

```stylus
$size(size)
    if $size >= 0:
        width $size
        height $size
```

The width and height properties will only be set when the passed size is larger or equal to 0.

When ifs work, for-loops have to work too, right? And yes, they do. You can iterate through lists, dictionaries and strings. This looks like this:

```stylus
for elem in [class1, class2, class3]:
    div.${$elem}
        margin 10px
```

When you only specify one variable (in this case elem) the variable will be filled with the current list-element, string-character or dictionary-key-value-pair respectively. You can always specify two variables then the first variable will be filled with the current index (or dictionary key) and the second one will receive the element value.

Another way of iterating would be the for ... to ... loop. The specified variable will start at 0 and iterate to the number after 'to':

```stylus
$var = 5;
for i to 20:
    $var = $var + $i
```

As I mentioned previously, Miro provides some basic functions to manipulate values but you can always add custom functions or override existing ones. Here is an example where a function is added to Color values

```stylus
@Color
    func removeBlue():
        $this = $this.setBlue(0)
```

The final value of `$this` will be returned.

Let's briefly talk about collection in Miro. There are lists and dictionaries. Lists are, well, a list of values. They are completely dynamic in typing, they can contain strings, numerics, other lists and other values at the same time. A simple list can be created using spaces:

```stylus
$simplelist = 10px #f00 'test'
```

The other way of creating lists would be using square brackets:

```stylus
$otherlist = [10px, #f00, 'test']
```

Lists can be manulated using functions or calculations. The +-operator for example adds a value to the list.

Dictionaries are a set of key-value pairs. The key is always an ident-value and the type of the value is dynamic. Dictionaries are created using curly braces:


```stylus
$dict = {main-color: #f00, other-color: #00f}
```

Dictionaries and lists can be defined over multiple lines:

```stylus
$dict = {
            main-color: #f00,
            other-color: #00f
        }
```
