package de.rainu.alexa.cloud.speechlet;

import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.SpeechletController;
import de.rainu.alexa.cloud.calendar.exception.CalendarWriteException;
import de.rainu.alexa.cloud.calendar.service.NewEventDialogService;
import de.rainu.alexa.cloud.service.SpeechService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for handling incoming event creation requests.
 */
@SpeechletController(BasicSpeechlet.ENDPOINT)
public class NewEventSpeechlet {

  @Autowired
  NewEventDialogService newEventDialogService;

  @Autowired
  SpeechService speechService;

  @OnIntent("NewEvent")
  public SpeechletResponse newEvent(final IntentRequest request, final Session session) {
    session.setAttribute(BasicSpeechlet.KEY_DIALOG_TYPE, BasicSpeechlet.DIALOG_TYPE_NEW_EVENT);

    try {
      return newEventDialogService.handleDialogAction(request, session);
    } catch (CalendarWriteException e) {
      return SpeechletResponse.newTellResponse(speechService.speechError(e));
    }
  }
}
