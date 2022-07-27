package syntax;

import java.util.HashMap;
import java.util.List;

import utility.SimpleType;
import utility.VISTSemanticException;
import utility.VariableValueTypePair;

/**
 *  @author Gino Glink
 *  @version 1.0
 *  @since 1.0
 * 
 *  Describes a VIST object node that can contain further nodes and simple types as children
 *  @see SimpleType
 */
public class VISTObject {
    private String identifier; // the object variable identifier used as a key
    private boolean excluded; // does the BASE keyword exclude this Object?
    private HashMap<String, VariableValueTypePair> simpleTypeChildren; // the simple type children of the object stored as a HashMap
    private HashMap<String, VISTObject> objectTypeChildren; // the object children of the object stored as a HashMap

    public VISTObject(final String identifier, boolean excluded) {
        this.identifier = identifier;
        this.excluded = excluded;
        simpleTypeChildren = new HashMap<>();
        objectTypeChildren = new HashMap<>();
    }

    /**
     * Traverses to the correct VISTObject and adds the SimpleType
     * @param simpleTypeIdentifier The variable identifier of the simple type to be inserted
     * @param simpleType The simple type to be inserted
     * @param variableValue The variable value of the simple type to be inserted
     * @param navigation A navigation path list to correctly traverse to the right VISTObject 
     * @param level Keeps track of recursion depth and adds the simple type when arrived at 0
     */
    public void addSimpleType(String simpleTypeIdentifier, SimpleType simpleType, String variableValue, List<String> navigation, int level) {
        // Check if variable name is not unique
        if (simpleTypeChildren.containsKey(simpleTypeIdentifier))
            throw new VISTSemanticException("variable identifier '" + simpleTypeIdentifier + "' already exists in object '" + identifier + "'"); 
        
        // Check if the parameter variable is to be added to this object    
        if (level == 0) {   
            simpleTypeChildren.put(simpleTypeIdentifier, new VariableValueTypePair(simpleType, variableValue));
        } else {
            // Check if there is no (more) object child, but still a level
            if (objectTypeChildren.isEmpty()) throw new VISTSyntaxException("VISTObject '" + identifier + "' has no more object children, but level is not 0: level=" + level);
            
            // Retrieve the current navigation option from the list 
            var nav = navigation.get(level - 1);

            // Else, there is no problem and the simple type can be added to a lower level recursively
            objectTypeChildren.get(nav).addSimpleType(simpleTypeIdentifier, simpleType, variableValue, navigation, level - 1);
        }
    }

    /**
     * Traverses to the correct VISTObject and adds a VISTObject
     * @param objectIdentifier The variable identifier of the object to be inserted
     * @param object The VISTObject to be inserted
     * @param navigation A navigation path list to correctly traverse to the right VISTObject 
     * @param level Keeps track of recursion depth and adds the simple type when arrived at 0
     */
    public void addObject(String objectIdentifier, VISTObject object, List<String> navigation, int level) {
        // Check if variable name is not unique
        if (objectTypeChildren.containsKey(objectIdentifier)) 
            throw new VISTSyntaxException("variable identifier '" + objectIdentifier + "' already exists in object '" + identifier + "'"); 
         
        // Check if the parameter object is to be added to this object    
        if (level == 0) {
            objectTypeChildren.put(objectIdentifier, object);
        }
        else {
            // Check if there is no (more) object child, but still a level
            if (objectTypeChildren.isEmpty()) throw new VISTSyntaxException("VISTObject '" + identifier + "' has no more object children, but level is not 0: level=" + level);
            
            // Else, there is no problem and the object can be added to a lower level recursively
            objectTypeChildren.get(navigation.get(level)).addObject(objectIdentifier, object, navigation, level - 1);
        }
    }

    /**
     * 
     * @return true if the VISTObject has neither simple type children nor object type children - else false
     */
    public boolean isEmpty() {
        return simpleTypeChildren.isEmpty() && objectTypeChildren.isEmpty();
    }

    /**
     * 
     * @return true if the VISTObject is marked as BASED - else false
     */
    public boolean isExcluded() {
        return excluded;
    }

    /**
     * 
     * @return The variable identifier of the VISTObject
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * 
     * @param identifier The variable identifier of the simple type
     * @return null if the variable does not exist in this object or the variable as a pair with its value and type both stored as strings
     */
    public VariableValueTypePair getSimpleTypeChild(String identifier) {
        return simpleTypeChildren.get(identifier);
    }

    /**
     * 
     * @param identifier The variable identifier of the object
     * @return null if the object does not exist in this object or the VISTObject if it does
     */
    public VISTObject getObjectTypeChild(String identifier) {
        return objectTypeChildren.get(identifier);
    }

    /**
     *  Used for debugging purposes.
     *  Prints the entire tree structure
     */
    public void print(String prefix) {
        System.out.println("Variable: '" + identifier + "' (VIST OBJECT)" + (excluded ? "[BASE]" : ""));

        if (isEmpty()) {
            System.out.println(prefix + "\t\t-- EMPTY --");
            return;
        }     

        System.out.println(prefix + "\t-VIST Objects:");
        if (objectTypeChildren.isEmpty()) {
            System.out.println(prefix + "\t\t--");
        } else {
            var iterator = objectTypeChildren.entrySet().iterator();
            while (iterator.hasNext()) {
                System.out.print(prefix + "\t\t"); 
                iterator.next().getValue().print("\t");
            }
        }

        System.out.println(prefix + "\t-Simple Types:");
        if (simpleTypeChildren.isEmpty()) {
            System.out.println(prefix + "\t\t--");
        } else {
            var iterator = simpleTypeChildren.entrySet().iterator();
            while (iterator.hasNext()) {
                var item = iterator.next();
                System.out.println(prefix + "\t\tVariable: '" + item.getKey() + "', Value: " + item.getValue().getVariableValue() + " (" + item.getValue().getSimpleType() + ")");
            }
        }
    }
}