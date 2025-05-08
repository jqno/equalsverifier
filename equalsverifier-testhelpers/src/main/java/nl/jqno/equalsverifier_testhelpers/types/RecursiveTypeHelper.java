package nl.jqno.equalsverifier_testhelpers.types;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RecursiveTypeHelper {

    public static class Node {

        final Node node;

        public Node(Node node) {
            this.node = node;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof Node other && Objects.equals(node, other.node);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(node);
        }
    }

    public static class SubNode extends Node {

        public SubNode(Node node) {
            super(node);
        }
    }

    public static class NodeContainer {

        final Node node;

        public NodeContainer(Node node) {
            this.node = node;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof NodeContainer other && Objects.equals(node, other.node);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(node);
        }
    }

    public static class SubNodeContainer extends NodeContainer {

        public SubNodeContainer(Node node) {
            super(node);
        }
    }

    public static class Tree {

        final List<Tree> innerTrees;

        public Tree(List<Tree> innerTrees) {
            this.innerTrees = innerTrees;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof Tree other && Objects.equals(innerTrees, other.innerTrees);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(innerTrees);
        }
    }

    public static class TreeContainer {

        final Tree tree;

        public TreeContainer(Tree tree) {
            this.tree = tree;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof TreeContainer other && Objects.equals(tree, other.tree);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(tree);
        }
    }

    @SuppressWarnings("unused")
    public static final class StaticFinalNodeContainer {

        private static final Node NODE = new Node(null);
        private final int i;

        public StaticFinalNodeContainer(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StaticFinalNodeContainer other && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    public static final class NodeArray {

        final NodeArray[] nodeArray;

        public NodeArray(NodeArray[] nodeArray) {
            this.nodeArray = nodeArray;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NodeArray other && Arrays.equals(nodeArray, other.nodeArray);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(nodeArray);
        }
    }

    public static final class TwoStepNodeA {

        final TwoStepNodeB node;

        public TwoStepNodeA(TwoStepNodeB node) {
            this.node = node;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TwoStepNodeA other && Objects.equals(node, other.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node);
        }
    }

    public static final class TwoStepNodeB {

        final TwoStepNodeA node;

        public TwoStepNodeB(TwoStepNodeA node) {
            this.node = node;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TwoStepNodeB other && Objects.equals(node, other.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node);
        }
    }

    public static final class TwoStepNodeArrayA {

        final TwoStepNodeArrayB[] nodes;

        public TwoStepNodeArrayA(TwoStepNodeArrayB[] nodes) {
            this.nodes = nodes;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TwoStepNodeArrayA other && Arrays.equals(nodes, other.nodes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(nodes);
        }
    }

    public static final class TwoStepNodeArrayB {

        final TwoStepNodeArrayA[] nodes;

        public TwoStepNodeArrayB(TwoStepNodeArrayA[] nodes) {
            this.nodes = nodes;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof TwoStepNodeArrayB other && Arrays.equals(nodes, other.nodes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(nodes);
        }
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
            return obj instanceof RecursiveType other && Objects.equals(recurse, other.recurse);
        }

        @Override
        public int hashCode() {
            return Objects.hash(recurse);
        }
    }

    public static final class RecursiveTypeContainer {

        private final RecursiveType recurse;

        public RecursiveTypeContainer(RecursiveType recurse) {
            this.recurse = recurse;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof RecursiveTypeContainer other && Objects.equals(recurse, other.recurse);
        }

        @Override
        public int hashCode() {
            return Objects.hash(recurse);
        }
    }
}
