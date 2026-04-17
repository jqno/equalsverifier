package nl.jqno.equalsverifier.integration.extended_contract;

import java.net.URI;
import java.net.URL;
import java.util.Objects;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.internal.checkers.fieldchecks.UrlFieldCheck;
import nl.jqno.equalsverifier_testhelpers.ExpectedException;
import org.junit.jupiter.api.Test;

class UrlTest {

    @Test
    void succeed_whenEqualsAndHashCodeUseUri() {
        EqualsVerifier.forClass(UrlWithUriEqualsAndHashCode.class).verify();
    }

    @Test
    void fail_whenEqualsUsesUrlDirectly() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UrlWithDirectEquals.class).verify())
                .assertFailure()
                .assertMessageContains(UrlFieldCheck.ERROR_DOC_TITLE, "url", Warning.URL_EQUALITY.toString());
    }

    @Test
    void fail_whenHashCodeUsesUrlDirectly() {
        ExpectedException
                .when(() -> EqualsVerifier.forClass(UrlWithUriEqualsButDirectHashCode.class).verify())
                .assertFailure()
                .assertMessageContains(UrlFieldCheck.ERROR_DOC_TITLE, "url", Warning.URL_EQUALITY.toString());
    }

    @Test
    void succeed_whenUrlEqualityWarningIsSuppressed() {
        EqualsVerifier.forClass(UrlWithDirectEquals.class).suppress(Warning.URL_EQUALITY).verify();
    }

    @Test
    void succeed_whenUrlHashCodeWarningIsSuppressed() {
        EqualsVerifier.forClass(UrlWithUriEqualsButDirectHashCode.class).suppress(Warning.URL_EQUALITY).verify();
    }

    @SuppressWarnings("unused")
    private static final class UrlWithDirectEquals {

        private final URL url;

        private UrlWithDirectEquals(URL url) {
            this.url = url;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof UrlWithDirectEquals other && Objects.equals(url, other.url);
        }

        @Override
        public int hashCode() {
            return Objects.hash(url);
        }
    }

    @SuppressWarnings("unused")
    private static final class UrlWithUriEqualsButDirectHashCode {

        private final URL url;

        private UrlWithUriEqualsButDirectHashCode(URL url) {
            this.url = url;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof UrlWithUriEqualsButDirectHashCode other
                    && Objects.equals(uriOf(url), uriOf(other.url));
        }

        @Override
        public int hashCode() {
            return Objects.hash(url);
        }
    }

    @SuppressWarnings("unused")
    private static final class UrlWithUriEqualsAndHashCode {

        private final URL url;

        private UrlWithUriEqualsAndHashCode(URL url) {
            this.url = url;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof UrlWithUriEqualsAndHashCode other && Objects.equals(uriOf(url), uriOf(other.url));
        }

        @Override
        public int hashCode() {
            return Objects.hash(uriOf(url));
        }
    }

    private static URI uriOf(URL url) {
        return url != null ? URI.create(url.toString()) : null;
    }
}
