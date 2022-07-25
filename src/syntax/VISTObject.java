package syntax;

import java.util.LinkedList;
import java.util.List;

import utility.SimpleType;
import utility.VariableNameValueType;

public class VISTObject {
    private String identifier;
    private List<VariableNameValueType> simpleTypeChildren;
    private List<VISTObject> objectTypeChildren;

    public VISTObject(final String identifier) {
        this.identifier = identifier;
        simpleTypeChildren = new LinkedList<>();
        objectTypeChildren = new LinkedList<>();
    }

    public void addSimpleType(String simpleTypeIdentifier, SimpleType simpleType, String variableValue) {
        simpleTypeChildren.add(new VariableNameValueType(simpleTypeIdentifier, simpleType, variableValue));
    }

    public void addObject(VISTObject object, int level) {
        if (level == 0) {
            objectTypeChildren.add(object);
        }

        addObject(object, level - 1);
    }

    public boolean isEmpty() {
        return simpleTypeChildren.isEmpty() && objectTypeChildren.isEmpty();
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<VariableNameValueType> getSimpleTypeChildren() {
        return simpleTypeChildren;
    }

    public List<VISTObject> getObjectTypeChildren() {
        return objectTypeChildren;
    }

    public void print() {
        System.out.println("Variable: '" + identifier + "' (VIST OBJECT)");

        if (isEmpty()) {
            System.out.println("\t--");
            return;
        }     

        System.out.println("\t-VIST Objects:");
        if (objectTypeChildren.isEmpty()) {
            System.out.println("\t\t--");
        } else {
            for (VISTObject vistObject : objectTypeChildren) {
                System.out.print("\t\t"); 
                vistObject.print();
            }
        }

        System.out.println("\t-Simple Types:");
        if (simpleTypeChildren.isEmpty()) {
            System.out.println("\t\t--");
        } else {
            for (var child : simpleTypeChildren) {
                System.out.print("\t\tVariable: '" + child.getVariableName() + "', Value: " + child.getVariableValue() + " (" + child.gSimpleType() + ")\n");
            }
        }
    }
}