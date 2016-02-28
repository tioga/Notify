function Home() {
    var self = this;
    self.testMode = app.mvc.observable();
    self.userName = app.mvc.observable();
    self.password = app.mvc.observable();
    self.authenticated = app.mvc.observable();

};
