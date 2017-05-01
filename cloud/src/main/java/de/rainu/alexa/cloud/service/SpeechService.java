package de.rainu.alexa.cloud.service;

import biweekly.component.VEvent;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
import java.util.List;
import java.util.Locale;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SpeechService {

  private static final String DATE_FORMAT = "dd.MM.";
  private static final String DAY_FORMAT = "EEEE";
  private static final String TIME_FORMAT = "HH:mm";

  @Autowired
  private MessageService messageService;

  public OutputSpeech speechWelcomeMessage() {
    final String speechText = messageService.de("welcome");
    return speechMessage(speechText);
  }

  public OutputSpeech speechHelpMessage() {
    final String speechText = messageService.de("help");
    return speechMessage(speechText);
  }

  public OutputSpeech readEvents(List<VEvent> events){
    return readEvents("", events);
  }

  public OutputSpeech readEvents(String moment, List<VEvent> events){
    if(events.isEmpty()) {
      final String speechText = messageService.de("event.nothing", moment);
      return speechMessage(speechText);
    }

    StringBuilder sb = new StringBuilder(messageService.de("event.start", moment));
    for(VEvent event : events) {
      sb.append("\n");
      sb.append(generateSpeechText(event));
    }

    return speechMessage(sb.toString());
  }

  private String generateSpeechText(VEvent event) {
    if(isToday(event)) {
      return generateSpeechTextForToday(event);
    }
    if(isTomorrow(event)) {
      return generateSpeechTextForTomorrow(event);
    }
    return generateSpeechTextForDate(event);
  }

  private boolean isToday(VEvent event) {
    DateTime start = getStartDate(event);
    DateTime now = DateTime.now();

    return start.getDayOfYear() == now.getDayOfYear() && start.getYear() == now.getYear();
  }

  private boolean isTomorrow(VEvent event) {
    DateTime start = getStartDate(event);
    DateTime tomorrow = DateTime.now().plusDays(1);

    return start.getDayOfYear() == tomorrow.getDayOfYear() && start.getYear() == tomorrow.getYear();
  }

  private DateTime getStartDate(VEvent event) {
    DateTime dateTime = new DateTime(event.getDateStart().getValue().getTime());

    return dateTime;
  }

  private DateTime getEndDate(VEvent event) {
    DateTime dateTime = new DateTime(event.getDateEnd().getValue().getTime());

    return dateTime;
  }

  private String generateSpeechTextForToday(VEvent event) {
    if(event.getDateStart().getValue().hasTime()) {
      final DateTime from = getStartDate(event);

      if(event.getDateEnd() != null && event.getDateEnd().getValue().hasTime()) {
        final DateTime to = getEndDate(event);

        return messageService.de("event.item.today.time.duration",
            from.toString(TIME_FORMAT),
            to.toString(TIME_FORMAT),
            event.getSummary().getValue());
      }

      return messageService.de("event.item.today.time",
          from.toString(TIME_FORMAT),
          event.getSummary().getValue());
    }

    return messageService.de("event.item.today",
        event.getSummary().getValue());
  }

  private String generateSpeechTextForTomorrow(VEvent event) {
    if(event.getDateStart().getValue().hasTime()) {
      final DateTime from = getStartDate(event);

      if(event.getDateEnd() != null && event.getDateEnd().getValue().hasTime()) {
        final DateTime to = getEndDate(event);

        return messageService.de("event.item.tomorrow.time.duration",
            from.toString(TIME_FORMAT),
            to.toString(TIME_FORMAT),
            event.getSummary().getValue());
      }

      return messageService.de("event.item.tomorrow.time",
          from.toString(TIME_FORMAT),
          event.getSummary().getValue());
    }

    return messageService.de("event.item.tomorrow",
        event.getSummary().getValue());
  }

  private String generateSpeechTextForDate(VEvent event) {
    final DateTime from = getStartDate(event);

    if(event.getDateStart().getValue().hasTime()) {
      if(event.getDateEnd() != null && event.getDateEnd().getValue().hasTime()) {
        final DateTime to = getEndDate(event);

        return messageService.de("event.item.date.time.duration",
            from.toString(DAY_FORMAT, Locale.GERMAN),
            from.toString(DATE_FORMAT),
            from.toString(TIME_FORMAT),
            to.toString(TIME_FORMAT),
            event.getSummary().getValue());
      }

      return messageService.de("event.item.date.time",
          from.toString(DAY_FORMAT, Locale.GERMAN),
          from.toString(DATE_FORMAT),
          from.toString(TIME_FORMAT),
          event.getSummary().getValue());
    }

    return messageService.de("event.item.date",
        from.toString(DAY_FORMAT, Locale.GERMAN),
        from.toString(DATE_FORMAT),
        event.getSummary().getValue());
  }

  public OutputSpeech speechError(CalendarReadException e) {
    final String speechText = messageService.de("event.error.read");
    return speechMessage(speechText);
  }

  private OutputSpeech speechMessage(String speechText) {
    // Create the plain text output.
    if(speechText.contains("<")) {
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
}
