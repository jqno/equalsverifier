package nl.jqno.equalsverifier.internal.reflection;

public final class ModuleHelper {

    private ModuleHelper() {}

    public static boolean runsOnModulePath() {
        return ModuleLayer.boot().findModule("nl.jqno.equalsverifier").isPresent();
    }
}
