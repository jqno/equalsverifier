package nl.jqno.equalsverifier;

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
        }
        return result;
    }
}
