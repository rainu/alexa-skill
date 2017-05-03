package de.rainu.alexa.cloud.calendar.model;

import static org.junit.Assert.assertEquals;

import java.util.Locale;
import org.joda.time.DateTime;
import org.junit.Test;

public class MomentTest {

  @Test
  public void endOfWeek() {
    assertEquals(7, Moment.endOfWeek(DateTime.now()).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(2)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(3)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(4)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(5)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(6)).getDayOfWeek());
    assertEquals(7, Moment.endOfWeek(DateTime.now().withDayOfWeek(7)).getDayOfWeek());
  }

  @Test
  public void next_monday() {
    assertEquals(
        DateTime.parse("2017-05-01").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-25"), 1).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-01").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-26"), 1).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-01").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-27"), 1).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-01").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-28"), 1).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-01").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-29"), 1).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-01").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-30"), 1).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-08").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-01"), 1).withTimeAtStartOfDay());
  }

  @Test
  public void next_tuesday() {
    assertEquals(
        DateTime.parse("2017-05-02").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-26"), 2).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-02").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-27"), 2).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-02").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-28"), 2).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-02").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-29"), 2).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-02").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-30"), 2).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-02").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-01"), 2).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-09").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-02"), 2).withTimeAtStartOfDay());
  }

  @Test
  public void next_wednesday() {
    assertEquals(
        DateTime.parse("2017-05-03").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-27"), 3).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-03").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-28"), 3).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-03").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-29"), 3).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-03").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-30"), 3).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-03").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-01"), 3).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-03").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-02"), 3).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-10").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-03"), 3).withTimeAtStartOfDay());
  }

  @Test
  public void next_thursday() {
    assertEquals(
        DateTime.parse("2017-05-04").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-28"), 4).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-04").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-29"), 4).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-04").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-30"), 4).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-04").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-01"), 4).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-04").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-02"), 4).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-04").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-03"), 4).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-11").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-04"), 4).withTimeAtStartOfDay());
  }

  @Test
  public void next_friday() {
    assertEquals(
        DateTime.parse("2017-05-05").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-29"), 5).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-05").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-30"), 5).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-05").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-01"), 5).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-05").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-02"), 5).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-05").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-03"), 5).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-05").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-04"), 5).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-12").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-05"), 5).withTimeAtStartOfDay());
  }

  @Test
  public void next_saturday() {
    assertEquals(
        DateTime.parse("2017-05-06").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-04-30"), 6).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-06").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-01"), 6).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-06").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-02"), 6).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-06").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-03"), 6).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-06").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-04"), 6).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-06").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-05"), 6).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-13").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-06"), 6).withTimeAtStartOfDay());
  }

  @Test
  public void next_sunday() {
    assertEquals(
        DateTime.parse("2017-05-07").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-01"), 7).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-07").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-02"), 7).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-07").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-03"), 7).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-07").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-04"), 7).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-07").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-05"), 7).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-07").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-06"), 7).withTimeAtStartOfDay());
    assertEquals(
        DateTime.parse("2017-05-14").withTimeAtStartOfDay(),
        Moment.next(DateTime.parse("2017-05-07"), 7).withTimeAtStartOfDay());
  }

  @Test
  public void getForLocale_german(){
    assertEquals(Moment.TODAY, Moment.getForLocale(Locale.GERMAN, "heute"));
    assertEquals(Moment.TOMORROW, Moment.getForLocale(Locale.GERMAN, "morgen"));
    assertEquals(Moment.OVERMORROW, Moment.getForLocale(Locale.GERMAN, "übermorgen"));
    assertEquals(Moment.THIS_WEEK, Moment.getForLocale(Locale.GERMAN, "diese woche"));
    assertEquals(Moment.NEXT_WEEK, Moment.getForLocale(Locale.GERMAN, "nächste woche"));

    assertEquals(Moment.MONDAY, Moment.getForLocale(Locale.GERMAN, "montag"));
    assertEquals(Moment.TUESDAY, Moment.getForLocale(Locale.GERMAN, "dienstag"));
    assertEquals(Moment.WEDNESDAY, Moment.getForLocale(Locale.GERMAN, "mittwoch"));
    assertEquals(Moment.THURSDAY, Moment.getForLocale(Locale.GERMAN, "donnerstag"));
    assertEquals(Moment.FRIDAY, Moment.getForLocale(Locale.GERMAN, "freitag"));
    assertEquals(Moment.SATURDAY, Moment.getForLocale(Locale.GERMAN, "samstag"));
    assertEquals(Moment.SUNDAY, Moment.getForLocale(Locale.GERMAN, "sonntag"));

    assertEquals(Moment.NEXT_MONDAY, Moment.getForLocale(Locale.GERMAN, "nächsten montag"));
    assertEquals(Moment.NEXT_TUESDAY, Moment.getForLocale(Locale.GERMAN, "nächsten dienstag"));
    assertEquals(Moment.NEXT_WEDNESDAY, Moment.getForLocale(Locale.GERMAN, "nächsten mittwoch"));
    assertEquals(Moment.NEXT_THURSDAY, Moment.getForLocale(Locale.GERMAN, "nächsten donnerstag"));
    assertEquals(Moment.NEXT_FRIDAY, Moment.getForLocale(Locale.GERMAN, "nächsten freitag"));
    assertEquals(Moment.NEXT_SATURDAY, Moment.getForLocale(Locale.GERMAN, "nächsten samstag"));
    assertEquals(Moment.NEXT_SUNDAY, Moment.getForLocale(Locale.GERMAN, "nächsten sonntag"));
  }
}
