package de.rainu.alexa.cloud.calendar.model;

import java.util.Locale;
import java.util.function.Supplier;
import org.joda.time.DateTime;

public enum Moment {
  TODAY(() -> DateTime.now().withTimeAtStartOfDay(), () -> DateTime.now().plusDays(1).withTimeAtStartOfDay()),
  TOMORROW(() -> DateTime.now().plusDays(1).withTimeAtStartOfDay(), () -> DateTime.now().plusDays(2).withTimeAtStartOfDay()),
  THIS_WEEK(() -> DateTime.now().withTimeAtStartOfDay(), () -> endOfWeek(DateTime.now().withTimeAtStartOfDay())),
  NEXT_WEEK(() -> DateTime.now().plusWeeks(1).withTimeAtStartOfDay(), () -> endOfWeek(DateTime.now().plusWeeks(1).withTimeAtStartOfDay()));

  private final Supplier<DateTime> from;
  private final Supplier<DateTime> to;

  Moment(Supplier<DateTime> from, Supplier<DateTime> to){
    this.from = from;
    this.to = to;
  }

  public DateTime getFrom() {
    return from.get();
  }

  public DateTime getTo() {
    return to.get();
  }

  static DateTime endOfWeek(DateTime time) {
    DateTime result = time.plusDays(7 - time.getDayOfWeek());
    return result;
  }

  public static Moment getForLocale(Locale locale, String moment){
    if(locale.getISO3Language().equals(Locale.GERMAN.getISO3Language())){
      return getForGerman(moment);
    }

    return null;
  }

  private static Moment getForGerman(String moment) {
    switch (moment.toLowerCase()) {
      case "heute": return TODAY;
      case "morgen": return TOMORROW;
      case "die woche": //fall through
      case "diese woche": return THIS_WEEK;
      case "nächste woche": return NEXT_WEEK;
      default: return null;
    }
  }
}
