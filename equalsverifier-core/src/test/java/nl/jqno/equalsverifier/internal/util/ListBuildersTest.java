package nl.jqno.equalsverifier.internal.util;

import static nl.jqno.equalsverifier_testhelpers.Util.coverThePrivateConstructor;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class ListBuildersTest {

    @Test
    void coverTheConstructor() {
        coverThePrivateConstructor(ListBuilders.class);
    }

    @Test
    void listContainsDuplicates() {
        assertThat(ListBuilders.listContainsDuplicates(List.of(1, 1))).isTrue();
        assertThat(ListBuilders.listContainsDuplicates(List.of(1, 2, 1))).isTrue();
        assertThat(ListBuilders.listContainsDuplicates(List.of(1, 2))).isFalse();
    }
}
