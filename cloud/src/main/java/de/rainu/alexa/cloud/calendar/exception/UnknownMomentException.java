package de.rainu.alexa.cloud.calendar.exception;

/**
 * This {@link Exception} should be thrown every times if moment is unknown.
 */
public class UnknownMomentException extends AlexaExcpetion {
  private static final String ERROR_MESSAGE_KEY = "event.error.unknown.moment";

  public UnknownMomentException() {
    super(ERROR_MESSAGE_KEY, "");
  }

  public UnknownMomentException(String messageKey, Throwable cause) {
    super(messageKey, "", cause);
  }
}
