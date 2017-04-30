package de.rainu.alexa.config;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.servlet.SpeechletServlet;
import java.util.Map;
import java.util.Map.Entry;
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

  @Autowired
  private ApplicationContext context;

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    Map<String, Speechlet> speechlets = context.getBeansOfType(Speechlet.class);

    for (Entry<String, Speechlet> speechletBean : speechlets.entrySet()) {
      final Speechlet speechlet = speechletBean.getValue();

      configureServlet(servletContext, speechlet);
    }
  }

  private static void configureServlet(final ServletContext servletContext, final Speechlet speechlet) {
    final RequestMapping mapping = speechlet.getClass().getAnnotation(RequestMapping.class);
    final SpeechletServlet servlet = new SpeechletServlet();
    servlet.setSpeechlet(speechlet);

    ServletRegistration.Dynamic serviceServlet = servletContext.addServlet(speechlet.getClass().getName(), servlet);
    serviceServlet.addMapping(mapping.value().length != 0 ? mapping.value() : mapping.path());

    log.info("Mapping URL path {} onto handler of type [{}]",
        serviceServlet.getMappings(),
        speechlet.getClass());
  }
}