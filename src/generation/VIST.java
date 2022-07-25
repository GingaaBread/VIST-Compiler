package generation;

import syntax.Lexer;
import utility.Collector;
import utility.VISTVariableNotFoundException;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Reads a VIST document, and allows retrieving its variables as Java variables
 */
public class VIST {
    private Collector collector; // the collector storing the parsed variables
    private Lexer lexer; // the lexer compiling the document

    // Initialises the variables and automatically begins parsing
    public VIST(final String vistDocument) {
        collector = new Collector();
        lexer = new Lexer(collector);

        // Begins the parsing process in the lexer
        lexer.match(vistDocument);
    }

    /// RETRIEVERS

    public Object retrieveFrom(final String objectPath, String variableName) { return collector.retrieveFrom(objectPath, variableName); }

    /**
     * 
     * @param variableName The name of the String variable 
     * @return The String variable value 
     * @throws VISTVariableNotFoundException
     */
    //public String retrieveString(final String variableName) { return collector.retrieveString(variableName); }

    /**
     * 
     * @param variableName The name of the char variable 
     * @return The char variable value 
     * @throws VISTVariableNotFoundException
     */
    //public char retrieveChar(final String variableName) { return collector.retrieveChar(variableName); }
    
    /**
     * 
     * @param variableName The name of the boolean variable 
     * @return The boolean variable value 
     * @throws VISTVariableNotFoundException
     */
    //public boolean retrieveBoolean(final String variableName) { return collector.retrieveBoolean(variableName); }

    /**
     * 
     * @param variableName The name of the float variable 
     * @return The float variable value 
     * @throws VISTVariableNotFoundException
     */
    //public float retrieveFloat(final String variableName) { return collector.retrieveFloat(variableName); }

    /**
     * 
     * @param variableName The name of the int variable 
     * @return The int variable value 
     * @throws VISTVariableNotFoundException
     */
    //public int retrieveInt(final String variableName) { return collector.retrieveInt(variableName); }

}
