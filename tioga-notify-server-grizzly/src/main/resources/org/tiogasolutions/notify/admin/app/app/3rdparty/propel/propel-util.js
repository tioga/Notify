
/*
 * Copyright 2014 Harlan Noonkester
 *
 * All rights reserved
 */

function Util() {
    var self = this;

    self.cloneUsingJson = function(object)
    {
        return JSON.parse(JSON.stringify(object));
    }

    self.consoleLog = function (msg) {
        if (console) {
            console.log(msg);
        } else {
            alert("Yo dog, no console! " + msg);
        }
    };

    self.greatestCommonDivisor = function(a, b) {
        if (b == 0) {
            return a
        }
        return self.greatestCommonDivisor(b, a % b)
    };

    self.imageRatio = function(width, height) {
        var min = Math.min(width, height);
        var max = Math.max(width, height);
        return (min / max).toPrecision(3);
    };

    self.findMatches = function(a, isMatch) {
        var val;
        var results = [];
        for(var i=0; i<a.length; i++) {
            val = a[i];
            if (isMatch(val)) {
                results.push(val);
            }
        }
        return results;
    };

    // TODO - ideally would support other formats and 7 digit numbers
    self.formatPhoneNumber = function (phoneNumber)
    {
        if (phoneNumber == null || phoneNumber.length != 10) {
            return phoneNumber;
        }
        // TODO - need to eval what this line is doing.
        phoneNumber = phoneNumber.replace(/[^0-9]/g, '');
        phoneNumber = phoneNumber.replace(/(\d{3})(\d{3})(\d{4})/, "($1) $2-$3");
        return phoneNumber;
    }

    // Return true if searchStr contains forStr, case insensitive.
    self.strContains = function(searchStr, forStr) {
        if (searchStr == null || forStr == null) {
            return false;
        } else {
            return searchStr.toLowerCase().indexOf(forStr.toLowerCase()) >= 0;
        }
    }
    
    self.firstCharToUpper = function (value) {
        if (self.isString(value)) {
            return value.charAt(0).toUpperCase() + value.slice(1);
        } else {
            return value;
        }
    }

    self.firstCharToLower = function (value) {
        if (self.isString(value)) {
            return value.charAt(0).toLowerCase() + value.slice(1);
        } else {
            return value;
        }
    }

    self.startsWith = function(value, prefix) {
        var str;
        if (!value || !prefix) {
            return false;
        }
        str = value.toString();
        return str.toString().indexOf(prefix) !== -1;
    }

    self.endsWith = function (value, suffix) {
        var str;
        if (!value || !suffix) {
            return false;
        }
        str = value.toString();
        return str.toString().indexOf(suffix, str.length - suffix.length) !== -1;
    }

    self.removeTrailing = function(value, remove) {
        if (!value || !remove) {
            return value;
        }
        var hasTrailing = value.indexOf(remove, value.length - remove.length) !== -1;
        if (hasTrailing) {
            value = value.substring(0, value.length - remove.length);
        }
        return value;
    };

    // REVIEW -
    self.formatMoney = function(num, decimalPlaces){
        var prefix = num < 0 ? "-" : "";
        var decimalPlaces = isNaN(decimalPlaces = Math.abs(decimalPlaces)) ? 2 : decimalPlaces;
        var i = parseInt(num = Math.abs(+num || 0).toFixed(decimalPlaces)) + "";
        var j = (j = i.length) > 3 ? j % 3 : 0;
        return prefix + (j ? i.substr(0, j) + "," : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1,") + (decimalPlaces ? "." + Math.abs(num - i).toFixed(decimalPlaces).slice(2) : "");
        //return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };

    self.equalsIgnoreCase = function (one, two) {
        // Alternative implementation can be done using regular expression which would support unicode.
        if (one == null) {
            return two == null;
        } else if (two == null) {
            return false;
        } else {
            return one.toUpperCase() === two.toUpperCase();
        }
    }

    self.isUndefined = function (obj) {
        return typeof obj === "undefined";
    };

    self.isEmpty = function(str) {
        return typeof str === "undefined" || str == null || str.length === 0;
    }

    self.isNotEmpty = function (str) {
        return typeof str != "undefined" && str != null && str.length > 0;
    }

    self.isUserDefinedObject = function (obj) {
        return Object.prototype.toString.call(obj) == "[object Object]";
    };

    self.isFunction = function (obj) {
        return Object.prototype.toString.call(obj) == "[object Function]";
    };

    self.isString = function(obj) {
        return Object.prototype.toString.call(obj) == "[object String]";
    };

    self.isArray = function(obj) {
        return Object.prototype.toString.call(obj) == "[object Array]";
    };

    self.isDate = function(obj) {
        return Object.prototype.toString.call(obj) == "[object Date]";
    };

    self.toJson = function (obj) {
        return JSON.stringify(obj);
    }

    self.format = function (format) {
        var args = Array.prototype.slice.call(arguments, 1);
        return format.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] != 'undefined'
              ? args[number]
              : match
            ;
        });
    };

    self.printElement = function (elementId) {
        var elem = document.getElementById(elementId);
        var domClone = elem.cloneNode(true);

        var printSection = document.getElementById("only-print-this-section");

        if (!printSection) {
            printSection = document.createElement("div");
            printSection.id = "only-print-this-section";
            document.body.appendChild(printSection);
        }

        printSection.innerHTML = "";
        printSection.appendChild(domClone);
        window.print();
    };

    // From https://developer.mozilla.org/en-US/docs/Web/API/URLUtils/search
    self.queryParams = function(queryString) {
        var params = {};

        function buildValue(sValue) {
            if (/^\s*$/.test(sValue)) { return null; }
            if (/^(true|false)$/i.test(sValue)) { return sValue.toLowerCase() === "true"; }
            if (isFinite(sValue)) { return parseFloat(sValue); }
            if (isFinite(Date.parse(sValue))) { return new Date(sValue); } // this conditional is unreliable in non-SpiderMonkey browsers
            return sValue;
        }

        if (queryString.length > 1) {
            for (var aItKey, nKeyId = 0, aCouples = queryString.substr(1).split("&"); nKeyId < aCouples.length; nKeyId++) {
                aItKey = aCouples[nKeyId].split("=");
                params[unescape(aItKey[0])] = aItKey.length > 1 ? buildValue(unescape(aItKey[1])) : null;
            }
        }
        return params;
    };

    self.traitStringToJS = function(traitStr) {
        var traitArray = traitStr.split(",");
        var js = {};
        for(var i=0;i<traitArray.length;i++) {
            var trait = traitArray[i].split(":");
            if (trait.length == 2) {
                js[trait[0]] = trait[1];
            } else {
                js[trait[0]] = null;
            }
        }
        return js;
    };
    
    self.hostUrl = function () {
        var hostUrl = window.location.protocol + "//" + window.location.host;
        return hostUrl;
    };

    self.appUrl = function () {
        var appUrl = window.location.protocol + "//" + window.location.host;
        var paths = window.location.pathname.split('/');
        if (paths.length > 1) {
            appUrl += "/" + paths[1];
        }
        return appUrl;
    };

    // Build a URL from the given array of path and query parameter object. The paths array must contain one
    // path at a minimum, the queryParameter argument is optional.
    self.buildUrl = function (paths, queryParamStruct) {
        if (!paths || paths.length == 0) {
            throw new Error("No path given, cannot build URL.");
        }

        // Use first path element as our base.
        var url = paths[0];
        if (self.endsWith(url, "/")) {
            // Remove any trailing /
            url = url.substring(0, url.length - 1);
        }

        // Add any additional path elements (starting at index 1).
        for (var i = 1; i < paths.length; i++) {
            var path = paths[i];
            if (!path || path.length == 0) {
                // Error if path argument is null or empty.
                throw Error("Cannot build URL, path argument is empty, url so far: " + url);
            }
            url += "/" + path;
        }

        if (queryParamStruct) {
            url += "?";
            for (var pName in queryParamStruct) {
                var pValue = queryParamStruct[pName];
                url += pName;
                url += "=";
                if (pValue != null) {
                    url += pValue;
                }
                url += "&";
            }
            // Need to remove last char from queryParams since we are adding ahead
            url = url.substring(0, url.length - 1);
        }

        return url;
    }

    // Builds a URL using a baseUrl and set of args (a frequent use case for other classes, i.e. Ajax,
    // but not often used directly in typical client). The args are examined, any string is treated as
    // a path and any object is treated as query parameter structure.
    self.buildUrlFromArgs = function (baseUrl, args) {
        var paths = [baseUrl];
        var queryParamStruct = null;

        // Assign path elements and queryParamStruct as they are found in the argument.s
        for (var i = 0; i < args.length; i++) {
            var arg = args[i];
            if (self.isUserDefinedObject(arg)) {
                if (queryParamStruct != null) {
                    throw new Error("Multiple objects in url args, only one can be provided which will be used as the query param struct");
                } else {
                    queryParamStruct = arg;
                }
            } else {
                paths.push(arg);
            }
        }

        return self.buildUrl(paths, queryParamStruct);
    }
}

