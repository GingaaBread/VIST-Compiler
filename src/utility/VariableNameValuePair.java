package utility;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Defines a 2-Tuple containing any T variable value type and a variable name
 */
public class VariableNameValuePair<T> {
    private String variableName;
    private T variableValue;
    
    public VariableNameValuePair(String variableName, T variableValue) {
        this.variableName = variableName;
        this.variableValue = variableValue;
    }

    @Override
    public String toString() 
    {
        return variableName + ", " + variableValue;
    }

    public String getVariableName() {
        return variableName;
    }

    public T getVariableValue() {
        return variableValue;
    }

}