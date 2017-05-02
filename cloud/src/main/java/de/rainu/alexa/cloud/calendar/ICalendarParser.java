package de.rainu.alexa.cloud.calendar;

import biweekly.Biweekly;
import biweekly.component.VEvent;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ICalendarParser {

  public List<VEvent> parseEvents(List<String> rawEvents) {
    List<VEvent> events = rawEvents.stream()
        .flatMap(raw -> parseEvent(raw).stream())
        .collect(Collectors.toList());

    return events;
  }

  public List<VEvent> parseEvent(String rawEvent) {
    List<VEvent> events = Biweekly.parse(rawEvent).all().stream()
        .map(calendar -> calendar.getEvents())
        .flatMap(e -> e.stream())
        .collect(Collectors.toList());

    return events;
  }
}
