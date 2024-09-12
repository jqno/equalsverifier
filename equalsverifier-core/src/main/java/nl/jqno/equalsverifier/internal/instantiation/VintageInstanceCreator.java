package nl.jqno.equalsverifier.internal.instantiation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.exceptions.ModuleException;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

public class VintageInstanceCreator implements InstanceCreator {

    private final PrefabValues prefabValues;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrefabValues is inherently mutable."
    )
    public VintageInstanceCreator(PrefabValues prefabValues) {
        this.prefabValues = prefabValues;
    }

    @Override
    public <T> Tuple<T> instantiate(TypeTag tag) {
        try {
            return prefabValues.giveTuple(tag);
        } catch (RuntimeException e) {
            // InaccessibleObjectException is not yet available in Java 8
            if (e.getClass().getName().endsWith("InaccessibleObjectException")) {
                throw new ModuleException(
                    "The class is not accessible via the Java Module system. Consider opening the module that contains it.",
                    e
                );
            } else {
                throw e;
            }
        }
    }
}
