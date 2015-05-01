package org.tiogasolutions.notify.server.grizzly;

import org.tiogasolutions.dev.common.EnvUtils;
import org.tiogasolutions.dev.common.IoUtils;
import org.tiogasolutions.dev.common.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppPathResolver {

  private final Logger logger;
  private final String springPropertyName;
  private final String configPropertyName;
  private final String runtimePropertyName;
  private final String profilesPropertyName;

  public AppPathResolver(Logger logger, String prefix) {
    this.logger = logger;
    this.springPropertyName = prefix + "spring.config";
    this.configPropertyName = prefix + " config.dir";
    this.runtimePropertyName = prefix + "runtime.dir";
    this.profilesPropertyName = prefix + "active.profiles";
  }

  public Path resolveRuntimePath() {
    return resolveRuntimePath("runtime");
  }

  public Path resolveRuntimePath(String directoryName) {

    // Identify runtimeDir from system property or program argument
    String runtimeDirArg = EnvUtils.findProperty(runtimePropertyName);

    if (runtimeDirArg == null) {
      // Look for a directory named "runtime" near the current directory -- useful for development
      File currentDir = IoUtils.currentDir();
      File moduleDir = IoUtils.findDirNear(currentDir, directoryName);

      if (moduleDir == null) {
        String msg = String.format("Application runtime directory not specified and could not be found automatically, must be provided with system property %s.", runtimePropertyName);
        throw new RuntimeException(msg);
      }
      runtimeDirArg = moduleDir.getAbsolutePath();
    }
    Path runtimeDir = Paths.get(runtimeDirArg).toAbsolutePath();

    // Verify the runtimeDir
    if (Files.notExists(runtimeDir)) {
      String msg = String.format("Runtime directory %s does not exist", runtimeDir);
      throw new RuntimeException(msg);

    } else if (Files.isDirectory(runtimeDir) == false) {
      String msg = String.format("Runtime directory %s is not a directory", runtimeDir);
      throw new RuntimeException(msg);
    }

    System.setProperty(runtimePropertyName, runtimeDir.toString());
    logger.info("Runtime dir: " + runtimeDir);

    return runtimeDir;
  }

  public String resolveSpringPath(Path configDir) throws FileNotFoundException {
    return resolveSpringPath(configDir, "spring-config.xml");
  }

  public String resolveSpringPath(Path configDir, String fileName) throws FileNotFoundException {

    String springFileName = EnvUtils.findProperty(springPropertyName);
    String springConfigPath;

    if (springFileName != null) {
      // The spring file was specified, make sure it actually exists.
      Path springConfig = configDir.resolve(springFileName);

      if (springConfig.toFile().exists() == false) {
        String msg = "The specified spring config file does not exist: " + springConfig;
        throw new FileNotFoundException(msg);
      } else {
        springConfigPath = springConfig.toUri().toString();
        logger.info("Using the specified spring config file");
      }

    } else {
      Path springConfig = configDir.resolve(fileName);
      if (springConfig.toFile().exists()) {
        logger.info("Using the external spring config file");
        springConfigPath = springConfig.toUri().toString();

      } else {
        springConfigPath = "classpath:/tioga-notify-server-grizzly/spring-config.xml";
        logger.info("Using the internal spring config file");
        logger.info("  Override by using the external spring config file: " + springConfig);
        logger.info("  Override by specifying the location of the external spring config file with the system property \""+springPropertyName+"\"");
      }
    }

    System.setProperty(springPropertyName, springConfigPath);
    logger.info("Spring file: " + springConfigPath);

    return springConfigPath;
  }

  public Path resolveConfigDir(Path runtimeDir) {
    return resolveConfigDir(runtimeDir, "config");
  }
  public Path resolveConfigDir(Path runtimeDir, String directoryName) {

    Path configDir = runtimeDir.resolve(directoryName);

    if (Files.notExists(configDir)) {
      String msg = String.format("Config directory %s does not exist", configDir);
      throw new RuntimeException(msg);

    } else if (!Files.isDirectory(runtimeDir)) {
      String msg = String.format("Config directory %s is not a directory", configDir);
      throw new RuntimeException(msg);
    }

    System.setProperty(configPropertyName, configDir.toString());
    logger.info("Config dir: " + configDir);

    return configDir;
  }

  public String resolveSpringProfiles() {
    return resolveSpringProfiles("hosted");
  }
  public String resolveSpringProfiles(String additionalProfiles) {

    String activeProfiles = EnvUtils.findProperty(profilesPropertyName, "");

    if (StringUtils.isNotBlank(activeProfiles)) activeProfiles += ",";
    activeProfiles += additionalProfiles;

    System.setProperty(profilesPropertyName, activeProfiles);
    logger.info("Active spring profiles: " + activeProfiles);

    return activeProfiles;
  }

  public interface Logger {
    void info(String message);
  }
}
