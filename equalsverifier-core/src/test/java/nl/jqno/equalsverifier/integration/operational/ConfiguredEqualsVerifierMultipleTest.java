package nl.jqno.equalsverifier.integration.operational;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import nl.jqno.equalsverifier.ConfiguredEqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import nl.jqno.equalsverifier.testhelpers.packages.correct.A;
import nl.jqno.equalsverifier.testhelpers.types.FinalMethodsPoint;
import nl.jqno.equalsverifier.testhelpers.types.GetClassPoint;
import nl.jqno.equalsverifier.testhelpers.types.MutablePoint;
import nl.jqno.equalsverifier.testhelpers.types.PointContainer;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.RecursiveType;
import nl.jqno.equalsverifier.testhelpers.types.RecursiveTypeHelper.RecursiveTypeContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.DoubleGenericContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.DoubleGenericContainerContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.SingleGenericContainer;
import nl.jqno.equalsverifier.testhelpers.types.TypeHelper.SingleGenericContainerContainer;
import org.junit.jupiter.api.Test;

class ConfiguredEqualsVerifierMultipleTest {

    @Test
    void succeed_whenCallingForPackage_givenAllClassesInPackageAreCorrect() {
        EqualsVerifier.configure().forPackage("nl.jqno.equalsverifier.testhelpers.packages.correct").verify();
    }

    @Test
    void succeed_whenEqualsVerifierUsesGetClassInsteadOfInstanceOf_givenUsingGetClassIsPreConfigured_forIterableOverload() {
        List<EqualsVerifierReport> reports = EqualsVerifier
                .configure()
                .usingGetClass()
                .forClasses(Arrays.asList(GetClassPoint.class, FinalMethodsPoint.class))
                .report();

        assertThat(reports.get(0).isSuccessful()).isTrue();
        assertThat(reports.get(1).isSuccessful()).isFalse();
    }

    @Test
    void succeed_whenEqualsUsesGetClassInsteadOfInstanceOf_givenUsingGetClassIsPreConfigured_forVarargOverload() {
        List<EqualsVerifierReport> reports = EqualsVerifier
                .configure()
                .usingGetClass()
                .forClasses(GetClassPoint.class, FinalMethodsPoint.class)
                .report();

        assertThat(reports.get(0).isSuccessful()).isTrue();
        assertThat(reports.get(1).isSuccessful()).isFalse();
    }

    @Test
    void suppressedWarningsArePassedOn() {
        EqualsVerifier
                .configure()
                .suppress(Warning.STRICT_INHERITANCE)
                .forClasses(PointContainer.class, A.class)
                .verify();
    }

    @Test
    void sanity_fail_whenTypeIsRecursive() {
        ExpectedException
                .when(() -> EqualsVerifier.forClasses(RecursiveType.class, A.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void succeed_whenTypeIsRecursive_givenPrefabValuesArePreconfigured() {
        EqualsVerifier
                .configure()
                .withPrefabValues(
                    RecursiveType.class,
                    new RecursiveType(null),
                    new RecursiveType(new RecursiveType(null)))
                .forClasses(RecursiveTypeContainer.class, A.class)
                .verify();
    }

    @Test
    void sanity_fail_whenSingleGenericTypeIsRecursive() {
        ExpectedException
                .when(() -> EqualsVerifier.forClasses(SingleGenericContainerContainer.class, A.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void succeed_whenSingleGenericTypeIsRecursive_givenGenericPrefabValuesArePreconfigured() {
        EqualsVerifier
                .configure()
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .forClasses(SingleGenericContainerContainer.class, A.class)
                .verify();
    }

    @Test
    void sanity_fail_whenDoubleGenericTypeIsRecursive() {
        ExpectedException
                .when(() -> EqualsVerifier.forClasses(DoubleGenericContainerContainer.class, A.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void succeed_whenDoubleGenericTypeIsRecursive_givenGenericPrefabValuesArePreconfigured() {
        EqualsVerifier
                .configure()
                .withGenericPrefabValues(DoubleGenericContainer.class, DoubleGenericContainer::new)
                .forClasses(DoubleGenericContainerContainer.class, A.class)
                .verify();
    }

    @Test
    void succeed_whenConfigurationIsShared() {
        ConfiguredEqualsVerifier ev = EqualsVerifier
                .configure()
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .withGenericPrefabValues(DoubleGenericContainer.class, DoubleGenericContainer::new);

        ev.forClasses(SingleGenericContainerContainer.class, A.class).verify();
        ev.forClasses(DoubleGenericContainerContainer.class, A.class).verify();
    }

    @Test
    void individuallySuppressedWarningsAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure().suppress(Warning.STRICT_INHERITANCE);

        // should succeed
        ev.forClasses(MutablePoint.class, A.class).suppress(Warning.NONFINAL_FIELDS).verify();

        // NONFINAL_FIELDS is not added to configuration, so should fail
        ExpectedException
                .when(() -> ev.forClasses(MutablePoint.class, A.class).verify())
                .assertFailure()
                .assertMessageContains("Mutability");
    }

    @Test
    void individuallyAddedPrefabValuesAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure();

        // should succeed
        ev
                .forClasses(RecursiveTypeContainer.class, A.class)
                .withPrefabValues(
                    RecursiveType.class,
                    new RecursiveType(null),
                    new RecursiveType(new RecursiveType(null)))
                .verify();

        // PrefabValues are not added to configuration, so should fail
        ExpectedException
                .when(() -> ev.forClasses(RecursiveTypeContainer.class, A.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void individuallyAddedGenericPrefabValuesAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure();

        // should succeed
        ev
                .forClasses(SingleGenericContainerContainer.class, A.class)
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .verify();

        // PrefabValues are not added to configuration, so should fail
        ExpectedException
                .when(() -> ev.forClasses(SingleGenericContainerContainer.class, A.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void succeed_whenFieldsAreNonfinalAndClassIsNonfinal_givenTwoWarningsAreSuppressedButInDifferentPlaces() {
        EqualsVerifier
                .configure()
                .suppress(Warning.STRICT_INHERITANCE)
                .forClasses(MutablePoint.class, A.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        EqualsVerifier
                .configure()
                .suppress(Warning.NONFINAL_FIELDS)
                .forClasses(MutablePoint.class, A.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }
}
