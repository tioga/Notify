
/*
 * Copyright 2014 Harlan Noonkester
 *
 * All rights reserved
 */

function Ajax(util, baseUrl) {
    if (!util) {
        throw new Error("Null util argument given to Ajax constructor.");
    }
    if (!baseUrl) {
        throw new Error("Null baseUrl argument given to Ajax constructor.");
    }
    var self = this;
    self.baseUrl = baseUrl;

    // Default OnAjaxError
    var defaultAjaxErrorHandler = function (xhr) {
        alert("Ajax failure - " + xhr.status + " : " + xhr.statusText);
        return xhr;
    };
    self.setDefaultAjaxErrorHandler = function (handler) {
        if (!util.isFunction(handler)) {
            throw new Error("Argument to setDefaultAjaxErrorHandler is not a function");
        }
        defaultAjaxErrorHandler = handler;
        return this;
    };

    // Default settings used for ajax (jQuery ajax settings argument).
    var defaultAjaxSettings = {};
    self.setDefaultAjaxSettings = function (defaultSettingsArg) {
        if (!util.isUserDefinedObject(defaultSettingsArg)) {
            throw new Error("setDefaultAjaxSettings argument is not an object");
        }
        defaultAjaxSettings = defaultSettingsArg;
        return this;
    };

    // REVIEW - should likely do this elsewhere, but we have all the needed pieces here.
    // Add catchAjax method to Promise. Arguments are the function to call when the error
    // has occurred (optional, defaults to default ajax error handler) and also optionally
    // an http status code (or list of) that should be used in the predicate (determining 
    // if the function is called for the given ajax error).
    Promise.prototype.catchAjax = function (arg1, arg2) {
        var onError = defaultAjaxErrorHandler;
        var statusCodes = null;
        if (arg1) {
            if (util.isFunction(arg1)) {
                onError = arg1;
            } else {
                statusCodes = arg1;
            }
        }
        if (arg2) {
            if (util.isFunction(arg2)) {
                onError = arg2;
            } else if (statusCodes == null) {
                statusCodes = arg2;
            }
        }

        // The predicate will ensure it's an ajax request error and that any specified status code will be taken into consideration.
        var predicate = new AjaxErrorPredicate(util, statusCodes);

        // Add a catch to "this" (assumed to be the promise) using the predicate and the onError function.
        return this.catch(predicate.isTrue, onError);
    };

    // Methods
    self.get = function () {
        var url = util.buildUrlFromArgs(self.baseUrl, arguments);
        var request = new AjaxRequest(util, url, defaultAjaxErrorHandler, defaultAjaxSettings);
        request.processData(true);
        request.type("GET");
        return request;
    };
    self.post = function () {
        var url = util.buildUrlFromArgs(self.baseUrl, arguments);
        var request = new AjaxRequest(util, url, defaultAjaxErrorHandler, defaultAjaxSettings);
        request.type("POST");
        return request;
    };
    self.put = function () {
        var url = util.buildUrlFromArgs(self.baseUrl, arguments);
        var request = new AjaxRequest(util, url, defaultAjaxErrorHandler, defaultAjaxSettings);
        request.type("PUT");
        return request;
    };
    self.delete = function () {
        var url = util.buildUrlFromArgs(self.baseUrl, arguments);
        var request = new AjaxRequest(util, url, defaultAjaxErrorHandler, defaultAjaxSettings);
        request.type("DELETE");
        return request;
    };

}

function AjaxRequest(util, url, defaultAjaxOnError, ajaxSettingsArg) {
    var self = this;
    if (!util) {
        throw new Error("Web constructed without util.");
    }

    // Clone the setting and callback args so we don't modify defaults.
    self.ajaxSettings = $.extend(true, {}, ajaxSettingsArg);

    // Execute
    self.execute = function () {
        self.ajaxSettings.url = url;
        var xhr = $.ajax(self.ajaxSettings);
        // Add the url as a field to help with error handling.
        xhr.requestUrl = url;

        // Resolve to a promise
        return Promise.resolve(xhr);
    };

    // Data
    self.data = function (data, contentType) {
        self.ajaxSettings.data = data;
        if (contentType !== undefined) {
            self.ajaxSettings.contentType = contentType;
        }
        return this;
    };
    self.jsonData = function (data) {
        self.ajaxSettings.data = data;
        self.ajaxSettings.contentType = "application/json";
        return this;
    };
    self.jsonObject = function (object) {
        self.ajaxSettings.data = util.toJson(object);
        self.ajaxSettings.contentType = "application/json";
        return this;
    };

    // ajaxSettings
    self.beforeSend = function () {
        self.ajaxSettings.beforeSend = arguments;
        return self;
    };
    self.type = function (type) {
        self.ajaxSettings.type = type;
        return this;
    };

    // Expected/requested data format specified by object in form {xml: 'text/xml',text: 'text/plain'}
    self.accepts = function (accepts) {
        if (util.isUserDefinedObject(accepts)) {
            throw new Error("Ajax.accepts() called with argument which is not an object, jQuery doc is not so clear on this but that is what's expected. May want to use dataType.");
        }
        self.ajaxSettings.accepts = accepts;
        return this;
    };

    // Expected/requested data format specified by string in form "application/json"
    self.dataType = function (dataType) {
        self.ajaxSettings.dataType = dataType;
        return this;
    };

    // If true data will be transformed to a query string, false data will be sent in content
    self.processData = function (processData) {
        self.ajaxSettings.processData = processData;
        return this;
    };

    // Content type of data being sent.
    self.contentType = function (contentType) {
        self.ajaxSettings.contentType = contentType;
        return this;
    };

    self.statusCodeCallbacks = function (statusCodeCallbacks) {
        self.ajaxSettings.statusCode = statusCodeCallbacks;
        return this;
    };
    self.headers = function (headers) {
        self.ajaxSettings.headers = headers;
        return this;
    };
    self.timeout = function (timeout) {
        self.ajaxSettings.timeout = timeout;
        return this;
    };
    self.credentials = function (username, password) {
        self.ajaxSettings.username = username;
        self.ajaxSettings.password = password;
        return this;
    };
}

function AjaxErrorPredicate(util, statusCodes) {
    var self = this;
    var codes = [];

    // Status codes may be an array or single value.
    if (statusCodes != null) {
        if (util.isArray(statusCodes)) {
            codes = statusCodes;
        } else {
            codes.push(statusCodes);
        }
    }

    // Return true if error is an XHR and meets any statusCode requirements.
    self.isTrue = function (error) {
        // A simple to check to see if it looks like an jqXHR
        if (error.status == undefined || error.statusCode == undefined) {
            // Definitely not an jqXHR, so return false (don't give to this handler).
            return false;
        }
        if (codes.length == 0) {
            return true;
        }
        for (var i = 0; i < codes.length; i++) {
            if (error.status == codes[i]) {
                return true;
            }
        }
        return false;
    };
}

