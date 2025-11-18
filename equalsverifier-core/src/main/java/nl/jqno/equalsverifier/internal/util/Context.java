package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.instantiation.SubjectCreator;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.ValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import org.objenesis.Objenesis;

public final class Context<T> {

    private final Class<T> type;
    private final Configuration<T> configuration;
    private final ClassProbe<T> classProbe;

    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;

    public Context(
            Configuration<T> configuration,
            UserPrefabValueProvider userPrefabs,
            FactoryCache factoryCache,
            FieldCache fieldCache,
            Objenesis objenesis) {
        this.type = configuration.type();
        this.configuration = configuration;
        this.classProbe = ClassProbe.of(configuration.type());
        var modes = configuration.modes();

        this.valueProvider = ValueProviderCreator.create(modes, userPrefabs, factoryCache, fieldCache, objenesis);
        this.subjectCreator = new SubjectCreator<>(configuration, this.valueProvider, objenesis);
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
