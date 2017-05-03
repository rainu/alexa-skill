package de.rainu.alexa.cloud.calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import biweekly.component.VEvent;
import de.rainu.alexa.cloud.calendar.model.Event;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

public class EventMapperTest {

  EventMapper toTest = new EventMapper();

  @Test
  public void map() {
    //given
    final VEvent event = new VEvent();
    event.setDateStart(DateTime.parse("2009-12-15T22:15:00").withZone(DateTimeZone.UTC).toDate(), true);
    event.setDateEnd(DateTime.parse("2010-08-13T20:15:00").withZone(DateTimeZone.UTC).toDate(), true);
    event.setSummary("<summary>");
    event.setDescription("<description>");

    final TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");

    //when
    final Event result = toTest.map(event, tz);

    //then
    assertEquals(DateTime.parse("2009-12-15T22:15:00").withZone(DateTimeZone.forID("Europe/Berlin")).toString(), result.getStart().toString());
    assertEquals(DateTime.parse("2010-08-13T20:15:00").withZone(DateTimeZone.forID("Europe/Berlin")).toString(), result.getEnd().toString());
    assertEquals("<summary>", result.getSummary());
    assertEquals("<description>", result.getDescription());
  }

  @Test
  public void map_noTime() {
    //given
    final VEvent event = new VEvent();
    event.setDateStart(DateTime.parse("2009-12-15T22:15:00").withZone(DateTimeZone.UTC).toDate(), false);
    event.setDateEnd(DateTime.parse("2010-08-13T21:15:00").withZone(DateTimeZone.UTC).toDate(), false);
    event.setSummary("<summary>");
    event.setDescription("<description>");

    final TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");

    //when
    final Event result = toTest.map(event, tz);

    //then
    assertEquals(DateTime.parse("2009-12-15T00:00:00").withZone(DateTimeZone.forID("Europe/Berlin")).toString(), result.getStart().toString());
    assertEquals(DateTime.parse("2010-08-13T00:00:00").withZone(DateTimeZone.forID("Europe/Berlin")).toString(), result.getEnd().toString());
    assertEquals("<summary>", result.getSummary());
    assertEquals("<description>", result.getDescription());
  }

  @Test
  public void map_nullSave() {
    //given
    final VEvent event = new VEvent();
    final TimeZone tz = TimeZone.getTimeZone("Europe/Berlin");

    //when
    final Event result = toTest.map(event, tz);

    //then
    assertNull(result.getStart());
    assertNull(result.getEnd());
    assertNull(result.getSummary());
    assertNull(result.getDescription());
  }
}
