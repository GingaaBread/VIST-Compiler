package syntax;

import java.util.LinkedList;
import java.util.List;

import utility.SimpleType;

public class VISTObject {
    private List<SimpleType> simpleTypeChildren;
    private List<VISTObject> objectTypeChildren;

    public VISTObject() {
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

        addObject(object, level - 1);
    }
}
