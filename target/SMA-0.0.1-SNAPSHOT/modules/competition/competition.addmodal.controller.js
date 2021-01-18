/**
 * Created by nikita on 2019-06-24.
 */
(function (window, document, $, angular) {

    var competitionApp = angular.module('competitionApp');

    competitionApp.controller("competitionAddModalCtrl", ["$scope", "$rootScope","competitionSvcs", "competitionModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata", function ($scope, $rootScope, competitionSvcs, competitionModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata) {
        $rootScope.pageHeader = "Competition";
        $rootScope.pageDescription = "";

        var mvm = this;
        var tabMapForFields = [
            ["name", "competitionDate","details"]
        ];
        console.log("entered in add modal");

        mvm.musicList = metadata.lkInstances.MusicType;
        mvm.studentList = metadata.lkInstances.StudentList;  /*for student dropdown -------------*/
        mvm.teacherList = metadata.lkInstances.TeacherList;  /*for teacher dropdown -------------*/
        console.log("musicList:",mvm.musicList);
        console.log("StudentList:",mvm.studentList);
        console.log("teacherList:",mvm.teacherList);

        mvm.formModel = competitionModel.getInstance("competition");
        console.log("formModel::",mvm.formModel);


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
                            formatYear: 'yyyy'
                        }
                })(uKey);
                return maps[uKey];
            };
            this.getMaps = function () {
                return maps;
            };
        })();

        mvm.createDateMap = function () {
            return dateMaps.getMap();
        };

        /* --------------end-------------------- */



        mvm.clearSectionFrom = function () {
            mvm.formModel.musicCategoryArray = {};
        }();
        mvm.musicCategoryConfig = {
            create: true,
            valueField: 'primaryKey',
            labelField: 'musicType',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Music Category:',
            searchField: ['musicCategory'],
            render: {
                item: function (item, escape) {
                    return '<div>' +
                        (item.musicType ? '<span class="subjectTitle">' + escape(item.musicType) + '</span>' : '') +
                        '</div>';
                },
                option: function (item, escape) {
                    return '<div>' +
                        '<span class="subjectTitle">' + escape(item.musicType) + '</span>' + '</div>';
                }
            }
        };

        mvm.tabConfig = {
            defaultActive: 0,
            mapid: "tabContainer",
            click: function (clickedIndex) {
                if (parseInt(clickedIndex) > parseInt(mvm.tabConfig.defaultActive)) {
                    mvm.validateTabSectionFieldsFn(mvm.tabConfig.defaultActive, function (status) {
                        if (status) {
                            mvm.tabConfig.setActive(clickedIndex, clickedIndex);
                        }
                    });
                } else {
                    mvm.tabConfig.setActive(clickedIndex, clickedIndex);
                }
            }
        };
        mvm.validateTabSectionFieldsFn = function (tabIndex, callback) {
            var formInputs = mvm.formConfig.vinputs;
            var formSelects = mvm.formConfig.vselect;
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




        mvm.formSubmit = function () {
            mvm.formConfig.formElement.trigger('submit');
        };

        mvm.formConfig = {
            preCompile: function (e) {
            },
            postCompile: function (e) {
                mvm.formConfig.formScope = e.scope;
                mvm.formConfig.formElement = e.element;
            },
            submit: function (e) {
                e.preventDefault();
                var validationResponse = mvm.formConfig.validateFormInputs();
                console.log("mvm.formModel:",mvm.formModel.musicCategoryArray);
                var form = mvm.formConfig.formScope.competitionForm;

                if(mvm.formModel.eoCompetitionMapping.length > 0) {
                    angular.forEach(mvm.formModel.eoCompetitionMapping,function(value,key){
                        value.isActive = false;
                    });

                }

                if(typeof(mvm.formModel.selectedStudentArray) == 'object') {

                    angular.forEach(mvm.formModel.selectedStudentArray, function (value, key) {
                        var flag = true;


                        for (var key2 in mvm.formModel.eoCompetitionMapping) {
                            if (value == mvm.formModel.eoCompetitionMapping[key2].eoStudentUser) {
                                mvm.formModel.eoCompetitionMapping[key2].isActive = true;
                                flag = false;
                                break;
                            }
                        }

                        if (flag) {
                            var map = {
                                "className": "EOCompetitionMapping",
                                "eoStudentUser": value
                            };
                            mvm.formModel.eoCompetitionMapping.push(map);
                        }
                    });
                }   //for students
                else {
                    if(mvm.formModel.eoCompetitionMapping.length > 0){
                        var flag = false;

                        for(var k in mvm.formModel.eoCompetitionMapping){
                            if(mvm.formModel.eoCompetitionMapping[k].eoStudentUser == mvm.formModel.selectedStudentArray){
                                flag = true;
                                mvm.formModel.eoCompetitionMapping[k].isActive = true;
                                break;
                            }
                        }
                        if(!flag) {
                            var map = {
                                "className": "EOCompetitionMapping",
                                "eoStudentUser": mvm.formModel.selectedStudentArray
                            };
                            mvm.formModel.eoCompetitionMapping.push(map);
                        }

                    }
                    else{
                        var map = {
                            "className": "EOCompetitionMapping",
                            "eoStudentUser": mvm.formModel.selectedStudentArray
                        };
                        mvm.formModel.eoCompetitionMapping.push(map);
                    }
                }

                if(typeof(mvm.formModel.selectedTeacherArray) == 'object') {    //for teachers

                    angular.forEach(mvm.formModel.selectedTeacherArray, function (value, key) {
                        var flag = true;


                        for (var key2 in mvm.formModel.eoCompetitionMapping) {
                            if (value == mvm.formModel.eoCompetitionMapping[key2].eoTeacherUser) {
                                mvm.formModel.eoCompetitionMapping[key2].isActive = true;
                                flag = false;
                                break;
                            }
                        }

                        if (flag) {
                            var map = {
                                "className": "EOCompetitionMapping",
                                "eoTeacherUser": value
                            };
                            mvm.formModel.eoCompetitionMapping.push(map);
                            console.log("map:::::3data:",map);
                        }
                    });
                }
                else {
                    if(mvm.formModel.eoCompetitionMapping.length > 0){
                        var flag = false;

                        for(var k in mvm.formModel.eoCompetitionMapping){
                            if(mvm.formModel.eoCompetitionMapping[k].eoTeacherUser == mvm.formModel.selectedTeacherArray){
                                flag = true;
                                mvm.formModel.eoCompetitionMapping[k].isActive = true;
                                break;
                            }
                        }
                        if(!flag) {
                            var map = {
                                "className": "EOCompetitionMapping",
                                "eoTeacherUser": mvm.formModel.selectedTeacherArray
                            };
                            mvm.formModel.eoCompetitionMapping.push(map);
                            console.log("map 3333333:",map);
                        }

                    }
                    else{
                        var map = {
                            "className": "EOCompetitionMapping",
                            "eoTeacherUser": mvm.formModel.selectedTeacherArray
                        };
                        mvm.formModel.eoCompetitionMapping.push(map);
                        console.log("map elseeee:",map);
                    }
                }

            if( typeof (mvm.formModel.selectedTeacherArray) == 'object') {    //for teacher
                 mvm.formModel.teachersPk = JSON.stringify(mvm.formModel.selectedTeacherArray);
                 }
                 else{
                 var teachers = [];
                 teachers.push(mvm.formModel.selectedTeacherArray);
                 mvm.formModel.teachersPk = JSON.stringify(teachers);

                 }

                if( typeof (mvm.formModel.selectedStudentArray) == 'object') {   //for students
                    mvm.formModel.studentsPk = JSON.stringify(mvm.formModel.selectedStudentArray);
                }
                else {
                    var students = [];
                    students.push(mvm.formModel.selectedStudentArray);
                    mvm.formModel.studentsPk = JSON.stringify(students);
                }


                if (validationResponse.invalidInputs.length == 0) {

                    if((Object.keys(mvm.formModel.musicCategoryArray)).length > 0) {
                   /* if((Object.keys(mvm.formModel.selectedStudentArray)).length > 0) {
                    if((Object.keys(mvm.formModel.selectedTeacherArray)).length > 0) {
*/
                            if(mvm.competitionDate != undefined) {
                                mvm.formModel.competitionDate = moment(mvm.competitionDate).format("DD-MM-YYYY");
                            //}
                            mvm.formModel.musicPk = JSON.stringify(mvm.formModel.musicCategoryArray);

                                if(mvm.formModel.details != "") {

                        console.log("mvm.formModel.studentsPk::",mvm.formModel.studentsPk);
                        console.log("mvm.formModel.teachersPk::",mvm.formModel.teachersPk);

                       /* if(mvm.formModel.studentsPk = {}) {
                            console.log("ifffffffff stud");
                            mvm.formModel.studentsPk = "";
                            console.log("mvm.formModel.studentsPk::",mvm.formModel.studentsPk);
                        }

                        if(mvm.formModel.teachersPk = {}) {
                            console.log("ifffffffff teacher");
                            mvm.formModel.teachersPk = "" ;
                            console.log("mvm.formModel.teachersPk::",mvm.formModel.teachersPk);
                        }*/
                            var blankFormInstance = angular.copy(mvm.formModel);
                            delete blankFormInstance.musicCategoryArray;
                            delete blankFormInstance.selectedStudentArray;
                            delete blankFormInstance.selectedTeacherArray;
                            delete blankFormInstance.studentsPk;
                            delete blankFormInstance.teachersPk;
                            console.log("mvm.blankFormInstance:",blankFormInstance);
                        competitionSvcs.createCompetition(blankFormInstance, function (res) {
                            if (res.data == 'Success') {
                                $uibModalInstance.close();
                                $state.reload();
                                notifySvcs.success({
                                    title: "Competition",
                                    content: "Added Successfully."
                                });
                            }
                        },
                        function (err) {
                            notifySvcs.error({
                                content: "Something went wrong"
                            })
                        });
                    } else {
                        notifySvcs.info({
                            content:"Please Fill Details"
                        })

                    }

                    }
                    else {
                        notifySvcs.info({
                            title: "Date",
                            content:"Please select Date."
                        });
                    }
                    } else{
                        notifySvcs.error({
                            content:"Please select Music Type"
                        })

                    }
                }
                else {

                    notifySvcs.error({
                        content: "Please Fill The Form Correctly."
                    });
                }

            }
        };

        mvm.clearSectionFrom = function () {
            mvm.formModel.selectedStudentArray = {};
        }();

        mvm.clearSectionFrom = function () {
            mvm.formModel.selectedTeacherArray = {};
        }();


        mvm.studentConfig = {
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


        mvm.teacherConfig = {
            create: true,
            valueField: 'primaryKey',
            labelField: 'teacherFullName',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Teacher:',
            searchField: ['teacherName'],
            render: {
                item: function (item, escape) {
                    return '<div>' +
                        (item.teacherFullName ? '<span class="subjectTitle">' + escape(item.teacherFullName) + '</span>' : '') +
                        '</div>';
                },
                option: function (item, escape) {
                    return '<div>' +
                        '<span class="subjectTitle">' + escape(item.teacherFullName) + '</span>' + '</div>';
                }
            }
        };



        /*mvm.clearForm = function(){
            mvm.formModel.selectedStudentArray = null;
        };*/



        mvm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);