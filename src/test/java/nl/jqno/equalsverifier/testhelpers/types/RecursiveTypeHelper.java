package nl.jqno.equalsverifier.testhelpers.types;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class RecursiveTypeHelper {
    public static final class Node {
        Node node;
    }

    public static final class NodeArray {
        NodeArray[] nodeArrays;
    }

    public static final class TwoStepNodeA {
        TwoStepNodeB node;
    }

    public static final class TwoStepNodeB {
        TwoStepNodeA node;
    }

    public static final class TwoStepNodeArrayA {
        TwoStepNodeArrayB[] nodes;
    }

    public static final class TwoStepNodeArrayB {
        TwoStepNodeArrayA[] nodes;
    }

    public static final class NotRecursiveA {
        NotRecursiveB b;
        NotRecursiveC c;
    }

    public static final class NotRecursiveB {
        NotRecursiveD d;
    }

    public static final class NotRecursiveC {
        NotRecursiveD d;
    }

    public static final class NotRecursiveD {}

    public static final class RecursiveWithAnotherFieldFirst {
        RecursiveThisIsTheOtherField point;
        RecursiveWithAnotherFieldFirst recurse;
    }

    public static final class RecursiveThisIsTheOtherField {}

    public static final class RecursiveType {
        private final RecursiveType recurse;

        public RecursiveType(RecursiveType recurse) {
            this.recurse = recurse;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    public static final class RecursiveTypeContainer {
        private final RecursiveType recurse;

        public RecursiveTypeContainer(RecursiveType recurse) {
            this.recurse = recurse;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }
}
