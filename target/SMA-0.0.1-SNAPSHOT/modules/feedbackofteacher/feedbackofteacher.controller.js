/**
 * Created by nikita on 2019-05-29.
 */
(function (window, document, $, angular) {

    var feedbackOfTeacherApp = angular.module('feedbackOfTeacherApp');

    feedbackOfTeacherApp.controller("feedbackOfTeacherCtrl", ["$scope", "$rootScope", "feedbackOfTeacherSvcs", "feedbackOfTeacherModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "registrationSvcs", "batchData", function ($scope, $rootScope, feedbackOfTeacherSvcs, feedbackOfTeacherModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, registrationSvcs, batchData) {
        console.log("feedbackOfTeacherApp");

        var vm = this;

        vm.studentPk = localStorage.get("userKey");
        console.log("vm.studentPk::::", vm.studentPk);

        vm.studBatchList = batchData.BatchList;
        console.log("vm.studBatchList::::", vm.studBatchList);

        vm.getSlot = function (e, d, batchPk) {
            console.log("e::::", e);
            console.log("batchPk:::", batchPk);
            console.log("d:::", d);
            vm.TeacherList = null;
            var reqMap = {
                studPk: localStorage.get("userKey"),
                batchPk: batchPk+""
            };
            console.log("reqMap", reqMap);
            feedbackOfTeacherSvcs.getSlotForStudent(reqMap, function (res) {
                    console.log("res.data---------- : ", res.data);
                    vm.slotData = res.data;
                    if (res.data.length > 0) {
                       /* notifySvcs.success({
                            content: "Slot are::::::"
                        });*/

                    } else {
                        notifySvcs.info({
                            content: "No Data"
                        })

                    }
                },
                function (err) {
                    notifySvcs.error({
                        content: "Action not performed! Please Fill Form Correctly "
                    });
                });


        };

        vm.getTeachList = function(e,batchPk,slotPk,teacherPk){

            console.log("batchPk:",batchPk);
            console.log("slotPk:::", slotPk);
            var reqMap = {
                batchPk: batchPk+"",
                slotPk: slotPk+"",
            };
            console.log("reqMap", reqMap);
            feedbackOfTeacherSvcs.getTeacherList(reqMap, function (res) {
                    console.log("res.data---------- : ", res.data);
                    vm.TeacherList = res.data;
                    if (res.data.length > 0) {
                       /* notifySvcs.success({
                            content: "Slot are::::::"
                        });*/

                    } else {
                        notifySvcs.error({
                            content: "No Data"
                        })

                    }
                },
                function (err) {
                    notifySvcs.error({
                        content: "Action not performed! Please Fill Form Correctly "
                    });
                });


        };

        vm.getAttributeList = function (e, teacherPk, batchPk, slotPk, index) {
            vm.attrList = {};
            var el = document.querySelector("#details" + index);
            console.log(el);
            var allExpandables = document.querySelectorAll(".expandable");
            var cls = el.classList;
            for (var i in allExpandables) {
                if (!isNaN(i) && allExpandables[i] != el) {
                    if (allExpandables[i].classList.value.indexOf("hide") != -1 != "hide") {
                        allExpandables[i].classList.add("hide");
                    }
                }
            }
            if (cls.value.indexOf("hide") != -1) {
                //flag for download excel button.
                vm.monthIndex = index;
                el.classList.remove("hide");


                var reqMap = {
                    className: "EOMasterTeacherCriteria",
                    batchPk: batchPk + "",
                    teacherPk: teacherPk + "",
                    slotPk: slotPk + "",
                    studPk: localStorage.get("userKey") + ""
                };

                feedbackOfTeacherSvcs.getAttrForRating(reqMap, function (ress) {
                        console.log(" vm.ress ", ress);
                        vm.attrList[index] = ress.data;
                        if (vm.attrList[index][0].primaryKey) {
                            vm.lengthOfAttr = null;
                        } else {
                            vm.lengthOfAttr = Object.keys(vm.attrList[index]).length - 1;
                        }
                        
                    },
                    function (err) {
                        notifySvcs.error({
                            content: "Action not performed! Please Fill Form Correctly."
                        });
                    });
            } else {
                el.classList.add("hide");
            }
            vm.flag = false;
        };

        vm.getMaxRating = function (attributeData, mainIndex, childIndex) {
            var temp = [];
            for (var k = 1; k <= parseInt(attributeData.maxRating); k++) {
                temp.push(k);
            }

            return temp;
        };

        vm.getRating = function (rating, starIndex, mainIndex, childIndex, entity) {
            var optedRating = starIndex + 1;


            if (rating >= starIndex) {
                document.getElementById('star' + childIndex + '' + starIndex).style.color = "orange";
            } else {
                document.getElementById('star' + childIndex + '' + starIndex).style.color = "white";
            }

        };

        vm.setRating = function (starIndex, mainIndex, childIndex, entity) {

          

            var optedRating = starIndex + 1;

            if (vm.lengthOfAttr) {
                for (var k = 0; k < entity.length; k++) {                   
                    document.getElementById('star' + childIndex + '' + k).style.color = "white";
                }


                if (starIndex != null) {
                    for (var k = 0; k <= starIndex; k++) {
                        document.getElementById('star' + childIndex + '' + k).style.color = "orange";
                    }

                    vm.attrList[mainIndex][childIndex]['optedRating'] = optedRating;
                }
          
                    vm.flag = true;
                   
                    for(var k in vm.attrList[mainIndex]){
                        if(vm.attrList[mainIndex][k].optedRating == null){
                            vm.flag = false;
                            
                            break;
                        }
                    }
            }

        };

        vm.disableRatingButton = true;

        vm.submitRating = function (teacherPk, batchPk, slotPk,date,month,year, mainIndex) {
            var blankInstanceForm = {};
            var avgRating = 0.0, divisorRating = 0.0, dividendRating = 0.0;
            for (var k in vm.attrList[mainIndex]) {
                if (vm.attrList[mainIndex][k].optedRating) {
                    dividendRating += (parseFloat(vm.attrList[mainIndex][k].optedRating) * parseFloat(vm.attrList[mainIndex][k].maxRating));
                    divisorRating += parseFloat(vm.attrList[mainIndex][k].maxRating);
                    vm.attrList[mainIndex][k]['eoTeacherUser'] = teacherPk;
                    vm.attrList[mainIndex][k]['eoBatch'] = batchPk;
                    vm.attrList[mainIndex][k]['eoDefinedSlot'] = slotPk;
                    vm.attrList[mainIndex][k]['eoStudentUser'] = localStorage.get("userKey");
                    vm.attrList[mainIndex][k]['className'] = "EOTeacherCriteria";

                    delete vm.attrList[mainIndex][k].criteria;
                    delete vm.attrList[mainIndex][k].maxRating;
                } else {
                    vm.disableRatingButton = true;
                     notifySvcs.error({
                     content: "Kindly fill atleast one rating."
                     });
                     break;
                }
            }
            console.log("dividendRating::111:::", dividendRating);
            console.log("divisorRating::11111:::", divisorRating);
            if (dividendRating != 0.0 && divisorRating != 0.0) {
                avgRating = (dividendRating / divisorRating).toFixed(2);
            }
            console.log("avgRating@11111@@@", avgRating);

            var ratingMap = {
                "className": "EOTeacherRating",
                "eoDefinedSlot": slotPk,
                "eoTeacherUser": teacherPk,
                "eoBatch": batchPk,
                "avgOptedRating": avgRating,
                "eoStudentUser": localStorage.get("userKey"),
                "date": date,
                "month": month,
                "year": year
            };

            blankInstanceForm['ratingMap'] = ratingMap;
            blankInstanceForm['criteriaList'] = vm.attrList[mainIndex];

            console.log("blankInstanceForm###:", blankInstanceForm);
            feedbackOfTeacherSvcs.createRatingsForTeacher(blankInstanceForm, function (res) {
                    if (res.data == 'Success') {
                        vm.attrList[mainIndex] = {};
                        var el = document.querySelector("#details" + mainIndex);
                        var allExpandables = document.querySelectorAll(".expandable");
                        var cls = el.classList;
                        el.classList.add("hide");

                        notifySvcs.success({
                            title: "Ratings",
                            content: "Added Successfully."
                        });
                    }
                },
                function (err) {
                    notifySvcs.error({
                        content: "Something went wrong"
                    })
                });
        };



    }]);

})(window, window.document, window.jQuery, window.angular);
