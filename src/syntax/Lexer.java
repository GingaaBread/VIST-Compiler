package syntax;

import java.util.LinkedList;
import java.util.List;

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
    private int index, line, linePosition;

    /// VARIABLE COLLECTION VARIABLES
    private String currentValue, currentIdentifier;
    private SimpleType currentType;
    private boolean baseObjectDetected;
    private VISTObject baseObject;
    private List<String> objectHierarchy;
    private List<VISTObject> abstractObjects;

    // Initialises the empty structures
    public Lexer() {
        this.objectHierarchy = new LinkedList<>();
        abstractObjects = new LinkedList<>();
        this.baseObject = new VISTObject("/", false);
    }

    /**
     * 
     * @param text: The VIST document text to be matched
     * @throws VISTSyntaxException
     * 
     * Parses the entire text, providing results to the VIST class
     */
    public VISTObject match(String text, boolean printDebugLogs) throws VISTSyntaxException {
        try {
            // Setup scanning variables
            this.text = text;
            currentToken = text.charAt(index);
            
            // Initiate parsing
            matchVariableBlockSequence();

            if (printDebugLogs) baseObject.print("");
        }
        catch (Exception e) {
            System.err.println("VIST Compile Error (halted at " + line + "[" + (linePosition - 1) + "])");
            throw e;
        }

        return baseObject;
    }

    // VariableBlockSequence := *( VariableBlock )
    private void matchVariableBlockSequence() {
        while (hasNext()) {
            matchVariableBlock();
            System.out.println(
                objectHierarchy.toString()
            );
        }
    }

    // VariableBlock := BaseObject | [ Identifier WS [ PrimitiveVariableBlockBody | ObjectVariableBlockBody | InheritedVariableBlockBody ] ]
    private void matchVariableBlock() throws VISTSyntaxException {
        matchIdentifier();
        matchWS();

        // Check if primitive
        if (is('i')) {
            if (baseObjectDetected) throw new VISTSyntaxException("The BASE keyword can only be applied to objects.");

            matchPrimitiveVariableBlockBody();
        }
        // Check if object
        else if (is('h')) {
            matchObjectVariableBlockBody();
        }
        // Check if inherited
        else if (is('u')) {
            matchInheritedVariableBlockBody();
        }
    }

    // PrimitiveVariableBlockBody := PrimitiveTypeAssignment WS Value Separator
    private void matchPrimitiveVariableBlockBody() {
        matchPrimitiveTypeAssignment();
        matchWS();
        matchValue();
        matchSeparator();
    }

    // ObjectVariableBlockBody := ObjectTypeAssignment *( WS ) ObjectTypeBody
    private void matchObjectVariableBlockBody() {
        matchObjectTypeAssignment();
        matchOptionalWS();
        matchObjectTypeBody();
    }

    // InheritedVariableBlockBody := InheritanceAssignment WS FamiliarIdentifier Separator
    private void matchInheritedVariableBlockBody() {
        matchInheritanceAssignment();
        matchWS();
        matchFamiliarIdentifier();
        matchSeparator();
    }

    // PrimitiveTypeAssignment := "IS" (tolowercase)
    private void matchPrimitiveTypeAssignment() throws VISTSyntaxException {
        if (!is('i')) throw new VISTSyntaxException("Primitive Type Assignment Syntax Error: Illegal Character, i/I expected. Found: " + currentToken);
        else next();

        if (!is('s')) throw new VISTSyntaxException("Primitive Type Assignment Syntax Error: Illegal Character, s/S expected. Found: " + currentToken);
        else next();
    }

    // InheritanceAssignment := "USES" (tolowercase)
    private void matchInheritanceAssignment() throws VISTSyntaxException {
        if (!is('u')) throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, u/U expected. Found: " + currentToken);
        else next();

        if (!is('s')) throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, s/S expected. Found: " + currentToken);
        else next();

        if (!is('e')) throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, e/E expected. Found: " + currentToken);
        else next();

        if (!is('s')) throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, s/S expected. Found: " + currentToken);
        else next();
    }

    // ObjectTypeAssignment := "HAS" (tolowercase)
    private void matchObjectTypeAssignment() throws VISTSyntaxException {
        if (!is('h')) throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, h/H expected. Found: " + currentToken);
        else next();

        if (!is('a')) throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, a/A expected. Found: " + currentToken);
        else next();

        if (!is('s')) throw new VISTSyntaxException("Object Type Assignment Syntax Error: Illegal Character, s/S expected. Found: " + currentToken);
        else next();
    }

    // ObjectTypeBody := "(" *( VariableBlock ) ")" *( WS ) Separator
    private void matchObjectTypeBody() {
        if (!is('(')) throw new VISTSyntaxException("Object Type Body Syntax Error: Illegal Character, '(' expected. Found: " + currentToken);
        else next();
        
        if (baseObjectDetected)
            abstractObjects.add(new VISTObject(currentIdentifier, true));
        
        // Registers the object
        objectHierarchy.add(0, currentIdentifier);

        baseObject.addObject(currentIdentifier, new VISTObject(currentIdentifier, baseObjectDetected), objectHierarchy, objectHierarchy.size() - 1);
        baseObjectDetected = false;

        matchOptionalWS();

        while (!is(')')) {
            matchVariableBlock();
        }

        if (!is(')')) throw new VISTSyntaxException("Object Type Body Syntax Error: Illegal Character, ')' expected. Found: " + currentToken);
        else next();

        // The object was closed, so the depth is decreased
        objectHierarchy.remove(0);

        matchSeparator();
    }

    // Matches either literals, a char, a colour, or any JSON value
    private void matchValue() throws VISTSyntaxException {
        /** TRY TO MATCH A LITERAL  */

        // Try to match the NULL literal
        if (is('n')) {
            next();
            if (is('u')) {
                next();
                if (is('l')) {
                    next();
                    if (is('l')) {
                        currentValue = "null";
                        next();
                    } else throw new VISTSyntaxException("NULL Literal Value Syntax Error: l/L expected. Found: " + currentToken);
                } else throw new VISTSyntaxException("NULL Literal Value Syntax Error: l/L expected. Found: " + currentToken);
            } else throw new VISTSyntaxException("NULL Literal Value Syntax Error: u/U expected. Found: " + currentToken);
        }

        // Try to match the TRUE literal
        else if (is('t')) {
            next();
            if (is('r')) {
                next();
                if (is('u')) {
                    next();
                    if (is('e')) {
                        currentValue = "true";
                        currentType = SimpleType.BOOLEAN;
                        next();
                    } else throw new VISTSyntaxException("TRUE Literal Value Syntax Error: e/E expected. Found: " + currentToken);
                } else throw new VISTSyntaxException("TRUE Literal Value Syntax Error: u/U expected. Found: " + currentToken);
            } else throw new VISTSyntaxException("TRUE Literal Value Syntax Error: r/R expected. Found: " + currentToken);
        } 

        // Try to match the FALSE literal
        else if (is('f')) {
            next();
            if (is('a')) {
                next();
                if (is('l')) {
                    next();
                    if (is('s')) {
                        next();
                        if (is('e')) {
                            currentValue = "false";
                            currentType = SimpleType.BOOLEAN;
                            next();
                        } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: e/E expected. Found: " + currentToken);
                    } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: s/S expected. Found: " + currentToken);
                } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: l/L expected. Found: " + currentToken);
            } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: a/A expected. Found: " + currentToken);
        } 

        /** TRY TO MATCH A CHAR */
        else if (is('\'')) {
            next();

            // Match any character
            currentValue = "'" + currentToken + "'";
            currentType = SimpleType.CHAR;
            next();

            if (is('\'')) {
                next();
            } else throw new VISTSyntaxException("Char Value Syntax Error: ' expected. Found: " + currentToken);
        }

        /** TRY TO MATCH A COLOUR */
        else if (is('#')) {
            next();

            StringBuilder bobTheBuilder = new StringBuilder("#"); 

            // Check first three HEX colour values
            for (int i = 0; i < 3; i++) {
                if (Character.isDigit(currentToken) || is('a') || is('b') || is('c') || is('d') || is('e') || is('f')) {
                    bobTheBuilder.append(currentToken);
                    next();
                } else throw new VISTSyntaxException("Colour Value Syntax Error: Letter or Digit (a-f / A-F) expected. Found: " + currentToken);
            }

            // Check if already matched
            if (Character.isWhitespace(currentToken) || is(';')) {
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
        else if (is('\"')) {
            next();
            
            StringBuilder bobTheBuilder = new StringBuilder("\"");
            while (!is('\"')) {
                bobTheBuilder.append(currentToken);
                next();
            }

            currentValue = bobTheBuilder.toString() + "\"";
            currentType = SimpleType.STRING;
            next();
        }

        /// NUMBER (int or float)
        else if (Character.isDigit(currentToken) ||  is('.')) {
            boolean fullstopUsed = false;
            StringBuilder bobTheBuilder = new StringBuilder();

            if (is('.'))
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

            if (is('.')) {
                if (fullstopUsed) throw new VISTSyntaxException("Float Value Syntax Error: Duplicate full-stop found. Found: " + currentToken);
                
                fullstopUsed = true;
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
            if (currentValue != null) {
                // Adds the simple type to the object
                baseObject.addSimpleType(currentIdentifier, currentType, currentValue, objectHierarchy, objectHierarchy.size());
            }

            // Resets the variable variables
            currentIdentifier = null;
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

    // Identifier := *( NonWSNorSlashCharacter )
    private void matchIdentifier() throws VISTSyntaxException, VISTSemanticException {
        if (isWS(currentToken)) throw new VISTSyntaxException("Identifier Syntax Error: Illegal WS");

        // Collects the identifier
        StringBuilder identifier = new StringBuilder();

        // An identifier is any non-WS sequence of characters without '/'
        while (!isWS(currentToken)) {
            if (is('/')) throw new VISTSemanticException("Illegal Identifier. Token '/' is reserved. Identifier: " + identifier.toString() + "/ <---");
            identifier.append(currentToken);
            next();
        }

        // Checks if the identifier is prefixed with the BASE keyword
        if (identifier.toString().equalsIgnoreCase("base")) {
            // BASE WS Identifier

            baseObjectDetected = true;

            matchWS();

            identifier = new StringBuilder();
            // An identifier is any non-WS sequence of characters without '/'
            while (!isWS(currentToken)) {
                if (is('/')) throw new VISTSemanticException("Illegal Identifier. Token '/' is reserved. Identifier: " + identifier.toString() + "/ <---");
                identifier.append(currentToken);
                next();
            }
        } 
        
        currentIdentifier = identifier.toString();
    }

    private void matchFamiliarIdentifier() throws VISTSemanticException, VISTSyntaxException {
        if (isWS(currentToken)) throw new VISTSyntaxException("Identifier Syntax Error: Illegal WS");
        
        String childIdentifier = currentIdentifier;

        // Collects the identifier
        StringBuilder identifier = new StringBuilder();

        // An identifier is any non-WS sequence of characters without '/'
        while (!isWS(currentToken) && !is(';')) {
            if (is('/')) throw new VISTSemanticException("Illegal Identifier. Token '/' is reserved. Identifier: " + identifier.toString() + "/ <---");
                        
            identifier.append(currentToken);
            next();
        }

        currentIdentifier = identifier.toString();

        // Check if there is no identifier matching this variable 
        if (abstractObjects.stream().anyMatch(obj -> obj.getIdentifier().equals(currentIdentifier))) {
            var copy = abstractObjects.stream()
                .filter(obj -> obj.getIdentifier().equals(currentIdentifier))
                .findFirst()
                .get();
                
            copy.include();
            copy.setIdentifier(childIdentifier);

            baseObject.addObject(
                childIdentifier,
                copy, 
                objectHierarchy, 
                objectHierarchy.size()
            );
        } else throw new VISTSemanticException("Trying to inherit from a non-existing variable. Variable identifier: '" + currentIdentifier + "'");
    }

    // Handles *( WS )
    private void matchOptionalWS() throws VISTSyntaxException {
        while (isWS(currentToken)) next();
    }

    // Advances to the next non-WS token
    private void matchWS() throws VISTSyntaxException {
        if (!isWS(currentToken)) throw new VISTSyntaxException("WS Syntax Error: Illegal character, WS expected. Found: " + currentToken);

        while (isWS(currentToken)) {
            next();
        }
    }

    /// HELPERS

    // Checks if the current token is the character or lowercase character
    private boolean is(char character) {
        return Character.toLowerCase(currentToken) == character;
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
            linePosition++;
            currentToken = text.charAt(index);

            // Increment line if \n found
            if (is('\n'))
            {
                line++;
                linePosition = 0;
            }
        }
    }

    // Checks if the token can be incremented or if hit document end
    private boolean hasNext() {
        return index + 1 < text.length();
    }
}
