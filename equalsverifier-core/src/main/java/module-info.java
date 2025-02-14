// When making changes to this file, make sure they are
// reflected in equalsverifier-release-nodep/../module-info.java!
module nl.jqno.equalsverifier {
    exports nl.jqno.equalsverifier;
    exports nl.jqno.equalsverifier.api;

    // Direct dependencies
    requires net.bytebuddy;
    requires org.objenesis;

    // Built-in prefab values
    requires static java.desktop;
    requires static java.naming;
    requires static java.rmi;
    requires static java.sql;
}
