/**
 * Created by vishuja
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("musicCategoryCtrl", ["$scope", "$rootScope", "registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "typeData", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, typeData) {
        $rootScope.pageHeader = "musicCategory";
        $rootScope.pageDescription = "Registration";
        var vm = this;

        vm.formModel = registrationModel.getInstance('musicCategory');

        vm.musicCategoryList = typeData.MusicCategoryList;
        for(var key in vm.musicCategoryList) {
            vm.column = 'course_name';
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
        vm.updateModal = function(instance){
            //console.log("instance:",instance);
            var reqMap = {
                eoMusicCategoryPK : instance.primaryKey +''
            };
            registrationSvcs.getMusicCategoryByPk(reqMap, function(res){
                if(res.data != null){
                    vm.formModel = res.data;
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
                title: "Do You want to delete <span style='color: #283593; font-weight: 700'>"+ d.description + "</span> ?",
                locale: 'custom',
                callback: function (result) {

                    if(result != null){
                        var reqMap = {
                            eoMusicCategoryPK : d.primaryKey+'',
                            reason : result
                        };
                        registrationSvcs.deleteMusicCategoryByPk( reqMap, function (res){
                            if(res.data == "Success"){
                                notifySvcs.success({
                                    content : "Music Type Successfully Removed"
                                });
                            }
                            if(res.data == "failure"){
                                notifySvcs.info({
                                    content : " Music Category is already in use"
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

        vm.addMusicCategory = function(){
            vm.formModel = {};
            vm.formModel = registrationModel.getInstance('musicCategory');
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
                if (validationResponse.invalidInputs.length == 0) {
                    var blankFormInstance = angular.copy(vm.formModel);

                    registrationSvcs.createMusicCategory(blankFormInstance, function (res) {
                        if (res.data == 'Success') {
                            $state.reload();
                            notifySvcs.success({
                                title: "Music Category",
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
            }
        };

    }]);

})(window, window.document, window.jQuery, window.angular);