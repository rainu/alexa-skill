package de.rainu.alexa.cloud.calendar.exception;

/**
 * This {@link Exception} should be thrown every times if the calendar could not write.
 */
public class CalendarWriteException extends AlexaExcpetion {
  private static final String ERROR_MESSAGE_KEY = "event.error.write";

  public CalendarWriteException(String message) {
    super(ERROR_MESSAGE_KEY, message);
  }

  public CalendarWriteException(String message, Throwable cause) {
    super(ERROR_MESSAGE_KEY, message, cause);
  }
}
