package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ModernSubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.reflection.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.vintage.PrefabValues;

public final class Context<T> {

    private final Class<T> type;
    private final Configuration<T> configuration;
    private final ClassProbe<T> classProbe;
    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;

    public Context(Configuration<T> configuration, FactoryCache factoryCache) {
        this.type = configuration.getType();
        this.configuration = configuration;
        this.classProbe = new ClassProbe<>(configuration.getType());

        FactoryCache cache = JavaApiPrefabValues.build().merge(factoryCache);
        PrefabValues prefabValues = new PrefabValues(cache);

        this.valueProvider = new VintageValueProvider(prefabValues);
        this.subjectCreator =
            new ModernSubjectCreator<>(configuration.getTypeTag(), configuration, valueProvider);
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

    public ValueProvider getValueProvider() {
        return valueProvider;
    }

    public SubjectCreator<T> getSubjectCreator() {
        return subjectCreator;
    }
}
