package nl.jqno.equalsverifier;

import java.util.Collections;
import java.util.EnumSet;

public final class ConfiguredEqualsVerifierApi {
    private final EnumSet<Warning> warningsToSuppress = EnumSet.noneOf(Warning.class);
    private boolean usingGetClass = false;

    public ConfiguredEqualsVerifierApi suppress(Warning... warnings) {
        Collections.addAll(warningsToSuppress, warnings);
        return this;
    }

    public ConfiguredEqualsVerifierApi usingGetClass() {
        usingGetClass = true;
        return this;
    }

    public <T> EqualsVerifierApi<T> forClass(Class<T> type) {
        return new EqualsVerifierApi<>(type, EnumSet.copyOf(warningsToSuppress), usingGetClass);
    }
}
