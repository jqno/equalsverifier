package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;
import static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedExceptionTestBase;
import nl.jqno.equalsverifier.testhelpers.types.Color;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Test;

public class SignificantFieldsTest extends ExpectedExceptionTestBase {
    @Test
    public void fail_whenEqualsUsesAFieldAndHashCodeDoesnt() {
        expectFailure(
                "Significant fields", "equals relies on", "yNotUsed", "but hashCode does not");
        EqualsVerifier.forClass(ExtraFieldInEquals.class).verify();
    }

    @Test
    public void
            succeed_whenEqualsUsesAFieldAndHashCodeDoesnt_givenStrictHashCodeWarningIsSuppressed() {
        EqualsVerifier.forClass(ExtraFieldInEquals.class)
                .suppress(Warning.STRICT_HASHCODE)
                .verify();
    }

    @Test
    public void fail_whenHashCodeIsConstant() {
        expectFailure("Significant fields", "equals relies on", "but hashCode does not");
        EqualsVerifier.forClass(ConstantHashCode.class).verify();
    }

    @Test
    public void succeed_whenHashCodeIsConstant_givenStrictHashCodeWarningIsSuppressed() {
        EqualsVerifier.forClass(ConstantHashCode.class).suppress(Warning.STRICT_HASHCODE).verify();
    }

    @Test
    public void fail_whenHashCodeUsesAFieldAndEqualsDoesnt() {
        expectFailure(
                "Significant fields", "hashCode relies on", "yNotUsed", "but equals does not");
        EqualsVerifier.forClass(ExtraFieldInHashCode.class).verify();
    }

    @Test
    public void
            fail_whenHashCodeUsesAFieldAndEqualsDoesnt_givenStrictHashCodeWarningIsSuppressed() {
        expectFailure(
                "Significant fields", "hashCode relies on", "yNotUsed", "but equals does not");
        EqualsVerifier.forClass(ExtraFieldInHashCode.class)
                .suppress(Warning.STRICT_HASHCODE)
                .verify();
    }

    @Test
    public void succeed_whenAllFieldsAreUsed_givenAllFieldsShouldBeUsed() {
        EqualsVerifier.forClass(FinalPoint.class).verify();
    }

