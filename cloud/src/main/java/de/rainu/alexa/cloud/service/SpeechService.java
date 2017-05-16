package de.rainu.alexa.cloud.service;

import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import de.rainu.alexa.cloud.calendar.exception.AlexaExcpetion;
import de.rainu.alexa.cloud.calendar.model.Event;
import java.util.List;
import java.util.Locale;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpeechService {

  private static final String DATE_FORMAT = "dd.MM.";
  private static final String DATE_YEAR_FORMAT = "dd.MM.yyyy";
  private static final String DAY_FORMAT = "EEEE";
  private static final String TIME_FORMAT = "HH:mm";

  @Autowired
  private MessageService messageService;

  public OutputSpeech speechWelcomeMessage(Locale locale) {
    final String speechText = messageService.de("welcome");
    return speechMessage(speechText);
  }

  public OutputSpeech speechBye(Locale locale) {
    final String speechText = messageService.de("bye");
    return speechMessage(speechText);
  }

  public OutputSpeech speechHelpMessage(Locale locale) {
    final String speechText = messageService.de("help");
    return speechMessage(speechText);
  }

  public OutputSpeech speechGeneralConfirmation(Locale locale) {
    final String speechText = messageService.de("confirm");
    return speechMessage(speechText);
  }

  public OutputSpeech readEvents(Locale locale, List<Event> events) {
    return readEvents(locale, "", events);
  }

  public OutputSpeech readEvents(Locale locale, String moment, List<Event> events) {
    if (events.isEmpty()) {
      final String speechText = messageService.de("event.nothing", moment);
      return speechMessage(speechText);
    }

    StringBuilder sb = new StringBuilder(messageService.de("event.listing.start", moment));
    for (Event event : events) {
      sb.append("<break time=\"500ms\"/>");
      sb.append(generateSpeechText(locale, event));
    }

    return speechMessage(sb.toString());
  }

  private String generateSpeechText(Locale locale, Event event) {
    if (isToday(event)) {
      return generateSpeechTextForToday(locale, event);
    }
    if (isTomorrow(event)) {
      return generateSpeechTextForTomorrow(locale, event);
    }
    return generateSpeechTextForDate(locale, event);
  }

  private boolean isToday(Event event) {
    DateTime start = event.getStart();
    DateTime now = DateTime.now();

    return start.getDayOfYear() == now.getDayOfYear() && start.getYear() == now.getYear();
  }

  private boolean isTomorrow(Event event) {
    DateTime start = event.getStart();
    DateTime tomorrow = DateTime.now().plusDays(1);

    return start.getDayOfYear() == tomorrow.getDayOfYear() && start.getYear() == tomorrow.getYear();
  }

  private String generateSpeechTextForToday(Locale locale, Event event) {
    if (event.startHasTime()) {
      final DateTime from = event.getStart();

      if (event.getEnd() != null && event.endHasTime()) {
        final DateTime to = event.getEnd();

        return messageService.de("event.item.today.time.duration",
            from.toString(TIME_FORMAT),
            to.toString(TIME_FORMAT),
            event.getSummary());
      }

      return messageService.de("event.item.today.time",
          from.toString(TIME_FORMAT),
          event.getSummary());
    }

    return messageService.de("event.item.today",
        event.getSummary());
  }

  private String generateSpeechTextForTomorrow(Locale locale, Event event) {
    if (event.startHasTime()) {
      final DateTime from = event.getStart();

      if (event.getEnd() != null && event.endHasTime()) {
        final DateTime to = event.getEnd();

        return messageService.de("event.item.tomorrow.time.duration",
            from.toString(TIME_FORMAT),
            to.toString(TIME_FORMAT),
            event.getSummary());
      }

      return messageService.de("event.item.tomorrow.time",
          from.toString(TIME_FORMAT),
          event.getSummary());
    }

    return messageService.de("event.item.tomorrow",
        event.getSummary());
  }

  private String generateSpeechTextForDate(Locale locale, Event event) {
    final DateTime from = event.getStart();

    if (event.startHasTime()) {
      if (event.getEnd() != null && event.endHasTime()) {
        final DateTime to = event.getEnd();

        return messageService.de("event.item.date.time.duration",
            from.toString(DAY_FORMAT, locale),
            from.toString(DATE_FORMAT),
            from.toString(TIME_FORMAT),
            to.toString(TIME_FORMAT),
            event.getSummary());
      }

      return messageService.de("event.item.date.time",
          from.toString(DAY_FORMAT, locale),
          from.toString(DATE_FORMAT),
          from.toString(TIME_FORMAT),
          event.getSummary());
    }

    return messageService.de("event.item.date",
        from.toString(DAY_FORMAT, locale),
        from.toString(DATE_FORMAT),
        event.getSummary());
  }

  public OutputSpeech speechCancelNewEvent(Locale locale) {
    final String speechText = messageService.de("event.new.cancel");
    return speechMessage(speechText);
  }

  public OutputSpeech speechError(Throwable t) {
    final String speechText;
    if (t instanceof AlexaExcpetion) {
      speechText = messageService.de(((AlexaExcpetion)t).getMessageKey());
    } else {
      speechText = messageService.de("event.error");
    }

    return speechMessage(speechText);
  }

  private OutputSpeech speechMessage(String speechText) {
    // Create the plain text output.
    if (speechText.contains("<")) {
      SsmlOutputSpeech speech = new SsmlOutputSpeech();

      final String targetSpeech = "<speak>" + speechText + "</speak>";
      speech.setSsml(targetSpeech);

      return speech;
    } else {
      PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
      speech.setText(speechText);

      return speech;
    }
  }

  public OutputSpeech confirmNewEvent(String title, DateTime from, DateTime to, Locale locale) {
    final String speechText;
    if(from.getYear() == to.getYear() && from.getDayOfYear() == to.getDayOfYear()) {
      speechText = messageService.de("event.new.confirm.sameday",
          from.toString(DAY_FORMAT, locale),
          from.toString(DATE_YEAR_FORMAT),
          from.toString(TIME_FORMAT),
          to.toString(TIME_FORMAT),
          title);
    } else {
      speechText = messageService.de("event.new.confirm",
          from.toString(DAY_FORMAT, locale),
          from.toString(DATE_YEAR_FORMAT),
          from.toString(TIME_FORMAT),
          to.toString(DAY_FORMAT, locale),
          to.toString(DATE_YEAR_FORMAT),
          to.toString(TIME_FORMAT),
          title);
    }

    return speechMessage(speechText);
  }

  public OutputSpeech speechNewEventSaved(Locale locale) {
    final String speechText = messageService.de("event.new.saved");
    return speechMessage(speechText);
  }

  public OutputSpeech speechConnectWithCalendar(String calendarName, Locale locale) {
    final String speechText = messageService.de("event.connect.calendar", calendarName);
    return speechMessage(speechText);
  }
}
