package org.tiogasolutions.notify.engine.web;

import org.tiogasolutions.dev.common.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by harlan on 3/16/15.
 */
public class StaticContentReader {
  private static final Logger logger = LoggerFactory.getLogger(StaticContentReader.class);
  private final Path rootPath;

  public StaticContentReader(Path rootPath) {
    this.rootPath = rootPath;
    logger.info("Reading static resources from: " + rootPath);
  }

  public StaticContentReader(String path) {
    rootPath = Paths.get(path);
    logger.info("Reading static resources from: " + rootPath);
  }

  public StaticContentReader() {
    rootPath = null;
    logger.info("Reading static content from");
  }

  public byte[] readContent(UriInfo uriInfo) {
    String contentPath = uriInfo.getPath();
    contentPath = contentPath.startsWith("/") ? contentPath.substring(1) : contentPath;
//    contentPath = contentPath.startsWith("app/") ? contentPath.substring(4) : contentPath;
    Path fullPath = rootPath.resolve(contentPath);
    try {
      return Files.readAllBytes(fullPath);
    } catch (IOException e) {
      throw ApiException.badRequest("Error reading static content " + fullPath);
    }
  }

  public byte[] readContent(String contentPath) {
    contentPath = contentPath.startsWith("/") ? contentPath.substring(1) : contentPath;
    //contentPath = contentPath.startsWith("app/") ? contentPath.substring(4) : contentPath;
    Path fullPath = rootPath.resolve(contentPath);
    try {
      return Files.readAllBytes(fullPath);
    } catch (IOException e) {
      throw ApiException.badRequest("Error reading static content " + fullPath);
    }
  }
}
