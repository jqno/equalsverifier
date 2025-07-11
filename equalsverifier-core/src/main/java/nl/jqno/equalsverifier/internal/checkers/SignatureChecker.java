package nl.jqno.equalsverifier.internal.checkers;

import static nl.jqno.equalsverifier.internal.util.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.reflection.ClassProbe;
import nl.jqno.equalsverifier.internal.util.Context;
import nl.jqno.equalsverifier.internal.util.Formatter;

public class SignatureChecker<T> implements Checker {

    private final Class<T> type;
    private final ClassProbe<T> classProbe;
    private final Set<Warning> warningsToSuppress;

    public SignatureChecker(Context<T> context) {
        this.type = context.getType();
        this.classProbe = context.getClassProbe();
        this.warningsToSuppress = context.getConfiguration().warningsToSuppress();
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

    private List<Method> getEqualsMethods() {
        var result = new ArrayList<Method>();

        for (Method method : type.getDeclaredMethods()) {
            if ("equals".equals(method.getName()) && !Modifier.isStatic(method.getModifiers())) {
                result.add(method);
            }
        }

        return result;
    }

    private void failOverloaded(String message) {
        fail(Formatter.of("Overloaded: %%.\nSignature should be: public boolean equals(Object obj)", message));
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

    private void checkEqualsIsDefined() {
        boolean dontAllowDirectlyInherited = !warningsToSuppress.contains(Warning.INHERITED_DIRECTLY_FROM_OBJECT);
        boolean isDirectlyInherited = classProbe.isEqualsInheritedFromObject();
        if (dontAllowDirectlyInherited && isDirectlyInherited) {
            fail(
                Formatter
                        .of(
                            "Equals is inherited directly from Object.\nSuppress Warning."
                                    + Warning.INHERITED_DIRECTLY_FROM_OBJECT.name() + " to skip this check."));
        }
    }
}
