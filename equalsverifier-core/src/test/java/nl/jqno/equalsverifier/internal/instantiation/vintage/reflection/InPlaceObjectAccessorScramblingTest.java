package nl.jqno.equalsverifier.internal.instantiation.vintage.reflection;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.factories.Factories.values;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InaccessibleObjectException;
import java.text.AttributedString;
import java.util.LinkedHashSet;

import nl.jqno.equalsverifier.internal.exceptions.ModuleException;
import nl.jqno.equalsverifier.internal.instantiation.BuiltinPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.ChainedValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.UserPrefabValueProvider;
import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;
import nl.jqno.equalsverifier.internal.instantiation.vintage.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.TypeTag;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import nl.jqno.equalsverifier_testhelpers.types.Point3D;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.StaticFinalContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

class InPlaceObjectAccessorScramblingTest {

    private static final LinkedHashSet<TypeTag> EMPTY_TYPE_STACK = new LinkedHashSet<>();
    private Objenesis objenesis;
    private VintageValueProvider valueProviderTest;

    @BeforeEach
    void setup() {
        var prefabs = new UserPrefabValueProvider();
        var chain = new ChainedValueProvider(prefabs, new BuiltinPrefabValueProvider());
        FactoryCache factoryCache = new FactoryCache();
        factoryCache.put(Point.class, values(new Point(1, 2), new Point(2, 3), new Point(1, 2)));
        objenesis = new ObjenesisStd();
        valueProviderTest = new VintageValueProvider(chain, factoryCache, objenesis);
    }

    @Test
    void scrambleReturnsThis() {
        Point original = new Point(2, 3);
        Point copy = copy(original);

        ObjectAccessor<Object> actual = doScramble(copy);
        assertThat(actual.get()).isSameAs(copy);
    }

    @Test
    void scramble() {
        Point original = new Point(2, 3);
        Point copy = copy(original);

        assertThat(copy).isEqualTo(original);
        doScramble(copy);
        assertThat(copy).isNotEqualTo(original);
    }

    @Test
    void deepScramble() {
        Point3D modified = new Point3D(2, 3, 4);
        Point3D reference = copy(modified);

        doScramble(modified);

        assertThat(reference).isNotEqualTo(modified);
        modified.z = 4;
        assertThat(reference).isNotEqualTo(modified);
    }

    @SuppressWarnings("static-access")
    @Test
    void scrambleStaticFinal() {
        StaticFinalContainer foo = new StaticFinalContainer();
        int originalInt = StaticFinalContainer.CONST;
        Object originalObject = StaticFinalContainer.OBJECT;

        doScramble(foo);

        assertThat(originalInt).isEqualTo(foo.CONST);
        assertThat(originalObject).isEqualTo(foo.OBJECT);
    }

    @Test
    void scrambleString() {
        StringContainer foo = new StringContainer();
        String before = foo.s;
        doScramble(foo);
        assertThat(foo.s).isNotEqualTo(before);
    }

    @Test
    void privateFinalStringCannotBeScrambled() {
        FinalAssignedStringContainer foo = new FinalAssignedStringContainer();
        String before = foo.s;

        doScramble(foo);

        assertThat(foo.s).isEqualTo(before);
    }

    @Test
    void scramblePrivateFinalPoint() {
        FinalAssignedPointContainer foo = new FinalAssignedPointContainer();
        Point before = foo.p;

        assertThat(foo.p).isEqualTo(before);
        doScramble(foo);
        assertThat(foo.p).isNotEqualTo(before);
    }

    @Test
    void scrambleNestedGenerics() {
        GenericContainerContainerContainer foo = new GenericContainerContainerContainer();

        assertThat(foo.strings.ts.t).isNull();
        assertThat(foo.points.ts.t).isNull();

        doScramble(foo);

        assertThat(foo.strings.ts.t).isNotNull();
        assertThat(foo.strings.ts.t.getClass()).isEqualTo(String.class);
        assertThat(foo.points.ts.t).isNotNull();
        assertThat(foo.points.ts.t.getClass()).isEqualTo(Point.class);
    }

    @Test
    void scrambleSutInaccessible() {
        AttributedString as = new AttributedString("x");

        ExpectedException
                .when(() -> doScramble(as))
                .assertThrows(InaccessibleObjectException.class)
                .assertMessageContains("accessible: module", "does not \"opens");
    }

    @Test
    void scrambleFieldInaccessible() {
        InaccessibleContainer ic = new InaccessibleContainer(new AttributedString("x"));

        ExpectedException.when(() -> doScramble(ic)).assertThrows(ModuleException.class);
    }

    @SuppressWarnings("unchecked")
    private <T> InPlaceObjectAccessor<T> create(T object) {
        return new InPlaceObjectAccessor<T>(object, (Class<T>) object.getClass());
    }

    private <T> T copy(T object) {
        return create(object).copy(objenesis);
    }

    private ObjectAccessor<Object> doScramble(Object object) {
        return create(object).scramble(valueProviderTest, TypeTag.NULL, EMPTY_TYPE_STACK);
    }

    static final class StringContainer {

        private String s = "x";
    }

    static final class FinalAssignedStringContainer {

        private final String s = "x";
    }

    static final class FinalAssignedPointContainer {

        private final Point p = new Point(2, 3);
    }

    static final class GenericContainerContainerContainer {

        private final GenericContainerContainer<String> strings =
                new GenericContainerContainer<>(new GenericContainer<>(null));
        private final GenericContainerContainer<Point> points =
                new GenericContainerContainer<>(new GenericContainer<>(null));
    }

    static final class GenericContainerContainer<T> {

        private GenericContainer<T> ts;

        public GenericContainerContainer(GenericContainer<T> ts) {
            this.ts = ts;
        }
    }

    static final class GenericContainer<T> {
        private T t;

        public GenericContainer(T t) {
            this.t = t;
        }
    }

    @SuppressWarnings("unused")
    static final class InaccessibleContainer {

        private AttributedString as;

        public InaccessibleContainer(AttributedString as) {
            this.as = as;
        }
    }
}
