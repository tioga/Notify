package org.tiogasolutions.notify.engine.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.lib.jaxrs.providers.TiogaReaderWriterProvider;
import org.tiogasolutions.notify.NotifyObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotifyReaderWriterProvider extends TiogaReaderWriterProvider {

    @Autowired
    public NotifyReaderWriterProvider(NotifyObjectMapper objectMapper) {
        super(objectMapper);
    }
}
