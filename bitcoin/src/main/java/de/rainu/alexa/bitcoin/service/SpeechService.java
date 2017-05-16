package de.rainu.alexa.bitcoin.service;

import static de.rainu.alexa.bitcoin.Constants.BEAN_NAMESPACE;

import com.amazon.speech.ui.OutputSpeech;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import de.rainu.alexa.bitcoin.model.BitcoinCurse;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service(SpeechService.BEAN_NAME)
public class SpeechService {
  public static final String BEAN_NAME = BEAN_NAMESPACE + "SpeechService";

  @Autowired @Qualifier(MessageService.BEAN_NAME)
  private MessageService messageService;

  public OutputSpeech speechWelcomeMessage(Locale locale) {
    final String speechText = messageService.de("welcome");
    return speechMessage(speechText);
  }

  public OutputSpeech speechBye(Locale locale) {
    final String speechText = messageService.de("bye");
    return speechMessage(speechText);
  }

  public OutputSpeech speechHelpMessage(Locale locale) {
    final String speechText = messageService.de("help");
    return speechMessage(speechText);
  }

  public OutputSpeech speechCurse(BitcoinCurse bitcoinCurse, Locale locale) {
    final String speechText = messageService.de("bitcoin.curse.current",
        bitcoinCurse.getEuro()
    );

    return speechMessage(speechText);
  }

  private OutputSpeech speechMessage(String speechText) {
    // Create the plain text output.
    if (speechText.contains("<")) {
      SsmlOutputSpeech speech = new SsmlOutputSpeech();

      final String targetSpeech = "<speak>" + speechText + "</speak>";
      speech.setSsml(targetSpeech);

      return speech;
    } else {
      PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
      speech.setText(speechText);

      return speech;
    }
  }
}
