package nl.jqno.equalsverifier.internal.prefab;

import java.util.Optional;
import javax.swing.tree.DefaultMutableTreeNode;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

class JavaxSwingValueSupplier<T> extends ValueSupplier<T> {

    public JavaxSwingValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(DefaultMutableTreeNode.class)) {
            return val(
                new DefaultMutableTreeNode(),
                new DefaultMutableTreeNode(new Object()),
                new DefaultMutableTreeNode());
        }

        return Optional.empty();
    }

}
