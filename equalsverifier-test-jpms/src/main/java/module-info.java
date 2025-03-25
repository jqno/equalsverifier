module equalsverifier.jpms {
    exports nl.jqno.equalsverifier.jpms.model;

    opens nl.jqno.equalsverifier.jpms.model;

    requires java.desktop;
    requires java.naming;
    requires java.rmi;
    requires java.sql;
}
