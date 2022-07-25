package syntax;

import utility.Collector;
import utility.SimpleType;
import utility.VISTSemanticException;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Scans VIST Documents, adds successfully parsed variables to the Collector class, and 
 *  throws VISTSyntaxExceptions when the input does not adhere to the correct VIST grammar
 * 
 *  @see Collector
 *  @see VISTSyntaxException
 */
public class Lexer {
    /// SCANNING VARIABLES
    private String text;
    private char currentToken;
    private int index;

    /// VARIABLE COLLECTION VARIABLES
    private String currentValue, currentName;
    private SimpleType currentType;
    private VISTObject baseObject;
    private int objectDepth;
    
    // Used for collecting successfully parsed variables
    private final Collector collector;

    public Lexer(Collector collector) {
        this.collector = collector;
    }

    /**
     * 
     * @param text: The VIST document text to be matched
     * @throws VISTSyntaxException
     * 
     * Parses the entire text, providing results to the Collector class
     */
    public void match(String text) throws VISTSyntaxException {
        // Setup scanning variables
        this.text = text;
        currentToken = text.charAt(index);
        
        // Initiate parsing
        matchVariableBlockSequence();

        // Collect all VIST objects
        collector.collect(baseObject);
        baseObject.print();
    }

    // VariableBlockSequence := *( VariableBlock )
    private void matchVariableBlockSequence() {
        while (hasNext()) {
            matchVariableBlock();
        }
    }

    // VariableBlock := Identifier WS PrimitiveTypeAssignment WS Value Separator
    private void matchVariableBlock() throws VISTSyntaxException {
        matchIdentifier();
        matchWS();

        // Check if primitive
        if (Character.toLowerCase(currentToken) == 'i') {
            matchPrimitiveTypeAssignment();
            matchWS();
            matchValue();
            matchSeparator();
        }
        // Check if object
        else if (Character.toLowerCase(currentToken) == 'h') {
            matchObjectTypeAssignment();
            matchOptionalWS();
            matchObjectTypeBody();
        }
    }

    // Matches the primitive type assignment keyword "IS" (tolowercase)
    private void matchPrimitiveTypeAssignment() throws VISTSyntaxException {
        if (Character.toLowerCase(currentToken) != 'i') throw new VISTSyntaxException("Primitive Type Assignment Syntax Error: Illegal Character, i/I expected. Found: " + currentToken);
        else next();

        if (Character.toLowerCase(currentToken) != 's') throw new VISTSyntaxException("Primitive Type Assignment Syntax Error: Illegal Character, s/S expected. Found: " + currentToken);
        else next();
    }

    // Matches the object type assignment keyword "HAS" (tolowercase)
    private void matchObjectTypeAssignment() throws VISTSyntaxException {
        if (Character.toLowerCase(currentToken) != 'h') throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, h/H expected. Found: " + currentToken);
        else next();

        if (Character.toLowerCase(currentToken) != 'a') throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, a/A expected. Found: " + currentToken);
        else next();

        if (Character.toLowerCase(currentToken) != 's') throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, s/S expected. Found: " + currentToken);
        else next();
    }

    // ObjectTypeBody := "(" *( VariableBlock ) ")" *( WS ) Separator
    private void matchObjectTypeBody() {
        if (currentToken != '(') throw new VISTSyntaxException("Object Type Body Syntax Error: Illegal Character, '(' expected. Found: " + currentToken);
        else next();

        // Registers the object
        objectDepth++;
        if (objectDepth == 1) {
            baseObject = new VISTObject(currentName);
        } else {
            baseObject.addObject(new VISTObject(currentName), objectDepth);
        }

        matchOptionalWS();

        while (currentToken != ')') {
            matchVariableBlock();
        }

        if (currentToken != ')') throw new VISTSyntaxException("Object Type Body Syntax Error: Illegal Character, ')' expected. Found: " + currentToken);
        else next();

        // The object was closed, so the depth is decreased
        objectDepth--;

        matchSeparator();
    }

