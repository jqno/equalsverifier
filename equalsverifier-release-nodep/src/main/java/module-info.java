module nl.jqno.equalsverifier {
    exports nl.jqno.equalsverifier;
    exports nl.jqno.equalsverifier.api;

    // Built-in prefab values
    requires static com.google.common;
    requires static java.desktop;
    requires static java.naming;
    requires static java.rmi;
    requires static java.sql;
    requires static javafx.base;
    requires static org.joda.time;
}
