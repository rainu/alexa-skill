package de.rainu.alexa.config;

import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.OnLaunch;
import de.rainu.alexa.annotation.OnSessionEnded;
import de.rainu.alexa.annotation.OnSessionStarted;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SpeechletHandlerHolder {

  SpeechletHandler<OnLaunch> onLaunch;

  List<SpeechletHandler<OnSessionEnded>> onSessionEnded = new ArrayList<>();

  List<SpeechletHandler<OnSessionStarted>> onSessionStarted = new ArrayList<>();

  Map<String, SpeechletHandler<OnIntent>> onIntent = new HashMap<>();


  SpeechletHandlerHolder setOnLaunch(Method method, Object handler){
    this.onLaunch = new SpeechletHandler<>();
    this.onLaunch.annotation = method.getAnnotation(OnLaunch.class);
    this.onLaunch.method = method;
    this.onLaunch.handler = handler;

    return this;
  }

  SpeechletHandlerHolder addSessionEnded(Method method, Object handler){
    SpeechletHandler<OnSessionEnded> h = new SpeechletHandler<>();
    h.annotation = method.getAnnotation(OnSessionEnded.class);
    h.method = method;
    h.handler = handler;

    this.onSessionEnded.add(h);

    return this;
  }

  SpeechletHandlerHolder addSessionStarted(Method method, Object handler){
    SpeechletHandler<OnSessionStarted> h = new SpeechletHandler<>();
    h.annotation = method.getAnnotation(OnSessionStarted.class);
    h.method = method;
    h.handler = handler;

    this.onSessionStarted.add(h);

    return this;
  }

  SpeechletHandlerHolder addOnIntent(Method method, Object handler){
    SpeechletHandler<OnIntent> h = new SpeechletHandler<>();
    h.annotation = method.getAnnotation(OnIntent.class);
    h.method = method;
    h.handler = handler;

    for(String intent : h.annotation.value()){
      if(this.onIntent.containsKey(intent)) {
        final SpeechletHandler<OnIntent> configured = onIntent.get(intent);
        throw new IllegalStateException("There is already a intent handler defined for '" +
            intent + "' in " + configured + " !");
      }

      this.onIntent.put(intent, h);
    }

    return this;
  }
}
