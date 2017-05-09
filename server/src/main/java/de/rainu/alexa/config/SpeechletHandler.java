package de.rainu.alexa.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

class SpeechletHandler<T extends Annotation> {

  T annotation;
  Method method;
  Object handler;

  @Override
  public String toString() {
    return handler.getClass().getName() + " -> " + method.getName() +
        Arrays.toString(method.getParameterTypes())
            .replace("[", "(")
            .replace("]", ")");
  }
}
