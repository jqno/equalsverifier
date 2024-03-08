package nl.jqno.equalsverifier.internal.instantiation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

public class VintageFieldInstantiationStrategy implements FieldInstantiationStrategy {

    private final PrefabValues prefabValues;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "PrefabValues is inherently mutable."
    )
    public VintageFieldInstantiationStrategy(PrefabValues prefabValues) {
        this.prefabValues = prefabValues;
    }

    @Override
    public <T> Tuple<T> instantiate(TypeTag tag) {
        return prefabValues.giveTuple(tag);
    }
}
