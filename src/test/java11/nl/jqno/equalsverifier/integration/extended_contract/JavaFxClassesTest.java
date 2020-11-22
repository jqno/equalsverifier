package nl.jqno.equalsverifier.integration.extended_contract;

import static nl.jqno.equalsverifier.testhelpers.Util.*;

import java.util.Map;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.types.Point;

import org.junit.jupiter.api.Test;

import javafx.beans.property.*;
import javafx.collections.*;

// CHECKSTYLE OFF: ParameterNumber

public class JavaFxClassesTest {
    @Test
    public void successfullyInstantiatesAJavaFxClass_whenJavaFxIsAvailable() {
        EqualsVerifier.forClass(JavaFxApiClassesContainer.class).verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtObservableListFieldsGenericContent() {
        EqualsVerifier.forClass(JavaFxObservableListContainer.class).verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtObservableMapFieldsGenericContent() {
        EqualsVerifier.forClass(JavaFxObservableMapContainer.class).verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtObservableSetFieldsGenericContent() {
        EqualsVerifier.forClass(JavaFxObservableSetContainer.class).verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtListPropertyFieldsGenericContent() {
        EqualsVerifier.forClass(JavaFxListPropertyContainer.class).verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtMapPropertyFieldsGenericContent() {
        EqualsVerifier.forClass(JavaFxMapPropertyContainer.class).verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtSetPropertyFieldsGenericContent() {
        EqualsVerifier.forClass(JavaFxSetPropertyContainer.class).verify();
    }

    @SuppressWarnings({ "rawtypes", "unused" })
    static final class JavaFxApiClassesContainer {
        private final ObservableList observableList;
        private final ObservableMap observableMap;
        private final ObservableSet observableSet;
        private final BooleanProperty booleanProperty;
        private final DoubleProperty doubleProperty;
        private final FloatProperty floatProperty;
        private final IntegerProperty integerProperty;
        private final ListProperty listProperty;
        private final LongProperty longProperty;
        private final MapProperty mapProperty;
        private final ObjectProperty objectProperty;
        private final SetProperty setProperty;
        private final StringProperty stringProperty;

        public JavaFxApiClassesContainer(ObservableList observableList, ObservableMap observableMap,
                ObservableSet observableSet, BooleanProperty booleanProperty, DoubleProperty doubleProperty,
                FloatProperty floatProperty, IntegerProperty integerProperty, ListProperty listProperty,
                LongProperty longProperty, MapProperty mapProperty, ObjectProperty objectProperty,
                SetProperty setProperty, StringProperty stringProperty) {
            this.observableList = observableList;
            this.observableMap = observableMap;
            this.observableSet = observableSet;
            this.booleanProperty = booleanProperty;
            this.doubleProperty = doubleProperty;
            this.floatProperty = floatProperty;
            this.integerProperty = integerProperty;
            this.listProperty = listProperty;
            this.longProperty = longProperty;
            this.mapProperty = mapProperty;
            this.objectProperty = objectProperty;
            this.setProperty = setProperty;
            this.stringProperty = stringProperty;
        }

        @Override
        public boolean equals(Object obj) {
            return defaultEquals(this, obj);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class JavaFxObservableListContainer {
        private final ObservableList<Point> list;

        public JavaFxObservableListContainer(ObservableList<Point> list) {
            this.list = list;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JavaFxObservableListContainer)) {
                return false;
            }
            JavaFxObservableListContainer other = (JavaFxObservableListContainer) obj;
            if (list == null || other.list == null) {
                return list == other.list;
            }
            if (list.size() != other.list.size()) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                Point x = list.get(i);
                Point y = other.list.get(i);
                if (!x.equals(y)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class JavaFxObservableMapContainer {
        private final ObservableMap<Point, Point> map;

        public JavaFxObservableMapContainer(ObservableMap<Point, Point> map) {
            this.map = map;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JavaFxObservableMapContainer)) {
                return false;
            }
            JavaFxObservableMapContainer other = (JavaFxObservableMapContainer) obj;
            if (map == null || other.map == null) {
                return map == other.map;
            }
            if (map.size() != other.map.size()) {
                return false;
            }
            for (Map.Entry<Point, Point> e : map.entrySet()) {
                if (!other.map.containsKey(e.getKey())) {
                    return false;
                }
                if (!other.map.get(e.getKey()).equals(e.getValue())) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class JavaFxObservableSetContainer {
        private final ObservableSet<Point> set;

        public JavaFxObservableSetContainer(ObservableSet<Point> set) {
            this.set = set;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JavaFxObservableSetContainer)) {
                return false;
            }
            JavaFxObservableSetContainer other = (JavaFxObservableSetContainer) obj;
            if (set == null || other.set == null) {
                return set == other.set;
            }
            if (set.size() != other.set.size()) {
                return false;
            }
            for (Point p : set) {
                if (!other.set.contains(p)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class JavaFxListPropertyContainer {
        private final ListProperty<Point> p;

        public JavaFxListPropertyContainer(ListProperty<Point> p) {
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JavaFxListPropertyContainer)) {
                return false;
            }
            JavaFxListPropertyContainer other = (JavaFxListPropertyContainer) obj;
            if (p != null && other.p != null && p.size() > 0 && other.p.size() > 0) {
                Point x = p.getValue().get(0);
                Point y = other.p.getValue().get(0);
                if (!x.equals(y)) {
                    return false;
                }
            }
            return Objects.equals(p, other.p);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class JavaFxMapPropertyContainer {
        private final MapProperty<Point, Point> p;

        public JavaFxMapPropertyContainer(MapProperty<Point, Point> p) {
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JavaFxMapPropertyContainer)) {
                return false;
            }
            JavaFxMapPropertyContainer other = (JavaFxMapPropertyContainer) obj;
            if (p != null && other.p != null && p.size() > 0 && other.p.size() > 0) {
                Point x = p.getValue().keySet().iterator().next();
                if (!other.p.getValue().containsKey(x)) {
                    return false;
                }
            }
            return Objects.equals(p, other.p);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }

    static final class JavaFxSetPropertyContainer {
        private final SetProperty<Point> p;

        public JavaFxSetPropertyContainer(SetProperty<Point> p) {
            this.p = p;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof JavaFxSetPropertyContainer)) {
                return false;
            }
            JavaFxSetPropertyContainer other = (JavaFxSetPropertyContainer) obj;
            if (p != null && other.p != null && p.size() > 0 && other.p.size() > 0) {
                Point x = p.getValue().iterator().next();
                if (!other.p.getValue().contains(x)) {
                    return false;
                }
            }
            return Objects.equals(p, other.p);
        }

        @Override
        public int hashCode() {
            return defaultHashCode(this);
        }
    }
}
