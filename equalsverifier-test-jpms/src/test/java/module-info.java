open module equalsverifier_jpms_test {
    requires nl.jqno.equalsverifier;

    requires org.junit.jupiter.api;
    requires org.assertj.core;

    requires java.desktop;
    requires java.naming;
    requires java.rmi;
    requires java.sql;
}
