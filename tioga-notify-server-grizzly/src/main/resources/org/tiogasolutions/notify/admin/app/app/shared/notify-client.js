function NotifyClient() {
    var self = this;

    // Add catchFinally to Promise Prototype which, when called, will add default catch and finally to the promise.
    Promise.prototype.catchFinally = function () {
        return this
            .catchAjax()
            .catch(Error, app.errorDialog)
            .finally(app.overlay.hide);
    };

    // Add publish to Promise Prototype which, when called, will add a then to publish an event.
    Promise.prototype.publish = function (eventType, argData) {
        return this.then(function (ajaxData) {
            var data = (argData != null) ? argData : ajaxData;
            app.bus.publish(eventType, data);
            return ajaxData;
        });
    };

    self.createDomain = function (domainName) {
        app.overlay.show();
        return app.ajax.put("api/v1/admin/domains", domainName)
            .execute();
    };

    self.fetchDomains = function () {
        app.overlay.show();
        return app.ajax.get("api/v1/admin/domains")
            .execute();
    };

    self.fetchDomainSummary = function (domainName) {
        app.overlay.show();
        return app.ajax.get("api/v1/admin/domains", domainName, "summary")
            .execute();
    };

    self.fetchDomainByName = function (domainName) {
        app.overlay.show();
        return app.ajax.get("api/v1/admin/domains", domainName)
            .execute();
    };

    self.createNotificationRequest = function (createData) {
        app.overlay.show();
        if (createData.traitString) {
            createData.traitMap = app.util.traitStringToJS(createData.traitString);
        }
        return app.ajax.post("api/v1/admin/domains", createData.domainName, "requests/simple-entry")
            .jsonObject(createData)
            .execute();
    };

    // Get system info
    self.queryNotifications = function (domainName, query) {
        app.overlay.show();
        return app.ajax.get("api/v1/admin/domains", domainName, "notifications", query)
            .execute();
    };

    self.openAttachment = function(domainName, notificationId, attachmentName) {
        var url = app.util.buildUrl(["api/v1/admin/domains", domainName, "notifications", notificationId, "attachments", attachmentName]);
        window.open(url);
    };

    // Get system info
    self.getSystemInfo = function () {
        app.overlay.show();
        return app.ajax.get("api/system")
            .execute();
    };

    // Update
    self.saveRouteCatalog = function (domainName, routeCatalogJson) {
        app.overlay.show();
        return app.ajax.put("api/v1/admin/domains", domainName, "route-catalog")
            .jsonData(routeCatalogJson)
            .execute();
    };
}
