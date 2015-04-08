package org.lqnotify.kernel.route;

import org.lqnotify.pub.route.Destination;
import org.lqnotify.pub.Notification;
import org.lqnotify.pub.route.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Isolates the logic for evaluating a route into a separate class for testability.
 */
public interface RouteEvaluator {

  Set<Destination> findDestinations(Notification notification);

}
