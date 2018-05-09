package nl.jqno.equalsverifier.internal.prefabvalues.factories.external;

import nl.jqno.equalsverifier.internal.prefabvalues.FactoryCache;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.ReflectiveCollectionCopyFactory;
import nl.jqno.equalsverifier.internal.prefabvalues.factories.ReflectiveJavaFxPropertyFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.jqno.equalsverifier.internal.reflection.Util.classForName;

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

        addJavaFxCollection(cache, "ObservableList", List.class, "observableList");
        addJavaFxCollection(cache, "ObservableMap", Map.class, "observableMap");
        addJavaFxCollection(cache, "ObservableSet", Set.class, "observableSet");
        addJavaFxProperty(cache, "BooleanProperty", "SimpleBooleanProperty", boolean.class);
        addJavaFxProperty(cache, "DoubleProperty", "SimpleDoubleProperty", double.class);
        addJavaFxProperty(cache, "FloatProperty", "SimpleFloatProperty", float.class);
        addJavaFxProperty(cache, "IntegerProperty", "SimpleIntegerProperty", int.class);
        addJavaFxProperty(cache, "ListProperty", "SimpleListProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableList"));
        addJavaFxProperty(cache, "LongProperty", "SimpleLongProperty", long.class);
        addJavaFxProperty(cache, "MapProperty", "SimpleMapProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableMap"));
        addJavaFxProperty(cache, "ObjectProperty", "SimpleObjectProperty", Object.class);
        addJavaFxProperty(cache, "SetProperty", "SimpleSetProperty", classForName(JAVAFX_COLLECTIONS_PACKAGE + "ObservableSet"));
        addJavaFxProperty(cache, "StringProperty", "SimpleStringProperty", String.class);

        return cache;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addJavaFxCollection(FactoryCache cache, String name, Class<?> copyFrom, String factoryMethod) {
        String className = JAVAFX_COLLECTIONS_PACKAGE + name;
        cache.put(classForName(className),
            new ReflectiveCollectionCopyFactory(className, copyFrom, JAVAFX_COLLECTIONS_PACKAGE + "FXCollections", factoryMethod));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addJavaFxProperty(FactoryCache cache, String declaredType, String actualType, Class<?> propertyType) {
        cache.put(classForName(JAVAFX_PROPERTY_PACKAGE + declaredType),
            new ReflectiveJavaFxPropertyFactory(JAVAFX_PROPERTY_PACKAGE + actualType, propertyType));
    }
}
