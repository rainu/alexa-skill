package de.rainu.alexa.cloud.speechlet;

import static de.rainu.alexa.cloud.speechlet.BasicSpeechlet.DIALOG_TYPE_NEW_EVENT;
import static de.rainu.alexa.cloud.speechlet.BasicSpeechlet.KEY_DIALOG_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazon.speech.Sdk;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletResponseEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.Application;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.User;
import com.amazon.speech.speechlet.interfaces.dialog.directive.DelegateDirective;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
import de.rainu.alexa.cloud.calendar.exception.CalendarWriteException;
import de.rainu.alexa.cloud.calendar.model.Event;
import de.rainu.alexa.cloud.calendar.model.Moment;
import de.rainu.alexa.cloud.calendar.service.CalendarService;
import de.rainu.alexa.cloud.service.MessageService;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    Sdk.DISABLE_REQUEST_SIGNATURE_CHECK_SYSTEM_PROPERTY + "=true"
})
public class CalendarSpeechletIT {

  @Autowired
  TestRestTemplate rest;

  @Autowired
  MessageService msg;

  @Autowired
  BasicSpeechlet toTestBasic;

  @Autowired
  NewEventSpeechlet toTestNewEvent;

  @Autowired
  QuerySpeechlet toTestQuery;

  ObjectMapper mapper;

  static {
    System.setProperty(Sdk.DISABLE_REQUEST_SIGNATURE_CHECK_SYSTEM_PROPERTY, "true");
  }

  @Before
  public void setup(){
    mapper = (ObjectMapper) ReflectionTestUtils.getField(SpeechletRequestEnvelope.class, "OBJECT_MAPPER");

    toTestQuery.calendarService = mock(CalendarService.class);

    try{
      toTestBasic.speechService = spy(toTestBasic.speechService);
      toTestQuery.speechService = spy(toTestQuery.speechService);
      toTestNewEvent.speechService = spy(toTestNewEvent.speechService);
    }catch(MockitoException e){
      //don't spy a spy...
    }
  }

