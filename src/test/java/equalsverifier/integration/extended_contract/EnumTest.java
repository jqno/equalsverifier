package equalsverifier.integration.extended_contract;

import equalsverifier.EqualsVerifier;
import equalsverifier.utils.Warning;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static equalsverifier.testhelpers.Util.defaultEquals;
import static equalsverifier.testhelpers.Util.defaultHashCode;

public class EnumTest {
    @Test
    public void succeed_whenClassIsAnEnum() {
        EqualsVerifier.forClass(Enum.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasAnEmptyEnumButIgnoresIt() {
        EqualsVerifier.forClass(EmptyEnumContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasASingletonEnumButIgnoresIt() {
        EqualsVerifier.forClass(SingletonContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasASingletonEnumAndUsesItInEquals() {
        EqualsVerifier.forClass(SingletonUser.class)
                .verify();
    }

    @Test
    public void succeed_whenSingletonIsUsedWithoutNullCheck_givenNullFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(NullThrowingSingletonUser.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void succeed_whenClassHasSingletonCollection() {
        EqualsVerifier.forClass(SingletonCollectionContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasMapWithSingletonKey() {
        EqualsVerifier.forClass(SingletonKeyMapContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenClassHasSingletonOptional() {
        EqualsVerifier.forClass(SingletonOptionalContainer.class)
                .verify();
    }

    enum Enum {
        ONE, TWO, THREE
    }

    enum Singleton { INSTANCE }

    enum Empty {}

    static final class EmptyEnumContainer {
        private final int i;

        @SuppressWarnings("unused")
        private final Empty empty;

        public EmptyEnumContainer(int i, Empty empty) { this.i = i; this.empty = empty; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EmptyEnumContainer)) {
                return false;
            }
            EmptyEnumContainer other = (EmptyEnumContainer)obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    static final class SingletonContainer {
        private final int i;

        @SuppressWarnings("unused")
        private final Singleton singleton = Singleton.INSTANCE;

        public SingletonContainer(int i) { this.i = i; }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SingletonContainer)) {
                return false;
            }
            SingletonContainer other = (SingletonContainer)obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    @SuppressWarnings("unused") // because of the use of defaultEquals and defaultHashCode
    static final class SingletonUser {
        private final Singleton singleton;

        public SingletonUser(Singleton singleton) {
            this.singleton = singleton;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class NullThrowingSingletonUser {
        private final Singleton singleton;

        public NullThrowingSingletonUser(Singleton singleton) {
            this.singleton = singleton;
        }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }

        @Override
        public int hashCode() {
            return singleton.hashCode();
        }
    }

    static final class SingletonCollectionContainer {
        private final Set<Singleton> singletonSet;

        public SingletonCollectionContainer(Set<Singleton> singletonSet) { this.singletonSet = singletonSet; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class SingletonKeyMapContainer {
        private final Map<Singleton, Object> singletonKeyMap;

        public SingletonKeyMapContainer(Map<Singleton, Object> singletonKeyMap) { this.singletonKeyMap = singletonKeyMap; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class SingletonOptionalContainer {
        private final Optional<Singleton> singletonOptional;

        public SingletonOptionalContainer(Optional<Singleton> singletonOptional) { this.singletonOptional = singletonOptional; }

        @Override public boolean equals(Object obj) { return defaultEquals(this, obj); }
        @Override public int hashCode() { return defaultHashCode(this); }
    }
}
