package org.tiogasolutions.notify.kernel.task;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.query.CouchViewQuery;
import org.tiogasolutions.couchace.core.api.response.GetEntityResponse;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.notify.kernel.common.AbstractStore;
import org.tiogasolutions.notify.kernel.common.CouchConst;
import org.tiogasolutions.notify.pub.TaskStatus;

import java.util.List;

import static java.lang.String.format;

/**
 * User: Harlan
 * Date: 2/7/2015
 * Time: 10:44 PM
 */
public class TaskStore extends AbstractStore {

  public TaskStore(CouchDatabase couchDatabase) {
    super(couchDatabase);
  }

  public TaskEntity createTask(CreateTask create) {
    ExceptionUtils.assertNotNull(create, "create");

    TaskEntity entity = TaskEntity.newEntity(create);

    couchDatabase.put()
        .entity(entity)
        .onError(r -> throwError(r, format(CREATE_ENTITY_ERROR, TaskEntity.class.getName(), entity.getTaskId())))
        .execute();

    return findTaskById(entity.getTaskId());
  }

  public void save(TaskEntity entity) {
    ExceptionUtils.assertNotNull(entity, "entity");

    couchDatabase.put()
      .entity(entity)
      .onError(r -> throwError(r, format(SAVE_ENTITY_ERROR, TaskEntity.class, entity.getTaskId())))
      .execute();

  }

  public TaskEntity saveAndReload(TaskEntity entity) {
    save(entity);

    return findTaskById(entity.getTaskId());
  }

  public void deleteTask(String taskId) {
    ExceptionUtils.assertNotNull(taskId, "taskId");

    TaskEntity task;

    try {
      task = findTaskById(taskId);

    } catch (ApiNotFoundException e) {
      return; // it's already gone, who cares.
    }

    couchDatabase.delete()
      .document(task.getTaskId(), task.getRevision())
      .onError(r -> throwError(r, format(DELETE_ENTITY_ERROR, TaskEntity.class, task.getTaskId())))
      .execute();
  }

  public ListQueryResult<TaskEntity> query(TaskQuery query) {
    int limit = (query.getLimit() <= 500) ? query.getLimit() : 500;

    CouchViewQuery viewQuery;
    if (query.getTaskStatus() != null) {
      TaskStatus taskStatus = query.getTaskStatus();
      viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByTaskStatusAndCreatedAt.name())
          .start(taskStatus, "\\ufff0")
          .end(taskStatus, null)
          .limit(limit + 1)
          .skip(query.getOffset())
          .descending(true)
          .build();

    } else if (StringUtils.isNotBlank(query.getDestinationName())) {
      String destinationName = query.getDestinationName();
      viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByDestinationNameAndCreatedAt.name())
          .start(destinationName, "\\ufff0")
          .end(destinationName, null)
          .limit(limit + 1)
          .skip(query.getOffset())
          .descending(true)
          .build();

    } else if (StringUtils.isNotBlank(query.getDestinationProvider())) {
      String destinationProvider = query.getDestinationProvider();
      viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByDestinationProviderAndCreatedAt.name())
          .start(destinationProvider, "\\ufff0")
          .end(destinationProvider, null)
          .limit(limit + 1)
          .skip(query.getOffset())
          .descending(true)
          .build();

    } else if (StringUtils.isNotBlank(query.getNotificationId())) {
      viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByNotification.name())
        .key(query.getNotificationId())
        .limit(limit)
        .build();

    } else {
      viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByCreatedAt.name())
        .start("\\ufff0")
        .end((Object)null)
        .limit(limit + 1)
        .skip(query.getOffset())
        .descending(true)
        .build();
    }

    GetEntityResponse<TaskEntity> getResponse = couchDatabase.get()
      .entity(TaskEntity.class, viewQuery)
      .onError(r -> throwError(r, "Error finding ready tasks"))
      .execute();

    List<TaskEntity> tasks = getResponse.getEntityList();

    return ListQueryResult.newResult(TaskEntity.class, limit, 0, tasks.size(), false, tasks);
  }

  public TaskEntity findTaskById(String entityId) {
    ExceptionUtils.assertNotNull(entityId, "entityId");

    GetEntityResponse<TaskEntity> getResponse = couchDatabase.get()
        .entity(TaskEntity.class, entityId)
        .onError(r -> throwError(r, format(FIND_ENTITY_ERROR, TaskEntity.class, entityId)))
        .onResponse(r -> throwIfNotFound(r, format(NOT_FOUND_ERROR, TaskEntity.class, entityId)))
        .execute();

    return getResponse.getFirstEntity();
  }
}
