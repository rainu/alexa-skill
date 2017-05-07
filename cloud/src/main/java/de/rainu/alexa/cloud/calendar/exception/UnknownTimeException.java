package de.rainu.alexa.cloud.calendar.exception;

/**
 * This {@link Exception} should be thrown every times if time was not understood.
 */
public class UnknownTimeException extends AlexaExcpetion {
  private static final String ERROR_MESSAGE_KEY = "event.error.unknown.time";

  public UnknownTimeException() {
    super(ERROR_MESSAGE_KEY, "");
  }

  public UnknownTimeException(Throwable cause) {
    super(ERROR_MESSAGE_KEY, "", cause);
  }
}
