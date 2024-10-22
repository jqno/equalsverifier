package nl.jqno.equalsverifier.internal.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import nl.jqno.equalsverifier.internal.reflection.JavaApiPrefabValues;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProvider;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProviderCreatorTest;
import nl.jqno.equalsverifier.internal.reflection.instantiation.VintageValueProviderTest;
import nl.jqno.equalsverifier.internal.reflection.vintage.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.vintage.FieldModifier;
import nl.jqno.equalsverifier.internal.reflection.vintage.ObjectAccessor;
import nl.jqno.equalsverifier.internal.util.Context;
import nl.jqno.equalsverifier.internal.util.PrefabValuesApi;

@AnalyzeClasses(packages = "nl.jqno.equalsverifier")
public final class ArchitectureTest {

    @ArchTest
    public static final ArchRule ONLY_VINTAGE_INSTANTIATORS_CAN_USE_VINTAGE_REFLECTION = noClasses()
        .that()
        .doNotBelongToAnyOf(
            VintageValueProvider.class,
            Context.class,
            PrefabValuesApi.class,
            JavaApiPrefabValues.class,
            // 👇 Test classes
            VintageValueProviderTest.class,
            VintageValueProviderCreatorTest.class
        )
        .and()
        .resideOutsideOfPackage("nl.jqno.equalsverifier.internal.reflection.vintage..")
        .should()
        .accessClassesThat()
        .resideInAPackage("nl.jqno.equalsverifier.internal.reflection.vintage..");

    @ArchTest
    public static final ArchRule DONT_USE_VINTAGE_REFLECTION_DIRECTLY = noClasses()
        .that()
        .resideInAPackage("nl.jqno.equalsverifier.internal.checkers..")
        .should()
        .accessClassesThat()
        .areAssignableTo(ClassAccessor.class)
        .orShould()
        .accessClassesThat()
        .areAssignableTo(ObjectAccessor.class)
        .orShould()
        .accessClassesThat()
        .areAssignableTo(FieldModifier.class);

    @ArchTest
    public static final ArchRule APACHE_COMMONS = noClasses()
        .that()
        .haveNameNotMatching(".*SuperclassTest.*")
        .should()
        .accessClassesThat()
        .resideInAPackage("org.apache.commons..");

    @ArchTest
    public static final ArchRule AWT = dontAllowImports_outsideFactoryProvidersAndTests_from(
        "java.awt.common.."
    );

    @ArchTest
    public static final ArchRule GUAVA = dontAllowImports_outsideFactoryProvidersAndTests_from(
        "com.google.common.."
    );

    @ArchTest
    public static final ArchRule JAVAFX = dontAllowImports_outsideFactoryProvidersAndTests_from(
        "javafx.."
    );

    @ArchTest
    public static final ArchRule JODA = dontAllowImports_outsideFactoryProvidersAndTests_from(
        "org.joda.."
    );

    private static final String FACTORYPROVIDER_PATTERN =
        "nl.jqno.equalsverifier.internal.reflection.vintage.prefabvalues.factoryproviders..";
    private static final String TEST_CLASS_PATTERN = ".*Test(\\$.*)?$";

    private ArchitectureTest() {
        // Do not instantiate
    }

    private static ArchRule dontAllowImports_outsideFactoryProvidersAndTests_from(
        String packageName
    ) {
        return noClasses()
            .that()
            .resideOutsideOfPackage(FACTORYPROVIDER_PATTERN)
            .and()
            .haveNameNotMatching(TEST_CLASS_PATTERN)
            .should()
            .accessClassesThat()
            .resideInAPackage(packageName);
    }
}
