package de.rainu.alexa.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

/**
 * A class with this annotation will scanned for methods annotated by
 * {@link OnIntent}, {@link OnLaunch}, {@link OnSessionStarted}, {@link OnSessionEnded}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Controller
public @interface SpeechletController {

  /**
   * The endpoint(s) of the skill.
   */
  String[] endpoint();

  /**
   * The name of this controller.
   * @return
   */
  @AliasFor("name")
  String value() default "";

  @AliasFor("value")
  String name() default "";
}
