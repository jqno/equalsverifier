package equalsverifier.reflection.annotations;

import java.util.HashMap;
import java.util.Map;

public class AnnotationCache {
    private final Map<Class<?>, AnnotationClassCache> cache = new HashMap<>();

    public boolean hasResolved(Class<?> type) {
        return cache.containsKey(type);
    }

    public boolean hasClassAnnotation(Class<?> type, Annotation annotation) {
        return hasResolved(type) && cache.get(type).hasClassAnnotation(annotation);
    }

    public boolean hasFieldAnnotation(Class<?> type, String fieldName, Annotation annotation) {
        return hasResolved(type) && cache.get(type).hasFieldAnnotation(fieldName, annotation);
    }

    public void addClassAnnotation(Class<?> type, Annotation annotation) {
        ensureEntry(type);
        cache.get(type).addClassAnnotation(annotation);
    }

    public void addField(Class<?> type, String fieldName) {
        ensureEntry(type);
        cache.get(type).addField(fieldName);
    }

    public void addFieldAnnotation(Class<?> type, String fieldName, Annotation annotation) {
        ensureEntry(type);
        cache.get(type).addFieldAnnotation(fieldName, annotation);
    }

    private void ensureEntry(Class<?> type) {
        if (!hasResolved(type)) {
            cache.put(type, new AnnotationClassCache());
        }
    }
}
