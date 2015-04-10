$(document).ready(Init);

// Initialize includes setting up root "app" namespace.
function Init() {
    var self = this;
    window.app = self;

    // Common error dialog.
    self.errorDialog = function (error) {
        var text = "Error: " + error.message;
        console.log(text);
        if (console.error) {
            console.error(error.stack);
        }
        alert(text);
    };

    // Global onerror handler
    window.onerror = function(message, url, linenumber) {
        var text = "JavaScript error: " + message + " on line " + linenumber + " for " + url;
        console.log(text);
        console.trace();
        alert(text);
    };

    // Propel js utility
    self.util = new Util();

    // Create baseUrl and Url builder (assumes window.webAppPath has been set).
    if (!window.webAppPath) {
        throw new Error("Web application path (Javascript window level var webAppPath) was not assigned in initial page.");
    }
    var baseUrl = self.util.hostUrl() +  window.webAppPath;
    baseUrl = self.util.removeTrailing(baseUrl, "/");
    self.url = function () {
        var builder = new UrlBuilder(self.util).path(baseUrl);
        return builder.build(arguments);
    };
    self.urlBuilder = function () {
        return new UrlBuilder(self.util).path(baseUrl);
    };

    // Event bus.
    self.bus = new EventBus(self.util, EventTypes);

    // Propel Mvc (knockout) abstraction.
    self.mvc = new Mvc(self.util);

    // HACK - basic auth hardcoded for now.
    // Ajax
    self.ajax = new Ajax(self.util, baseUrl);
    self.ajax.setDefaultAjaxSettings({
        contentType: "application/json",
        dataType: "json",
        processData: false
    });

/*
    self.ajax.setDefaultAjaxSettings({
        contentType: "application/json",
        dataType: "json",
        processData: false,
        headers : {'Authorization': 'Basic ' + userNamePasswordBase64}
    });
*/

    var ajaxError = function (xhr, textStatus, errorThrown) {
        // Build our custom HttpError
        var httpError = new HttpError(self.util);
        httpError.build(xhr, textStatus, errorThrown);

        // Show message in an alert.
        var message = httpError.fullText + "\n\nStatus: " + httpError.status;
        if (httpError.textStatus) {
            message += " - " + httpError.textStatus;
        }
        message += "\nURL: " + httpError.url;
        alert(message);
    };
    self.ajax.setDefaultAjaxErrorHandler(ajaxError);

    // Overlay used to block the UI.
    self.overlay = new Overlay();

    // Notify Client
    self.notifyClient = new NotifyClient();

    // Page Context
    self.pageContext = new PageContext();

    // Initialize root knockout model (should be called by any root app).
    self.initRoot = function (rootModel) {
        // Set global rootModel.
        app.rootModel = rootModel;

        // Configure and start pager.js (for routing), apply knockout binding.
        pager.extendWithPage(rootModel);
        self.mvc.applyBindings(rootModel);
        pager.start();
    };

}

// Event types
function EventTypes() {}
EventTypes.PAGE_CHANGED = null;
EventTypes.APP_DATA_LOADED = null;
EventTypes.DOMAIN_CREATED = null;
EventTypes.NOTIFICATION_REQUEST_CREATED = null;
