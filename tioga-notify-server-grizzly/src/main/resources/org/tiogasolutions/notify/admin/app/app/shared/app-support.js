
function Shared() {
}

// PageContext (specific to pager.js) - fires events when page changes, provides model for page info.
function PageContext() {
    var self = this;

    // Keep track of the page path.
    self.pagePath = undefined;

    // Return true if current page path starts with given pageId
    self.isPagePathRoot = function (pageId) {
        if (self.pagePath) {
            return app.util.startsWith(self.pagePath, pageId);
        } else {
            return false;
        }
    };

    // Return true if given path is a match for the current pagePath.
    self.isPagePath = function (path) {
        if (self.pagePath) {
            return self.pagePath === path;
        } else {
            return false;
        }
    };

    // Return true if given path ends with the given subpath
    self.isPagePathEnding = function (path) {
        if (self.pagePath) {
            return app.util.endsWith(self.pagePath, path);
        } else {
            return false;
        }
    };

    // Keeps pagePath up to date with Pager.
    pager.beforeShow.add(function (options) {

        // Set pagePath based on the new page.
        var newPagePath = "";
        var page = options.page;
        while (page && page.getId()) {
            newPagePath = page.getId() + "/" + newPagePath;
            page = page.getParentPage();
        }
        self.pagePath = newPagePath.substring(0, newPagePath.length - 1);

        // Publish event that page has changed.
        app.bus.publish(EventTypes.PAGE_CHANGED, self);
    });
}

function HttpError(util) {
    if (!util) {
        throw new Error("HttpError constructed without util.");
    }
    var self = this;
    self.fullText = null;
    self.status = 0;
    self.textStatus = null;
    self.errorThrown = null;
    self.messages = [];

    self.build = function(jqXHR, textStatus, errorThrown) {

        if (!jqXHR) {
            throw new Error("Cannot build HttpError, given null jqXHR argument.");
        }
        self.status = jqXHR.status;
        self.textStatus = textStatus;
        self.errorThrown = errorThrown;

        // Parse the response into FineMessages (based on content type).
        var responseText = jqXHR.responseText;

        var contentType = jqXHR.getResponseHeader("Content-Type");
        if (contentType === "text/plain") {
            self.messages.push(new HttpErrorMessage(responseText));

        } else if (contentType === "application/json") {
            var responseObject = JSON.parse(responseText);
            if (util.isString(responseObject.message)) {
                self.messages.push(new HttpErrorMessage(responseObject.message));
            } else if (util.isArray(responseObject.messages)) {
                for (var i=0; i < responseObject.messages.length; i++) {
                    self.messages.push(new HttpErrorMessage(responseObject.messages[i]));
                }
            } else {
                self.messages.push(new HttpErrorMessage("Unexpected error structure: " + responseText));
            }

        } else {
            self.messages.push(new HttpErrorMessage("Unable to parse response content type: " + contentType));
        }

        // Construct the text
        self.fullText = self.textStatus +
        " - " + errorThrown +
        " [" + self.status + "]\n";

        self.customText = errorThrown +
        " [" + self.status + "]\n";

        for (var j=0; j < self.messages.length; j++) {
            var message = self.messages[j];
            self.fullText += message.text;
            if (message.traitMap) {
                self.fullText += " ";
                self.fullText += message.traitText();
            }
            self.fullText += "\n";

            // Custom text does not include traits, formats for bad request.
            self.customText += message.customText;
            self.customText += "\n";
        }
    }

}

function HttpErrorMessage(message) {
    var self = this;
    self.text = null;
    self.customText = null;
    self.traitMap = null;

    // Parse text.
    if (Object.prototype.toString.call(message) == "[object String]") {
        self.text = message;
    } else if (message.hasOwnProperty("text")) {
        self.text = message.text;
    } else {
        self.text = "Unexpected message structure.";
    }

    // Trait map
    if (message.traitMap) {
        self.traitMap = message.traitMap;
    } else {
        self.traitMap = {};
    }

    if (self.traitMap.property) {
        self.customText = self.traitMap.property + " " + self.text;
    } else {
        self.customText = self.text;
    }

    self.fieldName = function() {
        return self.traitMap.property;
    };

    self.traitText = function() {
        var traitText = "";
        var value;
        for (var prop in self.traitMap) {
            if (self.traitMap.hasOwnProperty(prop)) {
                value = self.traitMap[prop];
                traitText += prop + ": " + value + ", ";
            }
        }
        if (traitText.length > 2) {
            traitText = "[" + traitText.substring(0, traitText.length - 2) + "]";
        }
        return traitText;
    };

}

// Overlay used to disable UI such as with Ajax calls.
function Overlay() {
    var self = this;
    var working = false;
    self.show = function (delayMiliseconds) {
        working = true;
        if (delayMiliseconds) {
            setTimeout(function() {
                if (working) {
                    $('body').addClass('working');
                }
            }, delayMiliseconds);
        } else {
            $('body').addClass('working');
        }
    };
    self.hide = function () {
        working = false;
        $('body').removeClass('working');
    };
}
