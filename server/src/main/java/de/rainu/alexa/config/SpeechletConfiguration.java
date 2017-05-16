package de.rainu.alexa.config;

import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.OnLaunch;
import de.rainu.alexa.annotation.OnSessionEnded;
import de.rainu.alexa.annotation.OnSessionStarted;
import de.rainu.alexa.annotation.SpeechletController;
import de.rainu.alexa.speechlet.NullSessionHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This class is responsible for initialisation of each speechlet found in the application context.
 * Each speechlet must have a {@link RequestMapping}-Annotation within the corresponding mapping url.
 */
@Configuration
public class SpeechletConfiguration implements ServletContextInitializer {
  private static final Logger log = LoggerFactory.getLogger(SpeechletConfiguration.class);

  public static final String[] REQUIRED_INTENTS = new String[]{
      "AMAZON.HelpIntent", "AMAZON.StopIntent", "AMAZON.CancelIntent"
  };
  public static final Set<String> REQUIRED_INTENT_SET = new HashSet<>(Arrays.asList(REQUIRED_INTENTS));

  private int servletCount = 0;

  @Autowired
  private ApplicationContext context;

  private SpeechletHandlerHolder nullSessionHandler;

  public SpeechletConfiguration() {
    this.nullSessionHandler = buildHandler(null, Arrays.asList(new NullSessionHandler()));
  }

  @Override
  public void onStartup(ServletContext ctx) throws ServletException {
    final Map<String, Object> speechlets = context.getBeansWithAnnotation(SpeechletController.class);

    final Map<String, List<Object>> endpointMapping = mapEndpoints(speechlets);
    final Map<String, SpeechletHandlerHolder> handlerMapping = mapHandler(endpointMapping);

    validateAndLog(handlerMapping);
    configure(ctx, handlerMapping);
  }

  private void configure(ServletContext ctx, Map<String, SpeechletHandlerHolder> mapping) {
    for(Entry<String, SpeechletHandlerHolder> entry : mapping.entrySet()) {
      configure(ctx, entry.getKey(), entry.getValue());
    }
  }

  private void configure(ServletContext ctx, String endpoint, SpeechletHandlerHolder handler) {
    final SpeechletServlet servlet = new SpeechletServletDispatcher(handler);

    ServletRegistration.Dynamic serviceServlet = ctx.addServlet("SpeechletServlet_" + (servletCount++), servlet);
    serviceServlet.addMapping(endpoint);
  }

  private void validateAndLog(Map<String, SpeechletHandlerHolder> mapping) {
    for(Entry<String, SpeechletHandlerHolder> entry : mapping.entrySet()) {
      final String endpoint = entry.getKey();
      final SpeechletHandlerHolder handler = entry.getValue();

      setFallbackSessionHandler(handler);

      checkRequired(endpoint, handler);
      logHandler(endpoint, handler);
    }
  }

  private void setFallbackSessionHandler(SpeechletHandlerHolder handler) {
    final Method method = nullSessionHandler.onSessionStarted.get(0).method;
    final Object instance = nullSessionHandler.onSessionStarted.get(0).handler;
    if(handler.onSessionStarted.isEmpty()) {
      handler.addSessionStarted(method, instance);
    }
    if(handler.onSessionEnded.isEmpty()) {
      handler.addSessionEnded(method, instance);
    }
  }

  private void logHandler(String endpoint, SpeechletHandlerHolder handler) {
    StringBuilder msg = new StringBuilder("Mapping URL path: ");
    msg.append(endpoint);
    msg.append("\n\tOnLaunch\n\t\t");
    msg.append(handler.onLaunch);
    msg.append("\n\tOnSessionStarted");
    for(SpeechletHandler<OnSessionStarted> h : handler.onSessionStarted) {
      msg.append("\n\t\t");
      msg.append(h);
    }
    msg.append("\n\tOnSessionEnded");
    for(SpeechletHandler<OnSessionEnded> h : handler.onSessionEnded) {
      msg.append("\n\t\t");
      msg.append(h);
    }
    msg.append("\n\tOnIntent");
    for(String intent : REQUIRED_INTENT_SET.stream().sorted().collect(Collectors.toList())) {
      msg.append("\n\t\t[");
      msg.append(intent);
      msg.append("] ");
      msg.append(handler.onIntent.get(intent));
    }
    for(String intent : handler.onIntent.keySet().stream().sorted().collect(Collectors.toList())) {
      if(REQUIRED_INTENT_SET.contains(intent)) {
        continue;
      }

      msg.append("\n\t\t[");
      msg.append(intent);
      msg.append("] ");
      msg.append(handler.onIntent.get(intent));
    }

    log.info(msg.toString());
  }

