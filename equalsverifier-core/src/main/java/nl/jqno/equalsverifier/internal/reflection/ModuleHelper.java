package nl.jqno.equalsverifier.internal.reflection;

/**
 * Note: this is a generic implementation for a multi-release jar class.
 * See equalsverifier-11 submodule.
 */
public final class ModuleHelper {

    private ModuleHelper() {}

    public static boolean runsOnModulePath() {
        return false;
    }
}
