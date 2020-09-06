package exceptions;

/**
 * ProcessingException class is used to throw an exception with message which is supposed to be
 * shown in the client app.
 */
public class ProcessingException extends RuntimeException {
    public ProcessingException(String message) {
        super(message);
    }
}
