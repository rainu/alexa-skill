package de.rainu.alexa.cloud.calendar.service;

import static de.rainu.alexa.cloud.config.ThreadPoolConfig.CALENDAR_CALL_POOL;

import de.rainu.alexa.cloud.calendar.CalendarCLIAdapter;
import de.rainu.alexa.cloud.calendar.EventMapper;
import de.rainu.alexa.cloud.calendar.ICalendarParser;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
import de.rainu.alexa.cloud.calendar.exception.CalendarWriteException;
import de.rainu.alexa.cloud.calendar.model.Event;
import de.rainu.alexa.cloud.config.CalendarConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

  @Autowired
  private EventMapper mapper;

  @Autowired
  @Qualifier(CALENDAR_CALL_POOL)
  private ExecutorService executor;

  /**
   * Gets a {@link List} of events.
   *
   * @param from
   * @param to
   * @return The List of found events.
   * @throws CalendarReadException If an error occurs while reading calendars.
   */
  public List<Event> getEvents(DateTime from, DateTime to) throws CalendarReadException {
    if(getCalendars() == null) return Collections.emptyList();

    try {
      List<Event> allEvents = new ArrayList<>();
      List<Future<List<Event>>> futures = new ArrayList<>(getCalendars().size());

      for(CalendarCLIAdapter calendar : getCalendars().values()) {
        Future<List<Event>> future = executor.submit(() -> {
          return calendar.readAgenda(from, to).stream()
              .flatMap(rawEvent -> parser.parseEvent(rawEvent).stream())
              .map(vEvent -> mapper.map(vEvent, calendar.getDefaultTimeZone()))
              .collect(Collectors.toList());
        });

        futures.add(future);
      }

      for(Future<List<Event>> future : futures) {
        allEvents.addAll(future.get());
      }
      allEvents.sort(Comparator.comparing(Event::getStart));

      return allEvents;
    } catch (Exception e) {
      throw new CalendarReadException("Could not read calendar.", e);
    }
  }

  /**
   * Gets a {@link List} of of next events (the current week - 7days).
   *
   * @return The List of found events.
   * @throws CalendarReadException If an error occurs while reading calendars.
   */
  public List<Event> getNextEvents() throws CalendarReadException {
    final DateTime from = DateTime.now();
    final DateTime to = from.plusWeeks(1).plusDays(1).withTimeAtStartOfDay();

    return getEvents(from, to);
  }

  /**
   * Create a new event.
   *
   * @param calendarName the target calendar. null means the default calendar (first created)
   * @param summary the summary of event
   * @param from Startdate of event
   * @param to Enddate of event
   * @return a uuid of the created events.
   * @throws CalendarWriteException If an error occurs while writing calendars.
   */
  public String createEvent(
      final String calendarName,
      final String summary,
      final DateTime from, final DateTime to) throws CalendarWriteException {

    final String targetCalendar = calendarName != null ? calendarName : CalendarConfiguration.NAME_OF_DEFAULT_CALENDAR;
    final CalendarCLIAdapter cli = getCalendars().get(targetCalendar);

    try {
      return cli.createEvent(summary, from, to);
    } catch (IOException e) {
      throw new CalendarWriteException("Could not write event into calendar '" + targetCalendar + "'.", e);
    }
  }

  Map<String, CalendarCLIAdapter> getCalendars(){
    if(calendars == null) {
      calendars = context.getBeansOfType(CalendarCLIAdapter.class);
    }

    return calendars;
  }
}
