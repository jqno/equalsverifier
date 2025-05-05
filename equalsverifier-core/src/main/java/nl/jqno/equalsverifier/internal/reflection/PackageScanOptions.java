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

    public record MustExtend(Class<?> type) implements ScanOption {
        public MustExtend {
            Objects.requireNonNull(type);
        }
    }

    public record ExceptClasses(Set<Class<?>> types) implements ScanOption {

        public ExceptClasses(Set<Class<?>> types) {
            this.types = Set.copyOf(types);
        }

        public ExceptClasses(Class<?> type, Class<?>... more) {
            this(build(type, more));
        }

        private static Set<Class<?>> build(Class<?> type, Class<?>... more) {
            Set<Class<?>> types = new HashSet<>();
            types.add(type);
            types.addAll(Arrays.asList(more));
            return types;
        }
    }

    public record ExclusionPredicate(Predicate<Class<?>> exclusionPredicate) implements ScanOption {}

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
