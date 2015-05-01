package org.tiogasolutions.notify.engine.web.writers;

import java.net.URL;

public class ThymeleafViewFactory {
  public static final String TAIL = ".html";
  public static final String ROOT = "/push-server-app/view/";

  public static final String WELCOME = validate("welcome");

  public static final String MANAGE_ACCOUNT =           validate("manage/account");
  public static final String MANAGE_API_CLIENT =        validate("manage/domain");

  public static final String MANAGE_API_REQUESTS =      validate("manage/push-request");

  public static final String MANAGE_API_EMAIL =         validate("manage/push-email");
  public static final String MANAGE_API_EMAILS =        validate("manage/push-emails");

  public static final String MANAGE_API_NOTIFICATION =  validate("manage/push-notification");
  public static final String MANAGE_API_NOTIFICATIONS = validate("manage/push-notifications");

  public static final String MANAGE_API_EVENT =         validate("manage/push-user-event");
  public static final String MANAGE_API_EVENTS =        validate("manage/push-user-events");

  private static String validate(String view) {
    String resource = ROOT+view+TAIL;
    URL url = ThymeleafViewFactory.class.getResource(resource);
    if (url == null) {
      String msg = String.format("The resource \"%s\" does not exist.", resource);
      throw new IllegalArgumentException(msg);
    }
    return view;
  }
}
