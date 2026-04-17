package nl.jqno.equalsverifier.internal.checkers.fieldchecks;

import static nl.jqno.equalsverifier.internal.util.Assert.assertNotEquals;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.exceptions.EqualsVerifierInternalBugException;
import nl.jqno.equalsverifier.internal.reflection.FieldProbe;
import nl.jqno.equalsverifier.internal.util.CachedHashCodeInitializer;
import nl.jqno.equalsverifier.internal.util.Formatter;
import nl.jqno.equalsverifier.internal.valueproviders.SubjectCreator;

public class UrlFieldCheck<T> implements FieldCheck<T> {

    public static final String ERROR_DOC_TITLE = "URL equality";

    private final SubjectCreator<T> subjectCreator;
    private final CachedHashCodeInitializer<T> cachedHashCodeInitializer;

    public UrlFieldCheck(SubjectCreator<T> subjectCreator, CachedHashCodeInitializer<T> cachedHashCodeInitializer) {
        this.subjectCreator = subjectCreator;
        this.cachedHashCodeInitializer = cachedHashCodeInitializer;
    }

    @Override
    public void execute(FieldProbe fieldProbe) {
        if (!URL.class.equals(fieldProbe.getType())) {
            return;
        }
        try {
            URLStreamHandler handler = new AlwaysEqualUrlHandler();
            URL url1 = new URL(null, "http://example.com", handler);
            URL url2 = new URL(null, "http://different.com", handler);

            T left = subjectCreator.withFieldSetTo(fieldProbe.getField(), url1);
            T right = subjectCreator.withFieldSetTo(fieldProbe.getField(), url2);

            checkEquals(fieldProbe.getField(), left, right);
            checkHashCode(fieldProbe.getField(), left, right);
        }
        catch (MalformedURLException e) {
            // Can't happen with hard-coded valid URLs
        }
    }

    private void checkEquals(Field field, T left, T right) {
        Formatter f = Formatter
                .of(
                    """
                    %%: field %% uses URL.equals()
                    This performs DNS resolution, making equality checks non-deterministic.
                    Consider using URI.create(url.toString()).equals() instead, or suppress Warning.%% to disable this check.""",
                    ERROR_DOC_TITLE,
                    field.getName(),
                    Warning.URL_EQUALITY);
        assertNotEquals(f, left, right);
    }

    private void checkHashCode(Field field, T left, T right) {
        Formatter f = Formatter
                .of(
                    """
                    %%: field %% uses URL.hashCode()
                    This performs DNS resolution, making hash codes non-deterministic.
                    Consider using URI.create(url.toString()).hashCode() instead, or suppress Warning.%% to disable this check.""",
                    ERROR_DOC_TITLE,
                    field.getName(),
                    Warning.URL_EQUALITY);
        int leftHashCode = cachedHashCodeInitializer.getInitializedHashCode(left);
        int rightHashCode = cachedHashCodeInitializer.getInitializedHashCode(right);
        assertNotEquals(f, leftHashCode, rightHashCode);
    }

    private static final class AlwaysEqualUrlHandler extends URLStreamHandler {

        @Override
        protected URLConnection openConnection(URL u) {
            throw new EqualsVerifierInternalBugException();
        }

        @Override
        protected boolean equals(URL u1, URL u2) {
            return true;
        }

        @Override
        protected int hashCode(URL u) {
            return 42;
        }
    }
}
