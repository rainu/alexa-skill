package de.rainu.alexa.cloud.service;

import static org.junit.Assert.assertEquals;

import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
import de.rainu.alexa.cloud.calendar.model.Event;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpeechServiceTest {

  @Autowired
  SpeechService toTest;

  Locale aLocale = Locale.GERMAN;

  @Test
  public void speechWelcomeMessage(){
    //given
    //when
    final PlainTextOutputSpeech result = (PlainTextOutputSpeech)toTest.speechWelcomeMessage(aLocale);

    //then
    assertEquals("Willkommen in deinem Kalender.", result.getText());
  }

  @Test
  public void speechHelpMessage(){
    //given
    //when
    final PlainTextOutputSpeech result = (PlainTextOutputSpeech)toTest.speechHelpMessage(aLocale);

    //then
    assertEquals("Frage mich: was ansteht, um die aktuellen Termine zu erfahren. Oder trage einen neuen Termin ein.", result.getText());
  }

  @Test
  public void speechError(){
    //given
    CalendarReadException e = new CalendarReadException("error");

    //when
    final PlainTextOutputSpeech result = (PlainTextOutputSpeech)toTest.speechError(e);

    //then
    assertEquals("Es tut mir leid. Ich konnte die Termine nicht auslesen.", result.getText());
  }

  @Test
  public void readEvents_empty(){
    //given
    List<Event> events = Collections.emptyList();
    final String moment = "heute";

    //when
    final PlainTextOutputSpeech result = (PlainTextOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals("Es stehen " + moment + " keine Termine an.", result.getText());
  }

  @Test
  public void readEvents_singleEventTodayNoTime() {
    //given
    Event event = new Event();
    event.setStart(DateTime.now(), false);
    event.setSummary("<summary>");

    List<Event> events = Arrays.asList(event);
    final String moment = "heute";

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals("<speak>Folgende Termine stehen " + moment + " an:<break time=\"500ms\"/>"
        + "Heute: " + event.getSummary() + "</speak>", result.getSsml());
  }

  @Test
  public void readEvents_singleEventTodayIncludesTime() {
    //given
    final DateTime start = DateTime.now();
    final Event event = new Event();
    event.setStart(start, true);
    event.setSummary("<summary>");

    final List<Event> events = Arrays.asList(event);
    final String moment = "heute";

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals("<speak>Folgende Termine stehen " + moment + " an:<break time=\"500ms\"/>"
        + "Heute um " + start.toString("HH:mm") + " Uhr: " + event.getSummary() + "</speak>", result.getSsml());
  }

  @Test
  public void readEvents_singleEventTodayDuration() {
    //given
    final DateTime start = DateTime.now();
    final DateTime end = start.plusHours(1).plusMinutes(1);
    final Event event = new Event();
    event.setStart(start, true);
    event.setEnd(end, true);
    event.setSummary("<summary>");

    final List<Event> events = Arrays.asList(event);
    final String moment = "heute";

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals("<speak>Folgende Termine stehen " + moment + " an:<break time=\"500ms\"/>"
        + "Heute von " + start.toString("HH:mm") + " Uhr bis " + end.toString("HH:mm") + " Uhr: " + event.getSummary() + "</speak>", result.getSsml());
  }

  @Test
  public void readEvents_singleEventTomorrowNoTime() {
    //given
    Event event = new Event();
    event.setStart(DateTime.now().plusDays(1), false);
    event.setSummary("<summary>");

    List<Event> events = Arrays.asList(event);
    final String moment = "morgen";

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals("<speak>Folgende Termine stehen " + moment + " an:<break time=\"500ms\"/>"
        + "Morgen: " + event.getSummary() + "</speak>", result.getSsml());
  }

  @Test
  public void readEvents_singleEventTomorrowIncludesTime() {
    //given
    final DateTime start = DateTime.now().plusDays(1);
    final Event event = new Event();
    event.setStart(start, true);
    event.setSummary("<summary>");

    final List<Event> events = Arrays.asList(event);
    final String moment = "morgen";

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals("<speak>Folgende Termine stehen " + moment + " an:<break time=\"500ms\"/>"
        + "Morgen um " + start.toString("HH:mm") + " Uhr: " + event.getSummary() + "</speak>", result.getSsml());
  }

  @Test
  public void readEvents_singleEventTomorrowDuration() {
    //given
    final DateTime start = DateTime.now().plusDays(1);
    final DateTime end = start.plusHours(1).plusMinutes(1);
    final Event event = new Event();
    event.setStart(start, true);
    event.setEnd(end, true);
    event.setSummary("<summary>");

    final List<Event> events = Arrays.asList(event);
    final String moment = "morgen";

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals("<speak>Folgende Termine stehen " + moment + " an:<break time=\"500ms\"/>"
        + "Morgen von " + start.toString("HH:mm") + " Uhr bis " + end.toString("HH:mm") + " Uhr: " + event.getSummary() + "</speak>", result.getSsml());
  }

  @Test
  public void readEvents_singleEventNextWeekNoTime() {
    //given
    final DateTime start = DateTime.now().plusWeeks(1);
    final Event event = new Event();
    event.setStart(start, false);
    event.setSummary("<summary>");

    List<Event> events = Arrays.asList(event);
    final String moment = "morgen";

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals(String.format("<speak>Folgende Termine stehen %s an:<break time=\"500ms\"/>Am %s, den <say-as interpret-as=\"date\" format=\"dm\">%s</say-as>: %s</speak>",
        moment,
        start.toString("EEEE", aLocale),
        start.toString("dd.MM."),
        event.getSummary()
        ), result.getSsml());
  }

  @Test
  public void readEvents_singleEventNextWeekIncludesTime() {
    //given
    final DateTime start = DateTime.now().plusWeeks(1);
    final Event event = new Event();
    event.setStart(start, true);
    event.setSummary("<summary>");

    final List<Event> events = Arrays.asList(event);
    final String moment = "morgen";

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals(String.format("<speak>Folgende Termine stehen %s an:<break time=\"500ms\"/>Am %s, den <say-as interpret-as=\"date\" format=\"dm\">%s</say-as> um %s Uhr: %s</speak>",
        moment,
        start.toString("EEEE", aLocale),
        start.toString("dd.MM."),
        start.toString("HH:mm"),
        event.getSummary()
    ), result.getSsml());
  }

  @Test
  public void readEvents_singleEventNextWeekDuration() {
    //given
    final DateTime start = DateTime.now().plusWeeks(1);
    final DateTime end = start.plusHours(1).plusMinutes(1);
    final Event event = new Event();
    event.setStart(start, true);
    event.setEnd(end, true);
    event.setSummary("<summary>");

    final List<Event> events = Arrays.asList(event);
    final String moment = "morgen";

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.readEvents(aLocale, moment, events);

    //then
    assertEquals(String.format("<speak>Folgende Termine stehen %s an:<break time=\"500ms\"/>Am %s, den <say-as interpret-as=\"date\" format=\"dm\">%s</say-as> von %s Uhr bis %s Uhr: %s</speak>",
        moment,
        start.toString("EEEE", aLocale),
        start.toString("dd.MM."),
        start.toString("HH:mm"),
        end.toString("HH:mm"),
        event.getSummary()
    ), result.getSsml());
  }

  @Test
  public void confirmNewEvent_sameDay() {
    //given
    final DateTime from = DateTime.parse("2010-08-13T20:15");
    final DateTime to = from.plusHours(1);

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.confirmNewEvent(from, to, aLocale);

    //then
    assertEquals(String.format("<speak>Kann ich den Termin am %s, den <say-as interpret-as=\"date\" format=\"dmy\">%s</say-as> von %s Uhr bis %s Uhr speichern?</speak>",
      "Freitag", "13.08.2010", "20:15", "21:15"
    ), result.getSsml());
  }

  @Test
  public void confirmNewEvent_differentDay() {
    //given
    final DateTime from = DateTime.parse("2010-08-13T20:15");
    final DateTime to = from.plusHours(1).plusDays(1);

    //when
    final SsmlOutputSpeech result = (SsmlOutputSpeech)toTest.confirmNewEvent(from, to, aLocale);

    //then
    assertEquals(String.format("<speak>Kann ich den Termin von %s, den <say-as interpret-as=\"date\" format=\"dmy\">%s</say-as> um %s Uhr bis %s, den <say-as interpret-as=\"date\" format=\"dm\">%s</say-as> um %s Uhr speichern?</speak>",
        "Freitag", "13.08.2010", "20:15", "Samstag", "14.08.2010", "21:15"
    ), result.getSsml());
  }

  @Test
  public void speechNewEventSaved(){
    //given
    //when
    final PlainTextOutputSpeech result = (PlainTextOutputSpeech)toTest.speechNewEventSaved(aLocale);

    //then
    assertEquals("OK. Ich habe den Termin gespeichert.", result.getText());
  }

  @Test
  public void speechGeneralConfirmation(){
    //given
    //when
    final PlainTextOutputSpeech result = (PlainTextOutputSpeech)toTest.speechGeneralConfirmation(aLocale);

    //then
    assertEquals("OK.", result.getText());
  }
}
