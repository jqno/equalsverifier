package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.internal.testhelpers.Util.defaultHashCode;

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

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class JavaxContainer {

        private final Reference ref;
        private final DefaultMutableTreeNode node;

        public JavaxContainer(Reference ref, DefaultMutableTreeNode node) {
            this.ref = ref;
            this.node = node;
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
