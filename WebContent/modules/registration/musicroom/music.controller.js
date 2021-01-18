/**
 * Created by Kundan Kumar on 06-May-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("musicRoomCtrl", ["$scope", "$rootScope", "registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "typeData", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, typeData) {
        $rootScope.pageHeader = "MusicRoom";
        $rootScope.pageDescription = "Registration";

        var vm = this;

        //console.log("working in music room  registration");

        vm.formModel = registrationModel.getInstance('musicRoom');
        //console.log("vm.formModel:", vm.formModel);

        vm.musicRoomList = typeData.MusicRoomList;
        for(var key in vm.musicRoomList) {
            console.log("cajbcjncjs");
            vm.firstName = vm.musicRoomList[key].room_name;
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
        //console.log("vm.musicRoomList:",vm.musicRoomList);

        vm.updateModal = function(instance){
            //console.log("instance:",instance);
            var reqMap = {
                eoMusicRoomPK : instance.primary_key +''
            };
            registrationSvcs.getMusicRoomByPk(reqMap, function(res){
                if(res.data != null){
                    //console.log("response:",res.data);
                    vm.formModel = res.data;
                    //console.log("vm.formModel------:",vm.formModel);
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
                title: "Do You want to delete <span style='color: #283593; font-weight: 700'>"+ d.room_name + "</span> ?",
                locale: 'custom',
                callback: function (result) {
                    //console.log('This was logged in the callback: ' + result);

                    if(result != null){
                        var reqMap = {
                            eoMusicRoomPK : d.primary_key+'',
                            reason : result
                        };
                        registrationSvcs.deleteMusicRoomByPk( reqMap, function (res){

                            if(res.data = "Success"){
                                notifySvcs.success({
                                    content : "Music Room Successfully Removed"
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
            vm.formModel = registrationModel.getInstance('musicRoom');
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
                        vm.formModel.maxStudent = parseInt(vm.formModel.maxStudent);
                        vm.formModel.className = 'EOMusicRoom';
                        var blankFormInstance = angular.copy(vm.formModel);
                        //console.log("blankFormInstance:", blankFormInstance);

                        registrationSvcs.createCourse(blankFormInstance, function (res) {
                            if (res.data == 'Success') {
                                $state.reload();
                                notifySvcs.success({
                                    title: "Music Room",
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