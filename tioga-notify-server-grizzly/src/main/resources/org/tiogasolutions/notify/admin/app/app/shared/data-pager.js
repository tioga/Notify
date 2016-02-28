var LocalDataPager = function() {
    var self = this;
    self.dataSet = ko.observableArray();
    self.pageSize = ko.pureComputed({
        read: function () {
            return this._pageSize();
        },
        write: function (val) {
            this._pageSize(isNaN(val) ? Number(val) : val);
        },
        owner: self
    });
    self.setPageSize = function (val) {
        var pagesize = isNaN(val) ? Number(val) : val;
        if (self.pageSizeOptions().indexOf(pagesize) === -1) {
            self.pageSizeOptions.push(pagesize);
        }
        self._pageSize(val);
        self.pageSizeOptions.sort(function (l, r) { return l === r ? 0 : l < r ? -1 : 1 });
        return self;
    };

    self._pageSize = ko.observable(10);
    self.currentPageIndex = ko.observable(0);
    self.pageSizeOptions = ko.observableArray([10, 25, 50, 100]);

    self.data = ko.computed(function () {
        var startIndex = this._pageSize() * this.currentPageIndex();
        return this.dataSet.slice(startIndex, startIndex + this._pageSize());
    }, self);

    self.lastIndexCurrentPage = ko.computed(function () {
        return (((((this.currentPageIndex() + 1) * this._pageSize()) - (this._pageSize() - 1)) + this.data().length) - 1);
    }, self);

    self.firstIndexCurrentPage = ko.computed(function () {
        return (((this.currentPageIndex() + 1) * this._pageSize()) - (this._pageSize() - 1));
    }, self);

    self.maxPageIndex = ko.computed(function () {
        return Math.ceil(ko.utils.unwrapObservable(this.dataSet).length / self._pageSize()) - 1;
    }, self);

    self.displayRangeText = ko.computed(function () {
        return 'Displaying items ' + this.firstIndexCurrentPage() + ' - ' + this.lastIndexCurrentPage() + ' of ' + this.dataSet().length;
    }, self);

    self.isMultiPage = ko.computed(function () {
        return this.maxPageIndex() > 0;
    }, self);

    self.isFirstPage = ko.computed(function () {
        return self.currentPageIndex() === 0;
    });

    self.isLastPage = ko.computed(function () {
        return self.currentPageIndex() === self.maxPageIndex();
    });

    self.startCurrentRange = ko.computed(function () {
        return self.currentPageIndex() - (self.currentPageIndex() % 10);
    });

    self.endCurrentRange = ko.computed(function () {
        return (self.currentPageIndex() - (self.currentPageIndex() % 10)) + 9;
    });

    self.jumpToFirst = function () {
        self.currentPageIndex(0);
    };

    self.previousPage = function () {
        if (self.currentPageIndex() > 0) {
            self.currentPageIndex(self.currentPageIndex() - 1);
        }
    };

    self.nextPage = function () {
        if (self.currentPageIndex() < self.maxPageIndex()) {
            self.currentPageIndex(self.currentPageIndex() + 1);
        }
    };

    self.jumpToLast = function () {
        self.currentPageIndex(self.maxPageIndex());
    };

    self.reload = function (e) {
        if (self.maxPageIndex() < self.currentPageIndex()) self.jumpToLast();
    };

    self.toggleSortProp = function (prop) {
        if (prop === "a") {
            return "d";
        } else {
            return "a";
        }
    };

    self.search = function (searchString, fields, observableArray) {
        if (searchString === '' || searchString === 'All') {
            self.updateSet(observableArray);
        } else {
            self.updateSet(ko.observableArray(observableArray().filter(function (o) {
                if ($.isArray(fields)) {
                    var isIn = -1;
                    $.each(fields, function (idx, obj) {
                        isIn = o[obj]().toLowerCase().indexOf(searchString.toLowerCase()) > -1;
                    });
                    return isIn;
                } else {
                    return o[fields]().toLowerCase().indexOf(searchString.toLowerCase()) > -1;
                }
            })));
        }
    };

    self.sort = function (field, sortOrder) {
        self.toggleSortProp(sortOrder);
        var sortFunc = function (field, a, b) { return a[field]() === b[field]() ? 0 : (a[field]() < b[field]() ? -1 : 1); };
        self.dataSet.sort(function (l, r) {
            if (sortOrder === "a" || sortOrder === "a") {
                return sortFunc(field, l, r);
            } else {
                return sortFunc(field, r, l);

            }
        });
        return self.toggleSortProp(sortOrder);
    };

    self.updateSet = function (data) {
        var self = this;
        self.dataSet.removeAll();
        $.each(ko.unwrap(data), function (i, o) {
            self.dataSet.push(o);
        });
        return self;
    };
}

var RemoteDataPager = function() {
    var self = this;
    self.dataSet = ko.observableArray();
    self.pageSize = ko.pureComputed({
        read: function () {
            return this._pageSize();
        },
        write: function (val) {
            this._pageSize(isNaN(val) ? Number(val) : val);
        },
        owner: self
    });
    self.setPageSize = function (val) {
        var pagesize = isNaN(val) ? Number(val) : val;
        if (self.pageSizeOptions().indexOf(pagesize) === -1) {
            self.pageSizeOptions.push(pagesize);
        }
        self._pageSize(val);
        self.pageSizeOptions.sort(function (l, r) { return l === r ? 0 : l < r ? -1 : 1 });
        return self;
    };

    self._pageSize = ko.observable(10);
    self.pageSizeOptions = ko.observableArray([10, 25, 50, 100]);

    self.hasNextPage = ko.observable();
    self.hasPreviousPage = ko.observable();
    self.offset = ko.observable(0);

    self.data = ko.computed(function () {
        return self.dataSet();
    }, self);

    self.isFirstPage = ko.computed(function () {
        return self.hasPreviousPage() === false;
    });

    self.isLastPage = ko.computed(function () {
        return self.hasNextPage() === false;
    });

    self.update = function(queryResult) {
        self.hasNextPage(queryResult.hasNextPage);
        self.hasPreviousPage(queryResult.hasPreviousPage);
        self.offset(queryResult.offset);
        self.updateSet(queryResult.results);
    };

    self.updateSet = function (data) {
        var self = this;
        self.dataSet.removeAll();
        $.each(ko.unwrap(data), function (i, o) {
            self.dataSet.push(o);
        });
        return self;
    };
};