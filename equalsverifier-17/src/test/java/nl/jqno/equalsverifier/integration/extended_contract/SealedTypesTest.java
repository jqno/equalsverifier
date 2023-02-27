package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.internal.testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

public class SealedTypesTest {

    @Test
    public void succeed_whenSealedParentHasAFinalChild_givenItHasCorrectEqualsAndHashCode() {
        EqualsVerifier
            .forClass(SealedParentWithFinalChild.class)
            .withRedefinedSubclass(FinalSealedChild.class)
            .verify();
    }

    @Test
    public void fail_whenSealedParentHasAFinalChild_givenEqualsVerifierIsCalledIncorrectly() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(SealedParentWithFinalChild.class).verify())
            .assertFailure();
    }

    @Test
    public void succeed_whenFinalChildHasCorrectEqualsAndHashCode() {
        EqualsVerifier.forClass(FinalSealedChild.class).withRedefinedSuperclass().verify();
    }

    @Test
    public void succeed_whenSealedParentHasANonsealedChild_givenItHasCorrectEqualsAndHashCode() {
        EqualsVerifier
            .forClass(SealedParentWithNonsealedChild.class)
            .withRedefinedSubclass(NonsealedSealedChild.class)
            .verify();
    }

    @Test
    public void fail_whenSealedParentHasANonsealedChild_givenEqualsVerifierIsCalledIncorrectly() {
        ExpectedException
            .when(() -> EqualsVerifier.forClass(SealedParentWithNonsealedChild.class).verify())
            .assertFailure();
    }

    @Test
    public void succeed_whenNonsealedChildHasCorrectEqualsAndHashCode() {
        EqualsVerifier.forClass(NonsealedSealedChild.class).withRedefinedSuperclass().verify();
    }

    @Test
    public void succeed_whenClassContainsASealedType() {
        EqualsVerifier.forClass(SealedTypeContainer.class).verify();
    }

    @Test
    public void fail_whenSealeadParentHasAnIncorrectImplementationOfEquals() {
        ExpectedException
            .when(() ->
                EqualsVerifier
                    .forClass(IncorrectSealedParent.class)
                    .withRedefinedSubclass(IncorrectSealedChild.class)
                    .verify()
            )
            .assertFailure();
    }

    public abstract static sealed class SealedParentWithFinalChild permits FinalSealedChild {

        private final int i;

        public SealedParentWithFinalChild(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SealedParentWithFinalChild other && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    public static final class FinalSealedChild extends SealedParentWithFinalChild {

        private final int j;

        public FinalSealedChild(int i, int j) {
            super(i);
            this.j = j;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FinalSealedChild other && super.equals(obj) && j == other.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), j);
        }
    }

    public abstract static sealed class SealedParentWithNonsealedChild
        permits NonsealedSealedChild {

        private final int i;

        public SealedParentWithNonsealedChild(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SealedParentWithNonsealedChild other && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    public static non-sealed class NonsealedSealedChild extends SealedParentWithNonsealedChild {

        private final int j;

        public NonsealedSealedChild(int i, int j) {
            super(i);
            this.j = j;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj instanceof NonsealedSealedChild other && super.equals(obj) && j == other.j;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(super.hashCode(), j);
        }
    }

    public final class SealedTypeContainer {

        private final SealedParentWithFinalChild sealedWithFinalChild;
        private final SealedParentWithNonsealedChild sealedWithNonsealedChild;

        public SealedTypeContainer(
            SealedParentWithFinalChild sealedWithFinalChild,
            SealedParentWithNonsealedChild sealedWithNonsealedChild
        ) {
            this.sealedWithFinalChild = sealedWithFinalChild;
            this.sealedWithNonsealedChild = sealedWithNonsealedChild;
        }

        @Override
        public boolean equals(Object obj) {
            return (
                obj instanceof SealedTypeContainer other &&
                Objects.equals(sealedWithFinalChild, other.sealedWithFinalChild) &&
                Objects.equals(sealedWithNonsealedChild, other.sealedWithNonsealedChild)
            );
        }

        @Override
        public int hashCode() {
            return Objects.hash(sealedWithFinalChild, sealedWithNonsealedChild);
        }
    }

    public abstract static sealed class IncorrectSealedParent permits IncorrectSealedChild {

        private final int i;

        public IncorrectSealedParent(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
    }

    public static final class IncorrectSealedChild extends IncorrectSealedParent {

        public IncorrectSealedChild(int i) {
            super(i);
        }
    }
}