  private void checkRequired(String endpoint, SpeechletHandlerHolder handler) {
    if(handler.onLaunch == null) {
      throw new IllegalStateException("Missing OnLaunch handler for endpoint '" + endpoint + "' !");
    }
    if(handler.onSessionStarted.isEmpty()) {
      throw new IllegalStateException("Missing OnSessionStarted handler for endpoint '" + endpoint + "' !");
    }
    if(handler.onSessionEnded.isEmpty()) {
      throw new IllegalStateException("Missing OnSessionEnded handler for endpoint '" + endpoint + "' !");
    }
    if(handler.onIntent.isEmpty()) {
      throw new IllegalStateException("Missing OnIntent handler for endpoint '" + endpoint + "' !");
    }
    for(String intent : REQUIRED_INTENTS) {
      if(!handler.onIntent.containsKey(intent)) {
        throw new IllegalStateException("Missing intent handler for '" + intent + "' for endpoint '" + endpoint + "' !");
      }
    }
  }

  private Map<String, SpeechletHandlerHolder> mapHandler(Map<String, List<Object>> endpointMapping) {
    Map<String, SpeechletHandlerHolder> mapping = new HashMap<>();
    for(Entry<String, List<Object>> entry : endpointMapping.entrySet()){
      final String endpoint = entry.getKey();
      final SpeechletHandlerHolder handler = buildHandler(endpoint, entry.getValue());

      mapping.put(endpoint, handler);
    }

    return mapping;
  }

  private SpeechletHandlerHolder buildHandler(String endpoint, List<Object> rawHandler) {
    SpeechletHandlerHolder holder = new SpeechletHandlerHolder();

    for(Object curRawHandler : rawHandler) {
      for(Method method : curRawHandler.getClass().getDeclaredMethods()) {
        if(method.isAnnotationPresent(OnLaunch.class)) {
          if(holder.onLaunch != null) {
            SpeechletHandler<OnLaunch> curHandler = new SpeechletHandler<>();
            curHandler.handler = curRawHandler;
            curHandler.method = method;

            throw new IllegalStateException("Multiple OnLaunch handler for endpoint '" + endpoint + "' found!\n\t" + holder.onLaunch + "\n\t" + curHandler);
          }

          holder.setOnLaunch(method, curRawHandler);
        } else if(method.isAnnotationPresent(OnSessionStarted.class)){
          holder.addSessionStarted(method, curRawHandler);
        } else if(method.isAnnotationPresent(OnSessionEnded.class)){
          holder.addSessionEnded(method, curRawHandler);
        } else if (method.isAnnotationPresent(OnIntent.class)) {
          holder.addOnIntent(method, curRawHandler);
        }
      }
    }

    return holder;
  }

  private Map<String, List<Object>> mapEndpoints(Map<String, Object> speechlets) {
    Map<String, List<Object>> endpointMapping = new HashMap<>();

    for(Entry<String, Object> entry : speechlets.entrySet()) {
      final Object speechlet = entry.getValue();
      final SpeechletController cfg = speechlet.getClass().getAnnotation(SpeechletController.class);

      for(String endpoint : cfg.endpoint()) {
        if (!endpointMapping.containsKey(endpoint)) {
          endpointMapping.put(endpoint, new ArrayList<>());
        }

        endpointMapping.get(endpoint).add(speechlet);
      }
    }

    return endpointMapping;
  }
}