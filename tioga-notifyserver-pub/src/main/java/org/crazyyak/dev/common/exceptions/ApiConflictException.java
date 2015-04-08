package org.crazyyak.dev.common.exceptions;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.fine.FineMessage;
import org.tiogasolutions.dev.common.fine.FineMessageSet;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

// TODO - This needs to be moved to crazy-yak

public class ApiConflictException extends ApiException {

  private ApiConflictException() {
    super(HttpStatusCode.NOT_FOUND);
  }

  private ApiConflictException(String message, String... traits) {
    super(HttpStatusCode.NOT_FOUND, message, traits);
  }

  private ApiConflictException(Throwable ex, String... traits) {
    super(HttpStatusCode.NOT_FOUND, ex, traits);
  }

  private ApiConflictException(String message, Throwable ex, String... traits) {
    super(HttpStatusCode.NOT_FOUND, message, ex, traits);
  }

  private ApiConflictException(FineMessage richMessage) {
    super(HttpStatusCode.NOT_FOUND, richMessage);
  }

  private ApiConflictException(FineMessage richMessage, Throwable ex) {
    super(HttpStatusCode.NOT_FOUND, richMessage, ex);
  }

  private ApiConflictException(FineMessageSet messageSet) {
    super(HttpStatusCode.NOT_FOUND, messageSet);
  }

  private ApiConflictException(FineMessageSet messageSet, Throwable ex) {
    super(HttpStatusCode.NOT_FOUND, messageSet, ex);
  }

  // TODO - Update ApiException.notFound(..) to dispatch to these methods.
  public static ApiConflictException conflict() {                                              return new ApiConflictException(); }
  public static ApiConflictException conflict(String message, String...traits) {               return new ApiConflictException(message, traits); }
  public static ApiConflictException conflict(Throwable ex, String...traits) {                 return new ApiConflictException(ex, traits); }
  public static ApiConflictException conflict(String message, Throwable ex, String...traits) { return new ApiConflictException(message, ex, traits); }
  public static ApiConflictException conflict(FineMessage richMessage) {                       return new ApiConflictException(richMessage); }
  public static ApiConflictException conflict(FineMessage richMessage, Throwable ex) {         return new ApiConflictException(richMessage, ex); }
  public static ApiConflictException conflict(FineMessageSet messageSet) {                     return new ApiConflictException(messageSet); }
  public static ApiConflictException conflictFound(FineMessageSet messageSet, Throwable ex) {       return new ApiConflictException(messageSet, ex); }
}
