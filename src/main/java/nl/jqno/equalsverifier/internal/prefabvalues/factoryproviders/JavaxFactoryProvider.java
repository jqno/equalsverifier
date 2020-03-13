package nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;

import javax.naming.Reference;
import javax.swing.tree.DefaultMutableTreeNode;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;

public final class JavaxFactoryProvider implements FactoryProvider {

    public FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        cache.put(
                Reference.class,
                values(new Reference("one"), new Reference("two"), new Reference("one")));
        cache.put(
                DefaultMutableTreeNode.class,
                values(
                        new DefaultMutableTreeNode(),
                        new DefaultMutableTreeNode(new Object()),
                        new DefaultMutableTreeNode()));

        return cache;
    }
}
