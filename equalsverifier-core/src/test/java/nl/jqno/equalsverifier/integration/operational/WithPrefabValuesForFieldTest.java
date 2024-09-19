package nl.jqno.equalsverifier.integration.operational;

import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.types.FinalPoint;
import org.junit.jupiter.api.Test;

public class WithPrefabValuesForFieldTest {

    private final FinalPoint pRed = new FinalPoint(3, 42);
    private final FinalPoint pBlue = new FinalPoint(3, 1337);
    private final int iRed = 111;
    private final int iBlue = 142;

    @Test
    public void fail_whenRecordHasSinglePrecondition() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SinglePrecondition.class)
                    .suppress(Warning.NULL_FIELDS)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains("x coordinate must be");
    }

    @Test
    public void succeed_whenRecordHasSinglePrecondition_givenPrefabValuesForField() {
        EqualsVerifier
            .forClass(SinglePrecondition.class)
            .withPrefabValuesForField("point", pRed, pBlue)
            .verify();
    }

    @Test
    public void fail_whenRecordHasDualPrecondition() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(DualPrecondition.class).verify())
            .assertFailure()
            .assertMessageContains("x must be between");
    }

    @Test
    public void fail_whenRecordHasDualPrecondition_givenPrefabValuesForOnlyOneField() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(DualPrecondition.class)
                    .withPrefabValuesForField("x", iRed, iBlue)
                    .verify()
            )
            .assertFailure()
            .assertMessageContains("y must be between");
    }

    @Test
    public void succeed_whenRecordHasDualPrecondition_givenPrefabValueForBothFields() {
        EqualsVerifier
            .forClass(DualPrecondition.class)
            .withPrefabValuesForField("x", iRed, iBlue)
            .withPrefabValuesForField("y", 505, 555)
            .verify();
    }

    @Test
    public void throw_whenFieldDoesNotExistInClass() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SinglePrecondition.class)
                    .withPrefabValuesForField("doesnt_exist", 1, 2)
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains("Precondition:", "does not contain field doesnt_exist");
    }

    @Test
    public void throw_whenFirstPrefabValueIsNull() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SinglePrecondition.class)
                    .withPrefabValuesForField("point", null, pBlue)
            )
            .assertThrows(NullPointerException.class);
    }

    @Test
    public void throw_whenSecondPrefabValueIsNull() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SinglePrecondition.class)
                    .withPrefabValuesForField("point", pRed, null)
            )
            .assertThrows(NullPointerException.class);
    }

    @Test
    public void throw_whenThePrefabValuesAreTheSame() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SinglePrecondition.class)
                    .withPrefabValuesForField("point", pRed, pRed)
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "both prefab values of type FinalPoint are equal"
            );
    }

    @Test
    public void throw_whenThePrefabValuesAreEqual() {
        FinalPoint red1 = new FinalPoint(3, 4);
        FinalPoint red2 = new FinalPoint(3, 4);

        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(SinglePrecondition.class)
                    .withPrefabValuesForField("point", red1, red2)
            )
            .assertThrows(IllegalStateException.class)
            .assertMessageContains(
                "Precondition",
                "both prefab values of type FinalPoint are equal"
            );
    }

    @Test
    public void dontThrow_whenAddingPrefabValuesFromAnotherModuleAndThereforeARedCopyCantBeMade() {
        EqualsVerifier
            .forClass(OtherModuleContainer.class)
            .withPrefabValuesForField("date", LocalDate.of(2024, 9, 18), LocalDate.of(2024, 9, 19))
            .verify();
    }

    static final class SinglePrecondition {

        private final FinalPoint point;

        public SinglePrecondition(FinalPoint point) {
            this.point = point;
        }

        @Override
        public boolean equals(Object obj) {
            if (point == null || point.getX() != 3) {
                throw new IllegalArgumentException("x coordinate must be 3! But was " + point);
            }
            if (!(obj instanceof SinglePrecondition)) {
                return false;
            }
            SinglePrecondition other = (SinglePrecondition) obj;
            return Objects.equals(point, other.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point);
        }
    }

    static final class DualPrecondition {

        private final int x;
        private final int y;

        public DualPrecondition(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (x < 100 || x > 200) {
                throw new IllegalArgumentException("x must be between 100 and 200! But was " + x);
            }
            if (y < 500 || y > 600) {
                throw new IllegalArgumentException("y must be between 500 and 600! But was " + y);
            }
            if (!(obj instanceof DualPrecondition)) {
                return false;
            }
            DualPrecondition other = (DualPrecondition) obj;
            return Objects.equals(x, other.x) && Objects.equals(y, other.y);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static final class OtherModuleContainer {

        private final LocalDate date;

        public OtherModuleContainer(LocalDate date) {
            this.date = date;
        }

        @Override
        public boolean equals(Object obj) {
            if (date == null || date.getMonth() != Month.SEPTEMBER) {
                throw new IllegalArgumentException("date must be in September! But was " + date);
            }
            if (!(obj instanceof OtherModuleContainer)) {
                return false;
            }
            OtherModuleContainer other = (OtherModuleContainer) obj;
            return Objects.equals(date, other.date);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date);
        }
    }
}
