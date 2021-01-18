/**
 * Created by Kundan Kumar on 06-May-19.
 */
(function (window, document, $, angular) {

    var registrationApp = angular.module('registrationApp');

    registrationApp.controller("studentBatchRegistrationCtrl", ["$scope", "$rootScope", "registrationSvcs", "registrationModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "typeData", function ($scope, $rootScope, registrationSvcs, registrationModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, typeData) {
        $rootScope.pageHeader = "Student Batch";
        $rootScope.pageDescription = "Registration";

        var vm = this;

        var tabMapForFields = [
            ["name", "status","courseType"]

        ];

        vm.formModel = registrationModel.getInstance('studentBatch');

        vm.batchList = typeData.BatchList;
        vm.studentBatchList = typeData.StudentBatchList;
        for(var key in vm.studentBatchList) {
            console.log("cajbcjncjs");
            vm.firstName = vm.studentBatchList[key].batchName;
            vm.column = 'batchName';
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
        vm.studentList = typeData.StudentList;
        vm.musicType = typeData.MusicType;

        vm.clearSectionFrom = function () {
            vm.formModel.selectedStudentArray = {};
        }();

        vm.clearForm = function(){
            vm.formModel.selectedStudentArray = null;
        };
        vm.setBatchType = function(instance){
            vm.formModel.selectedStudentArray = '';
            for(var k in vm.batchList){
                if(vm.batchList[k].primary_key == instance){
                    vm.formModel.batchType = vm.batchList[k].batch_type;
                    break;
                }
            }
        };


        vm.updateModal = function(instance){

            vm.formModel.eoBatch = instance.eoBatch + "";
            vm.formModel.batchType = instance.batchType + "";
            vm.formModel.eoStudentBatchMapping = instance.eoStudentBatchMapping;

            if(instance.batchType == 'Individual'){
                vm.formModel.selectedStudentArray = instance.eoBatchList[0].primaryKey+'';
            }
            else{
                var tempMap =[];
                for(var i in instance.eoBatchList){
                    tempMap.push(instance.eoBatchList[i].primaryKey+'');
                }
                vm.formModel.selectedStudentArray = tempMap;
            }
            vm.formModel.primaryKey = instance.primaryKey+'';
        };

        vm.deleteModal = function(d){
            console.log("d:::::",d);
            var locale = {
                OK: 'I Suppose',
                CONFIRM: 'Yes',
                CANCEL: 'No'
            };

            bootbox.addLocale('custom', locale);

            bootbox.prompt({
                title: "Do You want to delete <span style='color: #283593; font-weight: 700'>"+ d.batchName + "</span> ?",
                locale: 'custom',
                callback: function (result) {

                    if(result != null){
                        var reqMap = {
                            batchPk : d.primaryKey+'',
                            reason : result,
                            className : 'EOStudentBatch'
                        };
                        registrationSvcs.deleteBatchByPk( reqMap, function (res){
                            if(res.data = "Success"){
                                notifySvcs.success({
                                    content : "Batch Successfully Removed"
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
            vm.formModel = registrationModel.getInstance('studentBatch');
        };

        vm.studentConfig = {
            create: true,
            valueField: 'primaryKey',
            labelField: 'studentfullName',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Student:',
            searchField: ['studentName'],
            render: {
                item: function (item, escape) {
                    return '<div>' +
                        (item.studentfullName ? '<span class="subjectTitle">' + escape(item.studentfullName) + '</span>' : '') +
                        '</div>';
                },
                option: function (item, escape) {
                    return '<div>' +
                        '<span class="subjectTitle">' + escape(item.studentfullName) + '</span>' + '</div>';
                }
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
            },
            postCompile: function (e) {
                vm.formConfig.formScope = e.scope;
                vm.formConfig.formElement = e.element;
            },
            submit: function (e) {

                e.preventDefault();
                var validationResponse = vm.formConfig.validateFormInputs();
                var form = vm.formConfig.formScope.courseregistration;

                if(vm.formModel.eoStudentBatchMapping.length > 0){
                    angular.forEach(vm.formModel.eoStudentBatchMapping,function(value,key){
                        value.isActive = false;
                    });
                }

                if(typeof(vm.formModel.selectedStudentArray) == 'object') {

                    angular.forEach(vm.formModel.selectedStudentArray, function (value, key) {
                        var flag = true;

                        for (var key2 in vm.formModel.eoStudentBatchMapping) {
                            if (value == vm.formModel.eoStudentBatchMapping[key2].studentPk) {
                                vm.formModel.eoStudentBatchMapping[key2].isActive = true;
                                flag = false;
                                break;
                            }
                        }

                        if (flag) {
                            var map = {
                                "className": "EOStudentBatchMapping",
                                "studentPk": value
                            };
                            vm.formModel.eoStudentBatchMapping.push(map);
                        }
                    });
                }
                else {
                    if(vm.formModel.eoStudentBatchMapping.length > 0){
                        var flag = false;

                        for(var k in vm.formModel.eoStudentBatchMapping){
                            if(vm.formModel.eoStudentBatchMapping[k].studentPk == vm.formModel.selectedStudentArray){
                                flag = true;
                                vm.formModel.eoStudentBatchMapping[k].isActive = true;
                                break;
                            }
                        }
                        if(!flag){
                            var map = {
                                "className": "EOStudentBatchMapping",
                                "studentPk": vm.formModel.selectedStudentArray
                            };
                            vm.formModel.eoStudentBatchMapping.push(map);
                        }
                    }
                    else{
                        var map = {
                            "className": "EOStudentBatchMapping",
                            "studentPk": vm.formModel.selectedStudentArray
                        };
                        vm.formModel.eoStudentBatchMapping.push(map);
                    }
                }

                if( typeof (vm.formModel.selectedStudentArray) == 'object'){
                    vm.formModel.studentsPk = JSON.stringify(vm.formModel.selectedStudentArray);
                }
                else{
                    var students = [];
                    students.push(vm.formModel.selectedStudentArray);
                    vm.formModel.studentsPk = JSON.stringify(students);
                }

                if (validationResponse.invalidInputs.length == 0) {
                    vm.formModel.className = 'EOStudentBatch';
                    var blankFormInstance = angular.copy(vm.formModel);
                    delete blankFormInstance.selectedStudentArray;

                    registrationSvcs.createBatchesForStudent(blankFormInstance, function (res) {
                        if (res.data == 'Success') {
                            $state.reload();
                            notifySvcs.success({
                                title: "Student Batch Room",
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