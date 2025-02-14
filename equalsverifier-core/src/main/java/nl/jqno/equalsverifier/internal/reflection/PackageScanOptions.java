package nl.jqno.equalsverifier.internal.reflection;

import java.util.*;
import java.util.function.Predicate;

import nl.jqno.equalsverifier.ScanOption;

public class PackageScanOptions {

    private boolean scanRecursively = false;
    private boolean ignoreExternalJars = false;
    private Class<?> mustExtend = null;
    private Set<Class<?>> exceptClasses = new HashSet<>();
    private Predicate<Class<?>> exclusionPredicate = c -> false;

    public boolean scanRecursively() {
        return scanRecursively;
    }

    public boolean ignoreExternalJars() {
        return ignoreExternalJars;
    }

    public Class<?> mustExtend() {
        return mustExtend;
    }

    public Set<Class<?>> exceptClasses() {
        return Collections.unmodifiableSet(exceptClasses);
    }

    public Predicate<Class<?>> exclusionPredicate() {
        return exclusionPredicate;
    }

    public enum O implements ScanOption {
        RECURSIVE, IGNORE_EXTERNAL_JARS;
    }

    public static class MustExtend implements ScanOption {
        final Class<?> type;

        public MustExtend(Class<?> type) {
            Objects.requireNonNull(type);
            this.type = type;
        }
    }

    public static class ExceptClasses implements ScanOption {
        final Set<Class<?>> types;

        public ExceptClasses(Class<?> type, Class<?>... more) {
            this.types = new HashSet<>();
            this.types.add(type);
            this.types.addAll(Arrays.asList(more));
        }
    }

    public static class ExclusionPredicate implements ScanOption {
        final Predicate<Class<?>> exclusionPredicate;

        public ExclusionPredicate(Predicate<Class<?>> exclusionPredicate) {
            this.exclusionPredicate = exclusionPredicate;
        }
    }

    public static PackageScanOptions process(ScanOption... options) {
        PackageScanOptions result = new PackageScanOptions();
        for (ScanOption option : options) {
            if (option.equals(O.RECURSIVE)) {
                result.scanRecursively = true;
            }
            if (option.equals(O.IGNORE_EXTERNAL_JARS)) {
                result.ignoreExternalJars = true;
            }
            if (option instanceof MustExtend me) {
                result.mustExtend = me.type;
            }
            if (option instanceof ExceptClasses ec) {
                result.exceptClasses.addAll(ec.types);
            }
            if (option instanceof ExclusionPredicate ep) {
                result.exclusionPredicate = ep.exclusionPredicate;
            }
        }
        return result;
    }
}
