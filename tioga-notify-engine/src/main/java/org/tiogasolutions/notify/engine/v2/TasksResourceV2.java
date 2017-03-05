package org.tiogasolutions.notify.engine.v2;

import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.dev.domain.query.QueryResult;
import org.jvnet.hk2.annotations.Optional;
import org.tiogasolutions.notify.kernel.notification.NotificationKernel;
import org.tiogasolutions.notify.pub.task.TaskQuery;
import org.tiogasolutions.notify.pub.domain.DomainProfile;
import org.tiogasolutions.notify.kernel.domain.DomainKernel;
import org.tiogasolutions.notify.kernel.execution.ExecutionContext;
import org.tiogasolutions.notify.kernel.execution.ExecutionManager;
import org.tiogasolutions.notify.kernel.task.TaskEntity;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

public class TasksResourceV2 {

  private final DomainKernel domainKernel;
  private final NotificationKernel notificationKernelnel;
  private final ExecutionManager executionManager;

  public TasksResourceV2(ExecutionManager executionManager, DomainKernel domainKernel, NotificationKernel notificationKernelnel) {
    this.domainKernel = domainKernel;
    this.executionManager = executionManager;
    this.notificationKernelnel = notificationKernelnel;
  }

  private DomainProfile getDomainProfile() {
    ExecutionContext ec = executionManager.context();
    return domainKernel.findByApiKey(ec.getApiKey());
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public QueryResult<Task> getTasks(@Optional @QueryParam("taskStatus") TaskStatus taskStatus) {
    TaskQuery query = new TaskQuery().setTaskStatus(taskStatus);
    List<Task> tasks = new ArrayList<>();

    QueryResult<TaskEntity> result = notificationKernelnel.query(query);
    result.getResults().stream().forEach((task) -> tasks.add(task.toTask()));

    return ListQueryResult.newResult(Task.class, result.getLimit(), result.getOffset(), result.getTotalFound(), result.isTotalExact(), tasks);
  }

  @DELETE
  @Path("{taskId}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response deleteTask(@PathParam("taskId") String taskId) {
    notificationKernelnel.deleteTask(taskId);
    return Response.noContent().build();
  }
}