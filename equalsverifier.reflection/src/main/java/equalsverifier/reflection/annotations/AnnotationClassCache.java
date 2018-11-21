package equalsverifier.reflection.annotations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class AnnotationClassCache {
    private final Set<Annotation> classAnnotations = new HashSet<>();
    private final Map<String, Set<Annotation>> fieldAnnotations = new HashMap<>();

    public AnnotationClassCache() {}

    public boolean hasClassAnnotation(Annotation annotation) {
        return classAnnotations.contains(annotation);
    }

    public boolean hasFieldAnnotation(String fieldName, Annotation annotation) {
        return fieldAnnotations.containsKey(fieldName) && fieldAnnotations.get(fieldName).contains(annotation);
    }

    public void addClassAnnotation(Annotation annotation) {
        classAnnotations.add(annotation);
    }

    public void addField(String fieldName) {
        fieldAnnotations.put(fieldName, new HashSet<>());
    }

    public void addFieldAnnotation(String fieldName, Annotation annotation) {
        if (!fieldAnnotations.containsKey(fieldName)) {
            addField(fieldName);
        }
        fieldAnnotations.get(fieldName).add(annotation);
    }
}
