/*
 * Copyright 2016 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.internal.exceptions;

/**
 * Superclass for exceptions that exist only to send a message to the user when
 * something goes wrong. These exceptions do not need to be included as a cause
 * in the final stack trace. If they have a cause, this cause will serve
 * directly as the cause for the final stack trace, instead of the exception
 * itself.
 *
 * @author Jan Ouwens
 */
@SuppressWarnings("serial")
public abstract class MessagingException extends RuntimeException {
    public MessagingException() {
        super();
    }

    public MessagingException(String message) {
        super(message);
    }

    public MessagingException(Throwable cause) {
        super(cause);
    }

    public MessagingException(String message, Throwable cause) {
        super(message, cause);
    }
}
