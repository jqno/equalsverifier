package nl.jqno.equalsverifier.internal.exceptions;

import nl.jqno.equalsverifier.internal.util.Formatter;

public class MockitoException extends MessagingException {
    public MockitoException(String fieldName, String typeName, String methodName) {
        super(Formatter
                .of(
                    """
                    Unable to use Mockito %%:
                       tried to call method %%.%%, which EqualsVerifier can't mock.
                       Please provide a prefab value for type %%,
                       or use #set(Mode.skipMockito()) to skip using Mockito altogether.""",
                    fieldName == null ? "to mock unknown field" : "to mock field " + fieldName,
                    typeName,
                    methodName,
                    typeName)
                .format());
    }
}
