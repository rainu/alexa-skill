package de.rainu.alexa.cloud.speechlet;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.OnLaunch;
import de.rainu.alexa.annotation.SpeechletController;
import de.rainu.alexa.cloud.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for handling incoming basic requests.
 */
@SpeechletController(BasicSpeechlet.ENDPOINT)
public class BasicSpeechlet {
  public static final String ENDPOINT = "/cloud/calendar";

  protected static final String KEY_DIALOG_TYPE = "dialog-type";
  protected static final String DIALOG_TYPE_NEW_EVENT = "new-event";

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

  @OnIntent({"AMAZON.CancelIntent", "AMAZON.StopIntent"})
  public SpeechletResponse cancelOrStop(final IntentRequest request, final Session session) {
    final OutputSpeech speech;

    if(DIALOG_TYPE_NEW_EVENT.equals(session.getAttribute(KEY_DIALOG_TYPE))){
      speech = speechService.speechCancelNewEvent(request.getLocale());
    } else {
      speech = speechService.speechGeneralConfirmation(request.getLocale());
    }

    return SpeechletResponse.newTellResponse(speech);
  }
}
