package nl.jqno.equalsverifier.internal.testhelpers;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.Tuple;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.instantiation.*;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

@SuppressFBWarnings(
    value = "DM_STRING_CTOR",
    justification = "We want to have a copy of the string"
)
public final class TestValueProviders {

    public static final Tuple<Integer> INTS = Tuple.of(42, 1337, 42);
    public static final Tuple<String> STRINGS = Tuple.of("abc", "xyz", new String("abc"));

    private TestValueProviders() {}

    public static ValueProvider empty() {
        return new ValueProvider() {
            @Override
            public <T> Optional<Tuple<T>> provide(TypeTag tag, String label) {
                return Optional.empty();
            }
        };
    }

    public static ValueProvider simple() {
        PrefabValueProvider prefab = new PrefabValueProvider();
        prefab.register(int.class, null, INTS);
        prefab.register(Integer.class, null, INTS);
        prefab.register(String.class, null, STRINGS);

        return prefab;
    }

    public static VintageValueProvider vintage() {
        return vintage(new PrefabValueProvider(), new ObjenesisStd());
    }

    public static VintageValueProvider vintage(PrefabValueProvider prefabs, Objenesis objenesis) {
        ChainedValueProvider chain = new ChainedValueProvider(prefabs);
        ValueProvider builtin = new BuiltinValueProvider();
        chain.register(builtin);
        return new VintageValueProvider(chain, JavaApiPrefabValues.build(), objenesis);
    }
}
