package org.tiogasolutions.notifyserver.kernel.route;

import org.tiogasolutions.notifyserver.pub.route.Destination;
import org.tiogasolutions.notifyserver.pub.Notification;

import java.util.Set;

/**
 * Isolates the logic for evaluating a route into a separate class for testability.
 */
public interface RouteEvaluator {

  Set<Destination> findDestinations(Notification notification);

}
