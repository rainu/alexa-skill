package de.rainu.alexa.cloud.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

public class CalendarCLIAdapterTest {

  static final String CALDAV_URL = "<caldav-url>";
  static final String CALDAV_USER = "<caldav-user>";
  static final String CALDAV_PASSWORD = "<caldav-password>";
  static final String CALENDAR_URL = "<calendar-url>";

  CalendarCLIAdapter toTest;

  @Before
  public void setup() throws IOException {
    toTest =  new CalendarCLIAdapter(
        CALDAV_URL, CALDAV_USER, CALDAV_PASSWORD, CALENDAR_URL
    );

    toTest = spy(toTest);
    doNothing().when(toTest).doExecute(any(), any());
  }

  @Test
  public void readAgenda() throws IOException {
    //given
    final DateTime from = DateTime.now().minusDays(1);
    final DateTime to = from.plusDays(2);

    //when
    List<String> result = toTest.readAgenda(from, to);

    //then
    assertTrue(result.isEmpty());

    ArgumentCaptor<CommandLine> cmdCap = ArgumentCaptor.forClass(CommandLine.class);
    verify(toTest, times(1)).doExecute(cmdCap.capture(), any());

    assertEquals("calendar-cli.py", cmdCap.getValue().getExecutable());
    assertEquals(15, cmdCap.getValue().getArguments().length);
    assertEquals("--caldav-url", cmdCap.getValue().getArguments()[0]);
    assertEquals(CALDAV_URL, cmdCap.getValue().getArguments()[1]);
    assertEquals("--caldav-user", cmdCap.getValue().getArguments()[2]);
    assertEquals(CALDAV_USER, cmdCap.getValue().getArguments()[3]);
    assertEquals("--caldav-pass", cmdCap.getValue().getArguments()[4]);
    assertEquals(CALDAV_PASSWORD, cmdCap.getValue().getArguments()[5]);
    assertEquals("--calendar-url", cmdCap.getValue().getArguments()[6]);
    assertEquals(CALENDAR_URL, cmdCap.getValue().getArguments()[7]);
    assertEquals("--icalendar", cmdCap.getValue().getArguments()[8]);
    assertEquals("calendar", cmdCap.getValue().getArguments()[9]);
    assertEquals("agenda", cmdCap.getValue().getArguments()[10]);
    assertEquals("--from-time", cmdCap.getValue().getArguments()[11]);
    assertEquals(from.toString(), cmdCap.getValue().getArguments()[12]);
    assertEquals("--to-time", cmdCap.getValue().getArguments()[13]);
    assertEquals(to.toString(), cmdCap.getValue().getArguments()[14]);
  }

  @Test(expected = IOException.class)
  public void readAgenda_processError() throws IOException {
    //given
    final DateTime from = DateTime.now().minusDays(1);
    final DateTime to = from.plusDays(2);

    doThrow(new IOException()).when(toTest).doExecute(any(), any());

    //when
    toTest.readAgenda(from, to);
  }

  @Test
  public void createEvent() throws IOException {
    //given
    final String summary = "<summary>";
    final DateTime from = DateTime.parse("2010-08-13T20:15:00");
    final DateTime to = from.plusDays(1).plusHours(1).plusMinutes(1);

    doAnswer((inv) -> {
      OutputStream os = (OutputStream)ReflectionTestUtils.getField(
          ((DefaultExecutor)inv.getArguments()[1]).getStreamHandler(),
          "out");
      os.write("Added event with uid=578ee4a3-3330-11e7-a7d0-0242ac130006\n".getBytes());

      return null;
    }).when(toTest).doExecute(any(), any());

    //when
    final String result = toTest.createEvent(summary, from, to);

    //then
    assertEquals("578ee4a3-3330-11e7-a7d0-0242ac130006", result);

    ArgumentCaptor<CommandLine> cmdCap = ArgumentCaptor.forClass(CommandLine.class);
    verify(toTest, times(1)).doExecute(cmdCap.capture(), any());

    assertEquals("--caldav-url", cmdCap.getValue().getArguments()[0]);
    assertEquals(CALDAV_URL, cmdCap.getValue().getArguments()[1]);
    assertEquals("--caldav-user", cmdCap.getValue().getArguments()[2]);
    assertEquals(CALDAV_USER, cmdCap.getValue().getArguments()[3]);
    assertEquals("--caldav-pass", cmdCap.getValue().getArguments()[4]);
    assertEquals(CALDAV_PASSWORD, cmdCap.getValue().getArguments()[5]);
    assertEquals("--calendar-url", cmdCap.getValue().getArguments()[6]);
    assertEquals(CALENDAR_URL, cmdCap.getValue().getArguments()[7]);
    assertEquals("calendar", cmdCap.getValue().getArguments()[8]);
    assertEquals("add", cmdCap.getValue().getArguments()[9]);
    assertEquals("2010-08-13T20:15+1501m", cmdCap.getValue().getArguments()[10]);
    assertEquals(summary, cmdCap.getValue().getArguments()[11]);
  }
}
