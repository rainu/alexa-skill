package de.rainu.alexa.cloud.calendar.model;

import java.util.AbstractMap.SimpleEntry;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joda.time.DateTime;

public enum Moment {
  MONDAY (
      () -> next(DateTime.now(), 1).withTimeAtStartOfDay(),
      () -> next(DateTime.now(), 1).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "diesen montag")),

  TUESDAY (
      () -> next(DateTime.now(), 2).withTimeAtStartOfDay(),
      () -> next(DateTime.now(), 2).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "diesen dienstag")),

  WEDNESDAY (
      () -> next(DateTime.now(), 3).withTimeAtStartOfDay(),
      () -> next(DateTime.now(), 3).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "diesen mittwoch")),

  THURSDAY (
      () -> next(DateTime.now(), 4).withTimeAtStartOfDay(),
      () -> next(DateTime.now(), 4).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "diesen donnerstag")),

  FRIDAY (
      () -> next(DateTime.now(), 5).withTimeAtStartOfDay(),
      () -> next(DateTime.now(), 5).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "diesen freitag")),

  SATURDAY (
      () -> next(DateTime.now(), 6).withTimeAtStartOfDay(),
      () -> next(DateTime.now(), 6).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "diesen samstag")),

  SUNDAY (
      () -> next(DateTime.now(), 7).withTimeAtStartOfDay(),
      () -> next(DateTime.now(), 7).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "diesen sonntag")),

  NEXT_MONDAY (
      () -> next(DateTime.now().plusWeeks(1), 1).withTimeAtStartOfDay(),
      () -> next(DateTime.now().plusWeeks(1), 1).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "nächsten montag")),

  NEXT_TUESDAY (
      () -> next(DateTime.now().plusWeeks(1), 2).withTimeAtStartOfDay(),
      () -> next(DateTime.now().plusWeeks(1), 2).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "nächsten dienstag")),

  NEXT_WEDNESDAY (
      () -> next(DateTime.now().plusWeeks(1), 3).withTimeAtStartOfDay(),
      () -> next(DateTime.now().plusWeeks(1), 3).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "nächsten mittwoch")),

  NEXT_THURSDAY (
      () -> next(DateTime.now().plusWeeks(1), 4).withTimeAtStartOfDay(),
      () -> next(DateTime.now().plusWeeks(1), 4).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "nächsten donnerstag")),

  NEXT_FRIDAY (
      () -> next(DateTime.now().plusWeeks(1), 5).withTimeAtStartOfDay(),
      () -> next(DateTime.now().plusWeeks(1), 5).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "nächsten freitag")),

  NEXT_SATURDAY (
      () -> next(DateTime.now().plusWeeks(1), 6).withTimeAtStartOfDay(),
      () -> next(DateTime.now().plusWeeks(1), 6).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "nächsten samstag")),

  NEXT_SUNDAY (
      () -> next(DateTime.now().plusWeeks(1), 7).withTimeAtStartOfDay(),
      () -> next(DateTime.now().plusWeeks(1), 7).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "nächsten sonntag")),

  TODAY(
      () -> DateTime.now(),
      () -> DateTime.now().plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "heute")),

  TOMORROW(
      () -> DateTime.now().plusDays(1).withTimeAtStartOfDay(),
      () -> DateTime.now().plusDays(2).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "morgen")),

  OVERMORROW(
      () -> DateTime.now().plusDays(2).withTimeAtStartOfDay(),
      () -> DateTime.now().plusDays(3).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "übermorgen")),

  THIS_WEEK(
      () -> DateTime.now(),
      () -> DateTime.now().withDayOfWeek(7).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "diese woche")),

  NEXT_WEEK(
      () -> DateTime.now().plusWeeks(1).withDayOfWeek(1).withTimeAtStartOfDay(),
      () -> DateTime.now().withDayOfWeek(7).plusWeeks(1).plusDays(1).withTimeAtStartOfDay(),
      new SimpleEntry(Locale.GERMAN, "nächste woche"));

  private final Map<Locale, String> names;

  private final Supplier<DateTime> from;

  private final Supplier<DateTime> to;
  Moment(Supplier<DateTime> from, Supplier<DateTime> to, Entry<Locale, String>...names){
    this.from = from;
    this.to = to;
    this.names = Stream.of(names).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  public DateTime getFrom() {
    DateTime dateTime = from.get();
    return dateTime;
  }

  public DateTime getTo() {
    DateTime dateTime = to.get();

    return dateTime;
  }

  public String getName(Locale locale) {
    final Locale languageLocale = Locale.forLanguageTag(locale.getLanguage());

    return names.get(languageLocale);
  }

  static DateTime next(DateTime time, int targetDay) {
    if(time.getDayOfWeek() == targetDay) return time.plusDays(7);

    while(true){
      if(time.getDayOfWeek() == targetDay) {
        return time;
      }

      time = time.plusDays(1);
    }
  }

  public static Moment getForLocale(Locale locale, String moment){
    final Locale languageLocale = Locale.forLanguageTag(locale.getLanguage());
    for(Moment curMoment : Moment.values()) {
      if(moment.equals(curMoment.names.get(languageLocale))) {
        return curMoment;
      }
    }

    return null;
  }
}
