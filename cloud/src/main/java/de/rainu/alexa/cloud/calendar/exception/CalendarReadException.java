package de.rainu.alexa.cloud.calendar.exception;

/**
 * This {@link Exception} should be thrown every times if the calendar could not read.
 */
public class CalendarReadException extends AlexaExcpetion {
  private static final String ERROR_MESSAGE_KEY = "event.error.read";

  public CalendarReadException(String message) {
    super(ERROR_MESSAGE_KEY, message);
  }

  public CalendarReadException(String message, Throwable cause) {
    super(ERROR_MESSAGE_KEY, message, cause);
  }
}
