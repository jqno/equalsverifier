package nl.jqno.equalsverifier.internal.util;

import java.util.Set;

import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.internal.reflection.FieldCache;
import nl.jqno.equalsverifier.internal.valueproviders.*;
import org.objenesis.Objenesis;

public final class ValueProviderBuilder {
    private ValueProviderBuilder() {}

    public static ValueProvider build(
            Set<Mode> modes,
            UserPrefabValueCaches userPrefabCaches,
            FieldCache fieldCache,
            Objenesis objenesis) {

        var recursionDetector = new RecursionDetectingValueProvider();

        var userPrefabs = new UserPrefabValueProvider(userPrefabCaches);
        var userGenericPrefabs = new UserGenericPrefabValueProvider(userPrefabCaches, recursionDetector);
        var builtinPrefabs = new BuiltinPrefabValueProvider();
        var builtinGenericPrefabs = new BuiltinGenericPrefabValueProvider(recursionDetector);
        var versionSpecificBuiltinPrefabs = new BuiltinVersionSpecificValueProvider(recursionDetector);
        var mockito =
                new MockitoValueProvider(!ExternalLibs.isMockitoAvailable() || modes.contains(Mode.skipMockito()));
        var enumeration = new EnumValueProvider();
        var array = new ArrayValueProvider(recursionDetector);
        var abstr = new AbstractValueProvider(recursionDetector);
        var object = new ObjectValueProvider(recursionDetector, objenesis, modes.contains(Mode.finalMeansFinal()));

        var mainChain = new ChainedValueProvider(userPrefabs,
                userGenericPrefabs,
                builtinPrefabs,
                builtinGenericPrefabs,
                versionSpecificBuiltinPrefabs,
                enumeration,
                array,
                mockito,
                abstr,
                object);
        var caching = new CachingValueProvider(userPrefabCaches, fieldCache, mainChain);

        recursionDetector.setValueProvider(caching);

        return recursionDetector;
    }
}
