package org.lqnotify.pub;

public class TestExceptionA extends Exception {

  public TestExceptionA() {
    trimStackTrace();
  }

  public TestExceptionA(String message) {
    super(message);
    trimStackTrace();
  }

  public TestExceptionA(String message, Throwable cause) {
    super(message, cause);
    trimStackTrace();
  }

  public TestExceptionA(Throwable cause) {
    super(cause);
    trimStackTrace();
  }

  private void trimStackTrace() {
    this.getStackTrace(); // force building
    this.setStackTrace(new StackTraceElement[]{
        new StackTraceElement(TestExceptionA.class.getName(), "topMethod", TestExceptionA.class.getSimpleName()+".java", 13),
        new StackTraceElement(TestExceptionA.class.getName(), "middleMethod", TestExceptionA.class.getSimpleName()+".java", 34),
        new StackTraceElement(TestExceptionA.class.getName(), "bottomMethod", TestExceptionA.class.getSimpleName()+".java", 32)
    });
  }
}
