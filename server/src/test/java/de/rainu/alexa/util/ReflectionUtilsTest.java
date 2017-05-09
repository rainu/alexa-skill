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
    ReflectionUtils.call(method, toTest, new Argument(Long.class, 13L));
  }

  @Test
  public void exactMatch() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("integer", Integer.class);

    //when
    final Object result = ReflectionUtils.call(method, toTest, new Argument(Integer.class, 13));

    //then
    assertEquals("integer13", result);
  }

  @Test
  public void oneOfArgumentsMatches() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("integer", Integer.class);

    //when
    final Object result = ReflectionUtils.call(method, toTest,
        new Argument(Integer.class, 13),
        new Argument(Long.class, 14L));

    //then
    assertEquals("integer13", result);
  }

  @Test
  public void multipleArgumentsMatches() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("integer", Integer.class);

    //when
    final Object result = ReflectionUtils.call(method, toTest,
        new Argument(Integer.class, 13),
        new Argument(Integer.class, 14));

    //then
    assertEquals("integer13", result);
  }

  @Test
  public void subClassMatch() throws NoSuchMethodException {
    //given
    TestClass toTest = new TestClass();
    Method method = TestClass.class.getMethod("interfaceMethod", List.class);

    //when
    final Object result = ReflectionUtils.call(method, toTest,
        new Argument(Integer.class, 13),
        new Argument(ArrayList.class, new ArrayList()));

    //then
    assertEquals("interfaceArrayList", result);
  }
}
