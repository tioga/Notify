package org.tiogasolutions.notify.pub;

import org.testng.annotations.Test;
import org.tiogasolutions.dev.common.json.JsonTranslator;
import org.tiogasolutions.notify.pub.route.Destination;
import org.tiogasolutions.notify.pub.task.Task;
import org.tiogasolutions.notify.pub.task.TaskStatus;

import java.net.URI;
import java.time.ZonedDateTime;

import static org.testng.Assert.assertEquals;

@Test
public class TaskTest {
    private PubFixture fixture = PubFixture.it();
    private JsonTranslator translator;

    public TaskTest() {
        this.translator = fixture.getJsonTranslator();
    }

    public void testTranslation() throws Exception {
        URI self = URI.create("http://www.whatever.com");
        Destination destination = fixture.getDestination();
        Task oldValue = new Task(self, "123", "abc", TaskStatus.PENDING, "999", ZonedDateTime.now(), destination, null);

        String json = translator.toJson(oldValue);

        Task newValue = translator.fromJson(Task.class, json);
        assertEquals(newValue.getDestination(), oldValue.getDestination());
        assertEquals(newValue.getTaskStatus(), oldValue.getTaskStatus());
    }

}