    @Test
    public void succeed_whenAFieldIsUnused_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(OneFieldUnused.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenAFieldIsUnused() {
        expectFailure("Significant fields", "equals does not use", "colorNotUsed");
        EqualsVerifier.forClass(OneFieldUnused.class).verify();
    }

    @Test
    public void fail_whenANonfinalFieldIsUnused() {
        expectFailure("Significant fields", "equals does not use", "colorNotUsed");
        EqualsVerifier.forClass(OneNonfinalFieldUnused.class).verify();
    }

    @Test
    public void succeed_whenANonfinalFieldIsUnused_givenAllNonfinalFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(OneNonfinalFieldUnused.class)
                .suppress(Warning.ALL_NONFINAL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenAFieldIsUnused_givenOnlyAllNonfinalFieldsWarningIsSuppressed() {
        expectFailure("Significant fields", "equals does not use", "colorNotUsed");
        EqualsVerifier.forClass(OneFieldUnused.class)
                .suppress(Warning.ALL_NONFINAL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void succeed_whenATransientFieldIsUnused_givenAllFieldsShouldBeUsed() {
        EqualsVerifier.forClass(OneTransientFieldUnusedColorPoint.class).verify();
    }

    @Test
    public void succeed_whenAStaticFieldIsUnused_givenAllFieldsShouldBeUsed() {
        EqualsVerifier.forClass(OneStaticFieldUnusedColorPoint.class).verify();
    }

    @Test
    public void succeed_whenAFieldIsUnusedInASubclass_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(OneFieldUnusedExtended.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenAFieldIsUnusedInASubclass() {
        expectFailure("Significant fields", "equals does not use", "colorNotUsed");
        EqualsVerifier.forClass(OneFieldUnusedExtended.class).verify();
    }

    @Test
    public void succeed_whenNoEqualsMethodPresent_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(NoFieldsUsed.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void succeed_whenNoFieldsAreAdded_givenAllFieldsShouldBeUsed() {
        EqualsVerifier.forClass(NoFieldsAdded.class).verify();
    }

    @Test
    public void fail_whenNoFieldsAreUsed() {
        expectFailure("Significant fields", "equals does not use", "color");
        EqualsVerifier.forClass(NoFieldsUsed.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .verify();
    }

    @Test
    public void fail_whenNoFieldsAreUsed_givenUsingGetClass() {
        expectFailure("Significant fields", "equals does not use", "color");
        EqualsVerifier.forClass(NoFieldsUsed.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .usingGetClass()
                .verify();
    }

    @Test
    public void succeed_whenAFieldIsUnused_givenAllFieldsShouldBeUsedExceptThatField() {
        EqualsVerifier.forClass(OneFieldUnused.class).withIgnoredFields("colorNotUsed").verify();
    }

    @Test
    public void succeed_whenTwoFieldsAreUnused_givenAllFieldsShouldBeUsedExceptThoseTwo() {
        EqualsVerifier.forClass(TwoFieldsUnusedColorPoint.class)
                .withIgnoredFields("colorNotUsed", "colorAlsoNotUsed")
                .verify();
    }

    @Test
    public void succeed_whenRepeatingWithIgnoredFields_givenAllFieldsShouldBeUsedExceptThoseTwo() {
        EqualsVerifier.forClass(TwoFieldsUnusedColorPoint.class)
                .withIgnoredFields("colorNotUsed")
                .withIgnoredFields("colorAlsoNotUsed")
                .verify();
    }

    @Test
    public void
            succeed_whenRepeatingWithOnlyTheseFields_givenAllFieldsShouldBeUsedExceptThoseTwo() {
        EqualsVerifier.forClass(OneFieldUnused.class)
                .withOnlyTheseFields("x")
                .withOnlyTheseFields("y")
                .verify();
    }

    @Test
    public void fail_whenCombiningWithOnlyTheseFieldsAndWithIgnoredFields() {
        expectException(
                IllegalStateException.class,
                "Precondition",
                "you can call either withOnlyTheseFields or withIgnoredFields, but not both.");
        EqualsVerifier.forClass(OneFieldUnused.class)
                .withOnlyTheseFields("x", "y")
                .withIgnoredFields("colorNotUsed")
                .verify();
    }

    @Test
    public void fail_whenTwoFieldsAreUnUsed_givenAllFieldsShouldBeUsedExceptOneOfThemButNotBoth() {
        expectFailure("Significant fields", "equals does not use", "colorAlsoNotUsed");
        EqualsVerifier.forClass(TwoFieldsUnusedColorPoint.class)
                .withIgnoredFields("colorNotUsed")
                .verify();
    }

    @Test
    public void fail_whenAllFieldsAreUsed_givenAllFieldsShouldBeUsedExceptOneThatActuallyIsUsed() {
        expectFailure("Significant fields", "equals should not use", "x", "but it does");
        EqualsVerifier.forClass(FinalPoint.class).withIgnoredFields("x").verify();
    }

    @Test
    public void fail_whenOneFieldIsUnused_givenAllFieldsShouldBeUsedExceptTwoFields() {
        expectFailure("Significant fields", "equals should not use", "x", "but it does");
        EqualsVerifier.forClass(OneFieldUnused.class)
                .withIgnoredFields("x", "colorNotUsed")
                .verify();
    }

    @Test
    public void anExceptionIsThrown_whenANonExistingFieldIsExcepted() {
        expectException(
                IllegalStateException.class,
                "Precondition",
                "class FinalPoint does not contain field thisFieldDoesNotExist.");
        EqualsVerifier.forClass(FinalPoint.class).withIgnoredFields("thisFieldDoesNotExist");
    }

    @Test
    public void succeed_whenAFieldIsUnused_givenTheUsedFieldsAreSpecified() {
        EqualsVerifier.forClass(OneFieldUnused.class).withOnlyTheseFields("x", "y").verify();
    }

    @Test
    public void fail_whenAllFieldsAreUsed_givenTheUsedFieldsAreSpecifiedButWeMissedOne() {
        expectFailure("Significant fields", "equals should not use", "y", "but it does");
        EqualsVerifier.forClass(FinalPoint.class).withOnlyTheseFields("x").verify();
    }

    @Test
    public void fail_whenAFieldIsUnused_givenTheUnusedFieldIsAlsoSpecified() {
        expectFailure("Significant fields", "equals does not use", "colorNotUsed");
        EqualsVerifier.forClass(OneFieldUnused.class)
                .withOnlyTheseFields("x", "y", "colorNotUsed")
                .verify();
    }

    @Test
    public void anExceptionIsThrown_whenANonExistingFieldIsSpecified() {
        expectException(
                IllegalStateException.class,
                "Precondition",
                "class FinalPoint does not contain field thisFieldDoesNotExist.");
        EqualsVerifier.forClass(FinalPoint.class).withOnlyTheseFields("thisFieldDoesNotExist");
    }

    @Test
    public void anExceptionIsThrown_whenIgnoredFieldsOverlapWithSpecifiedFields() {
        expectException(
                IllegalStateException.class,
                "Precondition",
                "you can call either withOnlyTheseFields or withIgnoredFields, but not both.");
        EqualsVerifier.forClass(FinalPoint.class).withOnlyTheseFields("x").withIgnoredFields("x");
    }

    @Test
    public void anExceptionIsThrown_whenSpecifiedFieldsOverlapWithIgnoredFields() {
        expectException(
                IllegalStateException.class,
                "Precondition",
                "you can call either withOnlyTheseFields or withIgnoredFields, but not both.");
        EqualsVerifier.forClass(FinalPoint.class).withIgnoredFields("x").withOnlyTheseFields("x");
    }

    @Test
    public void
            succeed_whenAUsedFieldHasUnusedStaticFinalMembers_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(IndirectStaticFinalContainer.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenUnusedFieldIsStateless() {
        expectFailure("Significant fields", "statelessField", "or it is stateless");
        EqualsVerifier.forClass(UnusedStatelessContainer.class).verify();
    }

    @Test
    public void succeed_whenUnusedFieldIsStateless_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(UnusedStatelessContainer.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenUsedFieldIsStateless() {
        expectFailure("Significant fields", "statelessField", "or it is stateless");
        EqualsVerifier.forClass(UsedStatelessContainer.class).verify();
    }

    @Test
    public void succeed_whenUsedFieldIsStateless_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(UsedStatelessContainer.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void succeed_whenUsedFieldIsStateless_givenStatelessFieldIsIgnored() {
        EqualsVerifier.forClass(UsedStatelessContainer.class)
                .withIgnoredFields("statelessField")
                .verify();
    }

    @Test
    public void succeed_whenClassIsStateless_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(Stateless.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    public void fail_whenNonNullFieldIsEqualToNullField() {
        expectFailure(
                "Significant fields",
                "hashCode relies on",
                "s",
                "equals does not",
                "These objects are equal, but probably shouldn't be");
        EqualsVerifier.forClass(BugWhenFieldIsNull.class).verify();
    }

    @Test
    public void giveCorrectMessage_whenStaticFieldIsNotUsed_givenStaticFieldIsFirstField() {
        // See https://github.com/jqno/equalsverifier/issues/159
        expectFailure("Significant fields", "equals does not use i");
        EqualsVerifier.forClass(IdentityIntContainer.class)
                .suppress(Warning.IDENTICAL_COPY)
                .verify();
    }

    static final class ConstantHashCode {
        private final int x;
        private final int y;

        public ConstantHashCode(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    static final class ExtraFieldInEquals {
        private final int x;
        private final int yNotUsed;

        public ExtraFieldInEquals(int x, int y) {
            this.x = x;
            this.yNotUsed = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ExtraFieldInEquals)) {
                return false;
            }
            ExtraFieldInEquals p = (ExtraFieldInEquals) obj;
            return p.x == x && p.yNotUsed == yNotUsed;
        }

        @Override
        public int hashCode() {
            return x;
        }
    }

    static final class ExtraFieldInHashCode {
        private final int x;
        private final int yNotUsed;

        public ExtraFieldInHashCode(int x, int y) {
            this.x = x;
            this.yNotUsed = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ExtraFieldInHashCode)) {
                return false;
            }
            ExtraFieldInHashCode p = (ExtraFieldInHashCode) obj;
            return p.x == x;
        }

        @Override
        public int hashCode() {
            return x + (31 * yNotUsed);
        }
    }

    static final class OneFieldUnused {
        private final int x;
        private final int y;

        @SuppressWarnings("unused")
        private final Color colorNotUsed;

        public OneFieldUnused(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.colorNotUsed = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OneFieldUnused)) {
                return false;
            }
            OneFieldUnused other = (OneFieldUnused) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return x + (31 * y);
        }
    }

    static final class OneNonfinalFieldUnused {
        private final int x;
        private final int y;

        @SuppressWarnings("unused")
        private Color colorNotUsed;

        public OneNonfinalFieldUnused(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.colorNotUsed = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OneNonfinalFieldUnused)) {
                return false;
            }
            OneNonfinalFieldUnused other = (OneNonfinalFieldUnused) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return x + (31 * y);
        }
    }

    static final class OneTransientFieldUnusedColorPoint {
        private final int x;
        private final int y;

        @SuppressWarnings("unused")
        private final transient Color color;

        public OneTransientFieldUnusedColorPoint(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OneTransientFieldUnusedColorPoint)) {
                return false;
            }
            OneTransientFieldUnusedColorPoint other = (OneTransientFieldUnusedColorPoint) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return x + (31 * y);
        }
    }

    static final class OneStaticFieldUnusedColorPoint {
        @SuppressWarnings("unused")
        private static Color color;

        private final int x;
        private final int y;

        public OneStaticFieldUnusedColorPoint(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            OneStaticFieldUnusedColorPoint.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OneStaticFieldUnusedColorPoint)) {
                return false;
            }
            OneStaticFieldUnusedColorPoint other = (OneStaticFieldUnusedColorPoint) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return x + (31 * y);
        }
    }

    static final class OneFieldUnusedExtended extends Point {
        @SuppressWarnings("unused")
        private final Color colorNotUsed;

        public OneFieldUnusedExtended(int x, int y, Color color) {
            super(x, y);
            this.colorNotUsed = color;
        }
    }

    static final class NoFieldsUsed {
        @SuppressWarnings("unused")
        private final Color color;

        public NoFieldsUsed(Color color) {
            this.color = color;
        }
    }

    static final class NoFieldsAdded extends Point {
        public NoFieldsAdded(int x, int y) {
            super(x, y);
        }
    }

    static final class TwoFieldsUnusedColorPoint {
        private final int x;
        private final int y;

        @SuppressWarnings("unused")
        private final Color colorNotUsed;

        @SuppressWarnings("unused")
        private final Color colorAlsoNotUsed;

        public TwoFieldsUnusedColorPoint(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.colorNotUsed = color;
            this.colorAlsoNotUsed = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TwoFieldsUnusedColorPoint)) {
                return false;
            }
            TwoFieldsUnusedColorPoint other = (TwoFieldsUnusedColorPoint) obj;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return x + (31 * y);
        }
    }

