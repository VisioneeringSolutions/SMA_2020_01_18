/**
 * Created by nikita on 2019-05-25.
 */
(function (window, document, $, angular) {

    var newsApp = angular.module('newsApp');

    newsApp.controller("newsCtrl", ["$scope", "$rootScope", "newsSvcs", "newsModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "newsData", function ($scope, $rootScope, newsSvcs, newsModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, newsData) {
        console.log("newsApp");

        var vm = this;
        var tabMapForFields = [
            ["newsDate", "newsDesc"]
        ];
        vm.getDateFormat = function(Date) {
            var newdate = Date.split("-").reverse().join("-");

            return newdate;
        };

        vm.formModel = newsModel.getInstance('news');
        console.log("vm.formModel:::::::", vm.formModel);


        vm.newsList = newsData.NewsList;
        for (var key in vm.newsList) {
            vm.firstName = vm.newsList[key].news_desc;
            vm.column = 'news_desc';
            vm.reverse = false;
            vm.sortColumn = function (col) {
                vm.column = col;
                if (vm.reverse) {
                    vm.reverse = false;
                    vm.reverseclass = 'arrow-up';
                } else {
                    vm.reverse = true;
                    vm.reverseclass = 'arrow-down';
                }
            };
            vm.sortClass = function (col) {
                if (vm.column == col) {
                    if (vm.reverse) {
                        return 'arrow-down';
                    } else {
                        return 'arrow-up';
                    }
                } else {
                    return '';
                }
            };
        }
        vm.viewBy = 10;
        vm.paginationTotalItems = vm.newsList.length;
        console.log("vm.paginationTotalItems:::::", vm.paginationTotalItems);
        vm.paginationCurrentPage = 1;

        vm.paginationMaxSize = vm.viewBy;
        vm.setItemsPerPage = function (num) {
            console.log("num::::", num)
            vm.paginationMaxSize = num;
            vm.paginationCurrentPage = 1;
        };

        /*  vm.setItemsPerPageOnSearch = function () {
         if (vm.customFilter.$.length > 0) {
         var searchPageSize = (vm.consentsData.length) / vm.viewBy;

         vm.viewBy = angular.copy(vm.viewBy);
         vm.paginationMaxSize = vm.viewBy * searchPageSize;
         vm.paginationCurrentPage = 1;
         }
         else {
         vm.viewBy = 10 + "";
         vm.paginationTotalItems = vm.consentsData.length;
         vm.paginationCurrentPage = 1;


         vm.paginationMaxSize = vm.viewBy;
         vm.setItemsPerPage = function (num) {
         vm.paginationMaxSize = num;
         vm.paginationCurrentPage = 1;
         };
         }
         };*/


        vm.setItemsPerPageOnSearch = function () {
            if (vm.customFilter.$.length > 0) {
                var searchPageSize = (vm.newsList.length) / vm.viewBy;

                vm.viewBy = angular.copy(vm.viewBy);
                vm.paginationMaxSize = vm.viewBy * searchPageSize;
                vm.paginationCurrentPage = 1;
            }
            else {
                vm.viewBy = 10 + "";
                vm.paginationTotalItems = vm.newsList.length;
                vm.paginationCurrentPage = 1;


                vm.paginationMaxSize = vm.viewBy;
                vm.setItemsPerPage = function (num) {
                    vm.paginationMaxSize = num;
                    vm.paginationCurrentPage = 1;
                };
            }
        };

        console.log("vm.newsList:::::::", vm.newsList);


        /* for calendar */
        /*----------------------start--------------------*/
        var dateMaps = new (function () {
            var maps = {};
            var key = 0;
            this.getMap = function (format, popup, open, dateOptions) {
                var uKey = ++key;
                maps[uKey] = new (function (uKey) {
                    this.uKey = uKey;
                    this.format = format || 'yyyy-MM-dd';
                    this.popup = popup || {
                            opened: false
                        };
                    this.open = open || function () {
                            this.popup.opened = true;
                        };
                    this.dateOptions = dateOptions || {
                            formatYear: 'yy'
                        }
                })(uKey);
                return maps[uKey];
            };
            this.getMaps = function () {
                return maps;
            };
        })();

        vm.createDateMap = function () {
            return dateMaps.getMap();
        };
        /* --------------end-------------------- */


        vm.updateModal = function (instance) {
            console.log("instance:", instance);
            var reqMap = {
                eoNewsPK: instance.primary_key + '',
                className: "EONews"
            };
            console.log("reqMap before:", reqMap);
            newsSvcs.getNewsByPk(reqMap, function (res) {
                console.log("res after:", res);
                if (res.data != null) {
                    console.log("response:", res.data);
                    vm.formModel = res.data;
                    console.log("vm.formModel:", vm.formModel);
                    var newsDate = moment(vm.formModel.newsDate, "DD-MM-YYYY").format("YYYY-MM-DD");
                    vm.newsDate = new Date(newsDate);
                }
            });
        };

        vm.deleteModal = function (d) {
            console.log("d:", d);
            var locale = {
                OK: 'I Suppose',
                CONFIRM: 'Yes',
                CANCEL: 'No'
            };
            bootbox.prompt({
                closeButton: false,
                title: "Do You want to delete <span style='color: #283593; font-weight: 700'>" + d.news_desc + "</span> ?",
                callback: function (result) {
                    console.log('This was logged in the callback: ' + result);

                    if (result != null) {
                        var reqMap = {
                            newsPk: d.primary_key + '',
                            reason: result
                        };
                        newsSvcs.deleteNewsByPk(reqMap, function (res) {
                            $state.reload();
                            if (res.data = "Success") {
                                notifySvcs.success({
                                    content: "News Successfully Removed"
                                });
                            }
                            $state.reload();

                        }, function (err) {
                            notifySvcs.error({
                                content: "Something went wrong"
                            })
                        })
                    }
                }
            });
        };

        vm.addNews = function () {
            vm.formModel = newsModel.getInstance('news');
        };


        vm.tabConfig = {
            defaultActive: 0,
            mapid: "tabContainer",
            click: function (clickedIndex) {
                if (parseInt(clickedIndex) > parseInt(vm.tabConfig.defaultActive)) {
                    vm.validateTabSectionFieldsFn(vm.tabConfig.defaultActive, function (status) {
                        if (status) {
                            vm.tabConfig.setActive(clickedIndex, clickedIndex);
                        }
                    });
                } else {
                    vm.tabConfig.setActive(clickedIndex, clickedIndex);
                }
            }
        };
        vm.validateTabSectionFieldsFn = function (tabIndex, callback) {
            var formInputs = vm.formConfig.vinputs;
            var formSelects = vm.formConfig.vselect;
            var invaliFields = [];

            for (var fieldIndex in tabMapForFields[tabIndex]) {
                if (fieldIndex != "length") {
                    var fieldName = tabMapForFields[tabIndex][fieldIndex];
                    if (formInputs[fieldName]) {
                        if (!formInputs[fieldName].valid()) {
                            invaliFields.push(fieldName);
                        }
                    }
                    if (formSelects[fieldName]) {
                        if (!formSelects[fieldName].valid()) {
                            invaliFields.push(fieldName);
                        }
                    }
                }
            }

            if (invaliFields.length != 0) {
                callback(false);
                notifySvcs.error({
                    content: "Please Fill The Form Correctly."
                });
            } else {
                callback(true);
            }
        };


        vm.formSubmit = function () {
            vm.formConfig.formElement.trigger('submit');
        };

        vm.formConfig = {
            preCompile: function (e) {

            },
            postCompile: function (e) {

                vm.formConfig.formScope = e.scope;
                vm.formConfig.formElement = e.element;
            },
            submit: function (e) {

                e.preventDefault();
                var validationResponse = vm.formConfig.validateFormInputs();

                var form = vm.formConfig.formScope.newsRegistrationForm;

                console.log("vm.formModel:", vm.formModel);
                if (validationResponse.invalidInputs.length == 0) {
                    if (vm.newsDate != undefined) {
                        vm.formModel.newsDate = moment(vm.newsDate).format("DD-MM-YYYY");
                        vm.formModel.className = "EONews";
                        if (vm.formModel.newsDesc != "") {
                            var blankFormInstance = angular.copy(vm.formModel);
                            console.log("blankFormInstance::::", blankFormInstance);

                            newsSvcs.createNewsForAdmin(blankFormInstance, function (res) {
                                console.log("res::::", res);
                                if (res.data == 'Success') {
                                    $state.reload();
                                    notifySvcs.success({
                                        title: "News",
                                        content: "Success"
                                    });
                                }
                            }, function (err) {
                                notifySvcs.error({
                                    content: "Something went wrong"
                                })
                            });
                        }
                        else {
                            notifySvcs.info({
                                title: "News",
                                content: "Please Enter News."
                            });
                        }
                    }
                    else {
                        notifySvcs.info({
                            title: "Date",
                            content: "Please select Date."
                        });
                    }
                }
                else {
                    notifySvcs.error({
                        content: "Please Fill The Form Correctly."
                    });
                }
            }
        };


        /*    vm.submit= function () {
         // e.preventDefault();
         //var form = vm.formConfig.formScope.newsRegistrationForm;
         var validationResponse = vm.formConfig.validateFormInputs();

         var form = vm.formConfig.formScope.newsRegistrationForm;
         if (validationResponse.invalidInputs.length == 0) {
         vm.formModel.newsDate = moment(vm.newsDate).format("DD-MM-YYYY");
         vm.formModel.className = "EONews";
         var blankFormInstance = angular.copy(vm.formModel);
         console.log("blankFormInstance::::",blankFormInstance);

         newsSvcs.createNewsForAdmin(blankFormInstance, function (res) {
         console.log("res::::",res);
         if (res.data == 'Success') {
         $state.reload();
         notifySvcs.success({
         title: "News",
         content: "Success"
         });
         }
         },function (err) {
         notifySvcs.error({
         content: "Something went wrong"
         })
         });
         }
         else {
         notifySvcs.error({
         content: "Please Fill The Form Correctly."
         });
         }

         };*/


        /*
         vm.deleteModal = function(d){
         console.log("d:",d);
         var locale = {
         OK: 'I Suppose',
         CONFIRM: 'Yes',
         CANCEL: 'No'
         };

         bootbox.addLocale('custom', locale);

         bootbox.prompt({
         title: "Do You want to delete <span style='color: #283593; font-weight: 700'>"+ d.music_type + "</span> ?",
         locale: 'custom',
         callback: function (result) {
         console.log('This was logged in the callback: ' + result);

         if(result != null){
         var reqMap = {
         newsPk : d.primary_key+'',
         reason : result
         };
         newsSvcs.deleteNewsByPk( reqMap, function (res){

         if(res.data = "Success"){
         notifySvcs.success({
         content : "Course Successfully Removed"
         });
         }
         $state.reload();

         },function(err){
         notifySvcs.error({
         content:"something went wrong"
         })
         })
         }
         }
         });
         };*/

    }]);

})(window, window.document, window.jQuery, window.angular);

