package nl.jqno.equalsverifier.internal.instantiation.prefab;

import java.rmi.dgc.VMID;
import java.rmi.server.UID;
import java.util.Optional;

import nl.jqno.equalsverifier.internal.reflection.Tuple;

public class JavaRmiValueSupplier<T> extends ValueSupplier<T> {

    public JavaRmiValueSupplier(Class<T> type) {
        super(type);
    }

    @Override
    public Optional<Tuple<T>> get() {
        if (is(VMID.class)) {
            VMID red = new VMID();
            return val(red, new VMID(), red);
        }
        if (is(UID.class)) {
            UID red = new UID();
            return val(red, new UID(), red);
        }

        return Optional.empty();
    }

}
