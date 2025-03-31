package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.internal.SuppressFBWarnings;
import nl.jqno.equalsverifier.internal.instantiation.*;
import nl.jqno.equalsverifier.internal.instantiation.prefab.BuiltinPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import org.objenesis.Objenesis;

public final class Context<T> {

    private final Class<T> type;
    private final Configuration<T> configuration;
    private final ClassProbe<T> classProbe;

    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;

    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "FieldCache is inherently mutable")
    public Context(
            Configuration<T> configuration,
            UserPrefabValueProvider userPrefabs,
            FactoryCache factoryCache,
            FieldCache fieldCache,
            Objenesis objenesis) {
        this.type = configuration.getType();
        this.configuration = configuration;
        this.classProbe = ClassProbe.of(configuration.getType());
        var modes = configuration.getModes();

        var builtinPrefabs = new BuiltinPrefabValueProvider();
        var mockito =
                new MockitoValueProvider(!ExternalLibs.isMockitoAvailable() || modes.contains(Mode.skipMockito()));

        var vintageChain = new ChainedValueProvider(userPrefabs, builtinPrefabs, mockito);
        var cache = JavaApiPrefabValues.build().merge(factoryCache);
        var vintage = new VintageValueProvider(vintageChain, cache, objenesis);

        var mainChain = new ChainedValueProvider(userPrefabs, builtinPrefabs, mockito, vintage);
        var caching = new CachingValueProvider(fieldCache, mainChain);

        this.valueProvider = caching;
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

    @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "VintageValueProvider can use a mutable cache.")
    public ValueProvider getValueProvider() {
        return valueProvider;
    }

    public SubjectCreator<T> getSubjectCreator() {
        return subjectCreator;
    }
}
