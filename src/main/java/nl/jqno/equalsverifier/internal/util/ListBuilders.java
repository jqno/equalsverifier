package nl.jqno.equalsverifier.internal.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Helper functions for building lists with examples.
 */
public final class ListBuilders {
    private ListBuilders() {}

    /**
     * Builds a list with at least one example.
     *
     * @param first The first example.
     * @param more Zero or more additional examples.
     * @param <T> The type of example.
     * @return A list with at least one example.
     */
    @SafeVarargs
    public static <T> List<T> buildListOfAtLeastOne(T first, T... more) {
        if (first == null) {
            throw new IllegalArgumentException("First example is null.");
        }

        List<T> result = new ArrayList<>();
        result.add(first);
        addArrayElementsToList(result, more);

        return result;
    }

    /**
     * Builds a list with at least two examples.
     *
     * @param first The first example.
     * @param second The second example.
     * @param more Zero or more additional examples.
     * @param <T> The type of example.
     * @return A list with at least two examples.
     */
    @SafeVarargs
    public static <T> List<T> buildListOfAtLeastTwo(T first, T second, T... more) {
        if (first == null) {
            throw new IllegalArgumentException("First example is null.");
        }
        if (second == null) {
            throw new IllegalArgumentException("Second example is null.");
        }

        List<T> result = new ArrayList<>();
        result.add(first);
        result.add(second);
        addArrayElementsToList(result, more);

        return result;
    }

    @SafeVarargs
    private static <T> void addArrayElementsToList(List<T> list, T... more) {
        if (more != null) {
            for (T e : more) {
                if (e == null) {
                    throw new IllegalArgumentException("One of the examples is null.");
                }
                list.add(e);
            }
        }
    }

    /**
     * Determines whether a list contains the same example more than once.
     *
     * @param list The list that may or may not contain duplicates.
     * @param <T> The type of example.
     * @return Whether the given list contains duplicates.
     */
    public static <T> boolean listContainsDuplicates(List<T> list) {
        return list.size() != new HashSet<>(list).size();
    }
}
