package nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factoryproviders;

import static nl.jqno.equalsverifier.internal.instantiation.vintage.prefabvalues.factories.Factories.values;

import java.rmi.dgc.VMID;
import java.rmi.server.UID;

import nl.jqno.equalsverifier.internal.instantiation.vintage.FactoryCache;

public final class RmiFactoryProvider implements FactoryProvider {

    @Override
    public FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        VMID redVmid = new VMID();
        cache.put(VMID.class, values(redVmid, new VMID(), redVmid));
        UID redUid = new UID();
        cache.put(UID.class, values(redUid, new UID(), redUid));

        return cache;
    }
}
