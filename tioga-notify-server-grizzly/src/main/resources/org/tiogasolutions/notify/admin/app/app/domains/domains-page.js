function DomainsPage() {
    var self = this;
    self.domainOptions = app.mvc.observableArray();
    self.selectedDomainName = app.mvc.observable();
    self.selectedDomain = new Domain();
    self.isDomainSelected = app.mvc.observable(false);

    // Tabs
    self.currentTab = app.mvc.observable();
    self.routeCatalogTab = new DomainPageTab(self, "route-catalog");
    self.tasksTab = new DomainPageTab(self, "tasks");
    self.tabLoaded = function (arg) {
        // Keep track of the selected tab.
        var tabName = (arg.page) ? arg.page.currentId : null;
        // We default to "route-catalog" when we do not have a tab (it's role="start").
        tabName = app.util.isNotEmpty(tabName) ? tabName : "route-catalog";
        self.currentTab(tabName);
    };

    function domainNameSelected(domainName) {
        // When the domain name is selected navigate so URL is updated.
        if (domainName) {
            self.selectedDomain.load(domainName);
            self.isDomainSelected(true);
            pager.navigate("domains/" + domainName);
        } else {
            self.isDomainSelected(false)
            self.selectedDomain.clear();
        }
    };

    function loadDomainOptions() {
        return app.notifyClient.fetchDomains()
            .then(function(data) {
                self.domainOptions.removeAll();
                $.each(data.results, function (i, o) {
                    self.domainOptions.push(o.domainName)
                });
            });
    }

    app.bus.subscribe(EventTypes.DOMAIN_CREATED, function(domainData) {
        loadDomainOptions()
            .then(function() {
                self.selectedDomain.load(domainData.domainName);
                self.selectedDomainName(domainData.domainName);
                self.isDomainSelected(true);
            })
    });

    self.beforeShow = function(pageArg) {
        // Load the domain options
        loadDomainOptions()
            .then(function() {
                // Then auto selected the domain, if it exists in the route.
                var route = pageArg.page.route;
                if (route.length > 0 && app.util.isNotEmpty(route[0])) {
                    self.selectedDomain.load(route[0]);
                    self.selectedDomainName(route[0]);
                    self.isDomainSelected(true);
                }
                // In all cases create the domain name subscription.
                self.domainNameSubscription = self.selectedDomainName.subscribe(domainNameSelected)
            })
    };

    self.beforeHide = function() {
        // Remove the subscription when we hide.
        if (self.domainNameSubscription) {
            self.domainNameSubscription.dispose();
            self.domainNameSubscription = null;
        }
    };

    self.showCreateDomain = function() {
        var createDomain = new CreateDomain();
        app.dialogs.createDomain.show(createDomain);
    }
}

var DomainPageTab = function (domainsPage, pathSuffix) {
    var self = this;
    self.href = app.mvc.computed(function () {
        if (domainsPage.isDomainSelected()) {
            return app.url("app#domains", domainsPage.selectedDomainName(), pathSuffix);
        } else {
            return "#";
        }
    });
};

var CreateDomain = function() {
    var self = this;
    self.domainName = app.mvc.observable();

    self.createDomain = function() {
        return app.notifyClient.createDomain(self.domainName())
            .publish(EventTypes.DOMAIN_CREATED)
            .catchFinally();
    };

};
