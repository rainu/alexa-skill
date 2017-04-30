package de.rainu.alexa.cloud.config;

import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class CalendarConfiguration {

  public static final String ENVIRONMENT_PREFIX = "CALDAV_";
  public static final String ENVIRONMENT_SUFFIX_CALENDAR_URL = "CALENDAR_URL";
  public static final String ENVIRONMENT_SUFFIX_CALDAV_USER = "USER";
  public static final String ENVIRONMENT_SUFFIX_CALDAV_PW = "PASSWORD";
  public static final String ENVIRONMENT_SUFFIX_CALDAV_URL = "URL";
  public static final String ENVIRONMENT_SUFFIX_CALENDAR_NAME = "NAME";

  @Autowired
  private ApplicationContext context;

  @PostConstruct
  public void buildCalendars() {
    Map<String, String> relevantEnv = getSystemEnvironment().entrySet().stream()
        .filter(env -> env.getKey().startsWith(ENVIRONMENT_PREFIX))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

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

        check(caldavURL, i, ENVIRONMENT_SUFFIX_CALDAV_URL);
        check(calendarName, i, ENVIRONMENT_SUFFIX_CALENDAR_NAME);
        check(caldavUser, i, ENVIRONMENT_SUFFIX_CALDAV_USER);
        check(caldavPW, i, ENVIRONMENT_SUFFIX_CALDAV_PW);
        check(calendarURL, i, ENVIRONMENT_SUFFIX_CALENDAR_URL);

        context.getBean(calendarName,
            caldavURL, caldavUser, caldavPW, calendarURL);
      }
    }else{
      final String caldavURL = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_URL);
      final String caldavUser = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_USER);
      final String caldavPW = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_PW);
      final String calendarURL = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_URL);
      final String calendarName = relevantEnv.get(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_NAME);

      check(caldavURL, null, ENVIRONMENT_SUFFIX_CALDAV_URL);
      check(calendarName, null, ENVIRONMENT_SUFFIX_CALENDAR_NAME);
      check(caldavUser, null, ENVIRONMENT_SUFFIX_CALDAV_USER);
      check(caldavPW, null, ENVIRONMENT_SUFFIX_CALDAV_PW);
      check(calendarURL, null, ENVIRONMENT_SUFFIX_CALENDAR_URL);

      context.getBean(calendarName,
          caldavURL, caldavUser, caldavPW, calendarURL);
    }
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