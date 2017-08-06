package org.tiogasolutions.notify.engine.web;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;

@Test
public class NotifyExceptionMapperTest {

    public void testCleanUrl() {
        String result = NotifyExceptionMapper.cleanUrl(null);
        Assert.assertNull(result);

        result = NotifyExceptionMapper.cleanUrl(URI.create("http://www.google.com"));
        Assert.assertEquals(result, "www.google.com");

        result = NotifyExceptionMapper.cleanUrl(URI.create("https://www.google.com"));
        Assert.assertEquals(result, "www.google.com");

        result = NotifyExceptionMapper.cleanUrl(URI.create("file://www.google.com"));
        Assert.assertEquals(result, "file://www.google.com");
    }
}