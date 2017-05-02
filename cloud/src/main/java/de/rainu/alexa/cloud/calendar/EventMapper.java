package de.rainu.alexa.cloud.calendar;

import biweekly.component.VEvent;
import de.rainu.alexa.cloud.calendar.model.Event;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

  public Event map(VEvent event, TimeZone defaultTimeZone) {
    Event result = new Event();

    nullSave(() -> {
      final DateTime start = new DateTime(event.getDateStart().getValue().getTime(), DateTimeZone.forTimeZone(defaultTimeZone));
      result.setStart(start, event.getDateStart().getValue().hasTime());
    });

    nullSave(() -> {
      final DateTime end = new DateTime(event.getDateEnd().getValue().getTime(), DateTimeZone.forTimeZone(defaultTimeZone));
      result.setEnd(end, event.getDateStart().getValue().hasTime());
    });

    nullSave(() -> result.setSummary(event.getSummary().getValue()));
    nullSave(() -> result.setDescription(event.getDescription().getValue()));

    return result;
  }

  private void nullSave(Runnable runnable) {
    try {
      runnable.run();
    }catch(NullPointerException e) {}
  }
}
