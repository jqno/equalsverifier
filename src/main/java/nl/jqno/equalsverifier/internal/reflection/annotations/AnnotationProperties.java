package nl.jqno.equalsverifier.internal.reflection.annotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Contains all properties of an annotation necessary to to make decisions about
 * that annotation.
 *
 * Note that this object does not contain all possible properties; only the ones
 * that are actually used by EqualsVerifier.
 */
public class AnnotationProperties {
    private final String className;
    private Map<String, Set<String>> arrayValues = new HashMap<>();

    /**
     * Constructor.
     *
     * @param className The annotation's className string.
     */
    public AnnotationProperties(String className) {
        this.className = className;
    }

    /**
     * @return the annotation's className string.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Adds the content of an array value property.
     *
     * @param name The name of the array value property.
     * @param values The content of the array value property.
     */
    public void putArrayValues(String name, Set<String> values) {
        arrayValues.put(name, values);
    }

    /**
     * Retrieves the content of an array value property.
     *
     * @param name The name of the array value property.
     * @return The content of the array value property.
     */
    public Set<String> getArrayValues(String name) {
        return arrayValues.get(name);
    }
}
