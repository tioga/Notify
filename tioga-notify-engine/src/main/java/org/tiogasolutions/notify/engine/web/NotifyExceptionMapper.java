package org.tiogasolutions.notify.engine.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.tiogasolutions.lib.jaxrs.providers.TiogaJaxRsExceptionMapper;
import org.tiogasolutions.notify.notifier.Notifier;

import javax.ws.rs.ext.Provider;
import java.net.URI;

@Provider
public class NotifyExceptionMapper extends TiogaJaxRsExceptionMapper {

    private final Notifier notifier;

    @Autowired
    public NotifyExceptionMapper(Notifier notifier) {
        this.notifier = notifier;
    }

    @Override
    protected void log4xxException(String msg, Throwable throwable, int statusCode) {
        super.log4xxException(msg, throwable, statusCode);

//        notifier.begin()
//                .summary(msg)
//                .exception(throwable)
//                .trait("action", "Unhandled 4xx")
//                .trait("http-status-code", statusCode)
//                .trait("http-uri", cleanUrl(getUriInfo().getRequestUri()))
//                .send();
    }

    @Override
    protected void log5xxException(String msg, Throwable throwable, int statusCode) {
        super.log5xxException(msg, throwable, statusCode);

        notifier.begin()
                .summary(msg)
                .exception(throwable)
                .trait("action", "Unhandled 5xx")
                .trait("http-status-code", statusCode)
                .trait("http-uri", cleanUrl(getUriInfo().getRequestUri()))
                .send();
    }

    /**
     * Need to alter the URI so that it's technically not valid.
     * Slack will re-fetch this URI if we include it here.
     * @param uri The URI to be cleaned.
     * @return The URI minus the prefix http:// or https://
     */
    public static String cleanUrl(URI uri) {
        if (uri == null) {
            return null;
        } else if (uri.toASCIIString().toLowerCase().startsWith("http://")) {
            return uri.toASCIIString().substring(7);
        } else if (uri.toASCIIString().toLowerCase().startsWith("https://")) {
            return uri.toASCIIString().substring(8);
        } else {
            return uri.toASCIIString();
        }
    }
}
