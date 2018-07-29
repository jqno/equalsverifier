package nl.jqno.equalsverifier.integration.inheritance;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.IntegrationTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import org.junit.Test;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

public class AbstractHierarchyTest extends IntegrationTestBase {
    @Test
    public void succeed_whenEqualsAndHashCodeAreFinal_givenClassIsAbstract() {
        EqualsVerifier.forClass(AbstractFinalMethodsPoint.class)
                .verify();
    }

    @Test
    public void succeed_whenAnImplementingClassWithCorrectlyImplementedEquals_givenClassIsAbstract() {
        EqualsVerifier.forClass(AbstractRedefinablePoint.class)
                .withRedefinedSubclass(FinalRedefinedPoint.class)
                .verify();
    }

    @Test
    public void fail_whenEqualsThrowsNull_givenClassIsAbstract() {
        expectFailureWithCause(NullPointerException.class, "Non-nullity: equals throws NullPointerException");
        EqualsVerifier.forClass(NullThrowingColorContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenEqualsThrowsNull_givenClassIsAbstractAndWarningIsSuppressed() {
        EqualsVerifier.forClass(NullThrowingColorContainer.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }

    @Test
    public void fail_whenAbstractImplementationThrowsNpe() {
        expectFailure("Abstract delegation: equals throws AbstractMethodError when field object is null");
        EqualsVerifier.forClass(NullThrowingLazyObjectContainer.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withIgnoredFields("objectFactory")
                .verify();
    }

    @Test
    public void succeed_whenAbstractImplementationThrowsNpe_givenWarningIsSuppressed() {
        EqualsVerifier.forClass(NullThrowingLazyObjectContainer.class)
                .suppress(Warning.NULL_FIELDS, Warning.NONFINAL_FIELDS)
                .withIgnoredFields("objectFactory")
                .verify();
    }

    abstract static class AbstractFinalMethodsPoint {
        private final int x;
        private final int y;

        public AbstractFinalMethodsPoint(int x, int y) { this.x = x; this.y = y; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof AbstractFinalMethodsPoint)) {
                return false;
            }
            AbstractFinalMethodsPoint p = (AbstractFinalMethodsPoint)obj;
            return x == p.x && y == p.y;
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    abstract static class AbstractRedefinablePoint {
        private final int x;
        private final int y;

        public AbstractRedefinablePoint(int x, int y) { this.x = x; this.y = y; }

        public boolean canEqual(Object obj) {
            return obj instanceof AbstractRedefinablePoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof AbstractRedefinablePoint)) {
                return false;
            }
            AbstractRedefinablePoint p = (AbstractRedefinablePoint)obj;
            return p.canEqual(this) && x == p.x && y == p.y;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    static final class FinalRedefinedPoint extends AbstractRedefinablePoint {
        private final Color color;

        public FinalRedefinedPoint(int x, int y, Color color) { super(x, y); this.color = color; }

        @Override
        public boolean canEqual(Object obj) {
            return obj instanceof FinalRedefinedPoint;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FinalRedefinedPoint)) {
                return false;
            }
            FinalRedefinedPoint p = (FinalRedefinedPoint)obj;
            return p.canEqual(this) && super.equals(p) && color == p.color;
        }

        @Override public int hashCode() { return defaultHashCode(this); }
    }

    abstract static class NullThrowingColorContainer {
        private final Color color;

        public NullThrowingColorContainer(Color color) { this.color = color; }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof NullThrowingColorContainer)) {
                return false;
            }
            return color.equals(((NullThrowingColorContainer)obj).color);
        }

        @Override public final int hashCode() { return defaultHashCode(this); }
    }

    interface Supplier<T> {
        T get();
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
            AbstractLazyObjectContainer other = (AbstractLazyObjectContainer)obj;
            return getObject().equals(other.getObject());
        }

        @Override public int hashCode() { return getObject().hashCode(); }
    }

    static final class NullThrowingLazyObjectContainer extends AbstractLazyObjectContainer {
        private final Supplier<Object> objectFactory;

        protected NullThrowingLazyObjectContainer(Supplier<Object> flourFactory) { this.objectFactory = flourFactory; }

        @Override
        protected Object createObject() {
            return objectFactory.get();
        }
    }
}
