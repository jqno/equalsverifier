package nl.jqno.equalsverifier.integration.strictfinal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class WithPrefabValuesTest {
    @Test
    void fail_whenPrefabValueCantBeCopied() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(Node.class)
                    .withPrefabValues(Node.class, new Node(null, null), new Node(new Node(null, null), null))
                    .verify())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot copy prefab value")
                .hasMessageContaining("#withPrefabValues()");
    }

    @Test
    void succeed_whenPrefabValueCantBeCopied_givenRedCopy() {
        EqualsVerifier
                .forClass(Node.class)
                .withFactory(v -> new Node(v.get("node"), null))
                .withPrefabValues(
                    Node.class,
                    new Node(null, null),
                    new Node(new Node(null, null), null),
                    new Node(null, null))
                .verify();
    }

    @Test
    void fail_whenPrefabValueForFieldCantBeCopied() {
        assertThatThrownBy(
            () -> EqualsVerifier
                    .forClass(Node.class)
                    .withPrefabValuesForField("node", new Node(null, null), new Node(new Node(null, null), null))
                    .verify())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot copy prefab value")
                .hasMessageContaining("#withPrefabValuesForField()");
    }

    @Test
    void succeed_whenPrefabValueForFieldCantBeCopied_givenRedCopy() {
        EqualsVerifier
                .forClass(Node.class)
                .withFactory(v -> new Node(v.get("node"), null))
                .withPrefabValuesForField(
                    "node",
                    new Node(null, null),
                    new Node(new Node(null, null), null),
                    new Node(null, null))
                .verify();
    }

    static final class Node {
        private final Node node;

        public Node(Node node, Object discarded) {
            this.node = node;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Node other && Objects.equals(node, other.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(node);
        }
    }
}
