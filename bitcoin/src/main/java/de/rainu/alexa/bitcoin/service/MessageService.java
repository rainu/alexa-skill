package de.rainu.alexa.bitcoin.service;

import static de.rainu.alexa.bitcoin.Constants.BEAN_NAMESPACE;

import de.rainu.alexa.bitcoin.config.MessageConfiguration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * This service is responsible for resolving messages.
 */
@Service(MessageService.BEAN_NAME)
public class MessageService {
  public static final String BEAN_NAME = BEAN_NAMESPACE + "MessageService";

  @Autowired @Qualifier(MessageConfiguration.MESSAGES_DE)
  private Map<String, String> messages;

  public String de(String key, Object...args) {
    return String.format(messages.getOrDefault(key, key), args);
  }
}
