package de.rainu.alexa.cloud.calendar.model;

import org.joda.time.DateTime;

public class Event {
  private DateTime start;
  private boolean startHasTime;

  private DateTime end;
  private boolean endHasTime;

  private String summary;
  private String description;

  public Event() {
  }

  public Event(DateTime start) {
    this.start = start;
  }

  public DateTime getStart() {
    return start;
  }

  public void setStart(DateTime start, boolean hasTime) {
    this.start = start;
    this.startHasTime = hasTime;
  }

  public DateTime getEnd() {
    return end;
  }

  public void setEnd(DateTime end, boolean hasTime) {
    this.end = end;
    this.endHasTime = hasTime;
  }

  public boolean startHasTime() {
    return startHasTime;
  }

  public boolean endHasTime() {
    return endHasTime;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Event)) {
      return false;
    }

    Event event = (Event) o;

    if (startHasTime != event.startHasTime) {
      return false;
    }
    if (endHasTime != event.endHasTime) {
      return false;
    }
    if (start != null ? !start.equals(event.start) : event.start != null) {
      return false;
    }
    if (end != null ? !end.equals(event.end) : event.end != null) {
      return false;
    }
    if (summary != null ? !summary.equals(event.summary) : event.summary != null) {
      return false;
    }
    return description != null ? description.equals(event.description) : event.description == null;
  }

  @Override
  public int hashCode() {
    int result = start != null ? start.hashCode() : 0;
    result = 31 * result + (startHasTime ? 1 : 0);
    result = 31 * result + (end != null ? end.hashCode() : 0);
    result = 31 * result + (endHasTime ? 1 : 0);
    result = 31 * result + (summary != null ? summary.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Event{" +
        "start=" + start +
        ", startHasTime=" + startHasTime +
        ", end=" + end +
        ", endHasTime=" + endHasTime +
        ", summary='" + summary + '\'' +
        ", description='" + description + '\'' +
        '}';
  }
}
