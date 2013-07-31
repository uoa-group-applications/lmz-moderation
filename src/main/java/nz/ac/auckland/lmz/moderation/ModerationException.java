package nz.ac.auckland.lmz.moderation;

import nz.ac.auckland.lmz.errors.ExpectedErrorException;

import java.util.Map;

/**
 * This exception is thrown when there is an error during moderation procedures.
 */
public class ModerationException extends ExpectedErrorException {

    /** @see Exception#Exception(String) */
    public ModerationException(String message, Map context) {
        super(message, context);
    }

    /** @see Exception#Exception(String, Throwable) */
    public ModerationException(String message, Map context, Throwable cause) {
        super(message, context, cause);
    }

}
