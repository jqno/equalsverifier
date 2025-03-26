package nl.jqno.equalsverifier.internal.util;

import java.util.*;

import nl.jqno.equalsverifier.Warning;

public final class ConfigurationHelper {

    private ConfigurationHelper() {}

    public static final <T> Configuration<T> emptyConfiguration(Class<T> type, Warning... warnings) {
        return Configuration
                .build(
                    type,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptySet(),
                    null,
                    false,
                    null,
                    false,
                    warnings.length == 0 ? EnumSet.noneOf(Warning.class) : EnumSet.copyOf(Arrays.asList(warnings)),
                    Collections.emptySet(),
                    null,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptyList(),
                    Collections.emptyList());
    }

    public static final <T> Configuration<T> emptyConfigurationWithNonnullFields(Class<T> type, String... fieldNames) {
        return Configuration
                .build(
                    type,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Set.of(fieldNames),
                    Collections.emptySet(),
                    null,
                    false,
                    null,
                    false,
                    EnumSet.noneOf(Warning.class),
                    Collections.emptySet(),
                    null,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptyList(),
                    Collections.emptyList());
    }

    public static final <T> Configuration<T> emptyConfigurationWithPrefabbedFields(
            Class<T> type,
            String... fieldNames) {
        return Configuration
                .build(
                    type,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Set.of(fieldNames),
                    null,
                    false,
                    null,
                    false,
                    EnumSet.noneOf(Warning.class),
                    Collections.emptySet(),
                    null,
                    Collections.emptySet(),
                    Collections.emptySet(),
                    Collections.emptyList(),
                    Collections.emptyList());
    }
}
