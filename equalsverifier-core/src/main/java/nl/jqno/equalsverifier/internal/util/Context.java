package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.instantiation.*;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import org.objenesis.Objenesis;

public final class Context<T> {

    private final Class<T> type;
    private final Configuration<T> configuration;
    private final ClassProbe<T> classProbe;
    private final FieldCache fieldCache;

    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "FieldCache is inherently mutable")
    public Context(
            Configuration<T> configuration,
            FactoryCache factoryCache,
            FieldCache fieldCache,
            Objenesis objenesis) {
        this.type = configuration.getType();
        this.configuration = configuration;
        this.classProbe = ClassProbe.of(configuration.getType());
        this.fieldCache = fieldCache;

        FactoryCache cache = JavaApiPrefabValues.build().merge(factoryCache);
        VintageValueProvider vintage = new VintageValueProvider(cache, objenesis);
        CachingValueProvider caching = new CachingValueProvider(fieldCache, vintage);
        this.valueProvider = caching;
        this.subjectCreator = new SubjectCreator<>(configuration, valueProvider, fieldCache, objenesis);
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

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "VintageValueProvider can use a mutable cache.")
    public ValueProvider getValueProvider() {
        return valueProvider;
    }

    public SubjectCreator<T> getSubjectCreator() {
        return subjectCreator;
    }
}
