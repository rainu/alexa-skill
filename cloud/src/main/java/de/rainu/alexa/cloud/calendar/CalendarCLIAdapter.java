package de.rainu.alexa.cloud.calendar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.joda.time.DateTime;

/**
 * This class is a adapter for calendar-cli command.
 *
 * @link https://github.com/tobixen/calendar-cli
 */
public class CalendarCLIAdapter {

  private final String caldavURL;
  private final String caldavUser;
  private final String caldavPassword;
  private final String calendarURL;

  public CalendarCLIAdapter(String caldavURL, String caldavUser, String caldavPassword, String calendarURL) {
    this.caldavURL = caldavURL;
    this.caldavUser = caldavUser;
    this.caldavPassword = caldavPassword;
    this.calendarURL = calendarURL;
  }

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

    return rawEvents;
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
    PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
    exec.setStreamHandler(streamHandler);
    exec.execute(cmd);
    return(outputStream.toString());
  }

}
