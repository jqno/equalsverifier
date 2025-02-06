package nl.jqno.equalsverifier.jpms.model;

import java.util.Objects;

public final class FieldsFromJdkModulesHaver {
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
