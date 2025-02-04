package nl.jqno.equalsverifier;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.internal.reflection.PackageScanOptions;

final class ScanOptions {
    private ScanOptions() {}

    enum O implements ScanOption {
        RECURSIVE;
    }

    static class MustExtend implements ScanOption {
        final Class<?> type;

        MustExtend(Class<?> type) {
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
        }
        return result;
    }
}
