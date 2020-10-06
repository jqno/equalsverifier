package nl.jqno.equalsverifier.internal.checkers;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.FieldCheck;
import nl.jqno.equalsverifier.internal.prefabvalues.PrefabValues;
import nl.jqno.equalsverifier.internal.prefabvalues.TypeTag;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.reflection.FieldAccessor;
import nl.jqno.equalsverifier.internal.reflection.ObjectAccessor;
import nl.jqno.equalsverifier.internal.reflection.annotations.AnnotationCache;
import nl.jqno.equalsverifier.testhelpers.FactoryCacheFactory;
import nl.jqno.equalsverifier.testhelpers.types.Point;
import org.junit.Test;

public class FieldInspectorTest {
    private final PrefabValues prefabValues =
            new PrefabValues(FactoryCacheFactory.withPrimitiveFactories());
    private final ClassAccessor<Point> accessor = ClassAccessor.of(Point.class, prefabValues);

    @Test
    public void objectsAreReset_whenEachIterationBegins() {
        FieldInspector<Point> inspector = new FieldInspector<>(accessor, TypeTag.NULL);

        inspector.check(new ResetObjectForEachIterationCheck<>());
    }

    @Test
    public void objectsAreReset_whenEachIterationBegins_givenNullObjects() {
        FieldInspector<Point> inspector = new FieldInspector<>(accessor, TypeTag.NULL);
        Set<String> nullFields = new HashSet<>();
        AnnotationCache annotationCache = new AnnotationCache();

        inspector.checkWithNull(
                nullFields, annotationCache, new ResetObjectForEachIterationCheck<>());
    }

    private final class ResetObjectForEachIterationCheck<T> implements FieldCheck<T> {
        private Object originalReference;
        private Object originalChanged;

        @Override
        public void execute(
                ObjectAccessor<T> referenceAccessor,
                ObjectAccessor<T> copyAccessor,
                FieldAccessor fieldAccessor) {
            if (originalReference == null) {
                originalReference = referenceAccessor.copy();
                originalChanged = copyAccessor.copy();
            } else {
                assertEquals(originalReference, referenceAccessor.get());
                assertEquals(originalChanged, copyAccessor.get());
            }
            Field field = fieldAccessor.getField();

            referenceAccessor.withChangedField(field, prefabValues, TypeTag.NULL);
            copyAccessor.withChangedField(field, prefabValues, TypeTag.NULL);
        }
    }
}