function UrlBuilder(util) {
    var self = this;
    var paths = [];
    var queryParamStruct = null;

    // Add path elements, each argument added to the path.
    self.path = function() {
        for(var i=0; i<arguments.length; i++) {
            var p = arguments[i];
            if (!p || p.length == 0) {
                // Error if path argument is null or empty.
                throw Error("Path argument is empty, path so far: " + path);
            }
            paths.push(p);
        }
        return self;
    };

    // Add a query parameter
    self.param = function(pName, pVal) {
        if (!pName || pName.length == 0) {
            throw Error("Param name is undefined or empty.");
        } else {
            queryParamStruct[pName] = pVal;
        }
        return self;
    };

    self.build = function (args) {
        for (var i = 0; i < args.length; i++) {
            var arg = args[i];
            if (util.isUserDefinedObject(arg)) {
                if (queryParamStruct != null) {
                    // TODO - just need to merge the objects for this to work.
                    throw new Error("Query parameters specified by param method in build, currently only support specify query parameters in one of those ways.");
                } else {
                    queryParamStruct = arg;
                }
            } else {
                // We will check for null or empty later in buildUrl.
                paths.push(arg);
            }
        }

        // Must have atleast one path.
        if (paths.length == 0) {
            throw new Error("Cannot build URL, no path given");
        }

        // Check for a single absolute path
        return util.buildUrl(paths, queryParamStruct);
    }

}

