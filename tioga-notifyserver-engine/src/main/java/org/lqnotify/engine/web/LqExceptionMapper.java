package org.lqnotify.engine.web;

import org.tiogasolutions.lib.jaxrs.TiogaJaxRsExceptionMapper;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

public class LqExceptionMapper extends TiogaJaxRsExceptionMapper {

  public LqExceptionMapper() {
    super(true);
  }

  @Override
  public Response toResponse(Throwable ex) {

    if (ex instanceof NotAuthorizedException) {
      // Not authorized so return correct headers.
      return Response
          .status(Response.Status.UNAUTHORIZED)
          .header("WWW-Authenticate", "Basic realm=\"Notify\"")
          .type("text/plain")
          .entity("Not authorized")
          .build();
    }

    return super.toResponse(ex);
  }


  @Override
  protected void logInfo(String msg, Throwable ex) {
    // TODO - need to wire in a logging solution.
    System.out.printf("INFO: %s%n", msg);
    if (ex != null) ex.printStackTrace();
  }

  @Override
  protected void logError(String msg, Throwable ex) {
    // TODO - need to wire in a logging solution.
    System.out.printf("ERROR: %s%n", msg);
    if (ex != null) ex.printStackTrace();
  }

  protected void logMessage(String msg) {
    System.out.printf("EVENT: %s%n", msg);
  }

  @Override
  protected void logException(Throwable throwable, int status) {
    String msg = "Status " + status;
    if (uriInfo != null) {
      msg += " ";
      msg += uriInfo.getRequestUri();
    }

    List<Integer> minor = Arrays.asList(400, 401);

    if (minor.contains(status)) {
      logMessage(msg);
    } else {
      logError(msg, throwable);
    }
  }
}
