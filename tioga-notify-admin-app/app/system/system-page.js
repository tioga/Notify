function SystemPage() {
    var self = this;
    self.errorMessages = app.mvc.observableArray();
    self.hasErrors = app.mvc.computed(function () {
        return self.errorMessages().length > 0;
    });

}
