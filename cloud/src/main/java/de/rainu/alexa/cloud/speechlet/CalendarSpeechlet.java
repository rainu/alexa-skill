package de.rainu.alexa.cloud.speechlet;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.OnLaunch;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
import de.rainu.alexa.cloud.calendar.model.Event;
import de.rainu.alexa.cloud.calendar.model.Moment;
import de.rainu.alexa.cloud.calendar.service.CalendarService;
import de.rainu.alexa.cloud.service.SpeechService;
import de.rainu.alexa.speechlet.AbstractSpeechletDispatcher;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is responsible for handling all incoming requests.
 */
@RestController
@RequestMapping("/cloud/calendar")
public class CalendarSpeechlet extends AbstractSpeechletDispatcher {
  private static final Logger log = LoggerFactory.getLogger(CalendarSpeechlet.class);

  @Autowired
  CalendarService calendarService;

  @Autowired
  SpeechService speechService;

  /**
   * Creates and returns a {@code SpeechletResponse} with a welcome message.
   *
   * @return SpeechletResponse spoken and visual response for the given intent
   */
  @OnLaunch
  public SpeechletResponse getWelcomeResponse(final LaunchRequest request) {
    final OutputSpeech speech = speechService.speechWelcomeMessage(request.getLocale());

    return SpeechletResponse.newTellResponse(speech);
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
    final List<Event> nextEvents;

    final String moment = request.getIntent().getSlot("moment").getValue();
    final Moment requestedMoment = Moment.getForLocale(request.getLocale(), moment);

    try {
      nextEvents = calendarService.getEvents(requestedMoment.getFrom(), requestedMoment.getTo());
    } catch (CalendarReadException e) {
      log.error("Could not read calendar.", e);
      return SpeechletResponse.newTellResponse(speechService.speechError(e));
    }

    return SpeechletResponse.newTellResponse(speechService.readEvents(request.getLocale(), moment, nextEvents));
  }
}
