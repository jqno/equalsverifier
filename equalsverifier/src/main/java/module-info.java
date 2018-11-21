module equalsverifier {
	requires equalsverifier.checkers;
	requires equalsverifier.gentype;
	requires equalsverifier.prefabvalues;
	requires equalsverifier.utils;
    requires org.objectweb.asm;
    requires equalsverifier.prefabservice;
    uses equalsverifier.prefabservice.PrefabAbstract;
}
