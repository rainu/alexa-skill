package de.rainu.alexa.speechlet;

import de.rainu.alexa.annotation.OnSessionEnded;
import de.rainu.alexa.annotation.OnSessionStarted;

public class NullSessionHandler {

  @OnSessionStarted
  @OnSessionEnded
  public void doNothing(){}
}
