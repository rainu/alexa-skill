package de.rainu.alexa.cloud.speechlet;

import biweekly.component.VEvent;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.OnLaunch;
import de.rainu.alexa.cloud.calendar.exception.CalendarReadException;
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
  public SpeechletResponse getWelcomeResponse() {
    final OutputSpeech speech = speechService.speechWelcomeMessage();

    return SpeechletResponse.newTellResponse(speech);
  }

  /**
   * Creates a {@code SpeechletResponse} for the help intent.
   *
   * @return SpeechletResponse spoken and visual response for the given intent
   */
  @OnIntent("AMAZON.HelpIntent")
  public SpeechletResponse getHelpResponse() {
    final OutputSpeech speech = speechService.speechHelpMessage();

    return SpeechletResponse.newTellResponse(speech);
  }

  @OnIntent("NextEvents")
  public SpeechletResponse getNextEvents(){
    final List<VEvent> nextEvents;

    try {
      nextEvents = calendarService.getNextEvents();
    } catch (CalendarReadException e) {
      log.error("Could not read calendar.", e);
      return SpeechletResponse.newTellResponse(speechService.speechError(e));
    }

    return SpeechletResponse.newTellResponse(speechService.readEvents(nextEvents));
  }
}
