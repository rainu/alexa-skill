package de.rainu.alexa.util;

public class Argument {
  private Class<?> argClass;
  private Object argValue;

  public Argument(Class<?> argClass, Object argValue) {
    this.argClass = argClass;
    this.argValue = argValue;
  }

  public Class<?> getArgClass() {
    return argClass;
  }

  public Object getArgValue() {
    return argValue;
  }
}
