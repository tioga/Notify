package org.crazyyak.dev.common.exceptions;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.tiogasolutions.dev.common.fine.FineMessage;
import org.tiogasolutions.dev.common.fine.FineMessageSet;
import org.tiogasolutions.dev.common.net.HttpStatusCode;

// TODO - This needs to be moved to crazy-yak

public class ApiNotFoundException extends ApiException {
  
  private ApiNotFoundException() {
    super(HttpStatusCode.NOT_FOUND);
  }

  private ApiNotFoundException(String message, String... traits) {
    super(HttpStatusCode.NOT_FOUND, message, traits);
  }

  private ApiNotFoundException(Throwable ex, String... traits) {
    super(HttpStatusCode.NOT_FOUND, ex, traits);
  }

  private ApiNotFoundException(String message, Throwable ex, String... traits) {
    super(HttpStatusCode.NOT_FOUND, message, ex, traits);
  }

  private ApiNotFoundException(FineMessage richMessage) {
    super(HttpStatusCode.NOT_FOUND, richMessage);
  }

  private ApiNotFoundException(FineMessage richMessage, Throwable ex) {
    super(HttpStatusCode.NOT_FOUND, richMessage, ex);
  }

  private ApiNotFoundException(FineMessageSet messageSet) {
    super(HttpStatusCode.NOT_FOUND, messageSet);
  }

  private ApiNotFoundException(FineMessageSet messageSet, Throwable ex) {
    super(HttpStatusCode.NOT_FOUND, messageSet, ex);
  }

  // TODO - Update ApiException.notFound(..) to dispatch to these methods.
  public static ApiNotFoundException notFound() {                                              return new ApiNotFoundException(); }
  public static ApiNotFoundException notFound(String message, String...traits) {               return new ApiNotFoundException(message, traits); }
  public static ApiNotFoundException notFound(Throwable ex, String...traits) {                 return new ApiNotFoundException(ex, traits); }
  public static ApiNotFoundException notFound(String message, Throwable ex, String...traits) { return new ApiNotFoundException(message, ex, traits); }
  public static ApiNotFoundException notFound(FineMessage richMessage) {                       return new ApiNotFoundException(richMessage); }
  public static ApiNotFoundException notFound(FineMessage richMessage, Throwable ex) {         return new ApiNotFoundException(richMessage, ex); }
  public static ApiNotFoundException notFound(FineMessageSet messageSet) {                     return new ApiNotFoundException(messageSet); }
  public static ApiNotFoundException notFound(FineMessageSet messageSet, Throwable ex) {       return new ApiNotFoundException(messageSet, ex); }
}
