package nl.jqno.equalsverifier.internal.prefabvalues.factories.external;

import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.Tuple;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.AbstractReflectiveGenericFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.PrefabValueFactory;
import nl.jqno.equalsverifier.internal.reflection.ConditionalInstantiator;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.jqno.equalsverifier.internal.prefabvalues.factories.Factories.copy;
import static nl.jqno.equalsverifier.internal.reflection.Util.*;

public final class JavaFxFactory {
    private static final String JAVAFX_COLLECTIONS_PACKAGE = "javafx.collections.";
    private static final String JAVAFX_PROPERTY_PACKAGE = "javafx.beans.property.";

    private JavaFxFactory() {
        // Don't instantiate
    }

    /*
     * As long as there's no easy dependency for JavaFX to rely on in Maven,
     * we can't refer to the actual types, and have to go around it with String
     * representations of the type names.
     */
    public static FactoryCache getFactoryCache() {
        FactoryCache cache = new FactoryCache();

        cache.put(JAVAFX_COLLECTIONS_PACKAGE + "ObservableList", fxCollection(List.class, "observableList"));
        cache.put(JAVAFX_COLLECTIONS_PACKAGE + "ObservableMap", fxCollection(Map.class, "observableMap"));
        cache.put(JAVAFX_COLLECTIONS_PACKAGE + "ObservableSet", fxCollection(Set.class, "observableSet"));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "BooleanProperty", fxProperty("SimpleBooleanProperty", boolean.class));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "DoubleProperty", fxProperty("SimpleDoubleProperty", double.class));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "FloatProperty", fxProperty("SimpleFloatProperty", float.class));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "IntegerProperty", fxProperty("SimpleIntegerProperty", int.class));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "ListProperty",
            fxProperty("SimpleListProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableList")));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "LongProperty", fxProperty("SimpleLongProperty", long.class));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "MapProperty",
            fxProperty("SimpleMapProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableMap")));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "ObjectProperty", fxProperty("SimpleObjectProperty", Object.class));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "SetProperty",
            fxProperty("SimpleSetProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableSet")));
        cache.put(JAVAFX_PROPERTY_PACKAGE + "StringProperty", fxProperty("SimpleStringProperty", String.class));

        return cache;
    }

    private static <T> PrefabValueFactory<T> fxProperty(String typeName, Class<?> parameterRawType) {
        return new PropertyFactory<>(JAVAFX_PROPERTY_PACKAGE + typeName, parameterRawType);
    }

    static final class PropertyFactory<T> extends AbstractReflectiveGenericFactory<T> {
        private final String fullyQualifiedTypeName;
        private final Class<?> parameterRawType;

        PropertyFactory(String fullyQualifiedTypeName, Class<?> parameterRawType) {
            this.fullyQualifiedTypeName = fullyQualifiedTypeName;
            this.parameterRawType = parameterRawType;
        }

        @Override
        public Tuple<T> createValues(TypeTag tag, PrefabValues prefabValues, LinkedHashSet<TypeTag> typeStack) {
            ConditionalInstantiator ci = new ConditionalInstantiator(fullyQualifiedTypeName);
            TypeTag singleParameterTag = copyGenericTypesInto(parameterRawType, tag);

            Object red = ci.instantiate(classes(parameterRawType), objects(prefabValues.giveRed(singleParameterTag)));
            Object black = ci.instantiate(classes(parameterRawType), objects(prefabValues.giveBlack(singleParameterTag)));
            Object redCopy = ci.instantiate(classes(parameterRawType), objects(prefabValues.giveRed(singleParameterTag)));

            return Tuple.of(red, black, redCopy);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T, S> PrefabValueFactory<T> fxCollection(Class<S> source, String copyMethodName) {
        return copy(source, a -> {
            ConditionalInstantiator ci = new ConditionalInstantiator(JAVAFX_COLLECTIONS_PACKAGE + "FXCollections");
            return (T)ci.callFactory(copyMethodName, classes(source), objects(a));
        });
    }
}
