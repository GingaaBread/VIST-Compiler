package syntax;

import java.util.LinkedList;
import java.util.List;

import utility.SimpleType;

public class VISTObject {
    private String identifier;
    private List<SimpleType> simpleTypeChildren;
    private List<VISTObject> objectTypeChildren;

    public VISTObject(final String identifier) {
        this.identifier = identifier;
        simpleTypeChildren = new LinkedList<>();
        objectTypeChildren = new LinkedList<>();
    }

    public boolean isEmpty() {
        return simpleTypeChildren.isEmpty() && objectTypeChildren.isEmpty();
    }

    public void addObject(VISTObject object, int level) {
        if (level == 0) {
            objectTypeChildren.add(object);
        }

        object.addObject(object, level - 1);
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<SimpleType> getSimpleTypeChildren() {
        return simpleTypeChildren;
    }

    public List<VISTObject> getObjectTypeChildren() {
        return objectTypeChildren;
    }

    public void print() {
        System.out.println(identifier);

        if (isEmpty()) {
            System.out.println("--");
            return;
        }     

        if (!objectTypeChildren.isEmpty()) {
            System.out.println("VIST Objects:");
            for (VISTObject vistObject : objectTypeChildren) {
                System.out.print("\t"); 
                vistObject.print();
            }
        }

        if (!simpleTypeChildren.isEmpty()) {
            System.out.println("Simple Types:");
            for (var child : simpleTypeChildren) {
                System.out.print("\t" + child.name());
            }
        }
    }
}
