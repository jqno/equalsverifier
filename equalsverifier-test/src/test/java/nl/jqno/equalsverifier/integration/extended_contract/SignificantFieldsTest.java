package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.kotlin.KotlinScreen;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.Color;
import nl.jqno.equalsverifier_testhelpers.types.FinalPoint;
import nl.jqno.equalsverifier_testhelpers.types.Point;
import org.junit.jupiter.api.Test;

@SuppressWarnings("CheckReturnValue")
class SignificantFieldsTest {

    @Test
    void fail_whenEqualsUsesAFieldAndHashCodeDoesnt() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ExtraFieldInEquals.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals relies on", "yNotUsed", "but hashCode does not");
    }

    @Test
    void succeed_whenEqualsUsesAFieldAndHashCodeDoesnt_givenStrictHashCodeWarningIsSuppressed() {
        EqualsVerifier.forClass(ExtraFieldInEquals.class).suppress(Warning.STRICT_HASHCODE).verify();
    }

    @Test
    void fail_whenHashCodeIsConstant() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ConstantHashCode.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals relies on", "but hashCode does not");
    }

    @Test
    void succeed_whenHashCodeIsConstant_givenStrictHashCodeWarningIsSuppressed() {
        EqualsVerifier.forClass(ConstantHashCode.class).suppress(Warning.STRICT_HASHCODE).verify();
    }

    @Test
    void fail_whenHashCodeUsesAFieldAndEqualsDoesnt() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(ExtraFieldInHashCode.class).verify())
                .assertMessageContains("Significant fields", "hashCode relies on", "yNotUsed", "but equals does not");
    }

    @Test
    void fail_whenHashCodeUsesAFieldAndEqualsDoesnt_givenStrictHashCodeWarningIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(ExtraFieldInHashCode.class)
                            .suppress(Warning.STRICT_HASHCODE)
                            .verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "hashCode relies on", "yNotUsed", "but equals does not");
    }

    @Test
    void succeed_whenAllFieldsAreUsed_givenAllFieldsShouldBeUsed() {
        EqualsVerifier.forClass(FinalPoint.class).verify();
    }

    @Test
    void succeed_whenAFieldIsUnused_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(OneFieldUnused.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    void fail_whenAFieldIsUnused() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(OneFieldUnused.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use", "colorNotUsed");
    }

    @Test
    void fail_whenANonfinalFieldIsUnused() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(OneNonfinalFieldUnused.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use", "colorNotUsed");
    }

    @Test
    void succeed_whenANonfinalFieldIsUnused_givenAllNonfinalFieldsWarningIsSuppressed() {
        EqualsVerifier
                .forClass(OneNonfinalFieldUnused.class)
                .suppress(Warning.ALL_NONFINAL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    void fail_whenAFieldIsUnused_givenOnlyAllNonfinalFieldsWarningIsSuppressed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(OneFieldUnused.class)
                            .suppress(Warning.ALL_NONFINAL_FIELDS_SHOULD_BE_USED)
                            .verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use", "colorNotUsed");
    }

    @Test
    void succeed_whenATransientFieldIsUnused_givenAllFieldsShouldBeUsed() {
        EqualsVerifier.forClass(OneTransientFieldUnusedColorPoint.class).verify();
    }

    @Test
    void succeed_whenAStaticFieldIsUnused_givenAllFieldsShouldBeUsed() {
        EqualsVerifier.forClass(OneStaticFieldUnusedColorPoint.class).verify();
    }

    @Test
    void succeed_whenAFieldIsUnusedInASubclass_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(OneFieldUnusedExtended.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    void fail_whenAFieldIsUnusedInASubclass() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(OneFieldUnusedExtended.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use", "colorNotUsed");
    }

    @Test
    void succeed_whenNoEqualsMethodPresent_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier
                .forClass(NoFieldsUsed.class)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT, Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    void succeed_whenNoFieldsAreAdded_givenAllFieldsShouldBeUsed() {
        EqualsVerifier.forClass(NoFieldsAdded.class).verify();
    }

    @Test
    void fail_whenNoFieldsAreUsed() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(NoFieldsUsed.class)
                            .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                            .verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use", "color");
    }

    @Test
    void fail_whenNoFieldsAreUsed_givenUsingGetClass() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(NoFieldsUsed.class)
                            .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                            .usingGetClass()
                            .verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use", "color");
    }

    @Test
    void succeed_whenAFieldIsUnused_givenAllFieldsShouldBeUsedExceptThatField() {
        EqualsVerifier.forClass(OneFieldUnused.class).withIgnoredFields("colorNotUsed").verify();
    }

    @Test
    void succeed_whenTwoFieldsAreUnused_givenAllFieldsShouldBeUsedExceptThoseTwo() {
        EqualsVerifier
                .forClass(TwoFieldsUnusedColorPoint.class)
                .withIgnoredFields("colorNotUsed", "colorAlsoNotUsed")
                .verify();
    }

    @Test
    void succeed_whenRepeatingWithIgnoredFields_givenAllFieldsShouldBeUsedExceptThoseTwo() {
        EqualsVerifier
                .forClass(TwoFieldsUnusedColorPoint.class)
                .withIgnoredFields("colorNotUsed")
                .withIgnoredFields("colorAlsoNotUsed")
                .verify();
    }

    @Test
    void succeed_whenRepeatingWithOnlyTheseFields_givenAllFieldsShouldBeUsedExceptThoseTwo() {
        EqualsVerifier.forClass(OneFieldUnused.class).withOnlyTheseFields("x").withOnlyTheseFields("y").verify();
    }

    @Test
    void fail_whenCombiningWithOnlyTheseFieldsAndWithIgnoredFields() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(OneFieldUnused.class)
                            .withOnlyTheseFields("x", "y")
                            .withIgnoredFields("colorNotUsed")
                            .verify())
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "you can call either withOnlyTheseFields or withIgnoredFields, but not both.")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void fail_whenTwoFieldsAreUnUsed_givenAllFieldsShouldBeUsedExceptOneOfThemButNotBoth() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(TwoFieldsUnusedColorPoint.class)
                            .withIgnoredFields("colorNotUsed")
                            .verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use", "colorAlsoNotUsed")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void fail_whenAllFieldsAreUsed_givenAllFieldsShouldBeUsedExceptOneThatActuallyIsUsed() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(FinalPoint.class).withIgnoredFields("x").verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals should not use", "x", "but it does")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void fail_whenOneFieldIsUnused_givenAllFieldsShouldBeUsedExceptTwoFields() {
        ExpectedException
                .when(
                    () -> EqualsVerifier.forClass(OneFieldUnused.class).withIgnoredFields("x", "colorNotUsed").verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals should not use", "x", "but it does")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void anExceptionIsThrown_whenANonExistingFieldIsExcepted() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(FinalPoint.class).withIgnoredFields("thisFieldDoesNotExist"))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "class FinalPoint does not contain field thisFieldDoesNotExist.")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void succeed_whenAFieldIsUnused_givenTheUsedFieldsAreSpecified() {
        EqualsVerifier.forClass(OneFieldUnused.class).withOnlyTheseFields("x", "y").verify();
    }

    @Test
    void fail_whenAllFieldsAreUsed_givenTheUsedFieldsAreSpecifiedButWeMissedOne() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(FinalPoint.class).withOnlyTheseFields("x").verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals should not use", "y", "but it does")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void fail_whenAFieldIsUnused_givenTheUnusedFieldIsAlsoSpecified() {
        ExpectedException
                .when(
                    () -> EqualsVerifier
                            .forClass(OneFieldUnused.class)
                            .withOnlyTheseFields("x", "y", "colorNotUsed")
                            .verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use", "colorNotUsed")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void anExceptionIsThrown_whenANonExistingFieldIsSpecified() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(FinalPoint.class).withOnlyTheseFields("thisFieldDoesNotExist"))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains("Precondition", "class FinalPoint does not contain field thisFieldDoesNotExist.")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void anExceptionIsThrown_whenIgnoredFieldsOverlapWithSpecifiedFields() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(FinalPoint.class).withOnlyTheseFields("x").withIgnoredFields("x"))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "you can call either withOnlyTheseFields or withIgnoredFields, but not both.")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void anExceptionIsThrown_whenSpecifiedFieldsOverlapWithIgnoredFields() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(FinalPoint.class).withIgnoredFields("x").withOnlyTheseFields("x"))
                .assertThrows(IllegalStateException.class)
                .assertMessageContains(
                    "Precondition",
                    "you can call either withOnlyTheseFields or withIgnoredFields, but not both.")
                .assertMessageDoesNotContain(KotlinScreen.GAV);
    }

    @Test
    void succeed_whenAUsedFieldHasUnusedStaticFinalMembers_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier
                .forClass(IndirectStaticFinalContainer.class)
                .suppress(Warning.ALL_FIELDS_SHOULD_BE_USED)
                .verify();
    }

    @Test
    void fail_whenUnusedFieldIsStateless() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UnusedStatelessContainer.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "statelessField", "or it is stateless");
    }

    @Test
    void succeed_whenUnusedFieldIsStateless_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(UnusedStatelessContainer.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    void fail_whenUsedFieldIsStateless() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UsedStatelessContainer.class).verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "statelessField", "or it is stateless");
    }

    @Test
    void succeed_whenUsedFieldIsStateless_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(UsedStatelessContainer.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    void succeed_whenUsedFieldIsStateless_givenStatelessFieldIsIgnored() {
        EqualsVerifier.forClass(UsedStatelessContainer.class).withIgnoredFields("statelessField").verify();
    }

    @Test
    void succeed_whenClassIsStateless_givenAllFieldsWarningIsSuppressed() {
        EqualsVerifier.forClass(Stateless.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    void fail_whenNonNullFieldIsEqualToNullField() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(BugWhenFieldIsNull.class).verify())
                .assertFailure()
                .assertMessageContains(
                    "Significant fields",
                    "hashCode relies on",
                    "s",
                    "equals does not",
                    "These objects are equal, but probably shouldn't be");
    }

    @Test
    void giveCorrectMessage_whenStaticFieldIsNotUsed_givenStaticFieldIsFirstField() {
        // See https://github.com/jqno/equalsverifier/issues/159
        ExpectedException
                .when(
                    () -> EqualsVerifier.forClass(IdentityIntContainer.class).suppress(Warning.IDENTICAL_COPY).verify())
                .assertFailure()
                .assertMessageContains("Significant fields", "equals does not use i");
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
            return obj instanceof ConstantHashCode other && x == other.x && y == other.y;
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

    @SuppressWarnings("InconsistentHashCode")
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

    @SuppressWarnings("StaticAssignmentInConstructor")
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

        public static final X X_FIELD = new X();

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
            return Objects.hash(i, statelessField);
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

    @SuppressWarnings("unused")
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
