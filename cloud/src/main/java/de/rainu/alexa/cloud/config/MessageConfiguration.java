package de.rainu.alexa.cloud.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is responsible for define all used messages.
 */
@Configuration
public class MessageConfiguration {

//FIXME: implements message resource bundles (i have try it but have no solution found ;( )
//  @Bean
//  public ResourceBundleMessageSource messageSource() {
//    ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
//    resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
//    return resourceBundleMessageSource;
//  }

  public static final String MESSAGES_DE = "messages_de";

  @Bean
  @Qualifier(MESSAGES_DE)
  public Map<String, String> messages() {
    Map<String, String> m = new HashMap<>();

    m.put("welcome", "Willkommen in deinem Kalender. Du kannst hier Termine erfragen und erstellen.");
    m.put("help", "Frage mich: was ansteht, um die aktuellen Termine zu erfahren.");

    m.put("event.start", "Folgende Termine stehen %s an:");
    m.put("event.item.today", "Heute: %s");
    m.put("event.item.today.time", "Heute um %s Uhr: %s");
    m.put("event.item.today.time.duration", "Heute von %s Uhr bis %s Uhr: %s");
    m.put("event.item.tomorrow", "Morgen: %s");
    m.put("event.item.tomorrow.time", "Morgen um %s Uhr: %s");
    m.put("event.item.tomorrow.time.duration", "Morgen von %s Uhr bis %s Uhr: %s");
    m.put("event.item.date", "Am %s, den <say-as interpret-as=\"date\" format=\"dm\">%s</say-as>: %s");
    m.put("event.item.date.time", "Am %s, den <say-as interpret-as=\"date\" format=\"dm\">%s</say-as> um %s Uhr: %s");
    m.put("event.item.date.time.duration", "Am %s, den <say-as interpret-as=\"date\" format=\"dm\">%s</say-as> von %s Uhr bis %s Uhr: %s");
    m.put("event.nothing", "Es stehen %s keine Termine an.");
    m.put("event.error", "Es tut mir leid. Es ist leider ein Fehler aufgetreten.");
    m.put("event.error.unknown.moment", "Es tut mir leid. Diese Zeitangabe ist mir unbekannt.");
    m.put("event.error.read", "Es tut mir leid. Ich konnte die Termine nicht auslesen.");

    return Collections.unmodifiableMap(m);
  }
}
