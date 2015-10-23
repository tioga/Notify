// Works along with custom knockout "error" extender (defined in propel-mvc), mapping
// an ajax error to corresponding error observables defined off the given model.
function ErrorObserver(util, model) {
    if (!util) {
        throw new Error("ErrorObserver constructed without util.");
    }
    if (!model) {
        throw new Error("ErrorObserver constructed without model.");
    }
    var self = this;
    self.status = app.mvc.observable();
    self.message = app.mvc.observable("nothing yet");
    self.textStatus = app.mvc.observable();
    self.errorCode = app.mvc.observable();
    self.errorThrown = app.mvc.observable();
    self.allErrors = app.mvc.observableArray();
    self.unmatchedErrors = app.mvc.observableArray();
    self.hasUnmatched = app.mvc.computed(function() {
        return self.unmatchedErrors().length > 0;
    });

    // Keep a reference to all error observables (which are defined on the model as sub-observables).
    var errorObservables = {};
    for (var name in model) {
        var property = model[name];
        if (app.mvc.isObservable(property) && property.error && app.mvc.isObservable(property.error)) {
            // This is an observable with a child observable error property
            errorObservables[name] = property.error;
        }
    }

    // Assigned the given error to the appropriate observable (matching by fieldName),
    // or "unmatched" if not found.
    function assignErrorToObservable(error) {
        var propName = util.substringAfter(error.fieldName(), ".");
        var errorObservable = errorObservables[propName];
        if (!errorObservable) {
            // We did not find a corresponding error observable.
            self.unmatchedErrors.push(error);
        } else if (errorObservable()) {
            errorObservable(errorObservable() + "\n" + error.message());
        } else {
            errorObservable(error.message());
        }
    }

    self.addError = function (errorData) {
        var observableError = new ObservableError(errorData);
        self.allErrors.push(observableError);
        assignErrorToObservable(observableError);
    };

    // Build from the ajax response.
    self.build = function (xhr, textStatus, errorThrown) {
        var httpError = new HttpError(util);
        self.clear();
        if (!xhr) {
            throw new Error("Cannot build HttpError, given null jqXHR argument.");
        }
        httpError.build(xhr, textStatus, errorThrown);
        self.status(httpError.status);
        self.textStatus(httpError.textStatus);
        self.errorThrown(httpError.errorThrown);
        self.message(httpError.fullText);

        for (var j=0; j < httpError.messages.length; j++) {
            var message = httpError.messages[j];
            var errorData = {
                fieldName : message.fieldName(),
                message : message.text
            };
            self.addError(errorData);
        }

        // After processing if we don't have any error add the message as an error.
        if (self.allErrors().length == 0) {
            self.addError({ message: self.message() });
        }
    };

    // We can clear now that we know of all the observables.
    self.clear = function () {
        app.mvc.clearAll(self);
        app.mvc.clearAll(errorObservables);
    };
    self.clear();
}

function ObservableError(errorData) {
    var self = this;
    self.fieldName = app.mvc.observable(errorData.fieldName);
    self.message = app.mvc.observable(errorData.message);
}

