package syntax;

import javax.security.auth.login.CredentialException;

public class Lexer {
    private String text;
    private char currentToken;
    private int index;

    public void match(String text) throws Exception {
        this.text = text;
        currentToken = text.charAt(index);

        matchVariableBlock();
    }

    // VariableBlock
    private void matchVariableBlock() throws Exception {
        // If the currentToken is WS, throw an error
        if (isWS(currentToken)) throw new Exception("VariableBlock Syntax Error: Illegal WS, Expected: identifier");

        // Check for Identifier -> WS -> PrimitiveTypeAssignment -> WS -> Value -> Seperator
        matchIdentifier();
        matchWS();
        matchPrimitiveTypeAssignment();
        matchWS();
        matchValue();
        matchSeperator();
    }

    // Matches the primitive type assignment keyword "IS" (tolowercase)
    private void matchPrimitiveTypeAssignment() throws Exception {
        if (Character.toLowerCase(currentToken) != 'i') throw new Exception("Primitive Type Assignment Syntax Error: Illegal Character, i/I expected");
        else next();

        if (Character.toLowerCase(currentToken) != 's') throw new Exception("Primitive Type Assignment Syntax Error: Illegal Character, s/S expected");
        else next();
    }

    // Matches either literals, a char, a colour, or any JSON value
    private void matchValue() throws Exception {
        /** TRY TO MATCH A LITERAL  */

        // Try to match the NULL literal
        if (Character.toLowerCase(currentToken) == 'n') {
            next();
            if (Character.toLowerCase(currentToken) == 'u') {
                next();
                if (Character.toLowerCase(currentToken) == 'l') {
                    next();
                    if (Character.toLowerCase(currentToken) == 'l') {
                        next();
                    } else throw new Exception("NULL Literal Value Syntax Error: l/L expected");
                } else throw new Exception("NULL Literal Value Syntax Error: l/L expected");
            } else throw new Exception("NULL Literal Value Syntax Error: u/U expected");
        }

        // Try to match the TRUE literal
        else if (Character.toLowerCase(currentToken) == 't') {
            next();
            if (Character.toLowerCase(currentToken) == 'r') {
                next();
                if (Character.toLowerCase(currentToken) == 'u') {
                    next();
                    if (Character.toLowerCase(currentToken) == 'e') {
                        next();
                    } else throw new Exception("TRUE Literal Value Syntax Error: e/E expected");
                } else throw new Exception("TRUE Literal Value Syntax Error: u/U expected");
            } else throw new Exception("TRUE Literal Value Syntax Error: r/R expected");
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
                            next();
                        } else throw new Exception("FALSE Literal Value Syntax Error: e/E expected");
                    } else throw new Exception("FALSE Literal Value Syntax Error: s/S expected");
                } else throw new Exception("FALSE Literal Value Syntax Error: l/L expected");
            } else throw new Exception("FALSE Literal Value Syntax Error: a/A expected");
        } 

        /** TRY TO MATCH A CHAR */
        else if (currentToken == '\'') {
            next();

            // Match any character
            next();

            if (currentToken == '\'') {
                next();
            } else throw new Exception("Char Value Syntax Error: ' expected");
        }

        /** TRY TO MATCH A COLOUR */
        else if (currentToken == '#') {
            next();

            // Check first three HEX colour values
            for (int i = 0; i < 3; i++) {
                if (Character.isLetterOrDigit(currentToken)) next();
                else throw new Exception("Colour Value Syntax Error: Letter or Digit expected");
            }

            // Check if already matched
            if (Character.isWhitespace(currentToken) || currentToken == ';') {
                next();
            } 

            // Check all HEX colour values
            else {
                for (int j = 0; j < 3; j++) {
                    if (Character.isLetterOrDigit(currentToken)) next();
                    else throw new Exception("Colour Value Syntax Error: Letter or Digit expected");
                }
            }
        }

        /** TRY TO MATCH JSON VALUES */

        /// STRING

        /// NUMBER (int or float)
        else if (Character.isDigit(currentToken) ||  currentToken == '.') {
            boolean fullstopUsed = false;

            if (currentToken == '.')
            {
                fullstopUsed = true;
                next();
            } 

            if (!Character.isDigit(currentToken)) throw new Exception("Float Value Syntax Error: Digit after the full-stop expected");
            while (Character.isDigit(currentToken)) {
                next();
            }

            if (currentToken == '.') {
                if (fullstopUsed) throw new Exception("Float Value Syntax Error: Duplicate full-stop found");
                
                next();

                if (!Character.isDigit(currentToken)) throw new Exception("Float Value Syntax Error: Digit after the full-stop expected");
                while (Character.isDigit(currentToken)) {
                    next();
                }

                if (!isWS(currentToken) && currentToken != ';') throw new Exception("Number Value Syntax Error: WS expected");
            } else if (currentToken != ';' && !isWS(currentToken)) throw new Exception("Number Value Syntax Error: Unexpected character: " + currentToken);
        }


        // Else, there token is illegal
        else throw new Exception("Value Syntax Error: Illegal character: " + currentToken);
    }

    private void matchSeperator() throws Exception {
        matchOptionalWS();
        if (currentToken == ';') {
            next();
            System.out.println("Successfully created the variable block");
        }
        else throw new Exception("Seperator Syntax Error: ; expected");

        // Only match WS if this wasn't the last content
        if (index + 1 < text.length()) matchWS();
    }

    private void matchIdentifier() throws Exception {
        if (isWS(currentToken)) throw new Exception("Identifier Syntax Error: Illegal WS");

        // Collects the identifier
        StringBuilder identifier = new StringBuilder();

        // An identifier is any non-WS sequence of characters
        while (!isWS(currentToken)) {
            identifier.append(currentToken);
            next();
        }

        System.out.println(identifier.toString());
    }

    private void matchOptionalWS() throws Exception {
        while (isWS(currentToken)) {
            next();
        }
    }

    private void matchWS() throws Exception {
        if (!isWS(currentToken)) throw new Exception("WS Syntax Error: Illegal character, WS expected");

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

    private void next() throws Exception {
        //if (index + 1 >= text.length()) throw new Exception("Syntax Error: End of VIST file reached without resolve");
        if (index + 1 < text.length()) {
            // Increments the token
            index++;
            currentToken = text.charAt(index);
        }
    }
}
