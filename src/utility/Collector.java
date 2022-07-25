package utility;

import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

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
    /// LISTS TO KEEP TRACK OF VARIABLE TYPES    
    /*
    private List<VariableNameValueType<String>> stringVariables;
    private List<VariableNameValueType<Character>> charVariables;
    private List<VariableNameValueType<Integer>> intVariables;
    private List<VariableNameValueType<Float>> floatVariables;
    private List<VariableNameValueType<Boolean>> booleanVariables;*/
    //private List<ColorType> colourVariables;

    // Keeps track of identifiers to prevent non-unique variable names
    private List<String> identifiers;

    // Keeps track of all VIST objects
    private VISTObject baseObject;

    // Initialises all lists
    public Collector() {
        /*stringVariables = new LinkedList<>();
        charVariables = new LinkedList<>();
        intVariables = new LinkedList<>();
        floatVariables = new LinkedList<>();
        booleanVariables = new LinkedList<>();*/

        identifiers = new LinkedList<>();
        //colourVariables = new LinkedList<>();
    }

    /**
     * 
     * @param identifier The variable name identifier
     * @return true if the identifier already existed, false if it was reserved
     */
    public boolean reserveIdentifier(String identifier) {
        if (identifiers.contains(identifier)) return true;

        identifiers.add(identifier);
        return false;
    }

    /**
     * @param variableName The unique name of the variable
     * @param variableContent The actual String representation of the variable value
     * @param type The simple data type of the variable
     * @see SimpleType
     * 
     * Turns the arguments into VariableNameValuePairs and adds it to the correct list
     *//*
    public void collect(String variableName, String variableContent, SimpleType type) {
        switch (type) {
            case BOOLEAN:
                booleanVariables.add(new VariableNameValueType<Boolean>(variableName, Boolean.parseBoolean(variableContent)));
                break;
            case CHAR:
                charVariables.add(new VariableNameValueType<Character>(variableName, variableContent.charAt(1)));
                break;
            case COLOUR:
                //booleanVariables.add(new VariableNameValuePair<Boolean>(variableName, Boolean.parseBoolean(variableContent)));
                break;
            case FLOAT:
                floatVariables.add(new VariableNameValueType<Float>(variableName, Float.parseFloat(variableContent)));
                break;
            case INT:
                intVariables.add(new VariableNameValueType<Integer>(variableName, Integer.parseInt(variableContent)));
                break;
            case NULL:
                //booleanVariables.add(new VariableNameValuePair<Float>(variableName, Float.parseFloat(variableContent)));
                break;
            case STRING:
                stringVariables.add(new VariableNameValueType<String>(variableName, variableContent.substring(1, variableContent.length() - 1)));
                break;
            default: 
                throw new IllegalStateException("SimpleType '" + type + "' exists, but is not implemented.");
        }
    }*/

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
     * ! 0(n^2) 
     * @param objectPath The path to the variable in the following format: *( ObjectIdentifier "/")
     * @param variableName
     * @return
     */
    public Object retrieveFrom(String objectPath, String variableName) {
        String[] paths = objectPath.split("/");
        VISTObject current = baseObject;
        
        for (String vistObjectName : paths) {
            for (int i = 0; i < current.getObjectTypeChildren().size(); i++) {
                System.out.println(current.getObjectTypeChildren().get(i).getIdentifier());
                if (vistObjectName.equals(current.getObjectTypeChildren().get(i).getIdentifier())) {
                    current = current.getObjectTypeChildren().get(i);
                }
            }
        }

        for (int i = 0; i < current.getSimpleTypeChildren().size(); i++) {
            if (variableName.equals(current.getIdentifier()))
                return current.getSimpleTypeChildren().get(i);
        } 

        throw new VISTSyntaxException("Invalid VIST Variable Name: " + variableName + " was not found in /" + current.getIdentifier());
    }

    /*
    public boolean retrieveBoolean(String variableName) {
        for (var variableNameValuePair : booleanVariables)
            if (variableNameValuePair.getVariableName().equals(variableName)) return variableNameValuePair.getVariableValue();

        throw new VISTVariableNotFoundException("boolean", variableName);
    }

    public char retrieveChar(String variableName) {
        for (var variableNameValuePair : charVariables)
            if (variableNameValuePair.getVariableName().equals(variableName)) return variableNameValuePair.getVariableValue();

        throw new VISTVariableNotFoundException("char", variableName);
    }

    public float retrieveFloat(String variableName) {
        for (var variableNameValuePair : floatVariables)
            if (variableNameValuePair.getVariableName().equals(variableName)) return variableNameValuePair.getVariableValue();

        throw new VISTVariableNotFoundException("float", variableName);
    }

    public int retrieveInt(String variableName) {
        for (var variableNameValuePair : intVariables)
            if (variableNameValuePair.getVariableName().equals(variableName)) return variableNameValuePair.getVariableValue();

        throw new VISTVariableNotFoundException("int", variableName);
    }

    public String retrieveString(String variableName) {
        for (var variableNameValuePair : stringVariables)
            if (variableNameValuePair.getVariableName().equals(variableName)) return variableNameValuePair.getVariableValue();

        throw new VISTVariableNotFoundException("String", variableName);
    }
    
    /*
    public boolean retrieve(String variableName) {
        for (var variableNameValuePair : booleanVariables)
            if (variableNameValuePair.getVariableName().equals(variableName)) return variableNameValuePair.getVariableValue();

        throw new VISTVariableNotFoundException(variableName);
    }

    public boolean retrieve(String variableName) {
        for (var variableNameValuePair : booleanVariables)
            if (variableNameValuePair.getVariableName().equals(variableName)) return variableNameValuePair.getVariableValue();

        throw new VISTVariableNotFoundException(variableName);
    }

    public boolean retrieve(String variableName) {
        for (var variableNameValuePair : booleanVariables)
            if (variableNameValuePair.getVariableName().equals(variableName)) return variableNameValuePair.getVariableValue();

        throw new VISTVariableNotFoundException(variableName);
    }*/

    /* */
    /**
     *  Returns a string listing all variables 
     *//*
    @Override
    public String toString() {
        StringBuilder bobTheBuilder = new StringBuilder("Variables:\n");
        
        for (var bools : booleanVariables) {
            bobTheBuilder.append("\t" + bools.toString() + "\n");
        }
        for (var chars : charVariables) {
            bobTheBuilder.append("\t" + chars.toString() + "\n");
        }/*
        for (var colours : colours) {
            bobTheBuilder.append("\t" + colours.toString() + "\n");
        }*//*
        for (var floats : floatVariables) {
            bobTheBuilder.append("\t" + floats.toString() + "\n");
        }
        for (var ints : intVariables) {
            bobTheBuilder.append("\t" + ints.toString() + "\n");
        }
        /* 
        for (var nulls : null) {
            bobTheBuilder.append("\t" + bools.toString() + "\n");
        }*//*
        for (var strings : stringVariables) {
            bobTheBuilder.append("\t" + strings.toString() + "\n");
        }

        return bobTheBuilder.toString();
    }*/
}