    static final class X {
        public static final X X = new X();

        @Override
        public boolean equals(Object obj) {
            return obj instanceof X;
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    static final class IndirectStaticFinalContainer {
        private final X x;

        public IndirectStaticFinalContainer(X x) {
            this.x = x;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IndirectStaticFinalContainer)) {
                return false;
            }
            return Objects.equals(x, ((IndirectStaticFinalContainer) obj).x);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(x);
        }
    }

    static final class Stateless {
        @SuppressWarnings("unused")
        private final int irrelevant;

        public Stateless(int i) {
            this.irrelevant = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Stateless;
        }

        @Override
        public int hashCode() {
            return 42;
        }
    }

    static final class UnusedStatelessContainer {
        private final int i;

        @SuppressWarnings("unused")
        private final Stateless statelessField;

        public UnusedStatelessContainer(int i, Stateless statelessField) {
            this.i = i;
            this.statelessField = statelessField;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UnusedStatelessContainer)) {
                return false;
            }
            UnusedStatelessContainer other = (UnusedStatelessContainer) obj;
            return i == other.i;
        }

        @Override
        public int hashCode() {
            return i;
        }
    }

    static final class UsedStatelessContainer {
        private final int i;
        private final Stateless statelessField;

        public UsedStatelessContainer(int i, Stateless statelessField) {
            this.i = i;
            this.statelessField = statelessField;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof UsedStatelessContainer)) {
                return false;
            }
            UsedStatelessContainer other = (UsedStatelessContainer) obj;
            return i == other.i && Objects.equals(statelessField, other.statelessField);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    public static final class BugWhenFieldIsNull {
        private final String s;

        public BugWhenFieldIsNull(String s) {
            this.s = s;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof BugWhenFieldIsNull)) {
                return false;
            }
            BugWhenFieldIsNull other = (BugWhenFieldIsNull) obj;
            return !(this.s != null && other.s != null) || this.s.equals(other.s);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(s);
        }
    }

    public static final class IdentityIntContainer {
        public static final int WHATEVER = 1;
        private final int i;

        private IdentityIntContainer(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            return i;
        }
    }
}
