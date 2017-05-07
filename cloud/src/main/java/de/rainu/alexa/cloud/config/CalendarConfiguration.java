package de.rainu.alexa.cloud.config;

import de.rainu.alexa.cloud.calendar.CalendarCLIAdapter;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * This class is responsible for building {@link CalendarCLIAdapter} instances per each environment config set.
 */
@Configuration
public class CalendarConfiguration implements BeanFactoryAware {
  private static final Logger log = LoggerFactory.getLogger(CalendarConfiguration.class);

  public static final String ENVIRONMENT_PREFIX = "CALDAV_";
  public static final String ENVIRONMENT_SUFFIX_CALENDAR_URL = "CALENDAR_URL";
  public static final String ENVIRONMENT_SUFFIX_CALDAV_USER = "USER";
  public static final String ENVIRONMENT_SUFFIX_CALDAV_PW = "PASSWORD";
  public static final String ENVIRONMENT_SUFFIX_CALDAV_URL = "URL";
  public static final String ENVIRONMENT_SUFFIX_CALENDAR_NAME = "NAME";
  public static final String ENVIRONMENT_SUFFIX_TIMEZONE = "TIMEZONE";

  public static String NAME_OF_DEFAULT_CALENDAR;

  ConfigurableBeanFactory beanFactory;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = (ConfigurableBeanFactory)beanFactory;
  }

  @PostConstruct
  public void buildCalendars() {
    Map<String, String> relevantEnv = getSystemEnvironment().entrySet().stream()
        .filter(env -> env.getKey().startsWith(ENVIRONMENT_PREFIX))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

    if(relevantEnv.isEmpty()) {
      log.warn("No calendars defined!");
      return;
    }

    long count = relevantEnv.keySet().stream()
        .filter(e -> e.endsWith(ENVIRONMENT_SUFFIX_CALDAV_URL))
        .filter(e -> !e.endsWith(ENVIRONMENT_SUFFIX_CALENDAR_URL))
        .count();

    if (count > 1) {
      for (int i = 0; i < count; i++) {
        final String caldavURL = relevantEnv.get(env(i, ENVIRONMENT_SUFFIX_CALDAV_URL));
        final String caldavUser = relevantEnv.get(env(i, ENVIRONMENT_SUFFIX_CALDAV_USER));
        final String caldavPW = relevantEnv.get(env(i, ENVIRONMENT_SUFFIX_CALDAV_PW));
        final String calendarURL = relevantEnv.get(env(i, ENVIRONMENT_SUFFIX_CALENDAR_URL));
        final String calendarName = relevantEnv.get(env(i, ENVIRONMENT_SUFFIX_CALENDAR_NAME));
        final String timezone = relevantEnv.get(env(i, ENVIRONMENT_SUFFIX_TIMEZONE));

        if(i==0){
          //the first calendar is the default calendar
          NAME_OF_DEFAULT_CALENDAR = calendarName;
        }

        check(caldavURL, i, ENVIRONMENT_SUFFIX_CALDAV_URL);
        check(calendarName, i, ENVIRONMENT_SUFFIX_CALENDAR_NAME);
        check(caldavUser, i, ENVIRONMENT_SUFFIX_CALDAV_USER);
        check(caldavPW, i, ENVIRONMENT_SUFFIX_CALDAV_PW);
        check(calendarURL, i, ENVIRONMENT_SUFFIX_CALENDAR_URL);

        buildBean(calendarName, caldavURL, caldavUser, caldavPW, calendarURL, timezone);
      }
    }else{
      final String caldavURL = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_URL);
      final String caldavUser = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_USER);
      final String caldavPW = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_PW);
      final String calendarURL = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_URL);
      final String calendarName = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_NAME);
      final String timezone = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_TIMEZONE);

      NAME_OF_DEFAULT_CALENDAR = calendarName;

      check(caldavURL, null, ENVIRONMENT_SUFFIX_CALDAV_URL);
      check(calendarName, null, ENVIRONMENT_SUFFIX_CALENDAR_NAME);
      check(caldavUser, null, ENVIRONMENT_SUFFIX_CALDAV_USER);
      check(caldavPW, null, ENVIRONMENT_SUFFIX_CALDAV_PW);
      check(calendarURL, null, ENVIRONMENT_SUFFIX_CALENDAR_URL);

      buildBean(calendarName, caldavURL, caldavUser, caldavPW, calendarURL, timezone);
    }
  }

  void buildBean(String calendarName, String caldavURL, String caldavUser, String caldavPW, String calendarURL, String timezone) {
    log.info("New calendar configured: " + calendarName);
    final CalendarCLIAdapter adapter;

    if(timezone != null) {
      adapter = new CalendarCLIAdapter(caldavURL, caldavUser, caldavPW, calendarURL, TimeZone.getTimeZone(timezone));
    } else {
      adapter = new CalendarCLIAdapter(caldavURL, caldavUser, caldavPW, calendarURL);
    }

    beanFactory.registerSingleton(calendarName, adapter);
  }

  Map<String, String> getSystemEnvironment() {
    return System.getenv();
  }

  private void check(String value, Integer number, String suffix) {
    if (StringUtils.isEmpty(value)) {
      if(number != null) {
        throw new IllegalArgumentException("Environment variable '" + env(number, suffix) + "' is missing!");
      }else{
        throw new IllegalArgumentException("Environment variable '" + ENVIRONMENT_PREFIX + suffix + "' is missing!");
      }
    }
  }

  private String env(int i, String suffix) {
    return ENVIRONMENT_PREFIX + i + "_" + suffix;
  }
}