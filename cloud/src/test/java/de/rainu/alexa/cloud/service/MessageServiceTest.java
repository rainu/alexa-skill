package de.rainu.alexa.cloud.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MessageServiceTest {

  @Autowired
  MessageService toTest;

  @Test
  public void de(){
    //given
    final String messageKey = "help";

    //when
    final String result = toTest.de(messageKey);

    //then
    assertEquals("Frage mich: was ansteht, um die aktuellen Termine zu erfahren. Oder trage einen neuen Termin ein.", result);
  }

}
