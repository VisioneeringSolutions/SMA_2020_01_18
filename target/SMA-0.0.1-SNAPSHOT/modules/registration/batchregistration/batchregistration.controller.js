/**
 * Created by Kundan Kumar on 06-May-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("batchRegistrationCtrl", ["$scope", "$rootScope", "registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "typeData", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, typeData) {
        $rootScope.pageHeader = "Batch";
        $rootScope.pageDescription = "Registration";

        var vm = this;

        var tabMapForFields = [
            ["name", "status","courseType"]

        ];

        vm.formModel = registrationModel.getInstance('batch');

        vm.batchList = typeData.BatchList;
        for(var key in vm.batchList) {
            console.log("cajbcjncjs");
            vm.firstName = vm.batchList[key].batch_name;
            vm.column = 'batch_name';
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
        vm.courseList = typeData.CourseList;

        if(vm.formModel.status == '') {
            vm.formModel.status = 'Active';
        }

        /* for calendar */
        /*----------------------start--------------------*/
        var dateMaps = new (function () {
            var maps = {};
            var key = 0;
            this.getMap = function (format, popup, open, dateOptions) {
                var uKey = ++key;
                maps[uKey] = new (function (uKey) {
                    this.uKey = uKey;
                    this.format = format || 'dd-MM-yy';
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


        vm.updateModal = function(instance){
            //console.log("instance:",instance);
            var reqMap = {
                eoBatchPK : instance.primary_key +''
            };
            registrationSvcs.getBatchByPk(reqMap, function(res){
                if(res.data != null){
                    vm.formModel = res.data;
                    console.log("vm.formModel::",vm.formModel);
                    var startDate = moment(vm.formModel.startDate,"DD-MM-YYYY").format("YYYY-MM-DD");
                    vm.startDate = new Date(startDate);
                }
            });
        };

        vm.deleteModal = function(d){
            //console.log("d:",d);
            var locale = {
                OK: 'I Suppose',
                CONFIRM: 'Yes',
                CANCEL: 'No'
            };

            bootbox.addLocale('custom', locale);

            bootbox.prompt({
                title: "Do You want to delete <span style='color: #283593; font-weight: 700'>"+ d.batch_name + "</span> ?",
                locale: 'custom',
                callback: function (result) {
                    //console.log('This was logged in the callback: ' + result);

                    if(result != null){
                        var reqMap = {
                            batchPk : d.primary_key+'',
                            reason : result,
                            className : 'EOBatch'

                        };
                        registrationSvcs.deleteBatchByPk( reqMap, function (res){

                            if(res.data == "Success"){
                                notifySvcs.success({
                                    content : "Batch Successfully Removed"
                                });
                            }
                            if(res.data == "failure"){
                                notifySvcs.info({
                                    content : " Batch is already in use"
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
        };

        vm.addCourse = function(){
            vm.formModel = registrationModel.getInstance('batch');
            if (vm.formModel.status == '') {
                vm.formModel.status = 'Active';
            }
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
                ////console.log("preCompile ",e);
            },
            postCompile: function (e) {
                ////console.log("postCompile ",e);
                vm.formConfig.formScope = e.scope;
                vm.formConfig.formElement = e.element;
            },
            submit: function (e) {

                e.preventDefault();
                var validationResponse = vm.formConfig.validateFormInputs();

                var form = vm.formConfig.formScope.courseregistration;

                //console.log("vm.formModel:", vm.formModel);
                if (validationResponse.invalidInputs.length == 0) {
                    vm.formModel.className = 'EOBatch';
                    vm.formModel.startDate = moment(vm.startDate).format("DD-MM-YYYY");
                    var blankFormInstance = angular.copy(vm.formModel);
                    //console.log("blankFormInstance:", blankFormInstance);

                    registrationSvcs.createCourse(blankFormInstance, function (res) {
                        if (res.data == 'Success') {
                            $state.reload();
                            notifySvcs.success({
                                title: "Success"
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
            }
        };

    }]);

})(window, window.document, window.jQuery, window.angular);