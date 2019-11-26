package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.ClassAccessor;
import nl.jqno.equalsverifier.internal.util.Configuration;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class SignatureChecker<T> implements Checker {
    private final Class<T> type;
    private final ClassAccessor<T> classAccessor;
    private final Set<Warning> warningsToSuppress;

    public SignatureChecker(Configuration<T> config) {
        this.type = config.getType();
        this.classAccessor = config.getClassAccessor();
        this.warningsToSuppress = config.getWarningsToSuppress();
    }

    @Override
    public void check() {
        List<Method> equalsMethods = getEqualsMethods();
        if (equalsMethods.size() > 1) {
            failOverloaded("More than one equals method found");
        }
        if (equalsMethods.size() == 1) {
            checkEquals(equalsMethods.get(0));
        }
        checkEqualsIsDefined();
    }

    private void checkEqualsIsDefined() {
        boolean fail =
                !warningsToSuppress.contains(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                        && classAccessor.isEqualsInheritedFromObject();
        if (fail) {
            fail(
                    Formatter.of(
                            "Equals is inherited directly from Object.\n"
                                    + "Suppress Warning."
                                    + Warning.INHERITED_DIRECTLY_FROM_OBJECT.name()
                                    + " to skip this check."));
        }
    }

    private List<Method> getEqualsMethods() {
        List<Method> result = new ArrayList<>();

        for (Method method : type.getDeclaredMethods()) {
            if (method.getName().equals("equals")) {
                result.add(method);
            }
        }

        return result;
    }

    private void checkEquals(Method equals) {
        Class<?>[] parameterTypes = equals.getParameterTypes();
        if (parameterTypes.length > 1) {
            failOverloaded("Too many parameters");
        }
        if (parameterTypes.length == 0) {
            failOverloaded("No parameter");
        }
        Class<?> parameterType = parameterTypes[0];
        if (parameterType == type) {
            failOverloaded("Parameter should be an Object, not " + type.getSimpleName());
        }
        if (parameterType != Object.class) {
            failOverloaded("Parameter should be an Object");
        }
    }

    private void failOverloaded(String message) {
        fail(
                Formatter.of(
                        "Overloaded: %%.\nSignature should be: public boolean equals(Object obj)",
                        message));
    }
}
