package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.StringCompilerIntegrationTestBase;
import org.junit.Test;

public class JavaFxClassesTest extends StringCompilerIntegrationTestBase {
    @Test
    public void successfullyInstantiatesAJavaFxClass_whenJavaFxIsAvailable() {
        if (!isJavaFxAvailable()) {
            return;
        }

        Class<?> javafxClass = compile(JAVAFX_CLASS_NAME, JAVAFX_CLASS);
        EqualsVerifier.forClass(javafxClass)
                .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtObservableListFieldsGenericContent() {
        if (!isJavaFxAvailable()) {
            return;
        }

        Class<?> type = compile(JAVAFX_OBSERVABLELIST_CONTAINER_CLASS_NAME, JAVAFX_OBSERVABLELIST_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
            .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtObservableMapFieldsGenericContent() {
        if (!isJavaFxAvailable()) {
            return;
        }

        Class<?> type = compile(JAVAFX_OBSERVABLEMAP_CONTAINER_CLASS_NAME, JAVAFX_OBSERVABLEMAP_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
            .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtObservableSetFieldsGenericContent() {
        if (!isJavaFxAvailable()) {
            return;
        }

        Class<?> type = compile(JAVAFX_OBSERVABLESET_CONTAINER_CLASS_NAME, JAVAFX_OBSERVABLESET_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
            .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtListPropertyFieldsGenericContent() {
        if (!isJavaFxAvailable()) {
            return;
        }

        Class<?> type = compile(JAVAFX_LISTPROPERTY_CONTAINER_CLASS_NAME, JAVAFX_LISTPROPERTY_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
            .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtMapPropertyFieldsGenericContent() {
        if (!isJavaFxAvailable()) {
            return;
        }

        Class<?> type = compile(JAVAFX_MAPPROPERTY_CONTAINER_CLASS_NAME, JAVAFX_MAPPROPERTY_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
            .verify();
    }

    @Test
    public void succeed_whenEqualsLooksAtSetPropertyFieldsGenericContent() {
        if (!isJavaFxAvailable()) {
            return;
        }

        Class<?> type = compile(JAVAFX_SETPROPERTY_CONTAINER_CLASS_NAME, JAVAFX_SETPROPERTY_CONTAINER_CLASS);
        EqualsVerifier.forClass(type)
            .verify();
    }

    public boolean isJavaFxAvailable() {
        return isTypeAvailable("javafx.collections.ObservableList");
    }

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_CLASS_NAME = "JavaFXApiClassesContainer";
    private static final String JAVAFX_CLASS =
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;" +
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\n" +
            "\nimport javafx.collections.ObservableList;" +
            "\nimport javafx.collections.ObservableMap;" +
            "\nimport javafx.collections.ObservableSet;" +
            "\nimport javafx.beans.property.BooleanProperty;" +
            "\nimport javafx.beans.property.DoubleProperty;" +
            "\nimport javafx.beans.property.FloatProperty;" +
            "\nimport javafx.beans.property.IntegerProperty;" +
            "\nimport javafx.beans.property.ListProperty;" +
            "\nimport javafx.beans.property.LongProperty;" +
            "\nimport javafx.beans.property.MapProperty;" +
            "\nimport javafx.beans.property.ObjectProperty;" +
            "\nimport javafx.beans.property.SetProperty;" +
            "\nimport javafx.beans.property.StringProperty;" +
            "\n" +
            "\npublic final class JavaFXApiClassesContainer {" +
            "\n    private final ObservableList observableList;" +
            "\n    private final ObservableMap observableMap;" +
            "\n    private final ObservableSet observableSet;" +
            "\n    private final BooleanProperty booleanProperty;" +
            "\n    private final DoubleProperty doubleProperty;" +
            "\n    private final FloatProperty floatProperty;" +
            "\n    private final IntegerProperty integerProperty;" +
            "\n    private final ListProperty listProperty;" +
            "\n    private final LongProperty longProperty;" +
            "\n    private final MapProperty mapProperty;" +
            "\n    private final ObjectProperty objectProperty;" +
            "\n    private final SetProperty setProperty;" +
            "\n    private final StringProperty stringProperty;" +
            "\n    " +
            "\n    public JavaFXApiClassesContainer(" +
            "\n            ObservableList observableList," +
            "\n            ObservableMap observableMap," +
            "\n            ObservableSet observableSet," +
            "\n            BooleanProperty booleanProperty," +
            "\n            DoubleProperty doubleProperty," +
            "\n            FloatProperty floatProperty," +
            "\n            IntegerProperty integerProperty," +
            "\n            ListProperty listProperty," +
            "\n            LongProperty longProperty," +
            "\n            MapProperty mapProperty," +
            "\n            ObjectProperty objectProperty," +
            "\n            SetProperty setProperty," +
            "\n            StringProperty stringProperty) {" +
            "\n        this.observableList = observableList;" +
            "\n        this.observableMap = observableMap;" +
            "\n        this.observableSet = observableSet;" +
            "\n        this.booleanProperty = booleanProperty;" +
            "\n        this.doubleProperty = doubleProperty;" +
            "\n        this.floatProperty = floatProperty;" +
            "\n        this.integerProperty = integerProperty;" +
            "\n        this.listProperty = listProperty;" +
            "\n        this.longProperty = longProperty;" +
            "\n        this.mapProperty = mapProperty;" +
            "\n        this.objectProperty = objectProperty;" +
            "\n        this.setProperty = setProperty;" +
            "\n        this.stringProperty = stringProperty;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        return defaultEquals(this, obj);" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_OBSERVABLELIST_CONTAINER_CLASS_NAME = "JavaFXObservableListContainer";
    private static final String JAVAFX_OBSERVABLELIST_CONTAINER_CLASS =
        "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport javafx.collections.ObservableList;" +
            "\n" +
            "\npublic final class JavaFXObservableListContainer {" +
            "\n    private final ObservableList<Point> list;" +
            "\n    " +
            "\n    public JavaFXObservableListContainer(ObservableList<Point> list) {" +
            "\n        this.list = list;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXObservableListContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXObservableListContainer other = (JavaFXObservableListContainer)obj;" +
            "\n        if (list == null || other.list == null) {" +
            "\n            return list == other.list;" +
            "\n        }" +
            "\n        if (list.size() != other.list.size()) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        for (int i = 0; i < list.size(); i++) {" +
            "\n            Point x = list.get(i);" +
            "\n            Point y = other.list.get(i);" +
            "\n            if (!x.equals(y)) {" +
            "\n                return false;" +
            "\n            }" +
            "\n        }" +
            "\n        return true;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_OBSERVABLEMAP_CONTAINER_CLASS_NAME = "JavaFXObservableMapContainer";
    private static final String JAVAFX_OBSERVABLEMAP_CONTAINER_CLASS =
        "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport java.util.Map;" +
            "\nimport javafx.collections.ObservableMap;" +
            "\n" +
            "\npublic final class JavaFXObservableMapContainer {" +
            "\n    private final ObservableMap<Point, Point> map;" +
            "\n    " +
            "\n    public JavaFXObservableMapContainer(ObservableMap<Point, Point> map) {" +
            "\n        this.map = map;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXObservableMapContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXObservableMapContainer other = (JavaFXObservableMapContainer)obj;" +
            "\n        if (map == null || other.map == null) {" +
            "\n                return map == other.map;" +
            "\n        }" +
            "\n        if (map.size() != other.map.size()) {" +
            "\n                return false;" +
            "\n        }" +
            "\n        for (Map.Entry<Point, Point> e : map.entrySet()) {" +
            "\n                if (!other.map.containsKey(e.getKey())) {" +
            "\n                        return false;" +
            "\n                }" +
            "\n                if (!other.map.get(e.getKey()).equals(e.getValue())) {" +
            "\n                        return false;" +
            "\n                }" +
            "\n        }" +
            "\n        return true;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_OBSERVABLESET_CONTAINER_CLASS_NAME = "JavaFXObservableSetContainer";
    private static final String JAVAFX_OBSERVABLESET_CONTAINER_CLASS =
        "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport javafx.collections.ObservableSet;" +
            "\n" +
            "\npublic final class JavaFXObservableSetContainer {" +
            "\n    private final ObservableSet<Point> set;" +
            "\n    " +
            "\n    public JavaFXObservableSetContainer(ObservableSet<Point> set) {" +
            "\n        this.set = set;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXObservableSetContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXObservableSetContainer other = (JavaFXObservableSetContainer)obj;" +
            "\n        if (set == null || other.set == null) {" +
            "\n            return set == other.set;" +
            "\n        }" +
            "\n        if (set.size() != other.set.size()) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        for (Point p : set) {" +
            "\n            if (!other.set.contains(p)) {" +
            "\n                return false;" +
            "\n            }" +
            "\n        }" +
            "\n        return true;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_LISTPROPERTY_CONTAINER_CLASS_NAME = "JavaFXListPropertyContainer";
    private static final String JAVAFX_LISTPROPERTY_CONTAINER_CLASS =
        "\nimport javafx.beans.property.ListProperty;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport java.util.Objects;" +
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\n" +
            "\npublic final class JavaFXListPropertyContainer {" +
            "\n    private final ListProperty<Point> p;" +
            "\n    " +
            "\n    public JavaFXListPropertyContainer(ListProperty<Point> p) {" +
            "\n        this.p = p;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXListPropertyContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXListPropertyContainer other = (JavaFXListPropertyContainer)obj;" +
            "\n        if (p != null && other.p != null && p.size() > 0 && other.p.size() > 0) {" +
            "\n            Point x = p.getValue().get(0);" +
            "\n            Point y = other.p.getValue().get(0);" +
            "\n            if (!x.equals(y)) {" +
            "\n                return false;" +
            "\n            }" +
            "\n        }" +
            "\n        return Objects.equals(p, other.p);" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_MAPPROPERTY_CONTAINER_CLASS_NAME = "JavaFXMapPropertyContainer";
    private static final String JAVAFX_MAPPROPERTY_CONTAINER_CLASS =
        "\nimport javafx.beans.property.MapProperty;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport java.util.Objects;" +
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\n" +
            "\npublic final class JavaFXMapPropertyContainer {" +
            "\n    private final MapProperty<Point, Point> p;" +
            "\n    " +
            "\n    public JavaFXMapPropertyContainer(MapProperty<Point, Point> p) {" +
            "\n        this.p = p;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXMapPropertyContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXMapPropertyContainer other = (JavaFXMapPropertyContainer)obj;" +
            "\n        if (p != null && other.p != null && p.size() > 0 && other.p.size() > 0) {" +
            "\n            Point x = p.getValue().keySet().iterator().next(); " +
            "\n            if (!other.p.getValue().containsKey(x)) {" +
            "\n                return false;" +
            "\n            }" +
            "\n        }" +
            "\n        return Objects.equals(p, other.p);" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";

    // CHECKSTYLE: ignore DeclarationOrder for 2 lines.
    private static final String JAVAFX_SETPROPERTY_CONTAINER_CLASS_NAME = "JavaFXSetPropertyContainer";
    private static final String JAVAFX_SETPROPERTY_CONTAINER_CLASS =
        "\nimport javafx.beans.property.SetProperty;" +
            "\nimport nl.jqno.equalsverifier.testhelpers.types.Point;" +
            "\nimport java.util.Objects;" +
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;" +
            "\n" +
            "\npublic final class JavaFXSetPropertyContainer {" +
            "\n    private final SetProperty<Point> p;" +
            "\n    " +
            "\n    public JavaFXSetPropertyContainer(SetProperty<Point> p) {" +
            "\n        this.p = p;" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public boolean equals(Object obj) {" +
            "\n        if (!(obj instanceof JavaFXSetPropertyContainer)) {" +
            "\n            return false;" +
            "\n        }" +
            "\n        JavaFXSetPropertyContainer other = (JavaFXSetPropertyContainer)obj;" +
            "\n        if (p != null && other.p != null && p.size() > 0 && other.p.size() > 0) {" +
            "\n            Point x = p.getValue().iterator().next(); " +
            "\n            if (!other.p.getValue().contains(x)) {" +
            "\n                return false;" +
            "\n            }" +
            "\n        }" +
            "\n        return Objects.equals(p, other.p);" +
            "\n    }" +
            "\n    " +
            "\n    @Override" +
            "\n    public int hashCode() {" +
            "\n        return defaultHashCode(this);" +
            "\n    }" +
            "\n}";
}
