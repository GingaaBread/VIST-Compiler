package syntax;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Occurs when parsing a syntactically incorrect VIST document
 */
public class VISTSyntaxException extends RuntimeException {
    public VISTSyntaxException(final String message) {
        System.err.println(message);
    }
}