package nl.jqno.equalsverifier;

import java.util.*;
import java.util.function.Predicate;

import nl.jqno.equalsverifier.internal.reflection.PackageScanOptions;

final class ScanOptions {
    private ScanOptions() {}

    enum O implements ScanOption {
        RECURSIVE;
    }

    static class MustExtend implements ScanOption {
        final Class<?> type;

        MustExtend(Class<?> type) {
            Objects.requireNonNull(type);
            this.type = type;
        }
    }

    static class ExceptClasses implements ScanOption {
        final Set<Class<?>> types;

        ExceptClasses(Class<?> type, Class<?>... more) {
            this.types = new HashSet<>();
            this.types.add(type);
            this.types.addAll(Arrays.asList(more));
        }
    }

    static class ExclusionPredicate implements ScanOption {
        final Predicate<Class<?>> exclusionPredicate;

        ExclusionPredicate(Predicate<Class<?>> exclusionPredicate) {
            this.exclusionPredicate = exclusionPredicate;
        }
    }

    public static PackageScanOptions process(ScanOption... options) {
        PackageScanOptions result = new PackageScanOptions();
        for (ScanOption option : options) {
            if (option.equals(O.RECURSIVE)) {
                result.scanRecursively = true;
            }
            if (option instanceof MustExtend) {
                MustExtend me = (MustExtend) option;
                result.mustExtend = me.type;
            }
            if (option instanceof ExceptClasses) {
                ExceptClasses ec = (ExceptClasses) option;
                result.exceptClasses.addAll(ec.types);
            }
            if (option instanceof ExclusionPredicate) {
                ExclusionPredicate ep = (ExclusionPredicate) option;
                result.exclusionPredicate = ep.exclusionPredicate;
            }
        }
        return result;
    }
}