    // Matches either literals, a char, a colour, or any JSON value
    private void matchValue() throws VISTSyntaxException {
        /** TRY TO MATCH A LITERAL  */

        // Try to match the NULL literal
        if (Character.toLowerCase(currentToken) == 'n') {
            next();
            if (Character.toLowerCase(currentToken) == 'u') {
                next();
                if (Character.toLowerCase(currentToken) == 'l') {
                    next();
                    if (Character.toLowerCase(currentToken) == 'l') {
                        currentValue = "null";
                        next();
                    } else throw new VISTSyntaxException("NULL Literal Value Syntax Error: l/L expected. Found: " + currentToken);
                } else throw new VISTSyntaxException("NULL Literal Value Syntax Error: l/L expected. Found: " + currentToken);
            } else throw new VISTSyntaxException("NULL Literal Value Syntax Error: u/U expected. Found: " + currentToken);
        }

        // Try to match the TRUE literal
        else if (Character.toLowerCase(currentToken) == 't') {
            next();
            if (Character.toLowerCase(currentToken) == 'r') {
                next();
                if (Character.toLowerCase(currentToken) == 'u') {
                    next();
                    if (Character.toLowerCase(currentToken) == 'e') {
                        currentValue = "true";
                        currentType = SimpleType.BOOLEAN;
                        next();
                    } else throw new VISTSyntaxException("TRUE Literal Value Syntax Error: e/E expected. Found: " + currentToken);
                } else throw new VISTSyntaxException("TRUE Literal Value Syntax Error: u/U expected. Found: " + currentToken);
            } else throw new VISTSyntaxException("TRUE Literal Value Syntax Error: r/R expected. Found: " + currentToken);
        } 

        // Try to match the FALSE literal
        else if (Character.toLowerCase(currentToken) == 'f') {
            next();
            if (Character.toLowerCase(currentToken) == 'a') {
                next();
                if (Character.toLowerCase(currentToken) == 'l') {
                    next();
                    if (Character.toLowerCase(currentToken) == 's') {
                        next();
                        if (Character.toLowerCase(currentToken) == 'e') {
                            currentValue = "false";
                            currentType = SimpleType.BOOLEAN;
                            next();
                        } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: e/E expected. Found: " + currentToken);
                    } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: s/S expected. Found: " + currentToken);
                } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: l/L expected. Found: " + currentToken);
            } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: a/A expected. Found: " + currentToken);
        } 

        /** TRY TO MATCH A CHAR */
        else if (currentToken == '\'') {
            next();

            // Match any character
            currentValue = "'" + currentToken + "'";
            currentType = SimpleType.CHAR;
            next();

            if (currentToken == '\'') {
                next();
            } else throw new VISTSyntaxException("Char Value Syntax Error: ' expected. Found: " + currentToken);
        }

        /** TRY TO MATCH A COLOUR */
        else if (currentToken == '#') {
            next();

            StringBuilder bobTheBuilder = new StringBuilder("#"); 

            // Check first three HEX colour values
            for (int i = 0; i < 3; i++) {
                if (Character.isDigit(currentToken) || 
                    Character.toLowerCase(currentToken) == 'a' || Character.toLowerCase(currentToken) == 'b' || Character.toLowerCase(currentToken) == 'c' || 
                    Character.toLowerCase(currentToken) == 'd' || Character.toLowerCase(currentToken) == 'e' || Character.toLowerCase(currentToken) == 'f') {
                    bobTheBuilder.append(currentToken);
                    next();
                } else throw new VISTSyntaxException("Colour Value Syntax Error: Letter or Digit (a-f / A-F) expected. Found: " + currentToken);
            }

            // Check if already matched
            if (Character.isWhitespace(currentToken) || currentToken == ';') {
                next();
            } 

            // Check all HEX colour values
            else {
                for (int j = 0; j < 3; j++) {
                    if (Character.isLetterOrDigit(currentToken)) {
                        bobTheBuilder.append(currentToken);
                        next();
                    }
                    else throw new VISTSyntaxException("Colour Value Syntax Error: Letter or Digit expected. Found: " + currentToken);
                }
            }

            currentValue = bobTheBuilder.toString();
            currentType = SimpleType.COLOUR;
        }

        /** TRY TO MATCH JSON VALUES */

        /// STRING
        else if (currentToken == '\"') {
            next();
            
            StringBuilder bobTheBuilder = new StringBuilder("\"");
            while (currentToken != '\"') {
                bobTheBuilder.append(currentToken);
                next();
            }

            currentValue = bobTheBuilder.toString() + "\"";
            currentType = SimpleType.STRING;
            next();
        }

        /// NUMBER (int or float)
        else if (Character.isDigit(currentToken) ||  currentToken == '.') {
            boolean fullstopUsed = false;
            StringBuilder bobTheBuilder = new StringBuilder();

            if (currentToken == '.')
            {
                fullstopUsed = true;
                bobTheBuilder.append(".");
                next();
            } 

            if (!Character.isDigit(currentToken)) throw new VISTSyntaxException("Float Value Syntax Error: Digit after the full-stop expected. Found: " + currentToken);
            while (Character.isDigit(currentToken)) {
                bobTheBuilder.append(currentToken);
                next();
            }

            if (currentToken == '.') {
                if (fullstopUsed) throw new VISTSyntaxException("Float Value Syntax Error: Duplicate full-stop found. Found: " + currentToken);
                
                bobTheBuilder.append(".");
                next();

                if (!Character.isDigit(currentToken)) throw new VISTSyntaxException("Float Value Syntax Error: Digit after the full-stop expected. Found: " + currentToken);
                while (Character.isDigit(currentToken)) {
                    bobTheBuilder.append(currentToken);
                    next();
                }

                if (!isWS(currentToken) && currentToken != ';') throw new VISTSyntaxException("Number Value Syntax Error: WS expected");
            } else if (currentToken != ';' && !isWS(currentToken)) throw new VISTSyntaxException("Number Value Syntax Error: Unexpected character: " + currentToken);
            
            currentValue = bobTheBuilder.toString();
            currentType = fullstopUsed ? SimpleType.FLOAT : SimpleType.INT;
        }

        // Else, the token is illegal
        else throw new VISTSyntaxException("Value Syntax Error: Illegal character: " + currentToken);
    }

