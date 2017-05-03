package de.rainu.alexa.cloud.calendar.exception;

public abstract class AlexaExcpetion extends Exception {

  private String messageKey;

  public AlexaExcpetion(String messageKey) {
    super();
    this.messageKey = messageKey;
  }

  public AlexaExcpetion(String messageKey, String message) {
    super(message);
    this.messageKey = messageKey;
  }

  public AlexaExcpetion(String messageKey, String message, Throwable cause) {
    super(message, cause);
    this.messageKey = messageKey;
  }

  public String getMessageKey() {
    return messageKey;
  }
}
