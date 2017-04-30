package de.rainu.alexa.speechlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import de.rainu.alexa.annotation.OnIntent;
import de.rainu.alexa.annotation.OnLaunch;
import de.rainu.alexa.annotation.OnSessionEnded;
import de.rainu.alexa.annotation.OnSessionStarted;
import org.junit.Test;

public class AbstractSpeechletDispatcherTest {

  public static class TestClass extends AbstractSpeechletDispatcher {
    @OnLaunch
    public SpeechletResponse onStart(LaunchRequest request, Session session) {
      return null;
    }

    @OnIntent("hello")
    public SpeechletResponse onHello(IntentRequest request, Session session) {
      return null;
    }

    @OnIntent("bye")
    public SpeechletResponse onBye(IntentRequest request, Session session) throws SpeechletException {
      throw new SpeechletException("No good bye!");
    }

    @OnSessionStarted
    public void onSessionStarted1(SessionStartedRequest request, Session session) {

    }

    @OnSessionStarted
    public void onSessionStarted2(SessionStartedRequest request, Session session) {

    }

    @OnSessionEnded
    public void onSessionEnded1(SessionEndedRequest request, Session session) {

    }

    @OnSessionEnded
    public void onSessionEnded2(SessionEndedRequest request, Session session) {

    }
  }

  public static class InvalidHandler extends AbstractSpeechletDispatcher {
    @OnLaunch
    public void onStart(LaunchRequest request, Session session) {
    }

    @OnIntent("hello")
    public void onHello(IntentRequest request, Session session) {
    }
  }

  @Test
  public void onIntent() throws SpeechletException {
    //given
    TestClass toTest = spy(new TestClass());

    SpeechletResponse aResponse = mock(SpeechletResponse.class);
    doReturn(aResponse).when(toTest).onHello(any(), any());

    IntentRequest request = mock(IntentRequest.class);
    Intent intent = Intent.builder().withName("hello").build();
    doReturn(intent).when(request).getIntent();
    Session session = mock(Session.class);

    //when
    final SpeechletResponse response = toTest.onIntent(request, session);

    //then
    assertSame(aResponse, response);
    verify(toTest, times(1)).onHello(same(request), same(session));
    verify(toTest, times(0)).onBye(any(), any());
  }

  @Test
  public void intentThrowsException() throws SpeechletException {
    //given
    TestClass toTest = spy(new TestClass());

    SpeechletResponse aResponse = mock(SpeechletResponse.class);
    doReturn(aResponse).when(toTest).onHello(any(), any());

    IntentRequest request = mock(IntentRequest.class);
    Intent intent = Intent.builder().withName("bye").build();
    doReturn(intent).when(request).getIntent();
    Session session = mock(Session.class);

    //when
    try {
      toTest.onIntent(request, session);
      fail("Exception expected!");
    } catch (SpeechletException e) {
      assertEquals("No good bye!", e.getMessage());
    }

    //then
    verify(toTest, times(1)).onBye(any(), any());
    verify(toTest, times(0)).onHello(same(request), same(session));
  }

  @Test
  public void onLaunch() throws SpeechletException {
    //given
    TestClass toTest = spy(new TestClass());

    SpeechletResponse aResponse = mock(SpeechletResponse.class);
    doReturn(aResponse).when(toTest).onStart(any(), any());

    LaunchRequest request = mock(LaunchRequest.class);
    Session session = mock(Session.class);

    //when
    final SpeechletResponse response = toTest.onLaunch(request, session);

    //then
    assertSame(aResponse, response);
    verify(toTest, times(1)).onStart(same(request), same(session));
  }

  @Test
  public void onSessionStarted() throws SpeechletException {
    //given
    TestClass toTest = spy(new TestClass());

    SessionStartedRequest request = mock(SessionStartedRequest.class);
    Session session = mock(Session.class);

    //when
    toTest.onSessionStarted(request, session);

    //then
    verify(toTest, times(1)).onSessionStarted1(same(request), same(session));
    verify(toTest, times(1)).onSessionStarted2(same(request), same(session));
  }

  @Test
  public void onSessionEnded() throws SpeechletException {
    //given
    TestClass toTest = spy(new TestClass());

    SessionEndedRequest request = SessionEndedRequest.builder().withRequestId("requestId").build();
    Session session = mock(Session.class);

    //when
    toTest.onSessionEnded(request, session);

    //then
    verify(toTest, times(1)).onSessionEnded1(same(request), same(session));
    verify(toTest, times(1)).onSessionEnded2(same(request), same(session));
  }

  @Test
  public void invalidLaunchHandler() {
    //given
    InvalidHandler toTest = spy(new InvalidHandler());

    LaunchRequest request = mock(LaunchRequest.class);
    Session session = mock(Session.class);

    //when
    try {
      toTest.onLaunch(request, session);
      fail("Exception expected!");
    } catch (SpeechletException e) {}

    //then
    verify(toTest, never()).onStart(any(), any());
  }

  @Test
  public void invalidIntentHandler() throws SpeechletException {
    //given
    InvalidHandler toTest = spy(new InvalidHandler());

    IntentRequest request = mock(IntentRequest.class);
    Intent intent = Intent.builder().withName("hello").build();
    doReturn(intent).when(request).getIntent();
    Session session = mock(Session.class);

    //when
    try {
      toTest.onIntent(request, session);
      fail("Exception expected!");
    } catch (SpeechletException e) {}

    //then
    verify(toTest, never()).onHello(any(), any());
  }
}
