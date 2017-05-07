package de.rainu.alexa.cloud.calendar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a adapter for calendar-cli command.
 *
 * @link https://github.com/tobixen/calendar-cli
 */
public class CalendarCLIAdapter {
  private static final Logger log = LoggerFactory.getLogger(CalendarCLIAdapter.class);

  private final String caldavURL;
  private final String caldavUser;
  private final String caldavPassword;
  private final String calendarURL;
  private final TimeZone calendarTimeZone;

  private final static Pattern eventUUID = Pattern.compile("uid=([0-9a-fA-F-]*)");

  private static Map<String, String> env;
  static {
    env = new HashMap<>();

    //we need this env to prevent exceptions on umlauts (each calendar entry can have one -> we can not know that)
    env.put("PYTHONIOENCODING", "UTF-8");
    env.putAll(System.getenv());
  }

  public CalendarCLIAdapter(String caldavURL, String caldavUser, String caldavPassword, String calendarURL) {
    this(caldavURL, caldavUser, caldavPassword, calendarURL, TimeZone.getTimeZone("UTC"));
  }

  public CalendarCLIAdapter(String caldavURL, String caldavUser, String caldavPassword, String calendarURL, TimeZone tz) {
    this.caldavURL = caldavURL;
    this.caldavUser = caldavUser;
    this.caldavPassword = caldavPassword;
    this.calendarURL = calendarURL;
    this.calendarTimeZone = tz;
  }

  public TimeZone getDefaultTimeZone() {
    return calendarTimeZone;
  }

  /**
   * Reads all events from this calendar.
   *
   * @param from Startdate to search
   * @param to Enddate to search
   * @return a {@link List} of all raw (ical format) found events.
   * @throws IOException If the underlying process was failed.
   */
  public List<String> readAgenda(DateTime from, DateTime to) throws IOException {
    List<String> subCommands = new ArrayList<>();

    subCommands.add("--icalendar");
    subCommands.add("calendar");
    subCommands.add("agenda");

    if(from != null) {
      subCommands.add("--from-time");
      subCommands.add(from.toString());
    }
    if(to != null) {
      subCommands.add("--to-time");
      subCommands.add(to.toString());
    }

    final String rawOutput = execute(subCommands);

    List<String> rawEvents = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    for(String line : rawOutput.split("\n")){
      if(line.isEmpty() && sb.length() > 0) {
        rawEvents.add(sb.toString().trim());
        sb = new StringBuilder();
      }

      sb.append(line);
      sb.append("\n");
    }
    if(sb.length() > 0) {
      rawEvents.add(sb.toString().trim());
    }
    Iterator<String> iter = rawEvents.iterator();
    while (iter.hasNext()) {
      if(iter.next().isEmpty()) {
        iter.remove();
      }
    }

    return rawEvents;
  }

  /**
   * Creates a event in this calendar.
   *
   * @param summary the summary of event
   * @param from Startdate of event
   * @param to Enddate of event
   * @return a uuid of the created events.
   * @throws IOException If the underlying process was failed.
   */
  public String createEvent(final String summary, final DateTime from, final DateTime to) throws IOException {
    List<String> subCommands = new ArrayList<>();

    subCommands.add("calendar");
    subCommands.add("add");

    final Duration duration = new Interval(from, to).toDuration();
    final StringBuilder eventTime = new StringBuilder();
    eventTime.append(from.toString("yyyy-MM-dd'T'HH:mm"));
    eventTime.append("+");
    eventTime.append(duration.getStandardMinutes());
    eventTime.append("m");

    subCommands.add(eventTime.toString());
    subCommands.add(summary);

    final String rawOutput = execute(subCommands);
    final String uid;
    final Matcher matcher = eventUUID.matcher(rawOutput);
    if(matcher.find()) {
      uid = matcher.group(1);
    }else{
      uid = "<unknown>";
    }

    return uid;
  }

  private String execute(List<String> subCommands) throws IOException {
    CommandLine cmd = new CommandLine("calendar-cli.py");
    cmd.addArgument("--caldav-url");
    cmd.addArgument(caldavURL);
    cmd.addArgument("--caldav-user");
    cmd.addArgument(caldavUser);
    cmd.addArgument("--caldav-pass");
    cmd.addArgument(caldavPassword);
    cmd.addArgument("--calendar-url");
    cmd.addArgument(calendarURL);

    for(String subCommand : subCommands) {
      cmd.addArgument(subCommand);
    }

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    DefaultExecutor exec = new DefaultExecutor();
    final PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
    exec.setStreamHandler(streamHandler);

    try {
      doExecute(cmd, exec);
    }catch(IOException e) {
      log.error("Error on executing calendar-cli.py.\nCommand: {}\nOutput: {}",
          cmd.toString(),
          outputStream.toString());
      throw e;
    }

    return outputStream.toString();
  }

  void doExecute(CommandLine cmd, DefaultExecutor exec) throws IOException {
    long time = System.currentTimeMillis();

    try {
      exec.execute(cmd, env);
    }finally{
      if(log.isDebugEnabled()) {
        log.debug("Execution of command takes {}ms:\n{}",
            (System.currentTimeMillis() - time),
            cmd.toString());
      }
    }
  }

}
