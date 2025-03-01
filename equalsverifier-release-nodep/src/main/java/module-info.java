module nl.jqno.equalsverifier {
    exports nl.jqno.equalsverifier;
    exports nl.jqno.equalsverifier.api;

    // Direct dependencies are shaded,
    // so don't need to be declared here.

    // Built-in prefab values
    requires static java.desktop;
    requires static java.naming;
    requires static java.rmi;
    requires static java.sql;
}
