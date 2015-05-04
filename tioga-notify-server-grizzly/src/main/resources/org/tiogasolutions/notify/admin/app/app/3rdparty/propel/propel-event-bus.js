/*
 * Copyright 2014 Harlan Noonkester
 *
 * All rights reserved
 */

function EventBus(util, eventTypes) {
    var self = this;
    var subscriptions = [];

    // Auto assigns values of event type properties to their property name.
    for (var type in eventTypes) {
        if (eventTypes.hasOwnProperty(type)) {
            eventTypes[type] = type;
        }
    }

    self.hasSubscription = function(checkSubscription) {
        for (var i=0; i<subscriptions.length; i++) {
            if (subscriptions[i] === checkSubscription) {
                return true;
            }
        }
        return false;
    };

    self.subscriptionCount = function() {
        return subscriptions.length;
    };

    self.hasEventType = function(eventType) {
        if (!eventType) {
            return false;
        } else {
            return eventTypes.hasOwnProperty(eventType.toUpperCase());
        }
    };

    self.subscribe = function(subscribeTo, callback) {
        var subscription = new EventSubscription(subscribeTo, callback);
        subscriptions.push(subscription);
        return subscription;
    };

    self.unsubscribe = function(subscription) {
        var index = subscriptions.indexOf(subscription);
        if (index >= 0) {
            subscriptions.splice(index, 1);
        }
    };

    self.publish = function(eventType, data) {
        eventType = eventType.toUpperCase();
        if (!eventTypes.hasOwnProperty(eventType)) {
            throw Error("Attempting to publish with an undefined event type " + eventType);
        }
        for (var i=0; i<subscriptions.length; i++) {
            var subscription = subscriptions[i];
            // Data is first argument to subscriber
            subscription.handle(data, eventType);
        }
    };

    function EventSubscription(subscribedEventTypes, callback) {
        var self = this;
        if (!subscribedEventTypes) {
            throw Error("Attempting to subscribe with null event type.");
        }

        // Maintain as an array of event types.
        if (util.isArray(subscribedEventTypes) == false) {
            subscribedEventTypes = [subscribedEventTypes];
        }

        // Ensure all subscribedEventTypes exist.
        for (var i=0; i<subscribedEventTypes.length; i++) {
            var eventType = subscribedEventTypes[i];
            if (!eventTypes.hasOwnProperty(eventType.toUpperCase())) {
                throw Error("Attempting to subscribe to undefined event type " + eventType);
            }
        }

        // Data is first argument because in most cases that will be the only argument, 
        // subscriber will not care about the eventType.
        self.handle = function(data, eventType) {
            for (var i=0; i<subscribedEventTypes.length; i++) {
                var subscribedEventType = subscribedEventTypes[i];
                if (subscribedEventType === eventType) {
                    callback(data, eventType);
                    break;
                }
            }
        }
    }
}