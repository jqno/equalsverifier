module equalsverifier.reflection {
	exports equalsverifier.reflection;
    exports equalsverifier.reflection.annotations;
    requires equalsverifier.gentype;
    requires equalsverifier.prefabservice;
    requires com.github.spotbugs.annotations;
    requires org.objenesis;
    requires net.bytebuddy;
    requires org.objectweb.asm;
    uses equalsverifier.prefabservice.PrefabAbstract;
}
