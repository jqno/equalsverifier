package nl.jqno.equalsverifier.internal.reflection;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class PackageScanOptions {

    public boolean scanRecursively = false;
    public boolean ignoreExternalJars = false;
    public Class<?> mustExtend = null;
    public Set<Class<?>> exceptClasses = new HashSet<>();
    public Predicate<Class<?>> exclusionPredicate = c -> false;
}
