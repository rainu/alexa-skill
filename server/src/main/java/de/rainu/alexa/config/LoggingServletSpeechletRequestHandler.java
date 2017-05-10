package de.rainu.alexa.config;

import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletRequestHandlerException;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.servlet.ServletSpeechletRequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingServletSpeechletRequestHandler extends ServletSpeechletRequestHandler {

  private static final Logger log = LoggerFactory.getLogger(LoggingServletSpeechletRequestHandler.class);

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public byte[] handleSpeechletCall(SpeechletV2 speechlet, byte[] serializedSpeechletRequest)
      throws IOException, SpeechletRequestHandlerException, SpeechletException {

    if(log.isDebugEnabled()) {
      String json;
      try {
        json = mapper.writerWithDefaultPrettyPrinter()
              .writeValueAsString(mapper.readValue(serializedSpeechletRequest, Object.class));
      }catch (Exception e) {
        json = new String(serializedSpeechletRequest);
      }

      log.debug("Incoming request:\n{}", json);
    }

    byte[] response = super.handleSpeechletCall(speechlet, serializedSpeechletRequest);

    if(log.isDebugEnabled()) {
      String json;
      try {
        json = mapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(mapper.readValue(response, Object.class));
      }catch (Exception e) {
        json = new String(response);
      }

      log.debug("Outgoing response:\n{}", json);
    }

    return response;
  }
}
