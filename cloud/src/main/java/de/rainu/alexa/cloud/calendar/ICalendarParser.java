package de.rainu.alexa.cloud.calendar;

import biweekly.Biweekly;
import biweekly.component.VEvent;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ICalendarParser {

  public List<VEvent> parseEvents(List<String> rawEvents) {
    return rawEvents.stream()
        .map(raw -> Biweekly.parse(raw).all())
        .flatMap(calendars -> calendars.stream())
        .map(calendar -> calendar.getEvents())
        .flatMap(events -> events.stream())
        .collect(Collectors.toList());
  }
}
