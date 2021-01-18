/**
 * Created by nikita Saxena on 2019-05-17.
 */

(function (window, document, $, angular) {

    var batchForTeacherApp = angular.module('batchForTeacherApp');

    batchForTeacherApp.controller("batchForTeacherCtrl", ["$scope", "$rootScope", "batchForTeacherSvcs", "batchForTeacherModel", "lookupStoreSvcs", "$state", "notifySvcs", "localStorage", "http", "$timeout", "modalSvcs", "registrationSvcs","typeData", function ($scope, $rootScope, batchForTeacherSvcs, batchForTeacherModel, lookupStoreSvcs, $state, notifySvcs, localStorage, http, $timeout, modalSvcs, registrationSvcs,typeData) {
        //console.log("batchForTeacherApp");

        var vm = this;

        vm.formModel = batchForTeacherModel.getInstance("feedback");

        /* for calendar */
        /*---------calendar-------------start--------------------*/
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

        vm.createDateMap = function () {
            return dateMaps.getMap();
        };
        /* ------calendar--------end-------------------- */


        /*for 7 days date data---------start----*/
        vm.dateRange = function () {
            var getDaysInMonth = function (month, year) {
                return new Date(year, month, 0).getDate();
            };

            var dateValue = '';
            if (getDaysInMonth(new Date(vm.dateFrom).getMonth() + 1, new Date(vm.dateFrom).getFullYear()) > new Date(vm.dateFrom).getDate() + 6) {
                dateValue = (new Date(vm.dateFrom).getMonth() + 1) + '-' + (new Date(vm.dateFrom).getDate() + 6) + '-' + (new Date(vm.dateFrom).getFullYear());
            }
            else {
                if (new Date(vm.dateFrom).getMonth() < 11) {
                    var value1 = 6 - (getDaysInMonth(new Date(vm.dateFrom).getMonth() + 1, new Date(vm.dateFrom).getFullYear()) - new Date(vm.dateFrom).getDate());
                    dateValue = (new Date(vm.dateFrom).getMonth() + 2) + '-' + value1 + '-' + (new Date(vm.dateFrom).getFullYear());
                }
                else {
                    var value2 = 6 - (getDaysInMonth(new Date(vm.dateFrom).getMonth() + 1, new Date(vm.dateFrom).getFullYear()) - new Date(vm.dateFrom).getDate());
                    dateValue = 1 + '-' + value2 + '-' + (new Date(vm.dateFrom).getFullYear() + 1);
                }
            }
            var tempDate = moment(dateValue, "MM-DD-YYYY").format("MM-DD-YYYY");
            vm.dateTo = new Date(tempDate);
            vm.dayDateMap();
        };

        var dayCount = undefined;
        vm.dayDateMap = function () {
            vm.dateDayArray = [];
            dayCount = vm.dateFrom;
            vm.dateDayArray.push(moment((dayCount).addDays(0)).format("dddd") + '_' +
                (moment((dayCount).addDays(0)).format("YYYY-MM-DD")));
            for (var k = 0; k < 6; k++) {
                vm.dateDayArray.push(moment((dayCount).addDays(1)).format("dddd") + '_' +
                    (moment((dayCount).addDays(0)).format("YYYY-MM-DD")));
            }
        };
        /*for 7 days date data---------end----*/


        vm.submit = function () {

            var dayVal = dayCount;
            var reqMap = {
                dateFrom: (moment((dayVal).addDays(-6)).format("YYYY-MM-DD")),
                dateTo: moment(vm.dateTo).format("YYYY-MM-DD"),
                userPk: localStorage.get("userKey")
            };
            batchForTeacherSvcs.getBatchListForTeacher(reqMap, function (res) {
                    vm.reqDataData = res.data;
                    if (res.data.length > 0) {
                       /* notifySvcs.success({
                            content: " Assigned batch are:"
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


        vm.getStudentList = function (e, batchPk, slotPk) {   /* for stud list*/
            var reqqMap = {
                batchPk: batchPk + "",
                slotPk: slotPk + ""
            };
            batchForTeacherSvcs.getStudentListForTeacher(reqqMap, function (res) {
                vm.studentBatchList = res.data;
                vm.slotPkForHmeWrk = vm.studentBatchList[0].slotPk;
                vm.batchPkForHmeWrk = vm.studentBatchList[0].batchPk;
                if (vm.studentBatchList.length > 0) {
                    /*notifySvcs.success({
                        title: "Students",
                        content: "Success"
                    });*/
                }
            }, function (err) {
                notifySvcs.error({
                    content: "Error!!!!!!"
                });
            });
        };


        vm.getDateTime = function (e, batchPk) {
            vm.studentBatchList = null;
            var mapData = {
                userPk: localStorage.get("userKey"),
                batchPk: batchPk + ""

            };
            batchForTeacherSvcs.getDateAndTimeForBatch(mapData, function (res) {
                    vm.dateTimeData = res.data;
                    vm.slotDate = vm.dateTimeData[0].date;
                    vm.slotMonth = vm.dateTimeData[0].month;
                    vm.slotYear = vm.dateTimeData[0].year;
                    vm.slotPk = vm.dateTimeData[0].primary_key;
                    vm.batchPk = vm.dateTimeData[0].eobatch_primary_key;
                    if (res.data.length > 0) {
                        /*notifySvcs.success({
                            content: "Slots"
                        });*/
                    } else {
                        notifySvcs.error({
                            content: "No Data "
                        })

                    }
                },
                function (err) {
                    notifySvcs.error({
                        content: "Action not performed! Please Fill Form Correctly "
                    });
                });
        };



        /*upload multiple image------------------START---------*/
        vm.uploadConfig = {
            name: "UploadAvatar",
            model: undefined,
            entityName: "EOTeacherBatch",
            className: "EOImage",
            httpPath: "ajax/createImgObject",
            error: function (elm, scope) {
            },
            success: function (event, file, data, files, element, scope) {
                vm.uploadConfig.upload(function errorCallback(err) {
                    },
                    function successCallback(result) {
                        vm.formModel.eoImage = result.data.primaryKey;
                    });
            }
        };

        var expFiles = {};

        vm.countExpImage = function (index) {
            (function (index) {
                $timeout(function () {
                    if (expFiles['eoExpImage' + index]) {
                        if (Object.keys(expFiles['eoExpImage' + index]).length > 0) {
                            document.querySelector('#eoImageCountExp' + index).innerHTML = Object.keys(expFiles['eoExpImage' + index]).length + "";
                        } else {
                            document.querySelector('#eoImageCountExp' + index).innerHTML = "0";
                        }
                    }
                });
            })(index);

        };

        vm.fileChangeConfigExp = {
            onLoad: function (e, imageScope, imageElement, imageAttrs, fileIndex) {
                if (expFiles[imageAttrs['name']]) {
                    expFiles[imageAttrs['name']][e.vfile.name] = {
                        name: e.vfile.name,
                        size: e.vfile.size,
                        type: e.vfile.type,
                        result: e.target.result
                    };

                } else {
                    expFiles[imageAttrs['name']] = {};
                    expFiles[imageAttrs['name']][e.vfile.name] = {
                        name: e.vfile.name,
                        size: e.vfile.size,
                        type: e.vfile.type,
                        result: e.target.result
                    };
                }

                if (Object.keys(expFiles[imageAttrs['name']]).length > 0) {
                    document.getElementById('eoImageCountExp' + imageAttrs['eoIndex']).innerHTML = Object.keys(expFiles[imageAttrs['name']]).length + "";
                } else {
                    document.getElementById('eoImageCountExp' + imageAttrs['eoIndex']).innerHTML = "0";
                }
            },
            change: function (e, imageScope, imageElement, imageAttrs) {
            }
        };

        vm.showExpImagesList = function (elmName, index) {
            vm.addModalExp(expFiles[elmName], elmName, index);
        };

        vm.addModalExp = function (results, elmName, index) {
            modalInstance = modalSvcs.open({
                callback: function () {
                },
                fileInstances: results,
                name: elmName,
                events: {
                    delete: function (key, name) {
                        if (expFiles[key]) {
                            vm.eoExpImage.element[0].value = "";
                            if (expFiles[key][name]) {
                                delete expFiles[key][name];
                                if (Object.keys(expFiles[key]).length > 0) {
                                    document.getElementById('eoImageCountExp').innerHTML = Object.keys(expFiles[key]).length + "";
                                } else {
                                    document.getElementById('eoImageCountExp').innerHTML = "0";
                                }
                            }
                        }
                    }
                },
                templateUrl: "modules/batchforteacher/fileviewer.html",
                controller: "fileViewCtrl",
                controllerAs: "fileView"
            });
            modalInstance.rendered.then(function () {
                //////console.log("modal template rendered");
            });
            modalInstance.opened.then(function () {
                //////console.log("modal template opened");
            });
            modalInstance.closed.then(function () {
                //////console.log("modal template closed");
                modalInstance = undefined;
            });
        };
        /*upload multiple image---------------------END*/





        /*ratingsss-----------START---------*/
        vm.getAttributeList = function (e, studPk, batchPk, slotPk, index) {
            vm.attrList = {};
            var el = document.querySelector("#details" + index);
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
                vm.monthIndex = index;
                el.classList.remove("hide");
                var reqMap = {
                    className: "EOMasterStudentCriteria",
                    batchPk: batchPk + "",
                    studPk: studPk + "",
                    slotPk: slotPk + "",
                    teacherPk: localStorage.get("userKey") + ""
                };

                batchForTeacherSvcs.getAttributeForRating(reqMap, function (ress) {
                        console.log(" vm.ress ", ress.data);
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
        vm.submitRating = function (studPk, batchPk, slotPk, mainIndex) {
            console.log("mainIndex:",mainIndex);
            var blankInstanceForm = {};
            var avgRating = 0.0, divisorRating = 0.0, dividendRating = 0.0;
            console.log("vm.attrLis==========t:",vm.attrList);
            for (var k in vm.attrList[mainIndex]) {
                if (vm.attrList[mainIndex][k].optedRating) {
                    vm.disableRatingButton = false;
                    dividendRating += (parseFloat(vm.attrList[mainIndex][k].optedRating) * parseFloat(vm.attrList[mainIndex][k].maxRating));
                    divisorRating += parseFloat(vm.attrList[mainIndex][k].maxRating);
                    vm.attrList[mainIndex][k]['eoStudentUser'] = studPk;
                    vm.attrList[mainIndex][k]['eoBatch'] = batchPk;
                    vm.attrList[mainIndex][k]['eoDefinedSlot'] = slotPk;
                    vm.attrList[mainIndex][k]['eoTeacherUser'] = localStorage.get("userKey");
                    vm.attrList[mainIndex][k]['className'] = "EOStudentCriteria";

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
            if (dividendRating != 0.0 && divisorRating != 0.0) {
                avgRating = (dividendRating / divisorRating).toFixed(2);
            }


            var ratingMap = {
                "className": "EOStudentRating",
                "eoDefinedSlot": slotPk,
                "eoStudentUser": studPk,
                "eoBatch": batchPk,
                "avgOptedRating": avgRating,
                "eoTeacherUser": localStorage.get("userKey"),
                "date":vm.slotDate,
                "month":vm.slotMonth,
                "year":vm.slotYear

            };

            blankInstanceForm['ratingMap'] = ratingMap;
            blankInstanceForm['criteriaList'] = vm.attrList[mainIndex];
           batchForTeacherSvcs.createRatings(blankInstanceForm, function (res) {
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
        /*ratingsss----------------END---------*/

        /*homework------------------START--------------------------------------------------*/

        vm.formSubmit = function (e) {
            //console.log("e:11111111111:",e);
            vm.formModel.eoBatch = vm.batchPkForHmeWrk +"";
            vm.formModel.eoDefinedSlot = vm.slotPkForHmeWrk +"";
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
                var form = vm.formConfig.formScope.BatchForTeacherForm;
                vm.formModel.teacherPk = localStorage.get("userKey");
                vm.formModel.className = "EOTeacherBatch";



                vm.formModel.eoAttachmentArray = [];
                var qualFileArr = expFiles['eoExpImage'];
                for (var index in qualFileArr) {
                    if (index != 'length') {
                        var imageObj = {};
                        imageObj.type = qualFileArr[index].type;
                        imageObj.detail = qualFileArr[index].result;
                        imageObj.className = "EOImage";
                        imageObj.entityName = "EOTeacherBatch";
                        imageObj.displayName = qualFileArr[index].name;
                        vm.formModel.eoAttachmentArray.push(imageObj);
                    }
                }
                var blankFormInstance = angular.copy(vm.formModel);
                batchForTeacherSvcs.createFeedbackByTeacher(blankFormInstance, function (res) {
                        if (res.data == 'Success') {
                             $state.reload();
                            /*notifySvcs.success({
                                title: "Feedback",
                                content: "Added Successfully."
                            });*/
                        }
                    },
                    function (err) {
                        notifySvcs.error({
                            content: "Something went wrong"
                        })
                    });
            }
        };


        vm.getHomeWorkDetails= function(slotPk,batchPk) {
            var reqMap = {
                eoBatch : batchPk +"",
                eoDefinedSlot :slotPk+"",
                userPk: localStorage.get("userKey")
            };
            batchForTeacherSvcs.getHomeWrkList(reqMap, function (res) {
                    if(res.data != null) {
                        vm.formModel = res.data[0];
                    }
                    if (res.data.length > 0) {
                        // $state.reload();
                        /*notifySvcs.success({
                            content: " Home Work Details are::::"
                        });*/
                    } else {
                        notifySvcs.info({
                            content: "No Data For Home work :"
                        })

                    }
                },
                function (err) {
                    notifySvcs.error({
                        content: "Action not performed! Please Fill Form Correctly "
                    });
                });
        };


        /*homework----------------END------------------------------------------------------------*/

    }]);

})(window, window.document, window.jQuery, window.angular);
