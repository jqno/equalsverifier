package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.*;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

class EnumTest {

    @Test
    void succeed_whenClassIsAnEnum() {
        EqualsVerifier.forClass(SimpleEnum.class).verify();
    }

    @Test
    void succeed_whenClassHasAnEmptyEnumButIgnoresIt() {
        EqualsVerifier.forClass(EmptyEnumContainer.class).verify();
    }

    @Test
    void succeed_whenClassHasASingletonEnumButIgnoresIt() {
        EqualsVerifier.forClass(SingletonContainer.class).verify();
    }

    @Test
    void succeed_whenClassHasASingletonEnumAndUsesItInEquals() {
        EqualsVerifier.forClass(SingletonUser.class).verify();
    }

    @Test
    void succeed_whenClassHasAnArrayOfSingletonEnumAndUsesItInEquals() {
        EqualsVerifier.forClass(SingletonArrayUser.class).verify();
    }

    @Test
    void succeed_whenSingletonIsUsedWithoutNullCheck_givenNullFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(NullThrowingSingletonUser.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void succeed_whenClassHasSingletonCollection() {
        EqualsVerifier.forClass(SingletonCollectionContainer.class).verify();
    }

    @Test
    void succeed_whenClassHasMapWithSingletonKey() {
        EqualsVerifier.forClass(SingletonKeyMapContainer.class).verify();
    }

    @Test
    void succeed_whenClassHasSingletonOptional() {
        EqualsVerifier.forClass(SingletonOptionalContainer.class).verify();
    }

    enum SimpleEnum {
        ONE, TWO, THREE
    }

    enum Singleton {
        INSTANCE
    }

    enum Empty {}

    static final class EmptyEnumContainer {

        private final int i;

        @SuppressWarnings("unused")
        private final Empty empty;

        public EmptyEnumContainer(int i, Empty empty) {
            this.i = i;
            this.empty = empty;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof EmptyEnumContainer)) {
                return false;
            }
            EmptyEnumContainer other = (EmptyEnumContainer) obj;
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

        public SingletonContainer(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SingletonContainer)) {
                return false;
            }
            SingletonContainer other = (SingletonContainer) obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    static final class SingletonUser {

        private final Singleton singleton;

        public SingletonUser(Singleton singleton) {
            this.singleton = singleton;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SingletonUser other && Objects.equals(singleton, other.singleton);
        }

        @Override
        public int hashCode() {
            return Objects.hash(singleton);
        }
    }

    static final class SingletonArrayUser {

        private final Singleton[] singleton;

        public SingletonArrayUser(Singleton[] singleton) {
            this.singleton = singleton;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SingletonArrayUser other && Arrays.equals(singleton, other.singleton);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(singleton);
        }
    }

    static final class NullThrowingSingletonUser {

        private final Singleton singleton;

        public NullThrowingSingletonUser(Singleton singleton) {
            this.singleton = singleton;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof NullThrowingSingletonUser other && Objects.equals(singleton, other.singleton);
        }

        @Override
        public int hashCode() {
            return singleton.hashCode();
        }
    }

    static final class SingletonCollectionContainer {

        private final Set<Singleton> singletonSet;

        public SingletonCollectionContainer(Set<Singleton> singletonSet) {
            this.singletonSet = singletonSet;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SingletonCollectionContainer other
                    && Objects.equals(singletonSet, other.singletonSet);
        }

        @Override
        public int hashCode() {
            return Objects.hash(singletonSet);
        }
    }

    static final class SingletonKeyMapContainer {

        private final Map<Singleton, Object> singletonKeyMap;

        public SingletonKeyMapContainer(Map<Singleton, Object> singletonKeyMap) {
            this.singletonKeyMap = singletonKeyMap;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SingletonKeyMapContainer other
                    && Objects.equals(singletonKeyMap, other.singletonKeyMap);
        }

        @Override
        public int hashCode() {
            return Objects.hash(singletonKeyMap);
        }
    }

    static final class SingletonOptionalContainer {

        private final Optional<Singleton> singletonOptional;

        public SingletonOptionalContainer(Optional<Singleton> singletonOptional) {
            this.singletonOptional = singletonOptional;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SingletonOptionalContainer other
                    && Objects.equals(singletonOptional, other.singletonOptional);
        }

        @Override
        public int hashCode() {
            return Objects.hash(singletonOptional);
        }
    }
}
