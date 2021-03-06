package org.tiogasolutions.notify.kernel.task;

import org.tiogasolutions.couchace.core.api.CouchDatabase;
import org.tiogasolutions.couchace.core.api.query.CouchViewQuery;
import org.tiogasolutions.couchace.core.api.response.GetEntityResponse;
import org.tiogasolutions.dev.common.StringUtils;
import org.tiogasolutions.dev.common.exceptions.ApiBadRequestException;
import org.tiogasolutions.dev.common.exceptions.ApiNotFoundException;
import org.tiogasolutions.dev.common.exceptions.ExceptionUtils;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.notify.kernel.common.AbstractStore;
import org.tiogasolutions.notify.kernel.common.CouchConst;
import org.tiogasolutions.notify.pub.task.TaskQuery;
import org.tiogasolutions.notify.pub.task.TaskStatus;

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
        ExceptionUtils.assertNotNull(create, "create", ApiBadRequestException.class);

        TaskEntity entity = TaskEntity.newEntity(create);

        couchDatabase.put()
                .entity(entity)
                .onError(r -> throwError(r, format(CREATE_ENTITY_ERROR, TaskEntity.class.getName(), entity.getTaskId())))
                .execute();

        return findTaskById(entity.getTaskId());
    }

    public void save(TaskEntity entity) {
        ExceptionUtils.assertNotNull(entity, "entity", ApiBadRequestException.class);

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
        ExceptionUtils.assertNotNull(taskId, "taskId", ApiBadRequestException.class);

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

        String errorSuffix;
        CouchViewQuery viewQuery;

        if (query.getTaskStatus() != null) {
            errorSuffix = "by task status";
            TaskStatus taskStatus = query.getTaskStatus();
            viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByTaskStatusAndCreatedAt.name())
                    .start(taskStatus, "\\ufff0")
                    .end(taskStatus, null)
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .descending(true)
                    .build();

        } else if (StringUtils.isNotBlank(query.getDestinationName())) {
            errorSuffix = "by destination name";
            String destinationName = query.getDestinationName();
            viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByDestinationNameAndCreatedAt.name())
                    .start(destinationName, "\\ufff0")
                    .end(destinationName, null)
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .descending(true)
                    .build();

        } else if (StringUtils.isNotBlank(query.getDestinationProvider())) {
            errorSuffix = "by destination provider";
            String destinationProvider = query.getDestinationProvider();
            viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByDestinationProviderAndCreatedAt.name())
                    .start(destinationProvider, "\\ufff0")
                    .end(destinationProvider, null)
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .descending(true)
                    .build();

        } else if (StringUtils.isNotBlank(query.getNotificationId())) {
            errorSuffix = "by destination notification id";
            viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByNotification.name())
                    .key(query.getNotificationId())
                    .limit(limit)
                    .build();

        } else {
            errorSuffix = "by created at";
            viewQuery = CouchViewQuery.builder(CouchConst.TASK_DESIGN_NAME, TaskCouchView.ByCreatedAt.name())
                    .start("\\ufff0")
                    .end((Object) null)
                    .limit(limit + 1)
                    .skip(query.getOffset())
                    .descending(true)
                    .build();
        }

        GetEntityResponse<TaskEntity> getResponse = couchDatabase.get()
                .entity(TaskEntity.class, viewQuery)
                .onError(r -> {
                    // We are returning a query result. If not found, return an empty list instead.
                    if (r.isNotFound() == false) throwError(r, "Error finding Notification " + errorSuffix);
                })
                .execute();

        List<TaskEntity> tasks = getResponse.getEntityList();
        return ListQueryResult.newResult(TaskEntity.class, limit, 0, tasks.size(), false, tasks);
    }

    public TaskEntity findTaskById(String entityId) {
        ExceptionUtils.assertNotNull(entityId, "entityId", ApiBadRequestException.class);

        GetEntityResponse<TaskEntity> getResponse = couchDatabase.get()
                .entity(TaskEntity.class, entityId)
                .onError(r -> throwError(r, format(FIND_ENTITY_ERROR, TaskEntity.class, entityId)))
                .onResponse(r -> throwIfNotFound(r, format(NOT_FOUND_ERROR, TaskEntity.class, entityId)))
                .execute();

        return getResponse.getFirstEntity();
    }
}
