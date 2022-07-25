package utility;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Defines a 3-Tuple containing a simple variable type, a variable identifier, and a variable value as a string.
 *  The value needs to be converted into its respective type
 *  @see SimpleType
 */
public class VariableNameValueType {
    private String variableName;
    private SimpleType simpleType;
    private String variableValue;
    
    public VariableNameValueType(final String variableName, final SimpleType simpleType, final String variableValue) {
        this.variableName = variableName;
        this.simpleType = simpleType;
        this.variableValue = variableValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public SimpleType gSimpleType() {
        return simpleType;
    }
}