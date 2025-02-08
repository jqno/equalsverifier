package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.api.EqualsVerifierApi;
import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;

public final class FieldToPrefabValues {

    private FieldToPrefabValues() {}

    public static <T> void move(EqualsVerifierApi<T> api, Class<T> type, T red, T blue) {
        for (FieldProbe probe : FieldIterable.of(type)) {
            moveSingle(api, probe, red, blue);
        }
    }

    @SuppressWarnings("unchecked")
    @SuppressFBWarnings(
            value = "RV_RETURN_VALUE_IGNORED",
            justification = "We intentionally call api.withPrefabValues for its side-effect; we don't need its fluent API here.")
    private static <T, Q> void moveSingle(EqualsVerifierApi<T> api, FieldProbe probe, T red, T blue) {
        Class<Q> fType = (Class<Q>) probe.getType();
        Q fRed = (Q) probe.getValue(red);
        Q fBlue = (Q) probe.getValue(blue);
        api.withPrefabValues(fType, fRed, fBlue);
    }

}
