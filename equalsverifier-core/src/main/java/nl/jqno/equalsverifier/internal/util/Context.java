package nl.jqno.equalsverifier.internal.util;

import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.Mode;
import nl.jqno.equalsverifier.internal.instantiators.InstantiatorFactory;
import nl.jqno.equalsverifier.internal.instantiators.ProvidedFactoryInstantiator;
import nl.jqno.equalsverifier.internal.reflection.*;
import nl.jqno.equalsverifier.internal.valueproviders.*;
import org.objenesis.Objenesis;

public final class Context<T> {

    private final Class<T> type;
    private final Configuration<T> configuration;
    private final ClassProbe<T> classProbe;

    private final SubjectCreator<T> subjectCreator;
    private final ValueProvider valueProvider;

    public Context(
            Configuration<T> configuration,
            UserPrefabValueCaches userPrefabs,
            FieldCache fieldCache,
            Objenesis objenesis) {
        this.type = configuration.type();
        this.configuration = configuration;
        this.classProbe = ClassProbe.of(configuration.type());
        var modes = configuration.modes();

        this.valueProvider = ValueProviderBuilder.build(modes, userPrefabs, fieldCache, objenesis);
        var actualType =
                SubtypeManager.findInstantiableSubclass(ClassProbe.of(type), valueProvider, Attributes.empty());
        var forceFinalMeansFinal = configuration.modes().contains(Mode.finalMeansFinal());
        var instantiator = InstantiatorFactory
                .of(ClassProbe.of(actualType), configuration.factory(), objenesis, forceFinalMeansFinal);
        this.subjectCreator = new SubjectCreator<>(actualType,
                configuration,
                this.valueProvider,
                objenesis,
                instantiator,
                forceFinalMeansFinal);

        if (configuration.factory() != null && instantiator instanceof ProvidedFactoryInstantiator<T> pfi) {
            Set<String> fieldNames = new HashSet<>();
            for (var fp : FieldIterable.ofIgnoringStatic(type)) {
                fieldNames.add(fp.getName());
            }

            // TO DO once this works, move this to Configuration but call from Context.
            subjectCreator.plain(); // trigger the factory
            fieldNames.removeAll(pfi.getLastRequestedFields());
            configuration.ignoredFields().addAll(fieldNames);
        }
    }

    public Class<T> getType() {
        return type;
    }

    public Configuration<T> getConfiguration() {
        return configuration;
    }

    public ClassProbe<T> getClassProbe() {
        return classProbe;
    }

    public ValueProvider getValueProvider() {
        return valueProvider;
    }

    public SubjectCreator<T> getSubjectCreator() {
        return subjectCreator;
    }
}
