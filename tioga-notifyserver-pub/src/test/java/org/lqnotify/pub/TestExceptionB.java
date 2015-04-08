package org.lqnotify.pub;

public class TestExceptionB extends Exception {

  public TestExceptionB() {
    trimStackTrace();
  }

  public TestExceptionB(String message) {
    super(message);
    trimStackTrace();
  }

  public TestExceptionB(String message, Throwable cause) {
    super(message, cause);
    trimStackTrace();
  }

  public TestExceptionB(Throwable cause) {
    super(cause);
    trimStackTrace();
  }

  private void trimStackTrace() {
    this.getStackTrace(); // force building
    this.setStackTrace(new StackTraceElement[]{
        new StackTraceElement(TestExceptionB.class.getName(), "firstMethod", TestExceptionB.class.getSimpleName()+".java", 133),
        new StackTraceElement(TestExceptionB.class.getName(), "secondMethod", TestExceptionB.class.getSimpleName()+".java", 344),
        new StackTraceElement(TestExceptionB.class.getName(), "thirdMethod", TestExceptionB.class.getSimpleName()+".java", 352)
    });
  }
}
