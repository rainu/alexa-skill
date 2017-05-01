package de.rainu.alexa.cloud.service;

import de.rainu.alexa.cloud.config.MessageConfiguration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * This service is responsible for resolving messages.
 */
@Service
public class MessageService {

  @Autowired
  @Qualifier(MessageConfiguration.MESSAGES_DE)
  private Map<String, String> messages;

  public String de(String key, Object...args) {
    return String.format(messages.getOrDefault(key, key), args);
  }
}
