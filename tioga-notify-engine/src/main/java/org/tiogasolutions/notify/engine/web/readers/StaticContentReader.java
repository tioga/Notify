package org.tiogasolutions.notify.engine.web.readers;

import javax.ws.rs.core.UriInfo;

public interface StaticContentReader {
    byte[] readContent(UriInfo uriInfo);

    byte[] readContent(String contentPath);
}
