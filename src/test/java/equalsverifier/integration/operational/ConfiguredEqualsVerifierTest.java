package equalsverifier.integration.operational;

import equalsverifier.ConfiguredEqualsVerifier;
import equalsverifier.EqualsVerifier;
import equalsverifier.utils.Warning;
import equalsverifier.testhelpers.ExpectedExceptionTestBase;
import equalsverifier.testhelpers.types.GetClassPoint;
import equalsverifier.testhelpers.types.MutablePoint;
import equalsverifier.testhelpers.types.PointContainer;
import equalsverifier.testhelpers.types.RecursiveTypeHelper.RecursiveType;
import equalsverifier.testhelpers.types.RecursiveTypeHelper.RecursiveTypeContainer;
import equalsverifier.testhelpers.types.TypeHelper.DoubleGenericContainer;
import equalsverifier.testhelpers.types.TypeHelper.DoubleGenericContainerContainer;
import equalsverifier.testhelpers.types.TypeHelper.SingleGenericContainer;
import equalsverifier.testhelpers.types.TypeHelper.SingleGenericContainerContainer;
import org.junit.Test;

public class ConfiguredEqualsVerifierTest extends ExpectedExceptionTestBase {

    @Test
    public void succeed_whenEqualsUsesGetClassInsteadOfInstanceOf_givenUsingGetClassIsPreConfigured() {
        EqualsVerifier.configure()
                .usingGetClass()
                .forClass(GetClassPoint.class)
                .verify();
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
        expectFailure("Recursive datastructure");
        EqualsVerifier.forClass(RecursiveType.class)
                .verify();
    }

    @Test
    public void succeed_whenTypeIsRecursive_givenPrefabValuesArePreconfigured() {
        EqualsVerifier.configure()
                .withPrefabValues(RecursiveType.class, new RecursiveType(null), new RecursiveType(new RecursiveType(null)))
                .forClass(RecursiveTypeContainer.class)
                .verify();
    }

    @Test
    public void sanity_fail_whenSingleGenericTypeIsRecursive() {
        expectFailure("Recursive datastructure");
        EqualsVerifier.forClass(SingleGenericContainerContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenSingleGenericTypeIsRecursive_givenGenericPrefabValuesArePreconfigured() {
        EqualsVerifier.configure()
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .forClass(SingleGenericContainerContainer.class)
                .verify();
    }

    @Test
    public void sanity_fail_whenDoubleGenericTypeIsRecursive() {
        expectFailure("Recursive datastructure");
        EqualsVerifier.forClass(DoubleGenericContainerContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenDoubleGenericTypeIsRecursive_givenGenericPrefabValuesArePreconfigured() {
        EqualsVerifier.configure()
                .withGenericPrefabValues(DoubleGenericContainer.class, DoubleGenericContainer::new)
                .forClass(DoubleGenericContainerContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenConfigurationIsShared() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure()
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .withGenericPrefabValues(DoubleGenericContainer.class, DoubleGenericContainer::new);

        ev.forClass(SingleGenericContainerContainer.class)
                .verify();
        ev.forClass(DoubleGenericContainerContainer.class)
                .verify();
    }

    @Test
    public void individuallySuppressedWarningsAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure()
                .suppress(Warning.STRICT_INHERITANCE);

        // should succeed
        ev.forClass(MutablePoint.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();

        // NONFINAL_FIELDS is not added to configuration, so should fail
        expectFailure("Mutability");
        ev.forClass(MutablePoint.class)
                .verify();
    }

    @Test
    public void individuallyAddedPrefabValuesAreNotAddedGlobally() {
        ConfiguredEqualsVerifier ev = EqualsVerifier.configure();

        // should succeed
        ev.forClass(SingleGenericContainerContainer.class)
                .withGenericPrefabValues(SingleGenericContainer.class, SingleGenericContainer::new)
                .verify();

        // PrefabValues are not added to configuration, so should fail
        expectFailure("Recursive datastructure");
        ev.forClass(SingleGenericContainerContainer.class)
                .verify();
    }

    @Test
    public void succeed_whenFieldsAreNonfinalAndClassIsNonfinal_givenTwoWarningsAreSuppressedButInDifferentPlaces() {
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
