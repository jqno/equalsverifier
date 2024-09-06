package nl.jqno.equalsverifier.internal.util;

import nl.jqno.equalsverifier.internal.instantiation.*;

public final class Context<T> {

    private final Class<T> type;
    private final Configuration<T> configuration;
    private final ClassProbe<T> classProbe;
    private final SubjectCreator<T> subjectCreator;
    private final InstanceCreator instanceCreator;

    public Context(Configuration<T> configuration) {
        this.type = configuration.getType();
        this.configuration = configuration;
        this.classProbe = new ClassProbe<>(configuration.getType());
        this.instanceCreator = new VintageInstanceCreator(configuration.getPrefabValues());
        this.subjectCreator =
            new ModernSubjectCreator<>(configuration.getTypeTag(), instanceCreator, classProbe);
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

    public InstanceCreator getInstanceCreator() {
        return instanceCreator;
    }

    public SubjectCreator<T> getSubjectCreator() {
        return subjectCreator;
    }
}
