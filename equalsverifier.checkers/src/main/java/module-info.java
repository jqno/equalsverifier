module equalsverifier.checkers {
	exports equalsverifier.checkers;
	requires equalsverifier.utils;
	requires equalsverifier.reflection;
	requires equalsverifier.prefabvalues;
	requires com.github.spotbugs.annotations;
	requires equalsverifier.gentype;
	requires equalsverifier.prefabservice;
	uses equalsverifier.prefabservice.PrefabAbstract;
}
