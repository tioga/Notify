package org.tiogasolutions.notify.kernel.route;

import org.tiogasolutions.notify.pub.notification.Notification;
import org.tiogasolutions.notify.pub.route.Destination;

import java.util.Set;

/**
 * Isolates the logic for evaluating a route into a separate class for testability.
 */
public interface RouteEvaluator {

  Set<Destination> findDestinations(Notification notification);

}
