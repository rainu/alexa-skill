package de.rainu.alexa.bitcoin;

import static de.rainu.alexa.bitcoin.Constants.BEAN_NAMESPACE;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.Reprompt;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.OnLaunch;
import de.rainu.alexa.annotation.SpeechletController;
import de.rainu.alexa.bitcoin.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This class is responsible for handling incoming basic requests.
 */
@SpeechletController(name = BEAN_NAMESPACE + "BasicSpeechlet", endpoint = BitcoinSpeechlet.ENDPOINT)
public class BasicSpeechlet {

  @Autowired @Qualifier(SpeechService.BEAN_NAME)
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
    final OutputSpeech speech = speechService.speechBye(request.getLocale());

    final SpeechletResponse response = SpeechletResponse.newTellResponse(speech);
    return response;
  }
}
