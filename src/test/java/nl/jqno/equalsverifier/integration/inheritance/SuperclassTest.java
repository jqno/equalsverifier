package nl.jqno.equalsverifier.integration.inheritance;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.types.CanEqualPoint;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import nl.jqno.equalsverifier.testhelpers.types.ColorBlindColorPoint;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.AbstractEqualsAndHashCode;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.Empty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;

public class SuperclassTest extends ExpectedExceptionTestBase {
    @Test
    public void
            succeed_whenSubclassRedefinesEqualsButOnlyCallsSuper_givenSuperHasRedefinedAlsoAndAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(ColorBlindColorPoint.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenEqualsIsRedefinedSoItBreaksSymmetry_givenSuperHasRedefinedAlso() {
        expectFailure(
                "Symmetry",
                SymmetryBrokenColorPoint.class.getSimpleName(),
                "does not equal superclass instance",
                Point.class.getSimpleName());
        EqualsVerifier.forClass(SymmetryBrokenColorPoint.class).verify();
    }

    @Test
    public void fail_whenEqualsIsRedefinedSoItBreaksTransitivity_givenSuperHasRedefinedAlso() {
        expectFailure(
                "Transitivity",
                TransitivityBrokenColorPoint.class.getSimpleName(),
                "both equal superclass instance",
                Point.class.getSimpleName(),
                "which implies they equal each other.");
        EqualsVerifier.forClass(TransitivityBrokenColorPoint.class).verify();
    }

    @Test
    public void fail_whenClassHasDifferentHashCodeThanSuper_givenEqualsIsTheSame() {
        expectFailure(
                "Superclass",
                "hashCode for",
                HashCodeBrokenPoint.class.getSimpleName(),
                "should be equal to hashCode for superclass instance",
                Point.class.getSimpleName());
        EqualsVerifier.forClass(HashCodeBrokenPoint.class).verify();
    }

    @Test
    public void succeed_whenSuperDoesNotRedefineEquals() {
        EqualsVerifier.forClass(SubclassOfEmpty.class).verify();
        EqualsVerifier.forClass(SubOfEmptySubOfEmpty.class).verify();
        EqualsVerifier.forClass(SubOfEmptySubOfAbstract.class).verify();
    }

    @Test
    public void fail_whenSuperDoesNotRedefineEquals_givenSuperOfSuperDoesRedefineEquals() {
        expectFailure("Symmetry", BrokenCanEqualColorPointWithEmptySuper.class.getSimpleName());
        EqualsVerifier.forClass(BrokenCanEqualColorPointWithEmptySuper.class).verify();
    }

    @Test
    public void fail_whenWithRedefinedSuperclassIsUsed_givenItIsNotNeeded() {
        expectFailure(
                "Redefined superclass",
                ColorBlindColorPoint.class.getSimpleName(),
                "should not equal superclass instance",
                Point.class.getSimpleName(),
                "but it does");
        EqualsVerifier.forClass(ColorBlindColorPoint.class).withRedefinedSuperclass().verify();
    }

    @Test
    public void
            succeed_whenClassInheritsEqualsAndHashCode_givenSuperclassUsesCommonsLangEqualsBuilder() {
        EqualsVerifier.forClass(ConcreteEqualsInheriter.class).withRedefinedSuperclass().verify();
    }

    @Test
    public void
            succeed_whenVersionedEntityIncorrectlyImplementsCanEqual_givenIncorrectnessIsOnlyObservedFromSubclass() {
        EqualsVerifier.forClass(SymmetryBrokenForNullIdWithIncorrectCanEqualSuper.class).verify();
    }

    @Test
    public void
            fail_whenSuperclassIsVersionedEntityAndIncorrectlyImplementsCanEqual_givenASubclassThatExploitsTheIncorrectness() {
        expectFailure("Symmetry", "does not equal superclass instance");
        EqualsVerifier.forClass(SymmetryBrokenForNullIdWithIncorrectCanEqualSub.class).verify();
    }

    static class SymmetryBrokenColorPoint extends Point {
        private final Color color;

        public SymmetryBrokenColorPoint(int x, int y, Color color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SymmetryBrokenColorPoint)) {
                return false;
            }
            SymmetryBrokenColorPoint p = (SymmetryBrokenColorPoint) obj;
            return super.equals(obj) && p.color == color;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static class TransitivityBrokenColorPoint extends Point {
        private final Color color;

        public TransitivityBrokenColorPoint(int x, int y, Color color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Point)) {
                return false;
            }
            if (!(obj instanceof TransitivityBrokenColorPoint)) {
                return obj.equals(this);
            }
            TransitivityBrokenColorPoint p = (TransitivityBrokenColorPoint) obj;
            return super.equals(obj) && p.color == color;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static class HashCodeBrokenPoint extends Point {
        public HashCodeBrokenPoint(int x, int y) {
            super(x, y);
        }

        @Override
        public int hashCode() {
            return super.hashCode() + 1;
        }
    }

    static final class SubclassOfEmpty extends Empty {
        private final Color color;

        public SubclassOfEmpty(Color color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SubclassOfEmpty)) {
                return false;
            }
            return color == ((SubclassOfEmpty) obj).color;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static class EmptySubOfEmpty extends Empty {}

    static final class SubOfEmptySubOfEmpty extends EmptySubOfEmpty {
        private final Color color;

        public SubOfEmptySubOfEmpty(Color color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SubOfEmptySubOfEmpty)) {
                return false;
            }
            return color == ((SubOfEmptySubOfEmpty) obj).color;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    abstract static class EmptySubOfAbstract extends AbstractEqualsAndHashCode {}

    static final class SubOfEmptySubOfAbstract extends EmptySubOfAbstract {
        private final Color color;

        public SubOfEmptySubOfAbstract(Color color) {
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SubOfEmptySubOfAbstract)) {
                return false;
            }
            return color == ((SubOfEmptySubOfAbstract) obj).color;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static class EmptySubOfCanEqualPoint extends CanEqualPoint {
        public EmptySubOfCanEqualPoint(int x, int y) {
            super(x, y);
        }
    }

    static final class BrokenCanEqualColorPointWithEmptySuper extends EmptySubOfCanEqualPoint {
        private final Color color;

        public BrokenCanEqualColorPointWithEmptySuper(int x, int y, Color color) {
            super(x, y);
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BrokenCanEqualColorPointWithEmptySuper)) {
                return false;
            }
            BrokenCanEqualColorPointWithEmptySuper p = (BrokenCanEqualColorPointWithEmptySuper) obj;
            return super.equals(p) && color == p.color;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    public abstract static class AbstractEqualsDefinerWithEqualsBuilder {
        @Override
        public final boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public final int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @SuppressWarnings("unused")
    public static class ConcreteEqualsInheriter extends AbstractEqualsDefinerWithEqualsBuilder {
        private final int a;
        private final int b;

        public ConcreteEqualsInheriter(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class SymmetryBrokenForNullIdWithIncorrectCanEqualSuper {
        private final Long id;

        public SymmetryBrokenForNullIdWithIncorrectCanEqualSuper(Long id) {
            this.id = id;
        }

        @Override
        public final boolean equals(Object obj) {
            if (!(obj instanceof SymmetryBrokenForNullIdWithIncorrectCanEqualSuper)) {
                return false;
            }
            SymmetryBrokenForNullIdWithIncorrectCanEqualSuper other =
                    (SymmetryBrokenForNullIdWithIncorrectCanEqualSuper) obj;

            if (id != null) {
                return id.equals(other.id);
            } else if (other.id == null) {
                return other.newCanEqual(this);
            }
            return false;
        }

        public boolean newCanEqual(SymmetryBrokenForNullIdWithIncorrectCanEqualSuper o) {
            return true;
        }

        @Override
        public final int hashCode() {
            return Objects.hashCode(id);
        }
    }

    public static class SymmetryBrokenForNullIdWithIncorrectCanEqualSub
            extends SymmetryBrokenForNullIdWithIncorrectCanEqualSuper {
        public final String name;

        public SymmetryBrokenForNullIdWithIncorrectCanEqualSub(Long id, String name) {
            super(id);
            this.name = name;
        }

        @Override
        public boolean newCanEqual(SymmetryBrokenForNullIdWithIncorrectCanEqualSuper obj) {
            if (obj instanceof SymmetryBrokenForNullIdWithIncorrectCanEqualSub) {
                SymmetryBrokenForNullIdWithIncorrectCanEqualSub other =
                        (SymmetryBrokenForNullIdWithIncorrectCanEqualSub) obj;
                return Objects.equals(name, other.name);
            }
            return false;
        }
    }
}
