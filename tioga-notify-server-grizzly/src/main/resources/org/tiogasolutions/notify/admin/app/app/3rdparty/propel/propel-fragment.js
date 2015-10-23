/*
 * Copyright 2014 Harlan Noonkester
 *
 * All rights reserved
 */

function FragmentFactory(mvc, contentBaseUrl) {
    var self = this;

    // Establish patterns used for fragment creation (these can be overridden after construction).
    self.contentUrlPattern = "{contentBaseUrl}/{fragmentName}.html";
    self.fragmentRootIdPattern = "{fragmentName}-root";

    self.createFragment = function (fragmentName) {
        var contentUrl = self.contentUrlPattern
            .replace(/{contentBaseUrl}/gi, contentBaseUrl)
            .replace(/{fragmentName}/gi, fragmentName);
        var addFragmentToId = self.fragmentRootIdPattern
            .replace(/{fragmentName}/gi, fragmentName);

        // Create the fragment
        return new Fragment(fragmentName, mvc, {
            contentUrl: contentUrl,
            addFragmentToId: addFragmentToId
        });
    }
}

function Fragment(fragmentName, mvc, init) {
    var self = this;
    var fragmentElementAfterLoad = null;
    init = (init != null) ? init : {};
    // The url where the content for the fragment can be loaded.
    self.contentUrl = init.contentUrl;
    // The selector for the fragment root element.

    // Show the fragment, binding the given viewModel (callback is optional)
    self.show = function (viewModel) {
        var addFragmentToSelector = "#" + init.addFragmentToId;
        return self.showOn(viewModel, addFragmentToSelector);
    };

    self.showOn = function (addFragmentToSelector) {
        return self.showOn(null, addFragmentToSelector);
    };

    self.showOn = function (viewModel, addFragmentToSelector) {
        console.log("Showing fragment: " + fragmentName);
        return new Promise(function (resolve, reject) {
            $(addFragmentToSelector).load(self.contentUrl, function () {
                // Here we assume it's the first child element of the root element we loaded.
                fragmentElementAfterLoad = this.children[0];
                if (viewModel) {
                    mvc.applyBindings(viewModel, fragmentElementAfterLoad);
                }
                resolve();
            });
        });
    };

    // Hide the fragment
    self.hide = function () {
        if (fragmentElementAfterLoad) {
            fragmentElementAfterLoad.remove();
        }
    }
};

