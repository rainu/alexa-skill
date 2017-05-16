package de.rainu.alexa.bitcoin;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.OutputSpeech;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.SpeechletController;
import de.rainu.alexa.bitcoin.model.BitcoinCurse;
import de.rainu.alexa.bitcoin.service.BitcoinService;
import de.rainu.alexa.bitcoin.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@SpeechletController(endpoint = BitcoinSpeechlet.ENDPOINT)
public class BitcoinSpeechlet {
  public static final String ENDPOINT = "/rainu";

  @Autowired @Qualifier(SpeechService.BEAN_NAME)
  SpeechService speechService;

  @Autowired
  BitcoinService bitcoinService;

  @OnIntent("BITCOINGetCurse")
  public SpeechletResponse getCurse(final IntentRequest request, final Session session) {
    final BitcoinCurse currentCurse = bitcoinService.getCurrentCurse();
    final OutputSpeech speech = speechService.speechCurse(currentCurse, request.getLocale());

    return SpeechletResponse.newTellResponse(speech);
  }
}
