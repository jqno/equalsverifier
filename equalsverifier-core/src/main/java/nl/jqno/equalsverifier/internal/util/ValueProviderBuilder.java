package nl.jqno.equalsverifier.internal.util;

import java.util.Set;

import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.internal.instantiation.*;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import org.objenesis.Objenesis;

public final class ValueProviderBuilder {
    private ValueProviderBuilder() {}

    public static ValueProvider build(
            Set<Mode> modes,
            UserPrefabValueCaches userPrefabCaches,
            FactoryCache factoryCache,
            FieldCache fieldCache,
            Objenesis objenesis) {

        var recursionDetector = new RecursionDetectingValueProvider();

        var userPrefabs = new UserPrefabValueProvider(userPrefabCaches);
        var builtinPrefabs = new BuiltinPrefabValueProvider();
        var builtinGenericPrefabs = new BuiltinGenericPrefabValueProvider(recursionDetector);
        var versionSpecificBuiltinPrefabs = new BuiltinVersionSpecificValueProvider(recursionDetector);
        var mockito =
                new MockitoValueProvider(!ExternalLibs.isMockitoAvailable() || modes.contains(Mode.skipMockito()));
        var enumeration = new EnumValueProvider();
        var array = new ArrayValueProvider(recursionDetector);

        var vintageRecursionDetector = new RecursionDetectingValueProvider();
        var vintageChain = new ChainedValueProvider(userPrefabs,
                builtinPrefabs,
                builtinGenericPrefabs,
                versionSpecificBuiltinPrefabs,
                mockito,
                enumeration,
                array);

        vintageRecursionDetector.setValueProvider(vintageChain);

        var vintage = new VintageValueProvider(vintageRecursionDetector, factoryCache, objenesis);

        var mainChain = new ChainedValueProvider(userPrefabs,
                builtinPrefabs,
                builtinGenericPrefabs,
                versionSpecificBuiltinPrefabs,
                mockito,
                enumeration,
                array,
                vintage);
        var caching = new CachingValueProvider(userPrefabCaches, fieldCache, mainChain);

        recursionDetector.setValueProvider(caching);

        return recursionDetector;
    }
}
