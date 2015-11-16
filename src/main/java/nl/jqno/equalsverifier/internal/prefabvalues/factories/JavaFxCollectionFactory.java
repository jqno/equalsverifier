/*
 * Copyright 2015 Jan Ouwens
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
package nl.jqno.equalsverifier.internal.prefabvalues.factories;

import nl.jqno.equalsverifier.internal.ConditionalInstantiator;
import nl.jqno.equalsverifier.internal.prefabvalues.AbstractPrefabValueFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;

import java.util.LinkedHashSet;

import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.classes;
import static nl.jqno.equalsverifier.internal.ConditionalInstantiator.objects;

public final class JavaFxCollectionFactory<T> extends AbstractPrefabValueFactory<T> {
    private static final String PACKAGE_NAME = "javafx.collections.";
    private static final String FACTORY_TYPE = PACKAGE_NAME + "FXCollections";

    private final String typeName;
    private final Class<?> parameterRawType;
    private final String factoryType;
    private final String factoryMethod;

    public JavaFxCollectionFactory(String typeName, Class<?> singleParameterRawType, String factoryMethod) {
        this(PACKAGE_NAME + typeName, singleParameterRawType, FACTORY_TYPE, factoryMethod);
    }

    public JavaFxCollectionFactory(String typeName, Class<?> parameterRawType, String factoryType, String factoryMethod) {
        this.typeName = typeName;
        this.parameterRawType = parameterRawType;
        this.factoryType = factoryType;
        this.factoryMethod = factoryMethod;
    }

    @Override
    public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
        ConditionalInstantiator ci = new ConditionalInstantiator(typeName);
        TypeTag singleParameterTag = copyGenericTypesInto(parameterRawType, tag);

        Object red = ci.callFactory(factoryType, factoryMethod,
                classes(parameterRawType), objects(prefabValues.giveRed(singleParameterTag)));
        Object black = ci.callFactory(factoryType, factoryMethod,
                classes(parameterRawType), objects(prefabValues.giveBlack(singleParameterTag)));

        @SuppressWarnings("unchecked")
        Tuple<T> result = new Tuple<>((T)red, (T)black);

        return result;
    }
}
