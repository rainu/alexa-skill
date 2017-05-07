package de.rainu.alexa.cloud.calendar.exception;

/**
 * This {@link Exception} should be thrown every times if date was not understood.
 */
public class UnknownDateException extends AlexaExcpetion {
  private static final String ERROR_MESSAGE_KEY = "event.error.unknown.date";

  public UnknownDateException() {
    super(ERROR_MESSAGE_KEY, "");
  }

  public UnknownDateException(Throwable cause) {
    super(ERROR_MESSAGE_KEY, "", cause);
  }
}
