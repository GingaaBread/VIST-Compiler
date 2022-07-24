package syntax;

import utility.Collector;
import utility.SimpleType;

public class Lexer {
    private String text;
    private char currentToken;
    private int index;

    private String currentValue, currentName;
    private SimpleType currentType;
    
    private Collector collector;

    public Lexer(Collector collector) {
        this.collector = collector;
    }

    public void match(String text) throws VISTSyntaxException {
        this.text = text;
        currentToken = text.charAt(index);

        matchVariableBlock();
    }

    // VariableBlock
    private void matchVariableBlock() throws VISTSyntaxException {
        // If the currentToken is WS, throw an error
        if (isWS(currentToken)) throw new VISTSyntaxException("VariableBlock Syntax Error: Illegal WS, Expected: identifier");

        // Check for Identifier -> WS -> PrimitiveTypeAssignment -> WS -> Value -> Separator
        matchIdentifier();
        matchWS();
        matchPrimitiveTypeAssignment();
        matchWS();
        matchValue();
        matchSeparator();
    }

    // Matches the primitive type assignment keyword "IS" (tolowercase)
    private void matchPrimitiveTypeAssignment() throws VISTSyntaxException {
        if (Character.toLowerCase(currentToken) != 'i') throw new VISTSyntaxException("Primitive Type Assignment Syntax Error: Illegal Character, i/I expected");
        else next();

        if (Character.toLowerCase(currentToken) != 's') throw new VISTSyntaxException("Primitive Type Assignment Syntax Error: Illegal Character, s/S expected");
        else next();
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
                    } else throw new VISTSyntaxException("NULL Literal Value Syntax Error: l/L expected");
                } else throw new VISTSyntaxException("NULL Literal Value Syntax Error: l/L expected");
            } else throw new VISTSyntaxException("NULL Literal Value Syntax Error: u/U expected");
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
                    } else throw new VISTSyntaxException("TRUE Literal Value Syntax Error: e/E expected");
                } else throw new VISTSyntaxException("TRUE Literal Value Syntax Error: u/U expected");
            } else throw new VISTSyntaxException("TRUE Literal Value Syntax Error: r/R expected");
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
                        } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: e/E expected");
                    } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: s/S expected");
                } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: l/L expected");
            } else throw new VISTSyntaxException("FALSE Literal Value Syntax Error: a/A expected");
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
            } else throw new VISTSyntaxException("Char Value Syntax Error: ' expected");
        }

        /** TRY TO MATCH A COLOUR */
        else if (currentToken == '#') {
            next();

            StringBuilder bobTheBuilder = new StringBuilder("#"); 

            // Check first three HEX colour values
            for (int i = 0; i < 3; i++) {
                if (Character.isLetterOrDigit(currentToken)) {
                    bobTheBuilder.append(currentToken);
                    next();
                } else throw new VISTSyntaxException("Colour Value Syntax Error: Letter or Digit expected");
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
                    else throw new VISTSyntaxException("Colour Value Syntax Error: Letter or Digit expected");
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

            if (!Character.isDigit(currentToken)) throw new VISTSyntaxException("Float Value Syntax Error: Digit after the full-stop expected");
            while (Character.isDigit(currentToken)) {
                bobTheBuilder.append(currentToken);
                next();
            }

            if (currentToken == '.') {
                if (fullstopUsed) throw new VISTSyntaxException("Float Value Syntax Error: Duplicate full-stop found");
                
                bobTheBuilder.append(".");
                next();

                if (!Character.isDigit(currentToken)) throw new VISTSyntaxException("Float Value Syntax Error: Digit after the full-stop expected");
                while (Character.isDigit(currentToken)) {
                    bobTheBuilder.append(currentToken);
                    next();
                }

                if (!isWS(currentToken) && currentToken != ';') throw new VISTSyntaxException("Number Value Syntax Error: WS expected");
            } else if (currentToken != ';' && !isWS(currentToken)) throw new VISTSyntaxException("Number Value Syntax Error: Unexpected character: " + currentToken);
            
            currentValue = bobTheBuilder.toString();
            currentType = fullstopUsed ? SimpleType.FLOAT : SimpleType.INT;
        }

        // Else, there token is illegal
        else throw new VISTSyntaxException("Value Syntax Error: Illegal character: " + currentToken);
    }

    private void matchSeparator() throws VISTSyntaxException {
        matchOptionalWS();
        if (currentToken == ';') {            
            // Collects the result
            System.out.println("Successfully created the variable: " + currentName + ", (" + currentType + ") with the value: " + currentValue);
            collector.collect(currentName, currentValue, currentType);
            
            // Resets the variable variables
            currentName = "";
            currentType = null;
            currentValue = "";

            // Advances
            next();
        }
        else throw new VISTSyntaxException("Separator Syntax Error: ; expected");

        // Only match WS if this wasn't the last content
        if (index + 1 < text.length()) matchWS();
    }

    private void matchIdentifier() throws VISTSyntaxException {
        if (isWS(currentToken)) throw new VISTSyntaxException("Identifier Syntax Error: Illegal WS");

        // Collects the identifier
        StringBuilder identifier = new StringBuilder();

        // An identifier is any non-WS sequence of characters
        while (!isWS(currentToken)) {
            identifier.append(currentToken);
            next();
        }

        currentName = identifier.toString();
    }

    private void matchOptionalWS() throws VISTSyntaxException {
        while (isWS(currentToken)) {
            next();
        }
    }

    private void matchWS() throws VISTSyntaxException {
        if (!isWS(currentToken)) throw new VISTSyntaxException("WS Syntax Error: Illegal character, WS expected");

        // Advance to the next non-WS token
        while (isWS(currentToken)) {
            next();
        }
    }

    // WhiteSpace (WS) is defined as either: a space, tab, new line, carriage return
    private boolean isWS(char token) {
        return token == ' ' || token == '\t' || token == '\n' || token == '\r' || Character.isWhitespace(token);
    }

    // Management

    private void next() throws VISTSyntaxException {
        //if (index + 1 >= text.length()) throw new VISTSyntaxException("Syntax Error: End of VIST file reached without resolve");
        if (index + 1 < text.length()) {
            // Increments the token
            index++;
            currentToken = text.charAt(index);
        }
    }
}
