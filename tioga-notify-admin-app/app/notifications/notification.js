function Notification(domainName, data) {
    var self = this;
    self.domainName = app.mvc.observable(data.domainName);
    self.notificationId = app.mvc.observable(data.notificationId);
    self.topic = app.mvc.observable(data.topic);
    self.summary = app.mvc.observable(data.summary);
    self.trackingId = app.mvc.observable(data.trackingId);
    self.createdAt = app.mvc.observable(data.createdAtLocal);
    self.traitMap = app.mvc.observable(JSON.stringify(data.traitMap, null, ' '));
    self.exceptionInfo = new ExceptionInfo(data.exceptionInfo);
    self.attachments = app.mvc.observableArray();
    self.json = JSON.stringify(data, null, '\t');
    $.each(data.attachmentInfoList, function(i, o) {
        self.attachments.push(new AttachmentInfo(domainName, data.notificationId, o));
    });
    self.hasException = app.mvc.computed(function() {
        return app.util.isNotEmpty(self.exceptionInfo.exceptionType());
    });
    self.hasAttachments = app.mvc.computed(function() {
        return self.attachments().length > 0;
    });

    self.showException = function() {
        app.dialogs.exceptinInfo.show(self.exceptionInfo);
    };

    self.showDetails = function() {
        app.dialogs.notificationDetail.show(self);
    };


}

function ExceptionInfo(data) {
    var self = this;
    data = (data != null) ? data : {};
    self.exceptionType = app.mvc.observable(data.exceptionType);
    self.message = app.mvc.observable(data.message);
    self.stackTrace = app.mvc.observable(data.stackTrace);
}

function AttachmentInfo(domainName, notificationId, data) {
    var self = this;
    data = (data != null) ? data : {};
    self.name = app.mvc.observable(data.name);
    self.contentType = app.mvc.observable(data.contentType);
    self.openAttachment = function() {
        app.notifyClient.openAttachment(domainName, notificationId, self.name());
    }
}
