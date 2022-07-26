package generation;

import syntax.Lexer;
import utility.Collector;
import utility.VISTVariableNotFoundException;
import utility.VariableValueTypePair;

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
    public VIST(final String vistDocument, boolean printDebugLogs) {
        collector = new Collector();
        lexer = new Lexer(collector);

        // Begins the parsing process in the lexer
        lexer.match(vistDocument, printDebugLogs);
    }

    // By default, does not print debug logs
    public VIST(final String vistDocument) {
        this(vistDocument, false);
    }

    // Returns the requested variable value
    public Object retrieveFrom(final String objectPath, String variableName) { 
        return processSimpleType(collector.retrieveFrom(objectPath, variableName)); 
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
