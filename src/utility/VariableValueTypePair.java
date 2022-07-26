package utility;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Defines a 2-Tuple containing a simple variable type and a variable value as a string.
 *  The value needs to be converted into its respective type
 *  @see SimpleType
 */
public class VariableValueTypePair {
    private SimpleType simpleType; // the variable type (int / char / ...)
    private String variableValue; // the variable value (3 / 'A' / ...)
    
    public VariableValueTypePair(final SimpleType simpleType, final String variableValue) {
        this.simpleType = simpleType;
        this.variableValue = variableValue;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public SimpleType getSimpleType() {
        return simpleType;
    }
}