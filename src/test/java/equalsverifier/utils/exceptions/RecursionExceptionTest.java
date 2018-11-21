package equalsverifier.utils.exceptions;

import equalsverifier.prefabvalues.RecursionException;
import equalsverifier.gentype.TypeTag;
import equalsverifier.testhelpers.types.Point;
import org.junit.Test;

import java.util.LinkedHashSet;

import static org.junit.Assert.assertTrue;

public class RecursionExceptionTest {
    @Test
    public void descriptionContainsAllTypes() {
        LinkedHashSet<TypeTag> stack = new LinkedHashSet<>();
        stack.add(new TypeTag(String.class));
        stack.add(new TypeTag(Point.class));
        stack.add(new TypeTag(Object.class));

        String message = new RecursionException(stack).getDescription();

        for (TypeTag tag : stack) {
            assertTrue(message.contains(tag.toString()));
        }
    }
}
