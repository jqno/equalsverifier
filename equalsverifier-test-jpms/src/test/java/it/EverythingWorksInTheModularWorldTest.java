package it;

import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class EverythingWorksInTheModularWorldTest {

    @Test
    void classCanBeVerified() {
        EqualsVerifier.forClass(ClassPoint.class).verify();
    }

    @Test
    @Disabled("It's impossble to load `equalsverifier-16` in the module world "
            + "because it creates a split path. We can enable this test again "
            + "when EqualsVerifier's baseline becomes Java 17")
    void recordCanBeVerified() {
        EqualsVerifier.forClass(RecordPoint.class).verify();
    }

    @Test
    void classContainingFieldsFromOtherJdkModulesCanBeVerifier() {
        EqualsVerifier.forClass(FieldsFromJdkModulesHaver.class).verify();
    }

    @Test
    void nonFinalClassCanBeVerified() {
        EqualsVerifier.forClass(NonFinal.class).verify();
    }

    static final class ClassPoint {
        private final int x;
        private final int y;

        private ClassPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ClassPoint other && x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    record RecordPoint(int x, int y) {}

    static final class FieldsFromJdkModulesHaver {
        private final java.awt.Color desktopAwtColor;
        private final java.rmi.server.UID rmiUid;
        private final java.sql.Date sqlDate;
        private final javax.naming.Reference namingReference;

        private FieldsFromJdkModulesHaver(
                java.awt.Color c,
                java.rmi.server.UID u,
                java.sql.Date d,
                javax.naming.Reference r) {
            this.desktopAwtColor = c;
            this.rmiUid = u;
            this.sqlDate = d;
            this.namingReference = r;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FieldsFromJdkModulesHaver other
                    && Objects.equals(desktopAwtColor, other.desktopAwtColor)
                    && Objects.equals(rmiUid, other.rmiUid)
                    && Objects.equals(sqlDate, other.sqlDate)
                    && Objects.equals(namingReference, other.namingReference);
        }

        @Override
        public int hashCode() {
            return Objects.hash(desktopAwtColor, rmiUid, sqlDate, namingReference);
        }
    }

    static class NonFinal {
        private final int i;

        public NonFinal(int i) {
            this.i = i;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof NonFinal other && Objects.equals(i, other.i);
        }

        @Override
        public final int hashCode() {
            return Objects.hash(i);
        }
    }
}
