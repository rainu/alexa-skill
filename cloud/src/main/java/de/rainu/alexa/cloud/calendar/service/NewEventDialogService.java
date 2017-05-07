package de.rainu.alexa.cloud.calendar.service;

import com.amazon.speech.speechlet.DialogState;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import de.rainu.alexa.cloud.calendar.exception.CalendarWriteException;
import de.rainu.alexa.cloud.service.MessageService;
import de.rainu.alexa.cloud.service.SpeechService;
import java.time.Duration;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewEventDialogService {

  public static final String SLOT_SUMMARY = "summary";
  public static final String SLOT_DATE = "date";
  public static final String SLOT_TIME = "time";
  public static final String SLOT_DURATION = "duration";

  public static final String SESSION_FROM = "from";
  public static final String SESSION_TO = "to";
  public static final String SESSION_DATE_FORMAT = "date_format";

  @Autowired
  SpeechService speechService;

  @Autowired
  MessageService messageService;

  @Autowired
  CalendarService calendarService;

  public SpeechletResponse handleDialogAction(final IntentRequest request, final Session session)
      throws CalendarWriteException {
    switch (request.getIntent().getConfirmationStatus()) {
      default:
      case NONE: return handleProgress(request, session);
      case CONFIRMED: return handleConfirmed(request, session);
      case DENIED: return handleDenied(request, session);
    }
  }

  private SpeechletResponse handleProgress(IntentRequest request, Session session) {
    if(allSlotsFilled(request)) {
      final Duration duration = Duration.parse(request.getIntent().getSlot(SLOT_DURATION).getValue());
      final DateTime from = DateTime.parse(request.getIntent().getSlot(SLOT_DATE).getValue() + "T" + request.getIntent().getSlot(SLOT_TIME).getValue());
      final DateTime to = from.plus(duration.toMillis());

      final String dateFormat = messageService.de("event.new.card.content.time.format");
      session.setAttribute(SESSION_DATE_FORMAT, dateFormat);
      session.setAttribute(SESSION_FROM, from.toString(dateFormat));
      session.setAttribute(SESSION_TO, to.toString(dateFormat));

      final OutputSpeech speech = speechService.confirmNewEvent(from, to, request.getLocale());
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
    final String title = request.getIntent().getSlot(SLOT_SUMMARY).getValue();
    final String from = session.getAttribute(SESSION_FROM).toString();
    final String to = session.getAttribute(SESSION_TO).toString();

    calendarService.createEvent(null, title,
        parser.parseDateTime(from),
        parser.parseDateTime(to));

    SimpleCard card = new SimpleCard();
    card.setTitle(messageService.de("event.new.card.title"));
    card.setContent(messageService.de("event.new.card.content", title, from, to));

    return SpeechletResponse.newTellResponse(
        speechService.speechNewEventSaved(request.getLocale()),
        card);
  }

  private boolean allSlotsFilled(IntentRequest request) {
    return request.getIntent().getSlots().values().stream()
        .filter(s -> s.getValue() == null)
        .count() == 0;
  }
}
