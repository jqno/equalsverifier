package nl.jqno.equalsverifier.integration.extended_contract;

import java.util.Objects;

public class SealedClasses {
    public static sealed abstract class SealedParent permits SealedChild {
        private final int i;

        public SealedParent(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SealedParent other && i == other.i;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i);
        }
        
    }

    public static final class SealedChild extends SealedParent {
        private final int j;

        public SealedChild(int i, int j) {
            super(i);
            this.j = j;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SealedChild other && super.equals(obj) && j == other.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), j);
        }
    }
}
