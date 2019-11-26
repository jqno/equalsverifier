package nl.jqno.equalsverifier.internal.prefabvalues.factoryproviders;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.values;

import java.rmi.dgc.VMID;
import java.rmi.server.UID;
import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;

public final class RmiFactoryProvider implements FactoryProvider {

    public FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        VMID redVmid = new VMID();
        cache.put(VMID.class, values(redVmid, new VMID(), redVmid));
        UID redUid = new UID();
        cache.put(UID.class, values(redUid, new UID(), redUid));

        return cache;
    }
}
