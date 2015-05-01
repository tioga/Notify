
$(document).ready(function () {
    var appRoot = new AppRoot();
    app.initRoot(appRoot);
/*
    appRoot.load()
        .catch(Error, app.errorDialog)
        .catchFinally();
*/
});

function AppRoot() {
    var self = this;

    // Observable
    self.testMode = app.mvc.observable(false);
    self.authenticated = app.mvc.observable(false);
    self.userName = app.mvc.observable();

    // Models
    self.home = new Home();
    self.notificationsPage = new NotificationsPage();
    self.domainsPage = new DomainsPage();

    // Dialogs, reference from "app" namespace
    app.dialogs = new Dialogs();

    // Observable for page path - update via event subscription.
    self.pagePath = app.mvc.observable();
    app.bus.subscribe(EventTypes.PAGE_CHANGED, function (pageContext) {
        self.pagePath(pageContext.pagePath);
    });

    self.logout = function () {
        return app.notifyClient
            .logout()
            .then(self.load)
            .then(function () {
                window.location = app.url("app#home");
            })
            .catchFinally();
    };

    // Update called internally by successful load.
    function update(data) {
        self.testMode(false);
        self.authenticated(false);
        self.userName(null);
        if (data.testMode != undefined) {
            self.testMode(data.testMode);
        }
        if (data.authenticated != undefined) {
            self.authenticated(data.authenticated);
        }
        if (data.userName != undefined) {
            self.userName(data.userName);
        }
        app.bus.publish(EventTypes.APP_DATA_LOADED, data);
    }

    // We load shared data from system-info
    self.load = function () {
    };

}

function Dialogs() {
    var self = this;

    // Domains dialogs
    var factory = new DialogFactory(app.mvc, app.url("app/domains"), "#body-container");
    factory.dialogOptions = {backdrop : "static"};
    self.createDomain = factory.createDialog("create-domain");

    // Notifications dialogs
    factory = new DialogFactory(app.mvc, app.url("app/notifications"), "#body-container");
    factory.dialogOptions = {backdrop : "static"};
    self.exceptinInfo = factory.createDialog("exception-info");
    self.createNotification = factory.createDialog("create-notification");

}