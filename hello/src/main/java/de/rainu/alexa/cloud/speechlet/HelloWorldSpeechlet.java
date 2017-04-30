package de.rainu.alexa.cloud.speechlet;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.OnLaunch;
import de.rainu.alexa.speechlet.AbstractSpeechletDispatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorldSpeechlet extends AbstractSpeechletDispatcher {
  /**
   * Creates and returns a {@code SpeechletResponse} with a welcome message.
   *
   * @return SpeechletResponse spoken and visual response for the given intent
   */
  @OnLaunch
  public SpeechletResponse getWelcomeResponse() {
    String speechText = "Welcome to the Alexa Skills Kit, you can say hello";

    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("HelloWorld");
    card.setContent(speechText);

    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    // Create reprompt
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);

    return SpeechletResponse.newAskResponse(speech, reprompt, card);
  }

  /**
   * Creates a {@code SpeechletResponse} for the hello intent.
   *
   * @return SpeechletResponse spoken and visual response for the given intent
   */
  @OnIntent("HelloWorldIntent")
  public SpeechletResponse getHelloResponse() {
    String speechText = "Hello world";

    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("HelloWorld");
    card.setContent(speechText);

    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    return SpeechletResponse.newTellResponse(speech, card);
  }

  /**
   * Creates a {@code SpeechletResponse} for the help intent.
   *
   * @return SpeechletResponse spoken and visual response for the given intent
   */
  @OnIntent("AMAZON.HelpIntent")
  public SpeechletResponse getHelpResponse() {
    String speechText = "You can say hello to me!";

    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("HelloWorld");
    card.setContent(speechText);

    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    // Create reprompt
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);

    return SpeechletResponse.newAskResponse(speech, reprompt, card);
  }
}