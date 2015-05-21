package org.tiogasolutions.notify.kernel.common;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.response.CouchResponse;
import org.tiogasolutions.couchace.core.api.response.GetAttachmentResponse;
import org.tiogasolutions.couchace.core.api.response.GetEntityResponse;
import org.tiogasolutions.dev.common.exceptions.ApiConflictException;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.notify.notifier.NotifierException;

import static java.lang.String.format;

public class AbstractStore {

  protected static final String CREATE_ENTITY_ERROR = "Error creating %s with id %s";
  protected static final String SAVE_ENTITY_ERROR = "Error saving %s with id %s";
  protected static final String DELETE_ENTITY_ERROR = "Error deleting %s with id %s";
  protected static final String FIND_ENTITY_ERROR = "Error finding %s with id %s";
  protected static final String NOT_FOUND_ERROR = "%s not found with id %s";

  protected final CouchDatabase couchDatabase;

  public AbstractStore(CouchDatabase couchDatabase) {
    this.couchDatabase = couchDatabase;
  }

  protected final void throwError(CouchResponse response, String message) {
    if (response.isNotFound()) {
      throw ApiNotFoundException.notFound(message);

    } else if (response.isConflict()) {
      throw ApiConflictException.conflict(message);

    } else {
      message = format("%s: %s", message, response.getErrorReason()).trim();
      throw new NotifierException(message);
    }
  }

  protected final void throwIfNotFound(GetEntityResponse response, String message) {
    if (response.isEmpty() || response.isNotFound()) {
      throw ApiNotFoundException.notFound(message);
    }
  }

  protected final void throwIfNotFound(GetAttachmentResponse response, String message) {
    if (response.isEmpty() || response.isNotFound()) {
      throw ApiNotFoundException.notFound(message);
    }
  }
}
