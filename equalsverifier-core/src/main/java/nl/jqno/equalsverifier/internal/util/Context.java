package nl.jqno.equalsverifier.internal.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.FactoryCache;
import org.objenesis.Objenesis;

public final class Context<T> {

    private final Class<T> type;
    private final Configuration<T> configuration;
    private final ClassProbe<T> classProbe;

    private final ValueProvider valueProvider;
    private final SubjectCreator<T> subjectCreator;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "FieldCache is inherently mutable"
    )
    public Context(Configuration<T> configuration, FactoryCache factoryCache, Objenesis objenesis) {
        this.type = configuration.getType();
        this.configuration = configuration;
        this.classProbe = new ClassProbe<>(configuration.getType());

        FactoryCache cache = JavaApiPrefabValues.build().merge(factoryCache);
        this.valueProvider = DefaultValueProviders.create(cache, objenesis);
        this.subjectCreator = new SubjectCreator<>(configuration, valueProvider, objenesis);
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
