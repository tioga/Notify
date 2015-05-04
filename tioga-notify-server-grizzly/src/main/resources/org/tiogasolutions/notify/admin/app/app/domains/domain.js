
var Domain = function() {
    var self = this;
    self.profileId = app.mvc.observable();
    self.domainName = app.mvc.observable();
    self.domainStatus = app.mvc.observable();
    self.apiKey = app.mvc.observable();
    self.apiPassword = app.mvc.observable();
    self.notificationDbName = app.mvc.observable();
    self.requestDbName = app.mvc.observable();
    self.routeCatalog  = new RouteCatalog();

    function update(data) {
        self.profileId(data.profileId);
        self.domainName(data.domainName);
        self.domainStatus(data.domainStatus);
        self.apiKey(data.apiKey);
        self.apiPassword(data.apiPassword);
        self.notificationDbName(data.notificationDbName);
        self.requestDbName(data.requestDbName);
        self.routeCatalog.update(data.domainName, data.routeCatalog);
        return self;
    }

    self.load = function(domainName) {
        self.clear();
        if(domainName != undefined) {
            app.notifyClient.fetchDomainByName(domainName)
                .then(function(data) {
                    update(data);
                });
        }
    };

    self.clear = function() {
        app.mvc.clearAll(self);
        self.routeCatalog.clear();
    };
};

var RouteCatalog = function() {
    var self = this;
    self.domainName = app.mvc.observable();
    self.rawJson = app.mvc.observable();
    self.editMessage = app.mvc.observable();
    self.editMessageCss = app.mvc.observable();

    self.clear = function() {
        app.mvc.clearAll(self);
        self.editMessage("Edit the JSON below to change the route.");
        self.editMessageCss("alert-info");
    };

    self.update = function(domainName, routeCatalog) {
        self.clear();
        self.domainName(domainName);
        self.rawJson(JSON.stringify(routeCatalog, null, '\t'));
    };

    self.saveRouteCatalog = function() {
        return app.notifyClient.saveRouteCatalog(self.domainName(), self.rawJson())
            .then(function (routeCatalogData) {
                self.rawJson(JSON.stringify(routeCatalogData, null, '\t'));
                self.editMessage("Route catalog successfully saved.");
                self.editMessageCss("alert-success");
            })
            .catchFinally();
    }
};
