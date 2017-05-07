package de.rainu.alexa.cloud.calendar.service;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biweekly.component.VEvent;
import biweekly.util.com.google.ical.values.DateTimeValueImpl;
import de.rainu.alexa.cloud.calendar.CalendarCLIAdapter;
import de.rainu.alexa.cloud.calendar.EventMapper;
import de.rainu.alexa.cloud.calendar.ICalendarParser;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
import de.rainu.alexa.cloud.calendar.exception.CalendarWriteException;
import de.rainu.alexa.cloud.calendar.model.Event;
import de.rainu.alexa.cloud.config.CalendarConfiguration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class CalendarServiceTest {

  @Mock
  ICalendarParser parser;

  @Spy
  EventMapper mapper;

  CalendarService toTest;

  @Before
  public void setup(){
    toTest = new CalendarService();
    ReflectionTestUtils.setField(toTest, "parser", parser);
    ReflectionTestUtils.setField(toTest, "mapper", mapper);
    ReflectionTestUtils.setField(toTest, "executor", Executors.newFixedThreadPool(5));

    toTest = spy(toTest);
  }

  @Test
  public void getNextEvents() throws CalendarReadException {
    //given
    final List<Event> events = new ArrayList<>();
    doReturn(events).when(toTest).getEvents(any(), any());

    final DateTime now = DateTime.now();

    //when
    final List<Event> result = toTest.getNextEvents();

    //then
    ArgumentCaptor<DateTime> timeCap = ArgumentCaptor.forClass(DateTime.class);

    assertSame(events, result);
    verify(toTest, times(1)).getEvents(timeCap.capture(), timeCap.capture());

    assertTrue(timeCap.getAllValues().get(0).getMillis() - now.getMillis() <= 1000);
    assertEquals(
        now.plusDays(1).plusWeeks(1).withTimeAtStartOfDay().toString(),
        timeCap.getAllValues().get(1).toString());
  }

  @Test
  public void getEvents_noCalendars() throws CalendarReadException {
    //given
    doReturn(null).when(toTest).getCalendars();

    //when
    final List<Event> result = toTest.getEvents(DateTime.now(), DateTime.now().plusDays(1));

    //then
    assertTrue(result.isEmpty());
  }

  @Test
  public void getEvents() throws IOException, CalendarReadException {
    //given
    final CalendarCLIAdapter calendarOne = mock(CalendarCLIAdapter.class);
    final CalendarCLIAdapter calendarTwo = mock(CalendarCLIAdapter.class);
    final Map<String, CalendarCLIAdapter> calendars = new HashMap<>();
    calendars.put("one", calendarOne);
    calendars.put("two", calendarTwo);

    doReturn(TimeZone.getTimeZone("UTC")).when(calendarOne).getDefaultTimeZone();
    doReturn(TimeZone.getTimeZone("Europe/Berlin")).when(calendarTwo).getDefaultTimeZone();
    when(calendarOne.readAgenda(any(), any()))
        .thenReturn(Arrays.asList("<cal1-event1>", "<cal1-event2>"));
    when(calendarTwo.readAgenda(any(), any()))
        .thenReturn(Arrays.asList("<cal2-event1>"));
    doReturn(calendars).when(toTest).getCalendars();

    List<VEvent> cal1Event1 = Arrays.asList(e("cal1e1", DateTime.parse("2000-01-01")));
    List<VEvent> cal1Event2 = Arrays.asList(e("cal1e2", DateTime.parse("2010-01-01")), e("cal1e3", DateTime.parse("2000-01-02")));
    List<VEvent> cal2Event1 = Arrays.asList(e("cal2e1", DateTime.parse("2000-01-01")));

    doReturn(cal1Event1).when(parser).parseEvent(eq("<cal1-event1>"));
    doReturn(cal1Event2).when(parser).parseEvent(eq("<cal1-event2>"));
    doReturn(cal2Event1).when(parser).parseEvent(eq("<cal2-event1>"));

    //when
    final DateTime from = DateTime.now();
    final DateTime to = DateTime.now().plusDays(1);

    final List<Event> results = toTest.getEvents(from, to);

    //then
    verify(calendarOne, times(1)).readAgenda(from, to);
    verify(calendarTwo, times(1)).readAgenda(from, to);
    assertEquals("cal1e1", results.get(0).getSummary());
    assertEquals("cal2e1", results.get(1).getSummary());
    assertEquals("cal1e3", results.get(2).getSummary());
    assertEquals("cal1e2", results.get(3).getSummary());
  }

  VEvent e(String title, DateTime start) {
    VEvent event = new VEvent();
    event.setSummary(title);
    event.setDateStart(start.toDate());
    return event;
  }

  @Test
  public void createEvent_defaultCalendar() throws CalendarWriteException, IOException {
    //given
    final String summary = "<summary>";
    final DateTime from = DateTime.now();
    final DateTime to = from.plusHours(1);

    Map<String, CalendarCLIAdapter> calendar = new HashMap<>();
    calendar.put("default", mock(CalendarCLIAdapter.class));
    calendar.put("sec", mock(CalendarCLIAdapter.class));

    CalendarConfiguration.NAME_OF_DEFAULT_CALENDAR = "default";
    doReturn(calendar).when(toTest).getCalendars();
    doReturn("<uid-def>").when(calendar.get("default")).createEvent(any(), any(), any());
    doReturn("<uid-sec>").when(calendar.get("sec")).createEvent(any(), any(), any());

    //when
    final String result = toTest.createEvent(null, summary, from, to);

    //then
    assertEquals("<uid-def>", result);
    verify(calendar.get("default"), times(1)).createEvent(
        same(summary), same(from), same(to)
    );
    verify(calendar.get("sec"), never()).createEvent(any(), any(), any());
  }

  @Test
  public void createEvent_customCalendar() throws CalendarWriteException, IOException {
    //given
    final String summary = "<summary>";
    final DateTime from = DateTime.now();
    final DateTime to = from.plusHours(1);

    Map<String, CalendarCLIAdapter> calendar = new HashMap<>();
    calendar.put("default", mock(CalendarCLIAdapter.class));
    calendar.put("sec", mock(CalendarCLIAdapter.class));

    CalendarConfiguration.NAME_OF_DEFAULT_CALENDAR = "default";
    doReturn(calendar).when(toTest).getCalendars();
    doReturn("<uid-def>").when(calendar.get("default")).createEvent(any(), any(), any());
    doReturn("<uid-sec>").when(calendar.get("sec")).createEvent(any(), any(), any());

    //when
    final String result = toTest.createEvent("sec", summary, from, to);

    //then
    assertEquals("<uid-sec>", result);
    verify(calendar.get("sec"), times(1)).createEvent(
        same(summary), same(from), same(to)
    );
    verify(calendar.get("default"), never()).createEvent(any(), any(), any());
  }

  @Test(expected = CalendarWriteException.class)
  public void createEvent_calendarThrowsException() throws CalendarWriteException, IOException {
    //given
    final String summary = "<summary>";
    final DateTime from = DateTime.now();
    final DateTime to = from.plusHours(1);

    Map<String, CalendarCLIAdapter> calendar = new HashMap<>();
    calendar.put("default", mock(CalendarCLIAdapter.class));

    CalendarConfiguration.NAME_OF_DEFAULT_CALENDAR = "default";
    doReturn(calendar).when(toTest).getCalendars();
    doThrow(new IOException()).when(calendar.get("default")).createEvent(any(), any(), any());

    //when
    toTest.createEvent(null, summary, from, to);
  }
}
