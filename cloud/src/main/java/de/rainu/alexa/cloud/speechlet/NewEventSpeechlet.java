package de.rainu.alexa.cloud.speechlet;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.DialogState;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.interfaces.dialog.directive.ConfirmationStatus;
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
  public static final String SLOT_DATE_FROM = "date_from";
  public static final String SLOT_TIME_FROM = "time_from";
  public static final String SLOT_DATE_TO = "date_to";
  public static final String SLOT_TIME_TO = "time_to";
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
    final SpeechletResponse response;

    try {
      switch (request.getIntent().getConfirmationStatus()) {
        default:
        case NONE: response = handleProgress(request, session);
          break;
        case CONFIRMED: response = handleConfirmed(request, session);
          break;
        case DENIED: response = handleDenied(request, session);
          break;
      }
    } catch (CalendarWriteException e) {
      return SpeechletResponse.newTellResponse(speechService.speechError(e));
    }

    response.setShouldEndSession(false);
    return response;
  }

  @OnIntent("SetCalendarInSession")
  public SpeechletResponse handleSetCalendarInSession(IntentRequest request, Session session){
    final String calendarName = checkCalendarName(request, session);
    final OutputSpeech answer = speechService.speechConnectWithCalendar(calendarName, request.getLocale());
    final SpeechletResponse response = SpeechletResponse.newTellResponse(answer);
    response.setShouldEndSession(false);

    return response;
  }

  private SpeechletResponse handleProgress(IntentRequest request, Session session) {
    checkCalendarName(request, session);

    if(allSlotsFilled(request)) {
      final String title = collectSummary(request);
      final DateTime from = DateTime.parse(
          sv(request, SLOT_DATE_FROM) + "T" + sv(request, SLOT_TIME_FROM));
      final DateTime to = getTimeTo(request, from);

      final String dateFormat = messageService.de("event.new.card.content.time.format");
      session.setAttribute(SESSION_DATE_FORMAT, dateFormat);
      session.setAttribute(SESSION_FROM, from.toString(dateFormat));
      session.setAttribute(SESSION_TO, to.toString(dateFormat));

      final OutputSpeech speech = speechService.confirmNewEvent(title, from, to, request.getLocale());
      return SpeechletResponse.newDialogConfirmIntentResponse(speech);
    }

    //normally we want to delegate because we have defined the dialog into the model on alexa
    if( request.getDialogState() != DialogState.COMPLETED) {
      Intent updatedIntent = updateIntent(request.getIntent());
      return SpeechletResponse.newDialogDelegateResponse(updatedIntent);
    }

    return SpeechletResponse.newTellResponse(speechService.speechCancelNewEvent(request.getLocale()));
  }

  private Intent updateIntent(Intent intent) {
    Map<String, Slot> slots = new HashMap<>(intent.getSlots());

    if(sv(intent, SLOT_DATE_TO) != null || sv(intent, SLOT_TIME_TO) != null) {
      if(sv(intent, SLOT_DURATION) == null) {
        Slot updatedSlot = Slot.builder()
            .withName(SLOT_DURATION)
            .withConfirmationStatus(ConfirmationStatus.NONE)
            .withValue("<placeholder>")
            .build();
        slots.put(SLOT_DURATION, updatedSlot);
      }
    }

    return Intent.builder()
        .withName(intent.getName())
        .withConfirmationStatus(intent.getConfirmationStatus())
        .withSlots(slots)
        .build();
  }

  private DateTime getTimeTo(IntentRequest request, DateTime from) {
    final String sDuration = sv(request, SLOT_DURATION);
    final String sDateFrom = sv(request, SLOT_DATE_FROM);
    final String sDateTo = sv(request, SLOT_DATE_TO);
    final String sTimeTo = sv(request, SLOT_TIME_TO);

    if(sTimeTo != null) {
      if(sDateTo != null) {
        return DateTime.parse(sDateTo + "T" + sTimeTo);
      }

      return DateTime.parse(sDateFrom + "T" + sTimeTo);
    } else if(sDateTo != null) {
      return DateTime.parse(sDateTo).withTimeAtStartOfDay();
    }

    final Duration duration = Duration.parse(sDuration);
    return from.plus(duration.toMillis());
  }

  private SpeechletResponse handleDenied(IntentRequest request, Session session) {
    session.removeAttribute(BasicSpeechlet.KEY_DIALOG_TYPE);

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

    session.removeAttribute(BasicSpeechlet.KEY_DIALOG_TYPE);

    return SpeechletResponse.newTellResponse(
        speechService.speechNewEventSaved(request.getLocale()),
        card);
  }

  private String checkCalendarName(IntentRequest request, Session session) {
    if(sv(request, SLOT_CALENDAR) == null) {
      return null;
    }

    final String givenName = sv(request, SLOT_CALENDAR);
    if(givenName == null) {
      return null;
    }

    final String foundName = findCalendarName(givenName);
    session.setAttribute(SESSION_CALENDAR, foundName);

    return foundName;
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
    long summary = request.getIntent().getSlots().values().stream()
        .filter(s -> s.getValue() != null)
        .filter(s -> s.getName().startsWith(SLOT_PREFIX_SUMMARY))
        .count();

    Set<String> slots = request.getIntent().getSlots().values().stream()
        .filter(s -> s.getValue() != null)
        .map(s -> s.getName())
        .collect(Collectors.toSet());

    return
        slots.contains(SLOT_DATE_FROM) &&
        slots.contains(SLOT_TIME_FROM) &&
        (slots.contains(SLOT_DURATION) || slots.contains(SLOT_DATE_TO) || slots.contains(SLOT_TIME_TO)) &&
        summary > 0;
  }

  private String sv(final IntentRequest request, final String slot) {
    return sv(request.getIntent(), slot);
  }

  private String sv(final Intent intent, final String slot) {
    if(intent.getSlot(slot) == null) {
      return null;
    }

    return intent.getSlot(slot).getValue();
  }
}
