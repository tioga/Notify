package org.tiogasolutions.notify;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.tiogasolutions.dev.jackson.TiogaJacksonInjectable;
import org.tiogasolutions.dev.jackson.TiogaJacksonModule;
import org.tiogasolutions.dev.jackson.TiogaJacksonObjectMapper;

import java.util.Arrays;
import java.util.Collections;

public class NotifyObjectMapper extends TiogaJacksonObjectMapper {
    public NotifyObjectMapper() {
        super(Arrays.asList(new TiogaJacksonModule(),
                new NotifyJacksonModule()),
                Collections.<TiogaJacksonInjectable>emptyList());

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
