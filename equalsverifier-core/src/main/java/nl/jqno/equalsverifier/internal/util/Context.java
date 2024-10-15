package nl.jqno.equalsverifier.internal.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.instantiation.*;
import org.objenesis.Objenesis;

public final class Context<T> {

    private final Class<T> type;
    private final Configuration<T> configuration;
    private final ClassProbe<T> classProbe;

    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "FieldCache is inherently mutable"
    )
    public Context(
        Configuration<T> configuration,
        FactoryCache factoryCache,
        PrefabValueProvider prefabValueProvider,
        Objenesis objenesis
    ) {
        this.type = configuration.getType();
        this.configuration = configuration;
        this.classProbe = new ClassProbe<>(configuration.getType());

        List<ValueProvider> providers = new ArrayList<>();
        providers.add(prefabValueProvider);
        FactoryCache cache = JavaApiPrefabValues.build().merge(factoryCache);
        ValueProvider vintage = new VintageValueProvider(
            new ChainedValueProvider(providers),
            cache,
            objenesis
        );

        providers.add(vintage);

        this.valueProvider = new ChainedValueProvider(providers);
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
