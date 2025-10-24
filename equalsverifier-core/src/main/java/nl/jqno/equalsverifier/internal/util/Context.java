package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.Mode;
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

        var recursionDetector = new RecursionDetectingValueProvider();
        var mockito =
                new MockitoValueProvider(!ExternalLibs.isMockitoAvailable() || modes.contains(Mode.skipMockito()));

        var vintageRecursionDetector = new RecursionDetectingValueProvider();
        var vintageBuiltinPrefabs = new BuiltinPrefabValueProvider();
        var vintageGenericBuiltinPrefabs = new BuiltinGenericPrefabValueProvider(recursionDetector);
        var vintageVersionSpecificBuiltinPrefabs = new BuiltinVersionSpecificValueProvider(recursionDetector);
        var vintageChain = new ChainedValueProvider(userPrefabs,
                vintageBuiltinPrefabs,
                vintageGenericBuiltinPrefabs,
                vintageVersionSpecificBuiltinPrefabs,
                mockito);

        vintageRecursionDetector.setValueProvider(vintageChain);

        var builtinPrefabs = new BuiltinPrefabValueProvider();
        var builtinGenericPrefabs = new BuiltinGenericPrefabValueProvider(recursionDetector);
        var versionSpecificBuiltinPrefabs = new BuiltinVersionSpecificValueProvider(recursionDetector);

        var cache = JavaApiPrefabValues.build().merge(factoryCache);
        var vintage = new VintageValueProvider(vintageRecursionDetector, cache, objenesis);

        var mainChain = new ChainedValueProvider(userPrefabs,
                builtinPrefabs,
                builtinGenericPrefabs,
                versionSpecificBuiltinPrefabs,
                mockito,
                vintage);
        var caching = new CachingValueProvider(userPrefabs, fieldCache, mainChain);

        recursionDetector.setValueProvider(caching);

        this.valueProvider = caching;
        this.subjectCreator = new SubjectCreator<>(configuration, recursionDetector, objenesis);
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
