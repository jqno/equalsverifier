package nl.jqno.equalsverifier.internal.exceptions;

import nl.jqno.equalsverifier.internal.util.Formatter;

public class MockitoException extends MessagingException {
    public MockitoException(String fieldName, String typeName) {
        super(Formatter
                .of(
                    """
                            Unable to use Mockito to mock field %% of type %%.
                               Please provide a prefab value for this type, or use #set(Mode.skipMockito()) to skip using Mockito altogether.
                            """,
                    fieldName,
                    typeName)
                .format());
    }
}
