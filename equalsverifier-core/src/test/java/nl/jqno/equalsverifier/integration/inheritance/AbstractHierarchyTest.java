package nl.jqno.equalsverifier.integration.inheritance;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Color;
import org.junit.jupiter.api.Test;

class AbstractHierarchyTest {

    @Test
    void succeed_whenEqualsAndHashCodeAreFinal_givenClassIsAbstract() {
        EqualsVerifier.forClass(AbstractFinalMethodsPoint.class).verify();
    }

    @Test
    void succeed_whenAnImplementingClassWithCorrectlyImplementedEquals_givenClassIsAbstract() {
        EqualsVerifier
                .forClass(AbstractRedefinablePoint.class)
                .withRedefinedSubclass(FinalRedefinedPoint.class)
                .verify();
    }

    @Test
    void fail_whenEqualsThrowsNull_givenClassIsAbstract() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(NullThrowingColorContainer.class).verify())
                .assertFailure()
                .assertCause(NullPointerException.class)
                .assertMessageContains("Non-nullity: equals throws NullPointerException");
    }

    @Test
    void succeed_whenEqualsThrowsNull_givenClassIsAbstractAndWarningIsSuppressed() {
        EqualsVerifier.forClass(NullThrowingColorContainer.class).suppress(Warning.NULL_FIELDS).verify();
    }

    @Test
    void fail_whenAbstractImplementationThrowsNpe() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(NullThrowingLazyObjectContainer.class)
                            .suppress(Warning.NONFINAL_FIELDS)
                            .withIgnoredFields("objectFactory")
                            .verify())
                .assertFailure()
                .assertMessageContains(
                    "Abstract delegation: equals throws AbstractMethodError when field object is null");
    }

    @Test
    void succeed_whenAbstractImplementationThrowsNpe_givenWarningIsSuppressed() {
        EqualsVerifier
                .forClass(NullThrowingLazyObjectContainer.class)
                .suppress(Warning.NULL_FIELDS, Warning.NONFINAL_FIELDS)
                .withIgnoredFields("objectFactory")
                .verify();
    }

    abstract static class AbstractFinalMethodsPoint {

        private final int x;
        private final int y;

        public AbstractFinalMethodsPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof AbstractFinalMethodsPoint)) {
                return false;
            }
            AbstractFinalMethodsPoint p = (AbstractFinalMethodsPoint) obj;
            return x == p.x && y == p.y;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(x, y);
        }
    }

    abstract static class AbstractRedefinablePoint {

        private final int x;
        private final int y;

        public AbstractRedefinablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean canEqual(Object obj) {
            return obj instanceof AbstractRedefinablePoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractRedefinablePoint)) {
                return false;
            }
            AbstractRedefinablePoint p = (AbstractRedefinablePoint) obj;
            return p.canEqual(this) && x == p.x && y == p.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static final class FinalRedefinedPoint extends AbstractRedefinablePoint {

        private final Color color;

        public FinalRedefinedPoint(int x, int y, Color color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof FinalRedefinedPoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FinalRedefinedPoint)) {
                return false;
            }
            FinalRedefinedPoint p = (FinalRedefinedPoint) obj;
            return p.canEqual(this) && super.equals(p) && color == p.color;
        }

        @Override
        public int hashCode() {
            return 53 * super.hashCode() + Objects.hash(color);
        }
    }

    abstract static class NullThrowingColorContainer {

        private final Color color;

        public NullThrowingColorContainer(Color color) {
            this.color = color;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NullThrowingColorContainer)) {
                return false;
            }
            return color.equals(((NullThrowingColorContainer) obj).color);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(color);
        }
    }

    abstract static class AbstractLazyObjectContainer {

        private Object object;

        private Object getObject() {
            if (object == null) {
                object = createObject();
            }
            return object;
        }

        protected abstract Object createObject();

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof AbstractLazyObjectContainer)) {
                return false;
            }
            AbstractLazyObjectContainer other = (AbstractLazyObjectContainer) obj;
            return getObject().equals(other.getObject());
        }

        @Override
        public int hashCode() {
            return getObject().hashCode();
        }
    }

    interface SupplierThatDoesntHaveAPrefab<T> {
        T get();
    }

    static final class NullThrowingLazyObjectContainer extends AbstractLazyObjectContainer {

        private final SupplierThatDoesntHaveAPrefab<Object> objectFactory;

        protected NullThrowingLazyObjectContainer(SupplierThatDoesntHaveAPrefab<Object> flourFactory) {
            this.objectFactory = flourFactory;
        }

        @Override
        protected Object createObject() {
            return objectFactory.get();
        }
    }
}
