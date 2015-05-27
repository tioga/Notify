function NotificationsPage() {
    var self = this;
    self.searchResults = new NotificationSearchResult(self);
    self.topic = app.mvc.observable();
    self.topicOptions = app.mvc.observableArray();
    self.notificationId = app.mvc.observable();
    self.traitKey = app.mvc.observable();
    self.traitKeyOptions = app.mvc.observableArray();
    self.traitValue = app.mvc.observable();
    self.domainOptions = app.mvc.observableArray();
    self.domain = app.mvc.observable();
    self.domain.subscribe(function(selectedDomain) {
        // Fetch domain summary
        app.notifyClient.fetchDomainSummary(selectedDomain)
            .then(function(data) {
                // Update topic options.
                self.topicOptions.removeAll();
                $.each(data.topics, function (i, o) {
                    self.topicOptions.push(o.name)
                });
                // Update trait key options.
                self.traitKeyOptions.removeAll();
                $.each(data.traits, function (i, o) {
                    self.traitKeyOptions.push(o.key)
                });
            })
            .catchFinally();
    });

    self.beforeShow = function() {
        // Fetch domains.
        app.notifyClient.fetchDomains()
            .then(function(data) {
                // Update domain options.
                self.domainOptions.removeAll();
                $.each(data.results, function (i, o) {
                    self.domainOptions.push(o.domainName)
                });
            })
            .then(function() {
                var queryParams = app.util.queryParams(location.search);
                self.domain(queryParams.domain);
                self.notificationId(queryParams.notificationId);
                self.topic(queryParams.topic);
                self.traitKey(queryParams.traitKey);
                self.traitValue(queryParams.traitValue);

                // Auto select the fist domain, unless already set (as would be with query param).
                if (self.domain() == null && self.domainOptions().length > 1) {
                    self.domain(self.domainOptions()[1]);
                }
                // Perform an auto search.
                self.search();
            })
            .catchFinally();
    };

    self.search = function() {
        self.searchWithOffset(0);
    };

    self.showCreateNotification = function() {
        app.dialogs.createNotification.show();
    };

    self.searchWithOffset = function(offset) {
        if (self.domain() != null) {
            var query = {
                offset: offset,
                notificationId: self.notificationId(),
                topic: self.topic(),
                traitKey: self.traitKey(),
                traitValue: self.traitValue()
            };
            app.notifyClient.queryNotifications(self.domain(), query)
                .then(self.searchResults.update);

        } else {
            self.searchResults.clear();
        }
    };

    self.notificationSelected = function(notification) {
        alert("Selected: " + notification.summary());
    };

    self.showCreateNotification = function() {
        var createNotification = new CreateNotification(self.domainOptions);
        app.dialogs.createNotification.show(createNotification);
    }

}

function NotificationSearchResult(notificationPage) {
    var self = this;
    self.notifications = app.mvc.observableArray();
    self.dataPager = new RemoteDataPager().setPageSize(10);

    self.nextPage = function() {
        var offset = self.dataPager.offset() + self.dataPager.pageSize();
        notificationPage.searchWithOffset(offset);
    };

    self.previousPage = function() {
        var offset = self.dataPager.offset() - self.dataPager.pageSize();
        offset = offset < 0 ? 0 : offset;
        notificationPage.searchWithOffset(offset);
    };

    self.clear = function() {
        app.mvc.clearAll(self);
        self.dataPager.update({
            hasNextPage: false,
            hasPreviousPage: false,
            offset: 0,
            pageNumber: 0,
            results: []
        });
    };

    self.update = function(data) {
        self.clear();
        if (data) {
            $.each(data.results, function(i, o) {
                self.notifications.push(new Notification(notificationPage.domain(), o));
            });

            self.dataPager.update({
                hasNextPage: data.hasNext,
                hasPreviousPage: data.hasPrevious,
                offset: data.offset,
                //pageNumber: data.offset / data.pageSize(),
                pageNumber: 99,
                results: self.notifications()
            });

        }
        return this;
    }
}

var CreateNotification = function(domainOptionsObservable) {
    var self = this;
    self.domainOptions = domainOptionsObservable;
    self.domainName = app.mvc.observable();
    self.topic = app.mvc.observable();
    self.summary = app.mvc.observable();
    self.traitString = app.mvc.observable();

    self.createNotification = function() {
        var createNotificationData = app.mvc.toJS(self);
        return app.notifyClient.createNotificationRequest(createNotificationData)
            .publish(EventTypes.NOTIFICATION_REQUEST_CREATED)
            .catchFinally();
    };

};

