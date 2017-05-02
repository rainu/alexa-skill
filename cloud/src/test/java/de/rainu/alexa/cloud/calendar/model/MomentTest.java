package de.rainu.alexa.cloud.calendar.model;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.joda.time.DateTime;
import org.junit.Test;

public class MomentTest {

  @Test
  public void endOfWeek() {
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(1)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(2)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(3)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(4)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(5)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(6)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(7)).getDayOfWeek());
  }

  @Test
  public void getForLocale_german(){
    assertEquals(Moment.TODAY, Moment.getForLocale(Locale.GERMAN, "heute"));
    assertEquals(Moment.TOMORROW, Moment.getForLocale(Locale.GERMAN, "morgen"));
    assertEquals(Moment.THIS_WEEK, Moment.getForLocale(Locale.GERMAN, "diese woche"));
    assertEquals(Moment.THIS_WEEK, Moment.getForLocale(Locale.GERMAN, "die woche"));
    assertEquals(Moment.NEXT_WEEK, Moment.getForLocale(Locale.GERMAN, "nächste woche"));
  }
}
