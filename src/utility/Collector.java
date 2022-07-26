package utility;

import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import syntax.VISTObject;
import syntax.VISTSyntaxException;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Collects and manages all compiled variables
 */
public class Collector {
    // Keeps track of all VIST objects
    private VISTObject baseObject;

    /**
     * 
     * @param baseObject The base node of the VIST object tree
     * 
     * Collects the base object
     */
    public void collect(final VISTObject baseObject) {
        this.baseObject = baseObject;
    }

    // RETRIEVERS

    /**
     * @param objectPath The path to the variable in the following format: *( ObjectIdentifier "/")
     * @param variableName
     * @return
     */
    public VariableValueTypePair retrieveFrom(String objectPath, String variableName) {
        return retrieveFrom(objectPath, variableName, baseObject);
    }

    private VariableValueTypePair retrieveFrom(String objectPath, String variableName, VISTObject current) {
        if (objectPath.equals("") || objectPath.equals("/")) {
            var variable = current.getSimpleTypeChild(variableName);
        
            if (variable == null) throw new NoSuchElementException("VIST Retrieve Error: Variable '" + variableName + "' does not exist in object '" + current.getIdentifier() + "'");
                
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

            return retrieveFrom(objectPath, variableName, current.getObjectTypeChild(folders[1]));
        }

        throw new VISTSemanticException("Illegal Object Path. Path Grammar: '*(Obj)/Var' expected");
    }
}