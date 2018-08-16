package nl.jqno.equalsverifier;

import nl.jqno.equalsverifier.internal.util.Configuration;

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
        Configuration<T> config = Configuration.of(type)
                .withWarningsToSuppress(EnumSet.copyOf(warningsToSuppress))
                .withUsingGetClass(usingGetClass);
        return new EqualsVerifierApi<>(config);
    }
}
