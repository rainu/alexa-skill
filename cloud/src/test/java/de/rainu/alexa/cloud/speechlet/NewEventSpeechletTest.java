package de.rainu.alexa.cloud.speechlet;

import static de.rainu.alexa.cloud.speechlet.NewEventSpeechlet.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.DialogState;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.interfaces.dialog.directive.ConfirmationStatus;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rainu.alexa.cloud.calendar.exception.CalendarWriteException;
import de.rainu.alexa.cloud.calendar.service.CalendarService;
import de.rainu.alexa.cloud.service.MessageService;
import de.rainu.alexa.cloud.service.SpeechService;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class NewEventSpeechletTest {

  SpeechService speechService;

  @Autowired
  MessageService messageService;

  CalendarService calendarService;

  @Autowired
  NewEventSpeechlet toTest;

  ObjectMapper mapper;

  @Before
  public void setup(){
    mapper = new ObjectMapper();
    speechService = mock(SpeechService.class);
    calendarService = mock(CalendarService.class);
    messageService = spy(messageService);

    toTest.speechService = speechService;
    toTest.messageService = messageService;
    toTest.calendarService = calendarService;

    toTest = spy(toTest);
  }

  @Test
  public void inProgress_NotAllSlotsAreFilled() throws CalendarWriteException {
    //given
    final Session session = Session.builder()
        .withSessionId("<session-id>")
        .build();
    final Intent intent = Intent.builder()
        .withName("<intent>")
        .withSlots(slots(
          slot("day", null)
        ))
        .build();
    final IntentRequest request = IntentRequest.builder()
        .withRequestId("<requestId>")
        .withIntent(intent)
        .withDialogState(DialogState.IN_PROGRESS)
        .build();

    //when
    final SpeechletResponse response = toTest.handleDialogAction(request, session);

    //then
    assertEquals(
        json(SpeechletResponse.newDialogDelegateResponse()),
        json(response));
  }

  @Test
  public void inProgress_AllSlotsAreFilled() throws CalendarWriteException {
    //given
    final Intent intent = Intent.builder()
        .withName("<intent>")
        .withSlots(slots(
            slot(SLOT_DATE, "2010-08-13"),
            slot(SLOT_TIME, "20:15"),
            slot(SLOT_DURATION, "PT2H"),
            slot(SLOT_PREFIX_SUMMARY, "Title")
        ))
        .build();
    final IntentRequest request = IntentRequest.builder()
        .withRequestId("<requestId>")
        .withLocale(Locale.GERMANY)
        .withIntent(intent)
        .withDialogState(DialogState.IN_PROGRESS)
        .build();
    final Session session = Session.builder()
        .withSessionId("<sessionId>")
        .build();
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<confirm>");
    doReturn(speech).when(speechService).confirmNewEvent(any(), any(), any(), any());

    //when
    final SpeechletResponse response = toTest.handleDialogAction(request, session);

    //then
    ArgumentCaptor<DateTime> dateCap = ArgumentCaptor.forClass(DateTime.class);

    assertEquals(
        json(SpeechletResponse.newDialogConfirmIntentResponse(speech)),
        json(response));
    verify(speechService, times(1)).confirmNewEvent(
        eq("Title"), dateCap.capture(), dateCap.capture(), same(request.getLocale()));
    assertEquals("dd.MM.yyyy HH:mm", session.getAttribute(SESSION_DATE_FORMAT));
    assertEquals("13.08.2010 20:15", session.getAttribute(SESSION_FROM));
    assertEquals("13.08.2010 22:15", session.getAttribute(SESSION_TO));
    assertEquals(DateTime.parse("2010-08-13T20:15"), dateCap.getAllValues().get(0));
    assertEquals(DateTime.parse("2010-08-13T22:15"), dateCap.getAllValues().get(1));
  }

  @Test
  public void denied() throws CalendarWriteException {
    //given
    final Session session = Session.builder()
        .withSessionId("<session-id>")
        .build();
    final Intent intent = Intent.builder()
        .withName("<intent>")
        .withConfirmationStatus(ConfirmationStatus.DENIED)
        .build();
    final IntentRequest request = IntentRequest.builder()
        .withRequestId("<requestId>")
        .withLocale(Locale.GERMANY)
        .withIntent(intent)
        .withDialogState(DialogState.IN_PROGRESS)
        .build();
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<cancel>");
    doReturn(speech).when(speechService).speechCancelNewEvent(any());

    //when
    final SpeechletResponse response = toTest.handleDialogAction(request, session);

    //then
    verify(speechService, times(1)).speechCancelNewEvent(eq(request.getLocale()));
    assertEquals(
        json(SpeechletResponse.newTellResponse(speechService.speechCancelNewEvent(request.getLocale()))),
        json(response));
  }

  @Test
  public void confirmed() throws CalendarWriteException {
    //given
    final Intent intent = Intent.builder()
        .withName("<intent>")
        .withSlots(slots(
            slot(SLOT_DATE, "2010-08-13"),
            slot(SLOT_TIME, "20:15"),
            slot(SLOT_DURATION, "PT2H"),
            slot(SLOT_PREFIX_SUMMARY, "Title")
        ))
        .withConfirmationStatus(ConfirmationStatus.CONFIRMED)
        .build();
    final IntentRequest request = IntentRequest.builder()
        .withRequestId("<requestId>")
        .withLocale(Locale.GERMANY)
        .withIntent(intent)
        .withDialogState(DialogState.IN_PROGRESS)
        .build();
    final Session session = Session.builder()
        .withSessionId("<sessionId>")
        .withAttributes(attributes(
            attribute(SESSION_FROM, "13.08.2010 20:15"),
            attribute(SESSION_TO, "13.08.2010 22:15"),
            attribute(SESSION_DATE_FORMAT, "dd.MM.yyyy HH:mm")
        ))
        .build();
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<event-saved>");
    doReturn(speech).when(speechService).speechNewEventSaved(any());

    final SimpleCard card = new SimpleCard();
    card.setTitle("Neuer Termin erstellt");
    card.setContent("Titel: Title\nBeginn: 13.08.2010 20:15\nEnde:13.08.2010 22:15");

    //when
    final SpeechletResponse response = toTest.handleDialogAction(request, session);

    //then
    verify(speechService, times(1)).speechNewEventSaved(eq(request.getLocale()));
    verify(calendarService, times(1)).createEvent(
        null, "Title", DateTime.parse("2010-08-13T20:15"), DateTime.parse("2010-08-13T22:15"));
    assertEquals(
        json(SpeechletResponse.newTellResponse(speechService.speechNewEventSaved(request.getLocale()), card)),
        json(response));
  }

  @Test
  public void inProgress_CalendarName() throws CalendarWriteException {
    checkCalendarName("geburtstag", "Geburtstage");
    checkCalendarName("geburt", "Geburtstage");
    checkCalendarName("termin", "Termine");
    checkCalendarName("termine", "Termine");
    checkCalendarName("haus", "Haushalt");
    checkCalendarName("haushalt", "Haushalt");
    checkCalendarName("Progammierung", null);
  }

  private void checkCalendarName(final String calendarSlot, final String expectedCalendar) throws CalendarWriteException {
    final Intent intent = Intent.builder()
        .withName("<intent>")
        .withSlots(slots(
            slot("calendar", calendarSlot)
        ))
        .build();
    final IntentRequest request = IntentRequest.builder()
        .withRequestId("<requestId>")
        .withIntent(intent)
        .withDialogState(DialogState.IN_PROGRESS)
        .build();
    final Session session = Session.builder()
        .withSessionId("<sessionId>")
        .build();
    doReturn(new HashSet<>(Arrays.asList("Geburtstage", "Termine", "Haushalt"))).when(toTest).getCalendarNames();

    toTest.handleDialogAction(request, session);

    //then
    assertEquals(expectedCalendar, session.getAttribute(SESSION_CALENDAR));
  }

  private Map<String, Object> attributes(Entry<String, Object> ... attributes) {
    return Stream.of(attributes)
        .collect(Collectors.toMap(s -> s.getKey(), s -> s.getValue()));
  }

  private Entry<String, Object> attribute(String name, Object value) {
    return new SimpleEntry<>(name,value);
  }

  private Map<String,Slot> slots(Entry<String, Slot> ... slots) {
    return Stream.of(slots)
        .collect(Collectors.toMap(s -> s.getKey(), s -> s.getValue()));
  }

  private Entry<String, Slot> slot(String name, String value) {
    return new SimpleEntry<>(name,
        Slot.builder().withName(name).withValue(value).build());
  }

  private String json(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "<ERROR>";
    }
  }
}
