package de.rainu.alexa.cloud.speechlet;

import com.amazon.speech.speechlet.DialogState;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.SpeechletController;
import de.rainu.alexa.cloud.calendar.CalendarCLIAdapter;
import de.rainu.alexa.cloud.calendar.exception.CalendarWriteException;
import de.rainu.alexa.cloud.calendar.service.CalendarService;
import de.rainu.alexa.cloud.service.MessageService;
import de.rainu.alexa.cloud.service.SpeechService;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * This class is responsible for handling incoming event creation requests.
 */
@SpeechletController(BasicSpeechlet.ENDPOINT)
public class NewEventSpeechlet {

  public static final String SLOT_PREFIX_SUMMARY = "summary_";
  public static final String SLOT_DATE = "date";
  public static final String SLOT_TIME = "time";
  public static final String SLOT_DURATION = "duration";
  public static final String SLOT_CALENDAR = "calendar";

  public static final String SESSION_FROM = "from";
  public static final String SESSION_TO = "to";
  public static final String SESSION_DATE_FORMAT = "date_format";
  public static final String SESSION_CALENDAR = "calendar";

  @Autowired
  SpeechService speechService;

  @Autowired
  MessageService messageService;

  @Autowired
  CalendarService calendarService;

  @Autowired
  private ApplicationContext context;

  private Set<String> calendarNames;

  Set<String> getCalendarNames(){
    return context.getBeansOfType(CalendarCLIAdapter.class).keySet();
  }

  @OnIntent("NewEvent")
  public SpeechletResponse handleDialogAction(final IntentRequest request, final Session session) throws CalendarWriteException {
    session.setAttribute(BasicSpeechlet.KEY_DIALOG_TYPE, BasicSpeechlet.DIALOG_TYPE_NEW_EVENT);

    try {
      switch (request.getIntent().getConfirmationStatus()) {
        default:
        case NONE: return handleProgress(request, session);
        case CONFIRMED: return handleConfirmed(request, session);
        case DENIED: return handleDenied(request, session);
      }
    } catch (CalendarWriteException e) {
      return SpeechletResponse.newTellResponse(speechService.speechError(e));
    }
  }

  private SpeechletResponse handleProgress(IntentRequest request, Session session) {
    checkCalendarName(request, session);

    if(allSlotsFilled(request)) {
      final String title = collectSummary(request);
      final Duration duration = Duration.parse(request.getIntent().getSlot(SLOT_DURATION).getValue());
      final DateTime from = DateTime.parse(request.getIntent().getSlot(SLOT_DATE).getValue() + "T" + request.getIntent().getSlot(SLOT_TIME).getValue());
      final DateTime to = from.plus(duration.toMillis());

      final String dateFormat = messageService.de("event.new.card.content.time.format");
      session.setAttribute(SESSION_DATE_FORMAT, dateFormat);
      session.setAttribute(SESSION_FROM, from.toString(dateFormat));
      session.setAttribute(SESSION_TO, to.toString(dateFormat));

      final OutputSpeech speech = speechService.confirmNewEvent(title, from, to, request.getLocale());
      return SpeechletResponse.newDialogConfirmIntentResponse(speech);
    }

    //normally we want to delegate because we have defined the dialog into the model on alexa
    if( request.getDialogState() != DialogState.COMPLETED) {
      return SpeechletResponse.newDialogDelegateResponse();
    }

    return SpeechletResponse.newTellResponse(speechService.speechCancelNewEvent(request.getLocale()));
  }

  private SpeechletResponse handleDenied(IntentRequest request, Session session) {
    return SpeechletResponse.newTellResponse(speechService.speechCancelNewEvent(request.getLocale()));
  }

  private SpeechletResponse handleConfirmed(IntentRequest request, Session session)
      throws CalendarWriteException {
    final String dateFormat = session.getAttribute(SESSION_DATE_FORMAT).toString();
    final DateTimeFormatter parser = DateTimeFormat.forPattern(dateFormat);
    final String title = collectSummary(request);
    final String calendar = session.getAttribute(SESSION_CALENDAR) != null ? session.getAttribute(SESSION_CALENDAR).toString() : null;
    final String from = session.getAttribute(SESSION_FROM).toString();
    final String to = session.getAttribute(SESSION_TO).toString();

    calendarService.createEvent(calendar, title,
        parser.parseDateTime(from),
        parser.parseDateTime(to));

    SimpleCard card = new SimpleCard();
    card.setTitle(messageService.de("event.new.card.title"));
    card.setContent(messageService.de("event.new.card.content", title, from, to));

    return SpeechletResponse.newTellResponse(
        speechService.speechNewEventSaved(request.getLocale()),
        card);
  }

  private void checkCalendarName(IntentRequest request, Session session) {
    if(request.getIntent().getSlot(SLOT_CALENDAR) == null) {
      return;
    }

    final String givenName = request.getIntent().getSlot(SLOT_CALENDAR).getValue();
    if(givenName == null) {
      return;
    }

    final String foundName = findCalendarName(givenName);
    session.setAttribute(SESSION_CALENDAR, foundName);
  }

  private String findCalendarName(final String calendarName) {
    final String nCalendarName = calendarName.toLowerCase();
    Map<String, Integer> distances = new HashMap<>();

    for(String curCalendarName : getCalendarNames()) {
      String nCurCalendarName = curCalendarName.toLowerCase();
      int distance = StringUtils.getLevenshteinDistance(nCalendarName, nCurCalendarName);

      if(distance <= 3 || nCurCalendarName.contains(nCalendarName)) {
        distances.put(curCalendarName, distance);
      }
    }

    if(distances.size() == 1) {
      return distances.keySet().stream().findFirst().get();
    }

    return null;
  }

  private String collectSummary(IntentRequest request) {
    return request.getIntent().getSlots().values().stream()
        .filter(s -> s.getName().startsWith(SLOT_PREFIX_SUMMARY))
        .filter(s -> s.getValue() != null)
        .sorted((s1, s2) -> s1.getName().compareTo(s2.getName()))
        .map(s -> s.getValue())
        .collect(Collectors.joining(" "));
  }

  private boolean allSlotsFilled(IntentRequest request) {
    long withoutSummary = request.getIntent().getSlots().values().stream()
        .filter(s -> s.getValue() == null)
        .filter(s -> !s.getName().startsWith(SLOT_PREFIX_SUMMARY))
        .filter(s -> !s.getName().equals(SLOT_CALENDAR))
        .count();

    long summary = request.getIntent().getSlots().values().stream()
        .filter(s -> s.getValue() != null)
        .filter(s -> s.getName().startsWith(SLOT_PREFIX_SUMMARY))
        .filter(s -> !s.getName().equals(SLOT_CALENDAR))
        .count();

    return withoutSummary == 0 && summary >= 1;
  }
}
