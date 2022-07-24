# Java VIST Compiler
This is a Java compiler for VIST documents. Use the VIST class to parse a VIST document, and retrieve its variables.

## VIST Document
This section briefly summarises the main points of VIST. For more detailed information, read the standard.
### 1. Types & Values
VIST knows the following value types: 
- boolean: true / false
- char: '[ANY_CHAR]' 
- string: "[ANY_STRING]"
- float: X.Y
- int: X
- colour: 3 or 6 bit hexadecimal starting with #
### 2. Variable Blocks
Variable Blocks define variables, and use the following format:
VariableBlock := Identifier "IS" Value ";"
Like all keywords, "IS" is case-insensitive.
1. Example:
highscore IS 25925;
2. Example:
bossWasDefeated IS false;
3. Example:
worldToLoad IS "Overworld-1-5";

## Java Compiler
To use the compiler, import the VIST class from 'generation.VIST', and pass the VIST document as a String.
The document will be parsed automatically. Now use the non-static retrieve methods, which accept the variable names.   

### Example:
```java
public static void main(String[] args) throws Exception {
    final String test = "highscore IS 25929;";

    VIST vist = new VIST(test);
    int score = vist.retrieveInt("highscore");
    
    if (score > 2500) {
        // ...
    }
}
```