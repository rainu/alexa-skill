package de.rainu.alexa.cloud.calendar.exception;

/**
 * This {@link Exception} should be thrown every times if the calendar could not read.
 */
public class CalendarReadException extends Exception {

  public CalendarReadException(String message) {
    super(message);
  }

  public CalendarReadException(String message, Throwable cause) {
    super(message, cause);
  }
}
