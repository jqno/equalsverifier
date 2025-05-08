package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import nl.jqno.equalsverifier.internal.reflection.FieldIterable;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;

public final class FieldToPrefabValues {

    private FieldToPrefabValues() {}

    @SuppressWarnings("CheckReturnValue")
    public static <T> void move(SingleTypeEqualsVerifierApi<T> api, Class<T> type, T red, T blue) {
        for (FieldProbe probe : FieldIterable.ofIgnoringStatic(type)) {
            Object fRed = probe.getValue(red);
            Object fBlue = probe.getValue(blue);
            api.withPrefabValuesForField(probe.getName(), fRed, fBlue);
        }
    }
}
