package nl.jqno.equalsverifier.internal.util;

import java.util.Set;

import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.internal.instantiation.*;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import org.objenesis.Objenesis;

public final class ValueProviderCreator {
    private ValueProviderCreator() {}

    public static ValueProvider create(
            Set<Mode> modes,
            UserPrefabValueProvider userPrefabs,
            FactoryCache factoryCache,
            FieldCache fieldCache,
            Objenesis objenesis) {

        var recursionDetector = new RecursionDetectingValueProvider();

        var builtinPrefabs = new BuiltinPrefabValueProvider();
        var builtinGenericPrefabs = new BuiltinGenericPrefabValueProvider(recursionDetector);
        var versionSpecificBuiltinPrefabs = new BuiltinVersionSpecificValueProvider(recursionDetector);
        var mockito =
                new MockitoValueProvider(!ExternalLibs.isMockitoAvailable() || modes.contains(Mode.skipMockito()));
        var array = new ArrayValueProvider(recursionDetector);

        var vintageRecursionDetector = new RecursionDetectingValueProvider();
        var vintageChain = new ChainedValueProvider(userPrefabs,
                builtinPrefabs,
                builtinGenericPrefabs,
                versionSpecificBuiltinPrefabs,
                mockito,
                array);

        vintageRecursionDetector.setValueProvider(vintageChain);

        var vintage = new VintageValueProvider(vintageRecursionDetector, factoryCache, objenesis);

        var mainChain = new ChainedValueProvider(userPrefabs,
                builtinPrefabs,
                builtinGenericPrefabs,
                versionSpecificBuiltinPrefabs,
                mockito,
                array,
                vintage);
        var caching = new CachingValueProvider(userPrefabs, fieldCache, mainChain);

        recursionDetector.setValueProvider(caching);

        return recursionDetector;
    }
}
