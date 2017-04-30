package de.rainu.alexa.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If an {@link com.amazon.speech.slu.Intent} is incoming this method will be invoked if the
 * name of the intent is equals like the value of this annotation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface OnIntent {
  String value();
}
