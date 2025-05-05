package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;
import javax.naming.Reference;
import javax.swing.tree.DefaultMutableTreeNode;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

// CHECKSTYLE OFF: ParameterNumber

class JavaxClassesTest {

    @Test
    void succeed_whenClassUsesJavaxClasses() {
        EqualsVerifier.forClass(JavaxContainer.class).verify();
    }

    static final class JavaxContainer {

        private final Reference ref;
        private final DefaultMutableTreeNode node;

        public JavaxContainer(Reference ref, DefaultMutableTreeNode node) {
            this.ref = ref;
            this.node = node;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof JavaxContainer other
                    && Objects.equals(ref, other.ref)
                    && Objects.equals(node, other.node);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ref, node);
        }
    }
}
