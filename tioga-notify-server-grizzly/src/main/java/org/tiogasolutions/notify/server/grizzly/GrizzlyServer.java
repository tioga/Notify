package org.tiogasolutions.notify.server.grizzly;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

public class GrizzlyServer {
  private static Logger log = LoggerFactory.getLogger(GrizzlyServer.class);
  private static final int socketAcceptTimeoutMilli = 5000;

  private final GrizzlyServerConfig config;

  private HttpServer httpServer;
  private ServerSocket socket;
  private Thread acceptThread;
  /** handlerLock is used to synchronize access to socket, acceptThread and callExecutor. */
  private final ReentrantLock handlerLock = new ReentrantLock();

  public GrizzlyServer(GrizzlyServerConfig config) {
    this.config = config;
  }

  public void start(ResourceConfig resourceConfig) {
    try {
      startServer(resourceConfig);
      System.out.printf("Application started with WADL available at %sapplication.wadl%n", getBaseUri());

      if (config.isOpenBrowser()) {
        URI baseUri = getBaseUri();
        java.awt.Desktop.getDesktop().browse(baseUri);
      }

      Thread.currentThread().join();

    } catch (Throwable e) {
      log.error("Exception starting server", e);
      e.printStackTrace();
    }
  }

  public void shutdown() {
    if (httpServer != null) {
      httpServer.shutdown();
    }
  }

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   * @param resourceConfig -
   * @throws Exception whenever something bad happens
   */
  public void startServer(ResourceConfig resourceConfig) throws Exception {
    shutdownExisting();

    httpServer = GrizzlyHttpServerFactory.createHttpServer(config.getBaseUri(), resourceConfig);

    // Lock the handler, IllegalStateException thrown if we fail.
    lockHandler();
    try {
      if (acceptThread != null) {
        throw new IllegalStateException("Socket handler thread is already running.");
      }

      try {
        // Set the accept timeout so we won't block indefinitely.
        socket = new ServerSocket(config.getShutdownPort());
        socket.setSoTimeout(socketAcceptTimeoutMilli);

        String msg = String.format("%s is accepting connections on port %s from %s.",
            getClass().getSimpleName(),
            config.getShutdownPort(),
            socket.getInetAddress().getHostAddress());
        System.out.println(msg);
        log.info(msg);

      } catch(IOException ex) {
        String msg = String.format("IOException starting server socket, maybe port %s was not available.",
            config.getShutdownPort());
        log.error(msg, ex);
        System.err.println(msg);
        ex.printStackTrace();
      }

      Thread shutdownThread = new Thread(httpServer::shutdown, "shutdownHook");
      Runtime.getRuntime().addShutdownHook(shutdownThread);

      Runnable acceptRun = GrizzlyServer.this::socketAcceptLoop;
      acceptThread = new Thread(acceptRun);
      acceptThread.start();

    } finally {
      // Be sure to always give up the lock.
      unlockHandler();
    }

  }

  private void shutdownExisting() throws IOException {
    try(Socket localSocket = new Socket(config.getServerName(), config.getShutdownPort())) {
      try(OutputStream outStream = localSocket.getOutputStream()) {
        outStream.write("SHUTDOWN".getBytes());
        outStream.flush();
      }
    } catch (ConnectException ignored) {
    }
  }

  private void lockHandler() throws TimeoutException, InterruptedException {
    int timeout = 5;
    TimeUnit timeUnit = TimeUnit.SECONDS;

    if (!handlerLock.tryLock(timeout, timeUnit)) {
      String msg = String.format("Failed to obtain lock within %s %s", timeout, timeUnit);
      throw new TimeoutException(msg);
    }
  }

  /**
   * Really just used to improve readability and so we limit when we directly access handlerLock.
   */
  private void unlockHandler() {
    handlerLock.unlock();
  }

  public URI getBaseUri() {
    return config.getBaseUri();
  }

  private void socketAcceptLoop() {

    // Socket accept loop.
    while (!Thread.interrupted()) {
      try {

        // REVIEW - Sleep to allow another thread to lock the handler (never seems to happen without this). Could allow acceptThread to be interrupted in stop without the lock.
        Thread.sleep(5);

        // Lock the handler so we don't accept a new connection while stopping.
        lockHandler();
        Socket client;

        // Ensure we have not stopped or been interrupted.
        if (acceptThread == null || Thread.interrupted()) {
          log.info("Looks like SocketHandler has been stopped, terminate our acceptLoop.");
          System.out.println("Looks like SocketHandler has been stopped, terminate our acceptLoop.");
          return;
        }

        // We have are not stopped, so accept another connection.
        client = socket.accept();

        int val;
        StringBuilder builder = new StringBuilder();
        InputStream is = client.getInputStream();

        while ((val = is.read()) != -1) {
          builder.append((char)val);
          if ("SHUTDOWN".equals(builder.toString())) {
            log.info("Shutdown command received.");
            System.out.println("Shutdown command received.");
            httpServer.shutdownNow();
            System.exit(0);
          }
        }

      } catch (SocketTimeoutException | TimeoutException ex) {
        // Accept timed out, which is excepted, try again.

      } catch (Throwable ex) {
        log.error("Unexpected exception", ex);
        System.out.println("Unexpected exception");
        ex.printStackTrace();
        return;

      } finally {
        unlockHandler();
      }
    }
  }

}
