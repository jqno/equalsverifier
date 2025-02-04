package nl.jqno.equalsverifier.internal.reflection;

import java.util.HashSet;
import java.util.Set;

public class PackageScanOptions {

    public boolean scanRecursively = false;
    public Class<?> mustExtend = null;
    public Set<Class<?>> exceptClasses = new HashSet<>();
}