  @Test
  public void help(){
    //given
    HttpEntity<String> request = buildRequest("AMAZON.HelpIntent");

    //when
    final SpeechletResponseEnvelope response = perform(request);

    //then
    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        msg.de("help"),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void nextEvents() throws CalendarReadException {
    //given
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<event-listing>");
    List<Event> events = new ArrayList<>();

    doReturn(events).when(toTestQuery.calendarService).getNextEvents();
    doReturn(speech).when(toTestQuery.speechService).readEvents(any(), anyList());

    HttpEntity<String> request = buildRequest("NextEvents");

    //when
    final SpeechletResponseEnvelope response = perform(request);

    //then
    verify(toTestQuery.calendarService, times(1)).getNextEvents();
    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        speech.getText(),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void nextEvents_readError() throws CalendarReadException {
    //given
    doThrow(new CalendarReadException("error")).when(toTestQuery.calendarService).getNextEvents();

    HttpEntity<String> request = buildRequest("NextEvents");

    //when
    final SpeechletResponseEnvelope response = perform(request);

    //then
    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        msg.de("event.error.read"),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void eventQuery() throws CalendarReadException {
    //given
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<event-listing>");
    List<Event> events = new ArrayList<>();

    doReturn(events).when(toTestQuery.calendarService).getEvents(any(), any());
    doReturn(speech).when(toTestQuery.speechService).readEvents(any(), anyString(), anyList());

    //use cases
    testUseCase("diesen", "montag", Moment.MONDAY, speech);
    testUseCase("diesen", "dienstag", Moment.TUESDAY, speech);
    testUseCase("diesen", "mittwoch", Moment.WEDNESDAY, speech);
    testUseCase("diesen", "donnerstag", Moment.THURSDAY, speech);
    testUseCase("diesen", "freitag", Moment.FRIDAY, speech);
    testUseCase("diesen", "samstag", Moment.SATURDAY, speech);
    testUseCase("diesen", "sonntag", Moment.SUNDAY, speech);
    testUseCase("nächsten", "montag", Moment.MONDAY, speech);
    testUseCase("nächsten", "dienstag", Moment.TUESDAY, speech);
    testUseCase("nächsten", "mittwoch", Moment.WEDNESDAY, speech);
    testUseCase("nächsten", "donnerstag", Moment.THURSDAY, speech);
    testUseCase("nächsten", "freitag", Moment.FRIDAY, speech);
    testUseCase("nächsten", "samstag", Moment.SATURDAY, speech);
    testUseCase("nächsten", "sonntag", Moment.SUNDAY, speech);
  }

  private void testUseCase(String precision, String day,
      Moment expectedMoment, OutputSpeech expectedSpeech) throws CalendarReadException {

    //when
    HttpEntity<String> request = buildRequest("EventQuery",
        "precision", precision, "day", day);

    final SpeechletResponseEnvelope response = perform(request);

    //then
    verify(toTestQuery.speechService, times(1)).readEvents(
        eq(Locale.GERMANY), eq(expectedMoment.getName(Locale.GERMANY)), anyList());

    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        ((PlainTextOutputSpeech)expectedSpeech).getText(),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void eventQuery_unknownDay() throws CalendarReadException {
    //given
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<event-listing>");
    List<Event> events = new ArrayList<>();

    //when
    HttpEntity<String> request = buildRequest("EventQuery",
        "precision", "diesen", "day", "tag");

    final SpeechletResponseEnvelope response = perform(request);

    //then
    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        msg.de("event.error.unknown.moment"),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void eventQuery_unknownPrasicion() throws CalendarReadException {
    //given
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<event-listing>");
    List<Event> events = new ArrayList<>();

    doReturn(events).when(toTestQuery.calendarService).getEvents(any(), any());
    doReturn(speech).when(toTestQuery.speechService).readEvents(any(), anyString(), anyList());

    //when
    HttpEntity<String> request = buildRequest("EventQuery",
        "precision", "unbekannt", "day", "montag");

    final SpeechletResponseEnvelope response = perform(request);

    //then
    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        msg.de("event.error.unknown.moment"),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void nearEvents() throws CalendarReadException {
    //given
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<event-listing>");
    List<Event> events = new ArrayList<>();

    doReturn(events).when(toTestQuery.calendarService).getEvents(any(), any());
    doReturn(speech).when(toTestQuery.speechService).readEvents(any(), anyString(), anyList());

    //use cases
    testUseCase("heute", Moment.TODAY, speech);
    testUseCase("morgen", Moment.TOMORROW, speech);
    testUseCase("übermorgen", Moment.OVERMORROW, speech);
  }

  private void testUseCase(String near, Moment expectedMoment, OutputSpeech expectedSpeech) {
    //when
    HttpEntity<String> request = buildRequest("EventQueryNear","near", near);

    final SpeechletResponseEnvelope response = perform(request);

    //then
    verify(toTestQuery.speechService, times(1)).readEvents(
        eq(Locale.GERMANY), eq(expectedMoment.getName(Locale.GERMANY)), anyList());

    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        ((PlainTextOutputSpeech)expectedSpeech).getText(),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void nearEvents_unknown() throws CalendarReadException {
    //given
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<event-listing>");
    List<Event> events = new ArrayList<>();

    doReturn(events).when(toTestQuery.calendarService).getEvents(any(), any());
    doReturn(speech).when(toTestQuery.speechService).readEvents(any(), anyString(), anyList());

    //when
    HttpEntity<String> request = buildRequest("EventQueryNear",
        "near", "unbekannt");

    final SpeechletResponseEnvelope response = perform(request);

    //then
    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        msg.de("event.error.unknown.moment"),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void thisWeek() throws CalendarReadException {
    //given
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<event-listing>");
    List<Event> events = new ArrayList<>();

    doReturn(events).when(toTestQuery.calendarService).getEvents(any(), any());
    doReturn(speech).when(toTestQuery.speechService).readEvents(any(), anyString(), anyList());

    //when
    HttpEntity<String> request = buildRequest("EventQueryThisWeek");

    final SpeechletResponseEnvelope response = perform(request);

    //then
    verify(toTestQuery.speechService, times(1)).readEvents(
        eq(Locale.GERMANY), eq(Moment.THIS_WEEK.getName(Locale.GERMANY)), anyList());

    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        speech.getText(),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void nextWeek() throws CalendarReadException {
    //given
    final PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText("<event-listing>");
    List<Event> events = new ArrayList<>();

    doReturn(events).when(toTestQuery.calendarService).getEvents(any(), any());
    doReturn(speech).when(toTestQuery.speechService).readEvents(any(), anyString(), anyList());

    //when
    HttpEntity<String> request = buildRequest("EventQueryNextWeek");

    final SpeechletResponseEnvelope response = perform(request);

    //then
    verify(toTestQuery.speechService, times(1)).readEvents(
        eq(Locale.GERMANY), eq(Moment.NEXT_WEEK.getName(Locale.GERMANY)), anyList());

    assertNull(response.getResponse().getCard());
    assertTrue(response.getResponse().getOutputSpeech() instanceof PlainTextOutputSpeech);
    assertEquals(
        speech.getText(),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void newEvent() throws CalendarWriteException {
    //given
    //when
    HttpEntity<String> request = buildRequest("NewEvent");

    final SpeechletResponseEnvelope response = perform(request);

    //then
    assertFalse(response.getResponse().getDirectives().isEmpty());
    assertTrue(response.getResponse().getDirectives().get(0) instanceof DelegateDirective);
  }

  @Test
  public void cancel_dialog() throws CalendarWriteException {
    //given
    //when
    HttpEntity<String> request = buildRequestWithSession("AMAZON.CancelIntent",
      new SimpleEntry<>(KEY_DIALOG_TYPE, DIALOG_TYPE_NEW_EVENT)
    );

    final SpeechletResponseEnvelope response = perform(request);

    //then
    assertEquals(
        ((PlainTextOutputSpeech)toTestBasic.speechService.speechCancelNewEvent(Locale.GERMANY)).getText(),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  @Test
  public void cancel_noDialog() throws CalendarWriteException {
    //given
    //when
    HttpEntity<String> request = buildRequestWithSession("AMAZON.CancelIntent");

    final SpeechletResponseEnvelope response = perform(request);

    //then
    assertEquals(
        ((PlainTextOutputSpeech)toTestBasic.speechService.speechBye(Locale.GERMANY)).getText(),
        ((PlainTextOutputSpeech)response.getResponse().getOutputSpeech()).getText());
  }

  private SpeechletResponseEnvelope perform(HttpEntity<String> request){
    try {
      final ResponseEntity<String> response = rest.postForEntity(BasicSpeechlet.ENDPOINT, request, String.class);
      assertEquals(HttpStatus.OK, response.getStatusCode());

      return mapper.readValue(response.getBody(), SpeechletResponseEnvelope.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private HttpEntity<String> buildRequest(String intentName, String...rawSlots) {
    return buildRequest(intentName, new Entry[]{}, rawSlots);
  }

  private HttpEntity<String> buildRequestWithSession(String intentName, Entry<String, Object>...session) {
    return buildRequest(intentName, session, new String[]{});
  }

  private HttpEntity<String> buildRequest(String intentName, Entry<String, Object>[] sAttributes, String[] rawSlots) {
    final HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());

    Session session = Session.builder()
        .withUser(new User("<user-id>"))
        .withApplication(new Application("<application-id>"))
        .withSessionId("<session-id>")
        .withAttributes(Stream.of(sAttributes).collect(Collectors.toMap(s -> s.getKey(), s -> s.getValue())))
        .withIsNew(true)
        .build();

    Map<String, Slot> slots = new HashMap<>();
    for(int i=0; i < rawSlots.length; i += 2) {
      String key = rawSlots[i];
      String value = rawSlots[i+1];

      slots.put(key, Slot.builder().withName(key).withValue(value).build());
    }

    Intent intent = Intent.builder()
        .withName(intentName)
        .withSlots(slots)
        .build();

    SpeechletRequest request = IntentRequest.builder()
        .withRequestId("<request-id>")
        .withTimestamp(DateTime.now().toDate())
        .withLocale(Locale.GERMANY)
        .withIntent(intent)
        .build();

    SpeechletRequestEnvelope body = SpeechletRequestEnvelope.builder()
        .withVersion("1.0")
        .withSession(session)
        .withRequest(request)
        .build();

    try {
      return new HttpEntity<>(mapper.writeValueAsString(body), headers);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
