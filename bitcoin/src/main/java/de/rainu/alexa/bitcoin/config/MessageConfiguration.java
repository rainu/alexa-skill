package de.rainu.alexa.bitcoin.config;

import static de.rainu.alexa.bitcoin.Constants.BEAN_NAMESPACE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is responsible for define all used messages.
 */
@Configuration(BEAN_NAMESPACE + "MessageConfiguration")
public class MessageConfiguration {

//FIXME: implements message resource bundles (i have try it but have no solution found ;( )
//  @Bean
//  public ResourceBundleMessageSource messageSource() {
//    ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
//    resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
//    return resourceBundleMessageSource;
//  }

  public static final String MESSAGES_DE = BEAN_NAMESPACE + "messages_de";

  @Bean(MESSAGES_DE)
  public Map<String, String> messages() {
    Map<String, String> m = new HashMap<>();

    m.put("welcome", "Ich. Bin. Rainu.");
    m.put("confirm", "OK.");
    m.put("bye", "Bis zum nächsten mal.");
    m.put("help", "DU bist der Meister!");

    m.put("bitcoin.curse.current", "Ein Bitcoin ist momentan %s Euro wert!");

    return Collections.unmodifiableMap(m);
  }
}
