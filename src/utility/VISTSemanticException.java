package utility;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 *  
 *  Occurs when a VIST semantic rule is not met, for example, containing two variables with the same identifier
 */
public class VISTSemanticException extends RuntimeException {
    public VISTSemanticException(String message) {
        System.err.println("Semantic Error: " + message);
    }
}