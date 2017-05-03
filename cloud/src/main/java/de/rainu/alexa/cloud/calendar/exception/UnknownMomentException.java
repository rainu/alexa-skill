package de.rainu.alexa.cloud.calendar.exception;

/**
 * This {@link Exception} should be thrown every times if moment is unknown.
 */
public class UnknownMomentException extends AlexaExcpetion {
  private static final String ERROR_MESSAGE_KEY = "event.error.read";

  public UnknownMomentException() {
    super(ERROR_MESSAGE_KEY, "");
  }
}
