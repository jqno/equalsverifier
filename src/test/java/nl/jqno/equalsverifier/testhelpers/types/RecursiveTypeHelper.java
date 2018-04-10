package nl.jqno.equalsverifier.testhelpers.types;

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
}
