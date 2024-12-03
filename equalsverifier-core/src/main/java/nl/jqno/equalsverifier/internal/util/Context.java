package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.reflection.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import org.objenesis.Objenesis;

public final class Context<T> {

    private final Class<T> type;
    private final Configuration<T> configuration;
    private final ClassProbe<T> classProbe;
    private final FieldCache fieldCache;

    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "FieldCache is inherently mutable"
    )
    public Context(
        Configuration<T> configuration,
        FactoryCache factoryCache,
        FieldCache fieldCache,
        Objenesis objenesis
    ) {
        this.type = configuration.getType();
        this.configuration = configuration;
        this.classProbe = new ClassProbe<>(configuration.getType());
        this.fieldCache = fieldCache;

        FactoryCache cache = JavaApiPrefabValues.build().merge(factoryCache);
        this.valueProvider = new VintageValueProvider(cache, objenesis);
        this.subjectCreator =
            new SubjectCreator<>(configuration, valueProvider, fieldCache, objenesis);
    }

    public Class<T> getType() {
        return type;
    }

    public Configuration<T> getConfiguration() {
        return configuration;
    }

    public ClassProbe<T> getClassProbe() {
        return classProbe;
    }

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "A cache is inherently mutable")
    public FieldCache getFieldCache() {
        return fieldCache;
    }

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP",
        justification = "VintageValueProvider can use a mutable cache."
    )
    public ValueProvider getValueProvider() {
        return valueProvider;
    }

    public SubjectCreator<T> getSubjectCreator() {
        return subjectCreator;
    }
}
