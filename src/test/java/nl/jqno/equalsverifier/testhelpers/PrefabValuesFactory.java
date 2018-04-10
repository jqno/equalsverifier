package nl.jqno.equalsverifier.testhelpers;

import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;

public final class PrefabValuesFactory {
    private PrefabValuesFactory() {}

    public static PrefabValues withPrimitiveFactories() {
        PrefabValues result = new PrefabValues();
        result.addFactory(boolean.class, true, false, true);
        result.addFactory(byte.class, (byte)1, (byte)2, (byte)1);
        result.addFactory(char.class, 'a', 'b', 'a');
        result.addFactory(double.class, 0.5D, 1.0D, 0.5D);
        result.addFactory(float.class, 0.5F, 1.0F, 0.5F);
        result.addFactory(int.class, 1, 2, 1);
        result.addFactory(long.class, 1L, 2L, 1L);
        result.addFactory(short.class, (short)1, (short)2, (short)1);
        return result;
    }
}
