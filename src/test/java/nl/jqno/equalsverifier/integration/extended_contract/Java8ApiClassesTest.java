package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.StringCompilerIntegrationTestBase;
import org.junit.Test;

public class Java8ApiClassesTest extends StringCompilerIntegrationTestBase {
    @Test
    public void successfullyInstantiatesAJavaFxClass_whenJava8IsAvailable() throws Exception {
        if (!isJavaFxAvailable()) {
            return;
        }

        Class<?> java8Class = compile(JAVAFX_CLASS_NAME, JAVAFX_CLASS);
        EqualsVerifier.forClass(java8Class)
                .verify();
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
}
