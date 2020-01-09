package nl.jqno.equalsverifier.integration.extended_contract;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.testhelpers.StringCompilerTestBase;
import org.junit.Test;

// CHECKSTYLE OFF: DeclarationOrder

public class JavaMoneyClassesTest extends StringCompilerTestBase {
    @Test
    public void successfullyInstantiatesAJavaMoneyClass_whenJavaMoneyIsAvailable() {
        if (!isJavaMoneyAvailable()) {
            return;
        }

        Class<?> javaMoneyClass = compile(JAVAMONEY_CLASS_NAME, JAVAMONEY_CLASS);
        EqualsVerifier.forClass(javaMoneyClass).verify();
    }

    public boolean isJavaMoneyAvailable() {
        return isTypeAvailable("javax.money.CurrencyUnit");
    }

    private static final String JAVAMONEY_CLASS =
            "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultEquals;"
                    + "\nimport static nl.jqno.equalsverifier.testhelpers.Util.defaultHashCode;"
                    + "\n"
                    + "\nimport javax.money.CurrencyUnit;"
                    + "\nimport javax.money.MonetaryAmount;"
                    + "\nimport javax.money.Monetary;"
                    + "\nimport org.javamoney.moneta.Money;"
                    + "\n"
                    + "\npublic final class JavaMoneyClassesContainer {"
                    + "\n    private final CurrencyUnit currencyUnit;"
                    + "\n    private final MonetaryAmount monetaryAmount;"
                    + "\n    private final Money monetaMoney;"
                    + "\n    "
                    + "\n    public JavaMoneyClassesContainer("
                    + "\n            CurrencyUnit currencyUnit,"
                    + "\n            MonetaryAmount monetaryAmount,"
                    + "\n            Money monetaMoney) {"
                    + "\n        this.currencyUnit = currencyUnit;"
                    + "\n        this.monetaryAmount = monetaryAmount;"
                    + "\n        this.monetaMoney = monetaMoney;"
                    + "\n    }"
                    + "\n    "
                    + "\n    @Override"
                    + "\n    public boolean equals(Object obj) {"
                    + "\n        return defaultEquals(this, obj);"
                    + "\n    }"
                    + "\n    "
                    + "\n    @Override"
                    + "\n    public int hashCode() {"
                    + "\n        return defaultHashCode(this);"
                    + "\n    }"
                    + "\n}";
    private static final String JAVAMONEY_CLASS_NAME = "JavaMoneyClassesContainer";
}
