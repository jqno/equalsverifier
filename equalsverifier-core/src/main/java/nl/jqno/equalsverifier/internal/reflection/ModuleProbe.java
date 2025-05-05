package nl.jqno.equalsverifier.internal.reflection;

public final class ModuleProbe {

    private ModuleProbe() {}

    public static boolean runsOnModulePath() {
        return ModuleLayer.boot().findModule("nl.jqno.equalsverifier").isPresent();
    }
}