    // Separator := ( WS ) ";" ( WS )
    private void matchSeparator() throws VISTSyntaxException {
        matchOptionalWS();

        if (currentToken == ';') {        
            if (objectDepth != 0 && currentValue != null) {
                // Adds the simple type to the object  
                baseObject.addSimpleType(currentName, currentType, currentValue);
            }

            // Resets the variable variables
            currentName = null;
            currentType = null;
            currentValue = null;

            // Advances
            next();
        }
        else throw new VISTSyntaxException("Separator Syntax Error: ; expected");

        // Only match WS if this wasn't the last content
        //if (hasNext()) matchWS();
        matchOptionalWS();
    }

    // Identifier := *( non-WS-Character )
    private void matchIdentifier() throws VISTSyntaxException, VISTSemanticException {
        if (isWS(currentToken)) throw new VISTSyntaxException("Identifier Syntax Error: Illegal WS");

        // Collects the identifier
        StringBuilder identifier = new StringBuilder();

        // An identifier is any non-WS sequence of characters
        while (!isWS(currentToken)) {
            identifier.append(currentToken);
            next();
        }

        // Reserves the identifier and throws an error if non-unique
        currentName = identifier.toString();
        boolean identifierAlreadyExists = collector.reserveIdentifier(currentName);
        if (identifierAlreadyExists) throw new VISTSemanticException(" variable identifier '" + currentName + "' already exists");
    }

    // Handles *( WS )
    private void matchOptionalWS() throws VISTSyntaxException {
        while (isWS(currentToken)) {
            next();
        }
    }

    // Advances to the next non-WS token
    private void matchWS() throws VISTSyntaxException {
        if (!isWS(currentToken)) throw new VISTSyntaxException("WS Syntax Error: Illegal character, WS expected. Found: " + currentToken);

        while (isWS(currentToken)) {
            next();
        }
    }

    // WhiteSpace (WS) is defined as either: a space, tab, new line, carriage return
    private boolean isWS(char token) {
        return token == ' ' || token == '\t' || token == '\n' || token == '\r' || Character.isWhitespace(token);
    }

    /// STATE MANAGEMENT

    // Increments the token, if possible
    private void next() {
        //if (index + 1 >= text.length()) throw new VISTSyntaxException("Syntax Error: End of VIST file reached without resolve");
        if (hasNext()) {
            index++;
            currentToken = text.charAt(index);
        }
    }

    // Checks if the token can be incremented or if hit document end
    private boolean hasNext() {
        return index + 1 < text.length();
    }
}
