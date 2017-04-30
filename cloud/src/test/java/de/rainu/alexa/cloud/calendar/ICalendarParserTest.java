package de.rainu.alexa.cloud.calendar;

import static org.junit.Assert.assertEquals;

import biweekly.component.VEvent;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class ICalendarParserTest {

  ICalendarParser toTest = new ICalendarParser();

  @Test
  public void testParser(){
    //given
    List<String> rawEvents = Arrays.asList(
        "BEGIN:VCALENDAR\n"
        + "VERSION:2.0\n"
        + "PRODID:-//Sabre//Sabre VObject 4.1.1//EN\n"
        + "CALSCALE:GREGORIAN\n"
        + "BEGIN:VEVENT\n"
        + "DTSTAMP:20170411T200614Z\n"
        + "UID:20170411T200614Z-2ce178178dd977ce\n"
        + "DTSTART;VALUE=DATE:20170527\n"
        + "DTEND;VALUE=DATE:20170528\n"
        + "SUMMARY:Great Event!\n"
        + "STATUS:TENTATIVE\n"
        + "BEGIN:VALARM\n"
        + "TRIGGER:-PT420M\n"
        + "ACTION:DISPLAY\n"
        + "DESCRIPTION:Great Event!\n"
        + "END:VALARM\n"
        + "END:VEVENT\n"
        + "END:VCALENDAR",

        "BEGIN:VCALENDAR\n"
        + "VERSION:2.0\n"
        + "PRODID:-//Sabre//Sabre VObject 4.1.1//EN\n"
        + "CALSCALE:GREGORIAN\n"
        + "BEGIN:VEVENT\n"
        + "DTSTAMP:20170427T162550Z\n"
        + "UID:20170427T162550Z-2ce178178dd977ce\n"
        + "DTSTART:20170524T120000Z\n"
        + "DTEND:20170524T140000Z\n"
        + "SUMMARY:Birthdayparteeeeeeeeyyyy\n"
        + "LOCATION:@Home\n"
        + "STATUS:TENTATIVE\n"
        + "BEGIN:VALARM\n"
        + "TRIGGER:-PT10M\n"
        + "ACTION:DISPLAY\n"
        + "DESCRIPTION:Birthdayparteeeeeeeeyyyy\n"
        + "END:VALARM\n"
        + "END:VEVENT\n"
        + "END:VCALENDAR",

        "BEGIN:VCALENDAR\n"
        + "VERSION:2.0\n"
        + "PRODID:-//Sabre//Sabre VObject 4.1.1//EN\n"
        + "CALSCALE:GREGORIAN\n"
        + "BEGIN:VEVENT\n"
        + "DTSTAMP:20170430T163825Z\n"
        + "UID:20170412T100903Z-2ce178178dd977ce\n"
        + "SEQUENCE:2\n"
        + "SUMMARY:Doctor\n"
        + "DESCRIPTION:Goto doctor right now!\n"
        + "STATUS:TENTATIVE\n"
        + "CLASS:PUBLIC\n"
        + "DTSTART:20170503T080000Z\n"
        + "DTEND:20170503T090000Z\n"
        + "BEGIN:VALARM\n"
        + "TRIGGER:-P1D\n"
        + "ACTION;X-NC-GROUP-ID=0:DISPLAY\n"
        + "DESCRIPTION:Doctor\n"
        + "END:VALARM\n"
        + "END:VEVENT\n"
        + "END:VCALENDAR");

    //when
    List<VEvent> result = toTest.parseEvents(rawEvents);

    //then
    assertEquals(rawEvents.size(), result.size());
  }
}
