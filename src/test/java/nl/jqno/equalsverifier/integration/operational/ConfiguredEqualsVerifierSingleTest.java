package nl.jqno.equalsverifier.integration.operational;

import nl.jqno.equalsverifier.ConfiguredEqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.testhelpers.ExpectedException;
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

public class ConfiguredEqualsVerifierSingleTest {

    @Test
    public void
            succeed_whenEqualsUsesGetClassInsteadOfInstanceOf_givenUsingGetClassIsPreConfigured() {
        EqualsVerifier.configure().usingGetClass().forClass(GetClassPoint.class).verify();
    }

    @Test
    public void suppressedWarningsArePassedOn() {
        EqualsVerifier.configure()
                .suppress(Warning.STRICT_INHERITANCE)
                .forClass(PointContainer.class)
                .verify();
    }

    @Test
    public void sanity_fail_whenTypeIsRecursive() {
        ExpectedException.when(() -> EqualsVerifier.forClass(RecursiveType.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    public void succeed_whenTypeIsRecursive_givenPrefabValuesArePreconfigured() {
        EqualsVerifier.configure()
                .withPrefabValues(
                        RecursiveType.class,
                        new RecursiveType(null),
                        new RecursiveType(new RecursiveType(null)))
                .forClass(RecursiveTypeContainer.class)
                .verify();
    }

    @Test
    public void sanity_fail_whenSingleGenericTypeIsRecursive() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(SingleGenericContainerContainer.class)
                                        .verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    public void
            succeed_whenSingleGenericTypeIsRecursive_givenGenericPrefabValuesArePreconfigured() {
        EqualsVerifier.configure()
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .forClass(SingleGenericContainerContainer.class)
                .verify();
    }

    @Test
    public void sanity_fail_whenDoubleGenericTypeIsRecursive() {
        ExpectedException.when(
                        () ->
                                EqualsVerifier.forClass(DoubleGenericContainerContainer.class)
                                        .verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    public void
            succeed_whenDoubleGenericTypeIsRecursive_givenGenericPrefabValuesArePreconfigured() {
        EqualsVerifier.configure()
                .withGenericPrefabValues(DoubleGenericContainer.class, DoubleGenericContainer::new)
                .forClass(DoubleGenericContainerContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenConfigurationIsShared() {
        ConfiguredEqualsVerifier ev =
                EqualsVerifier.configure()
                        .withGenericPrefabValues(
                                SingleGenericContainer.class, SingleGenericContainer::new)
                        .withGenericPrefabValues(
                                DoubleGenericContainer.class, DoubleGenericContainer::new);

        ev.forClass(SingleGenericContainerContainer.class).verify();
        ev.forClass(DoubleGenericContainerContainer.class).verify();
    }

    @Test
    public void individuallySuppressedWarningsAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev =
                EqualsVerifier.configure().suppress(Warning.STRICT_INHERITANCE);

        // should succeed
        ev.forClass(MutablePoint.class).suppress(Warning.NONFINAL_FIELDS).verify();

        // NONFINAL_FIELDS is not added to configuration, so should fail
        ExpectedException.when(() -> ev.forClass(MutablePoint.class).verify())
                .assertFailure()
                .assertMessageContains("Mutability");
    }

    @Test
    public void individuallyAddedPrefabValuesAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure();

        // should succeed
        ev.forClass(SingleGenericContainerContainer.class)
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .verify();

        // PrefabValues are not added to configuration, so should fail
        ExpectedException.when(() -> ev.forClass(SingleGenericContainerContainer.class).verify())
                .assertFailure()
                .assertMessageContains("Recursive datastructure");
    }

    @Test
    public void
            succeed_whenFieldsAreNonfinalAndClassIsNonfinal_givenTwoWarningsAreSuppressedButInDifferentPlaces() {
        EqualsVerifier.configure()
                .suppress(Warning.STRICT_INHERITANCE)
                .forClass(MutablePoint.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        EqualsVerifier.configure()
                .suppress(Warning.NONFINAL_FIELDS)
                .forClass(MutablePoint.class)
                .suppress(Warning.STRICT_INHERITANCE)
                .verify();
    }
}
