
/*
 * Copyright 2014 Harlan Noonkester
 *
 * All rights reserved
 */

function Mvc(util) {
    var self = this;
    
    initExtenders();
    initBindings();

    self.applyBindings = function(model, element) {
        if (element === undefined) {
            ko.applyBindings(model);
        } else {
            ko.applyBindings(model, element);
        }
    };

    self.unwrap = function(model, element) {
        ko.unwrap(model, element);
    };

    self.toJson = function(model) {
        return ko.toJSON(model);
    };

    self.toJS = function(model) {
        return ko.toJS(model);
    };

    self.toJson = function(model) {
        return ko.toJSON(model);
    };

    self.clearAll = function(model) {
        for (var p in model) {
            if (model.hasOwnProperty(p)) {
                var prop = model[p];
                if (ko.isObservable(prop) && !ko.isComputed(prop)) {
                    if (util.isFunction(prop.removeAll)) {
                        // This is an observable array
                        prop.removeAll();
                    } else {
                        // Non-array observable
                        prop(null);
                    }
                }
            }
        }
    }

    self.clearExcluding = function (model, excluding) {
        if (util.isArray(excluding) == false) {
            throw new Error("mvc.clearExcluding called  withexcluding argument which is not an array");
        }
        for (var p in model) {
            if (model.hasOwnProperty(p)) {
                var prop = model[p];
                if (ko.isObservable(prop) && !ko.isComputed(prop)) {
                    if (excluding.indexOf(prop) == -1) {
                        if (util.isFunction(prop.removeAll)) {
                            // This is an observable array
                            prop.removeAll();
                        } else {
                            // Non-array observable
                            prop(null);
                        }
                    }
                }
            }
        }
    }

    self.observable = function (value) {
        if (value === undefined) {
            return ko.observable();
        } else {
            return ko.observable(value);
        }
    };

    self.observableArray = function(value) {
        if (value === undefined) {
            return ko.observableArray();
        } else {
            return ko.observableArray(value);
        }
    };

    self.computed = function(func) {
        return ko.computed(func);
    };

    self.updateModel = function(model, data, mapping) {
        mapping = (mapping) ? mapping : {};
        ko.mapping.fromJS(data, mapping, model);
        return model;
    };

    self.isObservable = function(value) {
        return ko.isObservable(value);
    };

    function initExtenders() {
        // Knockout extender which will create child observable "error", used for displaying validation errors.
        ko.extenders.error = function (target) {
            target.error = nf.mvc.observable();
        };

    }

    function initBindings() {

        ko.bindingHandlers.focusChange = {
            init: function (element, valueAccessor, allBindingsAccessor, viewModel) {
                var value = valueAccessor();
                var focusChangeCallback;
                if (ko.isObservable(value)) {
                    // If value is observable we should have a focusChange function, added as an extension.
                    if (!util.isFunction(value.focusChange)) {
                        throw new Error("focusChange binding could not find focusChange extender function on the observable - elementId = " + element.id);
                    }
                    $(element).focus(value.focusChange);
                    $(element).blur(value.focusChange);
                } else if (util.isFunction(value)) {
                    focusChangeCallback = value;
                    $(element).focus(focusChangeCallback);
                    $(element).blur(focusChangeCallback);
                } else if (util.isUserDefinedObject(value) && util.isFunction(value.action)) {
                    var action = value.action;
                    var additionalArgs = value;
                    // Delete the action from additional args so it's not passed into action (itself)
                    delete additionalArgs.action;
                    $(element).focus(function (event) {
                        action(event, additionalArgs);
                    });
                    $(element).blur(function () {
                        action(event, additionalArgs);
                    });
                }
            }
        };

        ko.bindingHandlers.focusChangeInvoke = {
            init: function (element, valueAccessor, allBindingsAccessor, viewModel) {
                // Support focusChange: actionFunc or focusChange: {action:actionFunc, name:'someName'}
                var value = valueAccessor();
                var additionalArgs = {
                };
                var action;
                if (util.isFunction(value)) {
                    action = value;
                } else if (util.isUserDefinedObject(value) && util.isFunction(value.action)) {
                    action = value.action;
                    additionalArgs = value;
                    // Delete the action from additional args so it's not passed into action (itself)
                    delete additionalArgs.action;
                } else {
                    throw new Error("Element " + element.id + " define focusChange binding but action method is not defined.");
                }
                $(element).focus(function (event) {
                    action(event, additionalArgs);
                });
                $(element).blur(function () {
                    action(event, additionalArgs);
                });
            }
        };

        ko.bindingHandlers.date = {
            update: function (element, valueAccessor, allBindingsAccessor, viewModel) {
                var value = ko.utils.unwrapObservable(valueAccessor());
                if (value != null) {
                    var pattern = allBindingsAccessor.datePattern || 'MM/DD/YYYY';
                    $(element).html(moment(value).format(pattern));
                }
            }
        };

        ko.bindingHandlers.datetime = {
            update: function (element, valueAccessor, allBindingsAccessor, viewModel) {
                var value = ko.utils.unwrapObservable(valueAccessor());
                if (value != null) {
                    var pattern = allBindingsAccessor.datePattern || 'MM/DD/YYYY HH:mm:ss';
                    var dateTime = moment(value);
                    $(element).html(dateTime.format(pattern));
                }
            }
        };

        ko.bindingHandlers.phoneNumber = {
            update: function (element, valueAccessor, allBindingsAccessor, viewModel) {
                var value = ko.utils.unwrapObservable(valueAccessor());
                $(element).html(util.formatPhoneNumber(value));
            }
        };

        ko.bindingHandlers.masked = {
            init: function (element, valueAccessor, allBindings) {
                if (window && window.navigator) {
                    var parseVersion = function (matches) {
                        if (matches) {
                            return parseFloat(matches[1]);
                        }
                    };

                    // Detect various browser versions because some old versions don't fully support the 'input' event
                    var operaVersion = window.opera && window.opera.version && parseInt(window.opera.version()),
                        userAgent = window.navigator.userAgent,
                        safariVersion = parseVersion(userAgent.match(/^(?:(?!chrome).)*version\/([^ ]*) safari/i)),
                        firefoxVersion = parseVersion(userAgent.match(/Firefox\/([^ ]*)/));
                }

                var previousElementValue = element.value,
                    timeoutHandle,
                    elementValueBeforeEvent;

                var updateModel = function (event) {
                    clearTimeout(timeoutHandle);
                    elementValueBeforeEvent = timeoutHandle = undefined;

                    function writeValueToProperty(property, allBindings, key, value, checkIfDifferent) {
                        if (!property || !ko.isObservable(property)) {
                            var propWriters = allBindings.get('_ko_property_writers');
                            if (propWriters && propWriters[key])
                                propWriters[key](value);
                        } else if (ko.isWriteableObservable(property) && (!checkIfDifferent || property.peek() !== value)) {
                            property(value);
                        }
                    }

                    var elementValue = element.value;
                    if (previousElementValue !== elementValue) {
                        // Provide a way for tests to know exactly which event was processed
                        previousElementValue = elementValue;
                        writeValueToProperty(valueAccessor(), allBindings, 'textInput', elementValue);
                    }
                };

                var deferUpdateModel = function (event) {
                    if (!timeoutHandle) {
                        // The elementValueBeforeEvent variable is set *only* during the brief gap between an
                        // event firing and the updateModel function running. This allows us to ignore model
                        // updates that are from the previous state of the element, usually due to techniques
                        // such as rateLimit. Such updates, if not ignored, can cause keystrokes to be lost.
                        elementValueBeforeEvent = element.value;
                        var handler = updateModel;
                        timeoutHandle = setTimeout(handler, 4);
                    }
                };

                var updateView = function () {
                    var modelValue = ko.utils.unwrapObservable(valueAccessor());

                    if (modelValue === null || modelValue === undefined) {
                        modelValue = '';
                    }

                    if (elementValueBeforeEvent !== undefined && modelValue === elementValueBeforeEvent) {
                        setTimeout(updateView, 4);
                        return;
                    }

                    // Update the element only if the element and model are different. On some browsers, updating the value
                    // will move the cursor to the end of the input, which would be bad while the user is typing.
                    if (element.value !== modelValue) {
                        previousElementValue = modelValue;  // Make sure we ignore events (propertychange) that result from updating the value
                        element.value = modelValue;
                    }
                };

                var onEvent = function (event, handler) {
                    ko.utils.registerEventHandler(element, event, handler);
                };

                if (ko.utils.ieVersion < 10) {
                    // Internet Explorer <= 8 doesn't support the 'input' event, but does include 'propertychange' that fires whenever
                    // any property of an element changes. Unlike 'input', it also fires if a property is changed from JavaScript code,
                    // but that's an acceptable compromise for this binding. IE 9 does support 'input', but since it doesn't fire it
                    // when using autocomplete, we'll use 'propertychange' for it also.
                    onEvent('propertychange', function (event) {
                        if (event.propertyName === 'value') {
                            updateModel(event);
                        }
                    });

                    if (ko.utils.ieVersion == 8) {
                        // IE 8 has a bug where it fails to fire 'propertychange' on the first update following a value change from
                        // JavaScript code. It also doesn't fire if you clear the entire value. To fix this, we bind to the following
                        // events too.
                        onEvent('keyup', updateModel);      // A single keystoke
                        onEvent('keydown', updateModel);    // The first character when a key is held down
                    }
                    if (ko.utils.ieVersion >= 8) {
                        // Internet Explorer 9 doesn't fire the 'input' event when deleting text, including using
                        // the backspace, delete, or ctrl-x keys, clicking the 'x' to clear the input, dragging text
                        // out of the field, and cutting or deleting text using the context menu. 'selectionchange'
                        // can detect all of those except dragging text out of the field, for which we use 'dragend'.
                        // These are also needed in IE8 because of the bug described above.
                        registerForSelectionChangeEvent(element, updateModel);  // 'selectionchange' covers cut, paste, drop, delete, etc.
                        onEvent('dragend', deferUpdateModel);
                    }
                } else {
                    // All other supported browsers support the 'input' event, which fires whenever the content of the element is changed
                    // through the user interface.
                    onEvent('input', updateModel);

                    if (safariVersion < 5 && ko.utils.tagNameLower(element) === "textarea") {
                        // Safari <5 doesn't fire the 'input' event for <textarea> elements (it does fire 'textInput'
                        // but only when typing). So we'll just catch as much as we can with keydown, cut, and paste.
                        onEvent('keydown', deferUpdateModel);
                        onEvent('paste', deferUpdateModel);
                        onEvent('cut', deferUpdateModel);
                    } else if (operaVersion < 11) {
                        // Opera 10 doesn't always fire the 'input' event for cut, paste, undo & drop operations.
                        // We can try to catch some of those using 'keydown'.
                        onEvent('keydown', deferUpdateModel);
                    } else if (firefoxVersion < 4.0) {
                        // Firefox <= 3.6 doesn't fire the 'input' event when text is filled in through autocomplete
                        onEvent('DOMAutoComplete', updateModel);

                        // Firefox <=3.5 doesn't fire the 'input' event when text is dropped into the input.
                        onEvent('dragdrop', updateModel);       // <3.5
                        onEvent('drop', updateModel);           // 3.5
                    }
                }

                // Bind to the change event so that we can catch programmatic updates of the value that fire this event.
                onEvent('change', updateModel);

                ko.computed(updateView, null, { disposeWhenNodeIsRemoved: element });

                // Find the mask in maskedOtions binding
                var mask;
                var options = {};
                if (allBindings().maskedOptions) {
                    var maskedOptions = allBindings().maskedOptions;
                    if (util.isString(maskedOptions)) {
                        // If only a string value for maskedOptions then use that as the mask
                        mask = maskedOptions;
                    } else if (util.isUserDefinedObject(maskedOptions)) {
                        // If an object then look for a mask property
                        mask = maskedOptions.mask;
                        // And use the rest as options.
                        options = maskedOptions;
                        delete options.mask;
                    }
                }
                if (mask) {
                    if (!options.placeholder) {
                        // Default to space as placeholder if not specified.
                        options.placeholder = " ";
                    }
                    // Apply mask using jquery.inputmask
                    $(element).inputmask($.extend({ "mask": mask }, options));
                } else {
                    throw Error("Element " + element.id + "bound with mask yet does not define a mask.");
                }
            }
        };

        ko.bindingHandlers.datePicker = {
            init: function(element, valueAccessor, allBindingsAccessor) {
                // Initialize pikaday with any configured date patterh.
                var datePattern = allBindingsAccessor.get('datePattern') || 'YYYY-MM-DD';
                var pikaday = new Pikaday({
                    field: element,
                    format: datePattern
                });

                // REVIEW - seems weird to put this on allBindingsAccessor but seemed better than on element and could not think of another alternative
                allBindingsAccessor.pikaday = pikaday;

                // Element value changed, update the observable
                ko.utils.registerEventHandler(element, "change", function () {
                    var observable = valueAccessor();
                    observable(pikaday.toString());
                });

                // Handle disposal (if KO removes by the template binding)
                ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
                    pikaday.destroy();
                });

            },
            update: function (element, valueAccessor, allBindingsAccessor, viewModel) {
                var value = ko.utils.unwrapObservable(valueAccessor());
                if (value != null) {
                    var datePattern = allBindingsAccessor.get('datePattern') || 'YYYY-MM-DD';
                    var dateValue = moment(value).format(datePattern);
                    allBindingsAccessor.pikaday.setDate(dateValue);
                } else {
                    allBindingsAccessor.pikaday.setDate(null);
                }
            }
        };

        ko.bindingHandlers.money = {
            update: function (element, valueAccessor, allBindingsAccessor, viewModel) {
                var value = ko.utils.unwrapObservable(valueAccessor());
                if (value != null) {
                    var formattedValue = "$" + util.formatMoney(value);
                    $(element).html(formattedValue);
                } else {
                    $(element).html('');
                }
            }
        };

        ko.bindingHandlers.spinner = {
            init: function (element, valueAccessor, allBindingsAccessor) {
                //initialize spinner with some optional options
                var options = allBindingsAccessor().spinnerOptions || {};
                $(element).spinner(options);

                //handle the field changing
                ko.utils.registerEventHandler(element, "spinchange", function () {
                    var val = $(element).spinner("value");
                    var valid = true;
                    if (options.min != null && val < options.min) {
                        valid = false;
                    }
                    if (options.max != null && val > options.max) {
                        valid = false;
                    }
                    if (valid) {
                        var observable = valueAccessor();
                        observable($(element).spinner("value"));
                    } else {

                        var lastValue = ko.utils.unwrapObservable(valueAccessor());
                        $(element).spinner("value", lastValue);
                    }
                });

                //handle disposal (if KO removes by the template binding)
                ko.utils.domNodeDisposal.addDisposeCallback(element, function () {
                    $(element).spinner("destroy");
                });

            },
            update: function (element, valueAccessor) {
                var value = ko.utils.unwrapObservable(valueAccessor());

                current = $(element).spinner("value");
                if (value !== current) {
                    $(element).spinner("value", value);
                }
            }
        };

    }
}

