package de.rainu.alexa.cloud.speechlet;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.OnLaunch;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
import de.rainu.alexa.cloud.calendar.exception.CalendarWriteException;
import de.rainu.alexa.cloud.calendar.exception.UnknownMomentException;
import de.rainu.alexa.cloud.calendar.model.Event;
import de.rainu.alexa.cloud.calendar.model.Moment;
import de.rainu.alexa.cloud.calendar.service.CalendarService;
import de.rainu.alexa.cloud.calendar.service.NewEventDialogService;
import de.rainu.alexa.cloud.service.SpeechService;
import de.rainu.alexa.speechlet.AbstractSpeechletDispatcher;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is responsible for handling all incoming requests.
 */
@RestController
@RequestMapping(CalendarSpeechlet.ENDPOINT)
public class CalendarSpeechlet extends AbstractSpeechletDispatcher {
  public static final String ENDPOINT = "/cloud/calendar";

  protected static final String KEY_DIALOG_TYPE = "dialog-type";
  protected static final String DIALOG_TYPE_NEW_EVENT = "new-event";

  private static final Logger log = LoggerFactory.getLogger(CalendarSpeechlet.class);

  @Autowired
  CalendarService calendarService;

  @Autowired
  SpeechService speechService;

  @Autowired
  NewEventDialogService newEventDialogService;

  /**
   * Creates and returns a {@code SpeechletResponse} with a welcome message.
   *
   * @return SpeechletResponse spoken and visual response for the given intent
   */
  @OnLaunch
  public SpeechletResponse getWelcomeResponse(final LaunchRequest request) {
    final OutputSpeech speech = speechService.speechWelcomeMessage(request.getLocale());

    return SpeechletResponse.newAskResponse(speech, new Reprompt());
  }

  /**
   * Creates a {@code SpeechletResponse} for the help intent.
   *
   * @return SpeechletResponse spoken and visual response for the given intent
   */
  @OnIntent("AMAZON.HelpIntent")
  public SpeechletResponse getHelpResponse(final IntentRequest request) {
    final OutputSpeech speech = speechService.speechHelpMessage(request.getLocale());

    return SpeechletResponse.newTellResponse(speech);
  }

  @OnIntent("AMAZON.CancelIntent")
  public SpeechletResponse cancel(final IntentRequest request, final Session session) {
    final OutputSpeech speech;

    if(DIALOG_TYPE_NEW_EVENT.equals(session.getAttribute(KEY_DIALOG_TYPE))){
      speech = speechService.speechCancelNewEvent(request.getLocale());
    } else {
      speech = speechService.speechGeneralConfirmation(request.getLocale());
    }

    return SpeechletResponse.newTellResponse(speech);
  }

  @OnIntent("NewEvent")
  public SpeechletResponse newEvent(final IntentRequest request, final Session session) {
    session.setAttribute(KEY_DIALOG_TYPE, DIALOG_TYPE_NEW_EVENT);

    try {
      return newEventDialogService.handleDialogAction(request, session);
    } catch (CalendarWriteException e) {
      return SpeechletResponse.newTellResponse(speechService.speechError(e));
    }
  }

  @OnIntent("NextEvents")
  public SpeechletResponse getNextEvents(final IntentRequest request){
    final List<Event> nextEvents;

    try {
      nextEvents = calendarService.getNextEvents();
    } catch (CalendarReadException e) {
      log.error("Could not read calendar.", e);
      return SpeechletResponse.newTellResponse(speechService.speechError(e));
    }

    return SpeechletResponse.newTellResponse(speechService.readEvents(request.getLocale(), nextEvents));
  }

  @OnIntent("EventQuery")
  public SpeechletResponse queryEvents(final IntentRequest request) {
    final Map<String, Slot> slots = request.getIntent().getSlots();
    final Moment moment = extractMoment(request.getLocale(),
        slots.get("precision"), slots.get("day"));

    return handleEventQuery(request.getLocale(), moment);
  }

  @OnIntent("EventQueryNear")
  public SpeechletResponse queryNearEvents(final IntentRequest request) {
    final Map<String, Slot> slots = request.getIntent().getSlots();
    final Moment moment = extractMoment(request.getLocale(),
        slots.get("near"));

    return handleEventQuery(request.getLocale(), moment);
  }

  @OnIntent("EventQueryThisWeek")
  public SpeechletResponse queryEventsThisWeek(final IntentRequest request) {
    final Moment moment = Moment.THIS_WEEK;

    return handleEventQuery(request.getLocale(), moment);
  }

  @OnIntent("EventQueryNextWeek")
  public SpeechletResponse queryEventsNextWeek(final IntentRequest request) {
    final Moment moment = Moment.NEXT_WEEK;

    return handleEventQuery(request.getLocale(), moment);
  }

  private SpeechletResponse handleEventQuery(Locale locale, Moment moment) {
    if(moment == null) {
      return SpeechletResponse.newTellResponse(speechService.speechError(new UnknownMomentException()));
    }

    List<Event> nextEvents;
    try {
      nextEvents = calendarService.getEvents(moment.getFrom(), moment.getTo());
    } catch (CalendarReadException e) {
      log.error("Could not read calendar.", e);
      return SpeechletResponse.newTellResponse(speechService.speechError(e));
    }

    return SpeechletResponse.newTellResponse(speechService.readEvents(locale, moment.getName(locale), nextEvents));
  }

  private Moment extractMoment(Locale locale, Slot...slots) {
    final String rawMoment = Stream.of(slots)
        .map(s -> s.getValue())
        .collect(Collectors.joining(" "));

    return Moment.getForLocale(locale, rawMoment);
  }
}
