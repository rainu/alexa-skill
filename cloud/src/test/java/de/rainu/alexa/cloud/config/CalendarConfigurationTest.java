package de.rainu.alexa.cloud.config;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import static de.rainu.alexa.cloud.config.CalendarConfiguration.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CalendarConfigurationTest {

  @Mock
  ApplicationContext context;

  @Spy
  @InjectMocks
  CalendarConfiguration toTest;

  @Test
  public void oneConfiguration() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();

    //then
    verify(context, times(1)).getBean(
        eq("calendar1"),
        eq("https://cloud.nextcloud.example/remote.php/dav"),
        eq("user"),
        eq("password"),
        eq("https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void oneConfiguration_missingName() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }

  @Test(expected = IllegalArgumentException.class)
  public void oneConfiguration_missingCaldavURL() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }

  @Test(expected = IllegalArgumentException.class)
  public void oneConfiguration_missingUser() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }

  @Test(expected = IllegalArgumentException.class)
  public void oneConfiguration_missingPassword() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }

  @Test(expected = IllegalArgumentException.class)
  public void oneConfiguration_missingCalendarURL() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }

  @Test
  public void twoConfiguration() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav2/calendars/user2/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();

    //then
    verify(context, times(1)).getBean(
        eq("calendar1"),
        eq("https://cloud.nextcloud.example/remote.php/dav"),
        eq("user"),
        eq("password"),
        eq("https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/"));
    verify(context, times(1)).getBean(
        eq("calendar2"),
        eq("https://cloud.nextcloud.example/remote.php/dav2"),
        eq("user2"),
        eq("password2"),
        eq("https://cloud.nextcloud.example/remote.php/dav2/calendars/user2/1c86a231-221d-4df6-ada9-c3425ffa2131/"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void twoConfiguration_missingName() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav2/calendars/user2/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }

  @Test(expected = IllegalArgumentException.class)
  public void twoConfiguration_missingCaldavUrl() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav2/calendars/user2/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }

  @Test(expected = IllegalArgumentException.class)
  public void twoConfiguration_missingUser() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav2/calendars/user2/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }

  @Test(expected = IllegalArgumentException.class)
  public void twoConfiguration_missingPassword() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav2/calendars/user2/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }

  @Test(expected = IllegalArgumentException.class)
  public void twoConfiguration_missingCalendarURL() {
    //given
    Map<String, String> env = new HashMap<>();
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar1");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password");
    env.put(ENVIRONMENT_PREFIX + "0_" + ENVIRONMENT_SUFFIX_CALENDAR_URL, "https://cloud.nextcloud.example/remote.php/dav/calendars/user/1c86a231-221d-4df6-ada9-c3425ffa2131/");

    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALENDAR_NAME, "calendar2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_URL, "https://cloud.nextcloud.example/remote.php/dav2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_USER, "user2");
    env.put(ENVIRONMENT_PREFIX + "1_" + ENVIRONMENT_SUFFIX_CALDAV_PW, "password2");

    doReturn(env).when(toTest).getSystemEnvironment();

    //when
    toTest.buildCalendars();
  }
}
