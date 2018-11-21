module equalsverifier.prefabvalues {
    requires equalsverifier.reflection;
    exports equalsverifier.prefabvalues;
    requires com.github.spotbugs.annotations;
    requires java.rmi;
    requires java.sql;
    requires equalsverifier.gentype;
    requires equalsverifier.prefabservice;
    requires com.google.common;
    requires java.naming;
    requires org.joda.time;
    requires java.datatransfer;
    requires java.desktop;
    provides equalsverifier.prefabservice.PrefabAbstract with equalsverifier.prefabvalues.PrefabValues;

}
