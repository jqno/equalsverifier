package nl.jqno.equalsverifier_testhelpers.types;

import java.util.List;
import java.util.Objects;

public class SparseArrays {
    public static final class SparseArray<T> {

        private final List<T> items;

        public SparseArray(List<T> items) {
            this.items = items;
        }

        public int size() {
            return items.size();
        }

        public T get(int i) {
            return items.get(i);
        }
        // There are no equals and hashCode
    }

    public static final class SparseArrayEqualsContainer {

        private final SparseArray<Point> sparseArray;

        public SparseArrayEqualsContainer(SparseArray<Point> sparseArray) {
            this.sparseArray = sparseArray;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SparseArrayEqualsContainer)) {
                return false;
            }
            SparseArrayEqualsContainer other = (SparseArrayEqualsContainer) obj;
            if (sparseArray == null || other.sparseArray == null) {
                return sparseArray == other.sparseArray;
            }
            if (sparseArray.size() != other.sparseArray.size()) {
                return false;
            }
            for (int i = 0; i < sparseArray.size(); i++) {
                Point a = sparseArray.get(i);
                Point b = other.sparseArray.get(i);
                if (!a.equals(b)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = 17;
            if (sparseArray != null) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    result += 59 * sparseArray.get(i).hashCode();
                }
            }
            return result;
        }
    }

    public static final class SparseArrayDirectEqualsContainer {

        private final SparseArray<Point> sparseArray;

        public SparseArrayDirectEqualsContainer(SparseArray<Point> sparseArray) {
            this.sparseArray = sparseArray;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof SparseArrayDirectEqualsContainer)) {
                return false;
            }
            SparseArrayDirectEqualsContainer other = (SparseArrayDirectEqualsContainer) obj;
            if (sparseArray == null || other.sparseArray == null) {
                return sparseArray == other.sparseArray;
            }
            if (sparseArray.items == null || other.sparseArray.items == null) {
                return sparseArray.items == other.sparseArray.items;
            }
            if (sparseArray.items.size() != other.sparseArray.items.size()) {
                return false;
            }
            for (int i = 0; i < sparseArray.items.size(); i++) {
                Point a = sparseArray.items.get(i);
                Point b = other.sparseArray.items.get(i);
                if (!a.equals(b)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = 17;
            if (sparseArray != null && sparseArray.items != null) {
                for (int i = 0; i < sparseArray.items.size(); i++) {
                    result += 59 * sparseArray.items.get(i).hashCode();
                }
            }
            return result;
        }
    }

    public static final class SparseArrayHashCodeContainer {

        private final SparseArray<Point> sparseArray;

        public SparseArrayHashCodeContainer(SparseArray<Point> sparseArray) {
            this.sparseArray = sparseArray;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SparseArrayHashCodeContainer other && Objects.equals(sparseArray, other.sparseArray);
        }

        @Override
        public int hashCode() {
            int result = 17;
            if (sparseArray != null) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    result += 59 * sparseArray.get(i).hashCode();
                }
            }
            return result;
        }
    }

    public static final class SparseArrayToStringContainer {

        private final SparseArray<Point> sparseArray;

        public SparseArrayToStringContainer(SparseArray<Point> sparseArray) {
            this.sparseArray = sparseArray;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof SparseArrayToStringContainer other && Objects.equals(sparseArray, other.sparseArray);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sparseArray);
        }

        @Override
        public String toString() {
            String result = SparseArrayToStringContainer.class.getSimpleName() + ": ";
            if (sparseArray != null) {
                for (int i = 0; i < sparseArray.size(); i++) {
                    result += sparseArray.get(i) + ", ";
                }
            }
            return result;
        }
    }
}
