package nl.jqno.equalsverifier.internal.valueproviders;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Note: {@link UserPrefabValueCaches} is tightly coupled with {@link UserPrefabValueProvider} and
 * {@link UserGenericPrefabValueProvider}. Therefore, most tests for this class can be found in their test classes.
 */
public class UserPrefabValueCachesTest {

    @Test
    @SuppressWarnings("unchecked")
    void copy() {
        var sut = new UserPrefabValueCaches();
        sut.register(String.class, "red", "blue", "red");
        sut.registerResettable(Integer.class, () -> 1, () -> 2, () -> 1);
        sut.registerGeneric(List.class, val -> List.of(val));
        sut.registerGeneric(Map.class, (k, v) -> Map.of(k, v));

        var actual = sut.copy();

        assertThat(actual).isNotSameAs(sut);
        assertThat(actual.getPlain(String.class)).isEqualTo(sut.getPlain(String.class));
        assertThat(actual.getResettable(Integer.class)).isEqualTo(sut.getResettable(Integer.class));
        assertThat(actual.getGeneric(List.class).apply(List.of(1)))
                .isEqualTo(sut.getGeneric(List.class).apply(List.of(1)));
        assertThat(actual.getGeneric(Map.class).apply(List.of(1, 2)))
                .isEqualTo(sut.getGeneric(Map.class).apply(List.of(1, 2)));
    }
}
