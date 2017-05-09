package de.rainu.alexa.cloud.speechlet;

import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.SpeechletController;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
import de.rainu.alexa.cloud.calendar.exception.UnknownMomentException;
import de.rainu.alexa.cloud.calendar.model.Event;
import de.rainu.alexa.cloud.calendar.model.Moment;
import de.rainu.alexa.cloud.calendar.service.CalendarService;
import de.rainu.alexa.cloud.service.SpeechService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for handling incoming event query requests.
 */
@SpeechletController(BasicSpeechlet.ENDPOINT)
public class QuerySpeechlet {
  private static final Logger log = LoggerFactory.getLogger(QuerySpeechlet.class);

  @Autowired
  CalendarService calendarService;

  @Autowired
  SpeechService speechService;

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
