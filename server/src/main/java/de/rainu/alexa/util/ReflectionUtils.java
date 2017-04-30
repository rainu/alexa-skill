package de.rainu.alexa.util;

import java.lang.reflect.Method;

/**
 * This class contains helpful methods for handling reflection.
 */
public abstract class ReflectionUtils {

  public static Object call(Method method, Object target, Object...arguments) {
    checkArguments(method, arguments);

    Object[] methodArguments = collectArguments(method, arguments);
    return org.springframework.util.ReflectionUtils.invokeMethod(method, target, methodArguments);
  }

  /**
   * Checks if the required arguments are available.
   */
  private static void checkArguments(Method method, Object[] arguments) {
    if(method.getParameterTypes().length > arguments.length){
      throw new IllegalArgumentException("Not enough arguments available for calling " + method.getName());
    }

    for(Class<?> requiredParamType : method.getParameterTypes()){
      boolean found = false;
      for(Object arg : arguments){
        if(requiredParamType.isAssignableFrom(arg.getClass())) {
          found = true;
        }
      }

      if(!found) {
        throw new IllegalArgumentException("No argument of type " + requiredParamType + " available!");
      }
    }
  }

  /**
   * Collect a object array for the given method by the given arguments.
   */
  private static Object[] collectArguments(Method method, Object[] arguments) {
    //at this point all arguments are available
    //no we have to order the arguments

    Object[] targetArgs = new Object[method.getParameterTypes().length];
    outerLoop: for(int i=0; i < method.getParameterTypes().length; i++){
      Class<?> requiredParamType = method.getParameterTypes()[i];

      for(Object arg : arguments){
        if(requiredParamType.isAssignableFrom(arg.getClass())) {
          targetArgs[i] = arg;
          continue outerLoop;
        }
      }
    }

    return targetArgs;
  }
}
