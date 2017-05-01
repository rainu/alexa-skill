package de.rainu.alexa.cloud.calendar.service;

import biweekly.component.VEvent;
import de.rainu.alexa.cloud.calendar.CalendarCLIAdapter;
import de.rainu.alexa.cloud.calendar.ICalendarParser;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * This service is responsible for read events from the configured calendars.
 */
@Service
public class CalendarService {

  @Autowired
  private ApplicationContext context;
  private Map<String, CalendarCLIAdapter> calendars;

  @Autowired
  private ICalendarParser parser;

  /**
   * Gets a {@link List} of of next events (the current week - 7days).
   *
   * @return The List of found events.
   * @throws CalendarReadException If an error occurs while reading calendars.
   */
  public List<VEvent> getNextEvents() throws CalendarReadException {
    if(getCalendars() == null) return Collections.emptyList();

    try {
      List<String> rawEvents = getCalendars().values().stream().findAny().get().readAgenda(DateTime.now(), DateTime.now().plusWeeks(1));
      return parser.parseEvents(rawEvents);
    } catch (IOException e) {
      throw new CalendarReadException("Could not read calendar.", e);
    }
  }

  public Map<String, CalendarCLIAdapter> getCalendars(){
    if(calendars == null) {
      calendars = context.getBeansOfType(CalendarCLIAdapter.class);
    }

    return calendars;
  }
}
