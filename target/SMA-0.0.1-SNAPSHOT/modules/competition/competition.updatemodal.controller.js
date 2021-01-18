/**
 * Created by nikita on 2019-06-24.
 */
(function (window, document, $, angular) {

    var competitionApp = angular.module('competitionApp');

    competitionApp.controller("competitionUpdateModalCtrl", ["$scope", "$rootScope","competitionSvcs", "competitionModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout","modalSvcs","$uibModalInstance","metadata","registrationSvcs", function ($scope, $rootScope, competitionSvcs, competitionModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout,modalSvcs,$uibModalInstance,metadata, registrationSvcs) {
        //console.log("In update model");
        var mvm = this;
        var tabMapForFields = [
            ["name", "competitionDate","details"]
        ];
        mvm.formModel = competitionModel.getInstance("updateCompetition");



        mvm.formModel = angular.copy(metadata.lkInstances.CompData);
        console.log("mvm.formModel ::::::",mvm.formModel);

       /* mvm.compArray = mvm.formModel.eoCompetitionMapping;
        //console.log(" mvm.compList ::::::", mvm.compArray );*/

        mvm.musicArray = angular.copy(metadata.lkInstances.MusicType);
       mvm.StudentArray = angular.copy(metadata.lkInstances.StudentList);
        mvm.teacherArray = angular.copy(metadata.lkInstances.TeacherList);

        mvm.setMusicType = function () {
            var tempMap = [];
            //console.log("mvm.formModel.musicCategoryList::",mvm.formModel.musicCategoryList);
            if (mvm.formModel.musicCategoryList != null) {
                for (var k in mvm.formModel.musicCategoryList) {
                    tempMap.push(mvm.formModel.musicCategoryList[k].primaryKey)
                }
            }

            mvm.formModel.musicCategoryArray = tempMap;
        }();

        mvm.setStudAndTeacher = function () {
            mvm.formModel.selectedStudentArray = [];
            mvm.formModel.selectedTeacherArray = [];
            for(var k in mvm.formModel.eoCompetitionMapping){

                if(mvm.formModel.eoCompetitionMapping[k].eoStudentUser != null){
                    mvm.formModel.selectedStudentArray.push(mvm.formModel.eoCompetitionMapping[k].eoStudentUser);
                }
                if(mvm.formModel.eoCompetitionMapping[k].eoTeacherUser != null){
                    mvm.formModel.selectedTeacherArray.push(mvm.formModel.eoCompetitionMapping[k].eoTeacherUser);
                }
            }
        }();


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

        mvm.createDateMap = function () {
            return dateMaps.getMap();
        };

        /* --------------end-------------------- */


        var competitionDate = moment(mvm.formModel.competitionDate, "DD-MM-YYYY").format("YYYY-MM-DD");
        mvm.competitionDate = new Date(competitionDate);


        mvm.musicCategoryConfig = {
            create: true,
            valueField: 'primaryKey',
            labelField: 'musicType',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Music Category:',
            searchField: ['musicType'],
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

        mvm.studentConfig = {
            create: true,
            valueField: 'primaryKey',
            labelField: 'studentfullName',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Select Students:',
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
                var form = mvm.formConfig.formScope.competitionForm;
                //console.log("mvm.formModel ::::::",mvm.formModel);
              //console.log("(mvm.fmMdl.eoComonMapg-before-----:",mvm.formModel.eoCompetitionMapping);
                if(mvm.formModel.eoCompetitionMapping.length > 0) {
                    angular.forEach(mvm.formModel.eoCompetitionMapping,function(value,key){
                        value.isActive = false;
                        //console.log("value: angular",value);
                    });
                }
                //console.log("eoCompeMpping:::aftr anglarforeach:----",mvm.formModel.eoCompetitionMapping);
                //console.log("mvm.formModel.selectedStudentArray::::----",mvm.formModel.selectedStudentArray);

                if(typeof(mvm.formModel.selectedStudentArray) == 'object') {//for students
                    //console.log("ifff typeof:---selectedStudentArray-",mvm.formModel.selectedStudentArray);

                    angular.forEach(mvm.formModel.selectedStudentArray, function (value, key) {
                        var flag = true;
                        

                        for (var key2 in mvm.formModel.eoCompetitionMapping) {
                            if (value == mvm.formModel.eoCompetitionMapping[key2].eoStudentUser) {
                                //console.log("iff value== studuser--",mvm.formModel.eoCompetitionMapping.eoStudentUser);
                                mvm.formModel.eoCompetitionMapping[key2].isActive = true;
                                flag = false;
                                break;
                            }
                        }

                        if (flag) {
                           // console.log(" if flag");
                            var map = {
                                "className": "EOCompetitionMapping",
                                "eoStudentUser": value
                            };
                            mvm.formModel.eoCompetitionMapping.push(map);
                        }
                        //console.log("map----------",mvm.formModel.eoCompetitionMapping);
                    });
                }
                else {
                    //console.log("elssssseeeeeee");
                    if(mvm.formModel.eoCompetitionMapping.length > 0){
                        var flag = false;

                        //console.log("eoCompetitionMapping----------",mvm.formModel.eoCompetitionMapping);
                        //console.log(".......selectedStudentArray----------",mvm.formModel.selectedStudentArray);
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
                            //console.log("map:::::3data:",map);
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
                            //console.log("map 3333333:",map);
                        }

                    }
                    else{
                        var map = {
                            "className": "EOCompetitionMapping",
                            "eoTeacherUser": mvm.formModel.selectedTeacherArray
                        };
                        mvm.formModel.eoCompetitionMapping.push(map);
                        //console.log("map elseeee:",map);
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

                //console.log("eoCompetitionMapping eoCompetitionMapping:",mvm.formModel.eoCompetitionMapping);
                if (validationResponse.invalidInputs.length == 0) {

                    if((Object.keys(mvm.formModel.musicCategoryArray)).length > 0) {
                         /*if((Object.keys(mvm.formModel.selectedStudentArray)).length > 0) {
                            if((Object.keys(mvm.formModel.selectedTeacherArray)).length > 0) {
*/
                            if(mvm.competitionDate != undefined) {
                                mvm.formModel.competitionDate = moment(mvm.competitionDate).format("DD-MM-YYYY");
                            }
                            mvm.formModel.musicPk = JSON.stringify(mvm.formModel.musicCategoryArray);
                            mvm.formModel.className = 'EOCompetition';

                            var blankFormInstance = angular.copy(mvm.formModel);
                            delete blankFormInstance.musicCategoryArray;
                            delete blankFormInstance.selectedStudentArray;
                            delete blankFormInstance.selectedTeacherArray;
                            delete blankFormInstance.studentsPk;
                             delete blankFormInstance.teachersPk;
                            //console.log("mvm.blankFormInstance:",blankFormInstance);


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

        mvm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);