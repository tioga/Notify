/*
 * Copyright 2014 Harlan Noonkester
 *
 * All rights reserved
 */

function DialogFactory(mvc, contentBaseUrl, dialogRootAppendSelector) {
    var self = this;

    // Establish patterns used for dialog creation (these can be overriden after construction).
    self.contentUrlPattern = "{contentBaseUrl}/{dialogName}-dialog.html";
    self.dialogRootIdPattern = "{dialogName}-root";
    self.dialogRootHtmlPattern = "<div id='{dialogRootId}' class='modal fade' tabindex='-1' role='dialog'></div>";
    self.dialogContentIdPattern = "{dialogName}-dialog";

    // Bootstrap modal options
    self.dialogOptions = {};

    self.createDialog = function (dialogName, dialogOptionsForThisDialog) {
        var contentUrl = self.contentUrlPattern
            .replace(/{contentBaseUrl}/gi, contentBaseUrl)
            .replace(/{dialogName}/gi, dialogName);
        var dialogRootId = self.dialogRootIdPattern
            .replace(/{dialogName}/gi, dialogName);
        var dialogContentId = self.dialogContentIdPattern
            .replace(/{dialogName}/gi, dialogName);

        // Create the dialog root html and append (this is the root html element the actual dialog will be appended to).
        var dialogRootHtml = self.dialogRootHtmlPattern
            .replace(/{dialogRootId}/gi, dialogRootId);
        $(dialogRootAppendSelector).append(dialogRootHtml);

        dialogOptionsForThisDialog = (dialogOptionsForThisDialog != null) ? dialogOptionsForThisDialog : self.dialogOptions; 

        // Create the dialog
        return new Dialog(mvc, {
            contentUrl: contentUrl,
            dialogRootId: dialogRootId,
            dialogContentId: dialogContentId,
            dialogOptions: dialogOptionsForThisDialog
        });
    }
}

function Dialog(mvc, init) {
    var self = this;
    init = (init != null) ? init : {};
    // The url where the content for the dialog can be loaded.
    self.contentUrl = init.contentUrl;
    // The selector for the dialog root element.
    self.dialogRootSelector = "#" + init.dialogRootId;
    // The selector for the dialog content
    self.dialogContentSelector = "#" + init.dialogContentId;
    // Bootstrap modal options
    self.dialogOptions = init.dialogOptions;

    // Show the dialog, binding the given viewModel (callback is optional)
    self.show = function (viewModel) {
        return new Promise(function (resolve, reject) {
            $(self.dialogRootSelector).load(self.contentUrl, function () {
                if (viewModel) {
                    mvc.applyBindings(viewModel, $(self.dialogContentSelector).get(0));
                }
                $(self.dialogRootSelector).modal(self.dialogOptions);

                // Once the model is shown we do the following.
                $(self.dialogRootSelector).on('shown.bs.modal', function () {
                    // Auto focus (bootstrap seems to have issues with this on it's own so we do it here).
                    $(self.dialogRootSelector + " [autofocus]").focus();

                    // Resolve the promise.
                    resolve();
                });

            });

        });
    };

    // Hide the dialog
    self.hide = function () {
        $(self.dialogRootSelector).modal('hide');
    }
};

