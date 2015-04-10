package org.tiogasolutions.notify.server.grizzly;

import java.net.URI;

/**
 * Created by harlan on 3/7/15.
 */
public class GrizzlyServerConfig {

  private String serverName = "localhost";
  private boolean shutDown = false;
  private int port = 8080;
  private int shutdownPort = 8005;
  private String context;
  private boolean openBrowser;

  public String getServerName() {
    return serverName;
  }

  public GrizzlyServerConfig setServerName(String serverName) {
    this.serverName = serverName;
    return this;
  }

  public boolean isShutDown() {
    return shutDown;
  }

  public GrizzlyServerConfig setShutDown(boolean shutDown) {
    this.shutDown = shutDown;
    return this;
  }

  public int getPort() {
    return port;
  }

  public GrizzlyServerConfig setPort(int port) {
    this.port = port;
    return this;
  }

  public int getShutdownPort() {
    return shutdownPort;
  }

  public GrizzlyServerConfig setShutdownPort(int shutdownPort) {
    this.shutdownPort = shutdownPort;
    return this;
  }

  public String getContext() {
    return context;
  }

  public GrizzlyServerConfig setContext(String context) {
    this.context = context;
    return this;
  }

  public boolean isOpenBrowser() {
    return openBrowser;
  }

  public GrizzlyServerConfig setOpenBrowser(boolean openBrowser) {
    this.openBrowser = openBrowser;
    return this;
  }

  public URI getBaseUri() {
    return URI.create("http://"+serverName+":"+ port+"/"+context+"/");
  }

}
