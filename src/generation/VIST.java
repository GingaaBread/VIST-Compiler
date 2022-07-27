package generation;

import java.util.NoSuchElementException;

import syntax.Lexer;
import syntax.VISTObject;
import utility.VISTSemanticException;
import utility.VariableValueTypePair;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Reads a VIST document, and allows retrieving its variables as Java variables
 */
public class VIST {
    private VISTObject baseObject; // keeps track of all VIST objects
    private Lexer lexer; // the lexer compiling the document

    /**
     * Creates the VIST compiler and automatically starts parsing
     * @param vistDocument - the text content of the VISTDocument
     * @param printDebugLogs - should the compiler print console logs for debug purposes?
     */
    public VIST(final String vistDocument, boolean printDebugLogs) {
        lexer = new Lexer();
        
        // Starts the parsing process in the lexer
        baseObject = lexer.match(vistDocument, printDebugLogs);
    }

    /**
     * Creates the VIST compiler and automatically starts parsing
     * @param vistDocument - the text content of the VISTDocument
     */
    public VIST(final String vistDocument) {
        this(vistDocument, false);
    }

    /**
     * Retrieves the variable from the specified path to the object
     * @param objectPath - the location path to the object that contains the variable. '/' points to the index object
     * @param variableName - the identifier of the variable
     * @return the primitive type of the variable as a Object 
     * @example Example Path: /ExampleData/Settings/displayLines
     */
    public Object retrieveFrom(final String objectPath, String variableName) { 
        return processSimpleType(retrieveVariableValueTypePairFrom(objectPath, variableName, baseObject)); 
    }

    /**
     * Recursively retrieves the VariableValueTypePair from the specified path
     * @throws NoSuchElementException - if the specified variable identifier does not exist in the specified path
     * @throws IllegalStateException - if the new object does not exist in the parent
     * @param objectPath - the path to the object that contains the variable. '/' points to the index variable
     * @param variableName - the identifier of the variable to be retrieved
     * @param current - the VISTObject used in the recursion steps
     * @return the VariableValueTypePair from the variable in the specified location
     */
    private VariableValueTypePair retrieveVariableValueTypePairFrom(String objectPath, String variableName, VISTObject current) {
        if (objectPath.equals("") || objectPath.equals("/")) {
            var variable = current.getSimpleTypeChild(variableName);
        
            if (variable == null) throw new NoSuchElementException("VIST Retrieve Error: Variable '" + variableName + "' does not exist in object '" + current.getIdentifier() + "'");
                
            if (current.isExcluded()) throw new VISTSemanticException("Variable '" + variableName + "' is located in a BASE object. Not allowed to access BASE objects.");
            
            return variable;
        }
        else if (objectPath.contains("/")) {
            var newPath = new StringBuilder();
            String[] folders = objectPath.split("/");

            for (int i = 2; i < folders.length; i++) {
                newPath.append("/" + folders[i]);
            }

            objectPath = newPath.toString();
            
            var newObj = current.getObjectTypeChild(folders[1]);

            if (newObj == null) throw new IllegalStateException(folders[1] + " does not exist in " + current.getIdentifier());

            return retrieveVariableValueTypePairFrom(objectPath, variableName, current.getObjectTypeChild(folders[1]));
        }

        throw new VISTSemanticException("Illegal Object Path. Path Grammar: '*(Obj)/Var' expected");
    }

    /**
     * 
     * @param type The String representation of a SimpleType
     * @return The int, float, char, boolean, or String representation 
     * 
     * Turns any SimpleType String representation into its respective Object representation
     */
    private Object processSimpleType(VariableValueTypePair type) {
        switch (type.getSimpleType()) {
            case BOOLEAN:
                return Boolean.parseBoolean(type.getVariableValue());
            case CHAR:
                return type.getVariableValue().charAt(1);
            case COLOUR:
                throw new NoSuchMethodError("Colours are not yet implemented");
            case FLOAT:
                return Float.parseFloat(type.getVariableValue());
            case INT:
                return Integer.parseInt(type.getVariableValue());
            case NULL:
                throw new NoSuchMethodError("NULL is not yet implemented");
            case STRING:
                return type.getVariableValue().substring(1, type.getVariableValue().length() - 1);
            default: 
                throw new IllegalStateException("SimpleType '" + type + "' exists, but is not implemented.");
        }
    }
}
