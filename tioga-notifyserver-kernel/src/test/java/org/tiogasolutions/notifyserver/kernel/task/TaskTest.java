package org.tiogasolutions.notifyserver.kernel.task;

import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.dev.domain.query.ListQueryResult;
import org.tiogasolutions.notifyserver.kernel.KernelAbstractTest;
import org.tiogasolutions.notifyserver.pub.Task;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.testng.Assert.assertEquals;

/**
 * Created by harlan on 3/7/15.
 */
@Test(enabled = false)
public class TaskTest extends KernelAbstractTest {

  @Inject
  private JsonTranslator translator;

  public TaskTest() {
  }

  public void translateEmptyQueryResult() {
    ListQueryResult<Task> emptyQr = ListQueryResult.newEmpty(Task.class);

    String json = translator.toJson(emptyQr);

    ListQueryResult<Task> translatedQr = translator.fromJson(ListQueryResult.class, json, Task.class);

    assertEquals(emptyQr, translatedQr);

  }
}
