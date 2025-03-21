package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.ConfiguredEqualsVerifier;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import nl.jqno.equalsverifier_testhelpers.types.GetClassPoint;
import nl.jqno.equalsverifier_testhelpers.types.MutablePoint;
import nl.jqno.equalsverifier_testhelpers.types.PointContainerOpenForSubclassAttack;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.RecursiveType;
import nl.jqno.equalsverifier_testhelpers.types.RecursiveTypeHelper.RecursiveTypeContainer;
import nl.jqno.equalsverifier_testhelpers.types.TypeHelper.*;
import org.junit.jupiter.api.Test;

class ConfiguredEqualsVerifierSingleTest {

    @Test
    void succeed_whenEqualsUsesGetClassInsteadOfInstanceOf_givenUsingGetClassIsPreConfigured() {
        EqualsVerifier.configure().usingGetClass().forClass(GetClassPoint.class).verify();
    }

    @Test
    void suppressedWarningsArePassedOn() {
        EqualsVerifier
                .configure()
                .suppress(Warning.STRICT_INHERITANCE)
                .forClass(PointContainerOpenForSubclassAttack.class)
                .verify();
    }

    @Test
    void sanity_fail_whenTypeIsRecursive() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(RecursiveType.class).verify())
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
                .forClass(RecursiveTypeContainer.class)
                .verify();
    }

    @Test
    void sanity_fail_whenSingleGenericTypeIsRecursive() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(SingleGenericContainerContainer.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void succeed_whenSingleGenericTypeIsRecursive_givenGenericPrefabValuesArePreconfigured() {
        EqualsVerifier
                .configure()
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .forClass(SingleGenericContainerContainer.class)
                .verify();
    }

    @Test
    void sanity_fail_whenDoubleGenericTypeIsRecursive() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(DoubleGenericContainerContainer.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void succeed_whenDoubleGenericTypeIsRecursive_givenGenericPrefabValuesArePreconfigured() {
        EqualsVerifier
                .configure()
                .withGenericPrefabValues(DoubleGenericContainer.class, DoubleGenericContainer::new)
                .forClass(DoubleGenericContainerContainer.class)
                .verify();
    }

    @Test
    void succeed_whenConfigurationIsShared() {
        ConfiguredEqualsVerifier ev = EqualsVerifier
                .configure()
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .withGenericPrefabValues(DoubleGenericContainer.class, DoubleGenericContainer::new);

        ev.forClass(SingleGenericContainerContainer.class).verify();
        ev.forClass(DoubleGenericContainerContainer.class).verify();
    }

    @Test
    void individuallySuppressedWarningsAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure().suppress(Warning.STRICT_INHERITANCE);

        // should succeed
        ev.forClass(MutablePoint.class).suppress(Warning.NONFINAL_FIELDS).verify();

        // NONFINAL_FIELDS is not added to configuration, so should fail
        ExpectedException
                .when(() -> ev.forClass(MutablePoint.class).verify())
                .assertFailure()
                .assertMessageContains("Mutability");
    }

    @Test
    void individuallyAddedPrefabValuesAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure();

        // should succeed
        ev
                .forClass(RecursiveTypeContainer.class)
                .withPrefabValues(
                    RecursiveType.class,
                    new RecursiveType(null),
                    new RecursiveType(new RecursiveType(null)))
                .verify();

        // PrefabValues are not added to configuration, so should fail
        ExpectedException
                .when(() -> ev.forClass(RecursiveTypeContainer.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void individuallyAddedGenericPrefabValuesAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure();

        // should succeed
        ev
                .forClass(SingleGenericContainerContainer.class)
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .verify();

        // PrefabValues are not added to configuration, so should fail
        ExpectedException
                .when(() -> ev.forClass(SingleGenericContainerContainer.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    void succeed_whenFieldsAreNonfinalAndClassIsNonfinal_givenTwoWarningsAreSuppressedButInDifferentPlaces() {
        EqualsVerifier
                .configure()
                .suppress(Warning.STRICT_INHERITANCE)
                .forClass(MutablePoint.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        EqualsVerifier
                .configure()
                .suppress(Warning.NONFINAL_FIELDS)
                .forClass(MutablePoint.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }
}
