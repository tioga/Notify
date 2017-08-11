package org.tiogasolutions.notify.notifier.builder;

import org.tiogasolutions.notify.notifier.send.SendNotificationResponse;

/**
 * User: Harlan
 * Date: 1/28/2015
 * Time: 12:46 AM
 */
public interface SendNotificationResponseCallback {
    void call(SendNotificationResponse response);
}
