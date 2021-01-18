/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    var commonApp = angular.module('commonApp');

    commonApp.directive("vfileUploadDrct", ["$timeout", "http", "vformSvcs", "$log", "$document", function ($timeout, http, vformSvcs, $log, $document) {
        return {
            require: 'ngModel',
            replace: true,
            scope: {
                config: '='
            },
            template: '<input type="file" ng-disabled="isdisabled" />',
            link: {
                pre: function (scope, element, attrs) {

                    scope.isdisabled = false;
                    if (scope.config) {
                        if (scope.config.isActive == undefined)
                            scope.config.isActive = true;
                        if (!scope.config.isActive) {
                            scope.isdisabled = true;
                        }
                    }
                    if (scope.config)
                        element.attr({"id": scope.config.name, "name": scope.config.name, type: scope.config.type});

                    if (!attrs['accept'])
                        element.attr('accept', 'image/png');
                },
                post: function (scope, elem, attrs, ngModel) {
                    //console.log(elem);
                    // check if valid input element
                    if (elem[0].nodeName.toLowerCase() !== 'input') {
                        $log.warn('vfileUploadDrct:', 'The directive will work only for input element, actual element is a', elem[0].nodeName.toLowerCase());
                        return;
                    }

                    // check if valid input type file
                    if (attrs.type != 'file') {
                        $log.warn('vfileUploadDrct:', 'Expected input type file, received instead:', attrs.type, 'on element:', elem);
                        return;
                    }

                    // the preview container
                    var container;

                    var fallbackImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAQAAAD9CzEMAAAA00lEQVR4Ae2XwQqDQAxEveinFD9e2MUfq6Cep7GnrPAg1JVCu5OTvEwe9FLtWlpqR6OyVn2aXbNGdX6KB4OLrmbRyIKsGsksWKsINhbUShM0wVcEk43CnAVY722mMEfBhPWD9mGOAlvBepSDwK1gPc5LASp8fbCJ81KACl9PNkOYo8CfKOtHUpijwJ841y1xToJy5VxXnLPgvUL1OAeBW4F6kKPAnYB6jKPAnYA68PZ/8EOCJtjvfvmdqwjSvR8gTz1YcCiytgs/TvLnvaDi/J2gCV63ZgZdEb12DwAAAABJRU5ErkJggg==";

                    // get custom class or set default
                    var previewClass = attrs.previewClass || 'media-preview';

                    // get custom class or set default
                    var containerClass = attrs.containerClass || 'media-container';

                    // as default if nothing is specified or
                    // the element specified is not a valid html
                    // element: create the default media container
                    // and append before input element
                    if (!attrs.previewContainer || ( !document.getElementById(attrs.previewContainer) && !angular.isElement(attrs.previewContainer) )) {

                        // create container
                        container = angular.element(document.createElement('div'));

                        // append before elem
                        elem.parent()[0].insertBefore(container[0], elem[0]);

                    } else {

                        // get the container
                        container = angular.isElement(attrs.previewContainer) ? attrs.previewContainer : angular.element(document.getElementById(attrs.previewContainer));
                    }

                    // add default class
                    container.addClass(containerClass);

                    // add element to the container
                    function addToContainer(element) {
                        element.addClass(previewClass);
                        return container.append(element);
                    }

                    // the change function
                    function onChange(e) {

                        if (!scope.config.isActive) {
                            return false;
                        }

                        // get files
                        var files = elem[0].files;

                        // update model value
                        attrs.multiple ? ngModel.$setViewValue(files) : ngModel.$setViewValue(files[0]);

                        if (!attrs.multiple) {
                            if (attrs['fsize']) {
                                if (!(files[0].size <= parseInt(attrs['fsize']))) {
                                    //console.log(ngModel);
                                    delete elem[0].files[0];
                                    ngModel.$setViewValue(undefined);

                                    if (scope.config.error && typeof(scope.config.error) == "function") {
                                        scope.config.error(elem, scope);
                                    }

                                    e.preventDefault();
                                    return false;
                                }
                                ;
                            }
                        }

                        // reset container
                        container.empty();

                        // check if there are files to read
                        if (files && files.length) {

                            scope.config.upload = function (successFn, errorFn) {
                                vformSvcs.uploadImage({
                                    metadata: files
                                }, function (result) {
                                    if (successFn && typeof(successFn) == "function")
                                        successFn(result);

                                }, function (err) {
                                    if (errorFn && typeof(errorFn) == "function")
                                        errorFn(err);

                                }, scope.config.httpPath);
                            };

                            // start the load process for each file
                            angular.forEach(files, function (data, index) {

                                //console.log(files[index]);

                                // init variables
                                var $reader = new FileReader(), result, $mediaElement;

                                // set fallback image on error
                                $reader.onloaderror = function (e) {
                                    result = fallbackImage;
                                }

                                // set resulting image
                                $reader.onload = function (e) {
                                    //console.log("onload", e);
                                    result = e.target.result;
                                }

                                $reader.onprogress = function (e) {
                                    //console.log("onprogress", e);
                                }
                                $reader.onabort = function (e) {
                                    //console.log("onabort", e);
                                }
                                $reader.onloadstart = function (e) {
                                    //console.log("onloadstart", e);
                                }

                                // when file reader has finished
                                // add the source to element and append it
                                $reader.onloadend = function (e) {
                                    //console.log("onloadend", e);

                                    var base64Matcher = new RegExp("(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{4})");
                                    //console.log(base64Matcher.exec(result + ""));

                                    // if audio
                                    if (result.indexOf('data:audio') > -1) {

                                        $mediaElement = angular.element(document.createElement('audio'));
                                        $mediaElement.attr('controls', 'true');

                                    } else if (result.indexOf('data:video') > -1) {

                                        $mediaElement = angular.element(document.createElement('video'));
                                        $mediaElement.attr('controls', 'true');

                                    } else if (result.indexOf('data:image') > -1) {

                                        $mediaElement = angular.element(document.createElement('img'));

                                    } else {

                                        $mediaElement = angular.element(document.createElement('img'));

                                    }

                                    // add the source
                                    $mediaElement.attr('style', "height:128px;width:128px;");
                                    $mediaElement.attr('src', result);
                                    files[index].baseData = result;
                                    files[index].className = scope.config.className;
                                    files[index].entityName = scope.config.entityName;
                                    if (scope.config.primaryKey)
                                        files[index].primaryKey = scope.config.primaryKey;
                                    if (scope.config.headerPk) {
                                        files[index].headerPk = scope.config.headerPk;
                                    }
                                    if (scope.config.success && typeof(scope.config.success) == "function") {
                                        scope.config.success(e, files[index], result, files, elem, scope);
                                    }
                                    // finally add to the container
                                    return addToContainer($mediaElement);
                                }

                                // read file
                                $reader.readAsDataURL(data);
                            })

                        }

                    }

                    // bind change event
                    elem.on('change', onChange);

                    // unbind event listener to prevent memory leaks
                    scope.$on('$destroy', function () {
                        elem.off('change', onChange);
                    });

                    if (scope.config.init && typeof(scope.config.init) == "function") {
                        scope.config.init(elem);
                    }

                }
            }
        };
    }]);

})(window, window.document, window.jQuery, window.angular);
