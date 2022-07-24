package utility;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Occurs when attempting to retrieve a non-existing variable in the Collector class 
 *  @see Collector
 */
public class VISTVariableNotFoundException extends RuntimeException {
    public VISTVariableNotFoundException(final String type, final String variableName) {
        System.err.println(type + " variable with name: '" + variableName + "' was not found in the VIST file content.");
    }
}