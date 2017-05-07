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

    m.put("welcome", "Willkommen in deinem Kalender.");
    m.put("confirm", "OK.");
    m.put("help", "Frage mich: was ansteht, um die aktuellen Termine zu erfahren. Oder trage einen neuen Termin ein.");

    m.put("event.listing.start", "Folgende Termine stehen %s an:");
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

    m.put("event.new.cancel", "OK. Ich habe den Termin verworfen.");
    m.put("event.new.saved", "OK. Ich habe den Termin gespeichert.");
    m.put("event.new.confirm", "Kann ich den Termin von %s, den <say-as interpret-as=\"date\" format=\"dmy\">%s</say-as> um %s Uhr bis %s, den <say-as interpret-as=\"date\" format=\"dm\">%s</say-as> um %s Uhr speichern?");
    m.put("event.new.confirm.sameday", "Kann ich den Termin am %s, den <say-as interpret-as=\"date\" format=\"dmy\">%s</say-as> von %s Uhr bis %s Uhr speichern?");
    m.put("event.new.card.title", "Neuer Termin erstellt");
    m.put("event.new.card.content", "Titel: %s\nBeginn: %s\nEnde:%s");
    m.put("event.new.card.content.time.format", "dd.MM.yyyy HH:mm");

    m.put("event.error", "Es tut mir leid. Es ist leider ein Fehler aufgetreten.");
    m.put("event.error.unknown.moment", "Es tut mir leid. Diese Zeitangabe ist mir unbekannt.");
    m.put("event.error.unknown.date", "Es tut mir leid. Das Datum habe ich nicht verstanden.");
    m.put("event.error.unknown.time", "Es tut mir leid. Diese Uhrzeit habe ich nicht verstanden.");
    m.put("event.error.read", "Es tut mir leid. Ich konnte die Termine nicht auslesen.");
    m.put("event.error.write", "Es tut mir leid. Ich konnte den Termin nicht speichern.");
    m.put("event.error.understand", "Es tut mir leid. Das habe ich nicht verstanden.");

    return Collections.unmodifiableMap(m);
  }
}
