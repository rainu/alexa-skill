package de.rainu.alexa.util;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ReflectionUtilsTest {

  public static class TestClass {
    public String integer(Integer i) {
      return "integer" + i;
    }

    public String interfaceMethod(List list) {
      return "interface" + list.getClass().getSimpleName();
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void argumentsNotEnough() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("integer", Integer.class);

    //when
    ReflectionUtils.call(method, toTest);
  }

  @Test(expected = IllegalArgumentException.class)
  public void argumentsNotRight() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("integer", Integer.class);

    //when
    ReflectionUtils.call(method, toTest, 13L);
  }

  @Test
  public void exactMatch() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("integer", Integer.class);

    //when
    final Object result = ReflectionUtils.call(method, toTest, 13);

    //then
    assertEquals("integer13", result);
  }

  @Test
  public void oneOfArgumentsMatches() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("integer", Integer.class);

    //when
    final Object result = ReflectionUtils.call(method, toTest, 13, 14L);

    //then
    assertEquals("integer13", result);
  }

  @Test
  public void multipleArgumentsMatches() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("integer", Integer.class);

    //when
    final Object result = ReflectionUtils.call(method, toTest, 13, 14);

    //then
    assertEquals("integer13", result);
  }

  @Test
  public void subClassMatch() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("interfaceMethod", List.class);

    //when
    final Object result = ReflectionUtils.call(method, toTest, 13, new ArrayList());

    //then
    assertEquals("interfaceArrayList", result);
  }
}
