/*
 * Copyright 2014 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.testhelpers;

import nl.jqno.equalsverifier.internal.StaticFieldValueStash;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;

public final class PrefabValuesFactory {
    private PrefabValuesFactory() {}

    public static PrefabValues withPrimitiveFactories(StaticFieldValueStash stash) {
        PrefabValues result = new PrefabValues(stash);
        result.addFactory(boolean.class, true, false);
        result.addFactory(byte.class, (byte)1, (byte)2);
        result.addFactory(char.class, 'a', 'b');
        result.addFactory(double.class, 0.5D, 1.0D);
        result.addFactory(float.class, 0.5F, 1.0F);
        result.addFactory(int.class, 1, 2);
        result.addFactory(long.class, 1L, 2L);
        result.addFactory(short.class, (short)1, (short)2);
        return result;
    }
}
