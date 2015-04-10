package org.tiogasolutions.notify.kernel.execution;

/**
 * User: Harlan
 * Date: 2/9/2015
 * Time: 11:21 PM
 */
public interface ExecutionAccessor {
  boolean hasContext();

  ExecutionContext context();

  String domainName();
}
