package org.lqnotify.pub;

import com.fasterxml.jackson.annotation.*;

public class ExceptionTraceElement {

  private final String className;
  private final String methodName;
  private final String fileName;
  private final int lineNumber;

  @JsonCreator
  public ExceptionTraceElement(@JsonProperty("className") String className,
                               @JsonProperty("methodName") String methodName,
                               @JsonProperty("fileName") String fileName,
                               @JsonProperty("lineNumber") int lineNumber) {

    this.className = className;
    this.methodName = methodName;
    this.fileName = fileName;
    this.lineNumber = lineNumber;
  }

  public ExceptionTraceElement(StackTraceElement element) {
    this.className = element.getClassName();
    this.methodName = element.getMethodName();
    this.fileName = element.getFileName();
    this.lineNumber = element.getLineNumber();
  }

  public String getClassName() {
    return className;
  }

  public String getMethodName() {
    return methodName;
  }

  public String getFileName() {
    return fileName;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ExceptionTraceElement that = (ExceptionTraceElement) o;

    if (lineNumber != that.lineNumber) return false;
    if (className != null ? !className.equals(that.className) : that.className != null) return false;
    if (fileName != null ? !fileName.equals(that.fileName) : that.fileName != null) return false;
    if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = className != null ? className.hashCode() : 0;
    result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
    result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
    result = 31 * result + lineNumber;
    return result;
  }

  @Override
  public String toString() {
    return String.format("at %s.%s(%s:%s)", className, methodName, fileName, lineNumber);
  }
}
