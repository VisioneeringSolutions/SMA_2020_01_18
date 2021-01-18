/**
 * Created by dell on 30-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("overallRatingCtrl", ["$scope", "$rootScope", "reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs, $state, notifySvcs, localStorage, $timeout) {
        $rootScope.pageHeader = "";
        $rootScope.pageDescription = "";

        var vm = this;

        vm.yearArray = window.sInstances.yearArray;
        vm.setYear = function(){
            vm.year = vm.yearArray[1];
        }();

        vm.getMusicType = function () {
            var reqMap = {
                "objName": "LKMusicType"
            };
            reportSvcs.getMusicType(reqMap, function (res) {
                vm.musicTypeArr = res.data;
            }, function (err) {

            });
        }();


        /***********Teacher Ratings start*********************************/

        var teacherRatingChart = function () {
            var ctx = document.getElementById("teacherRating");
            console.log("ctx : ",ctx);


            vm.teacherRatingData = {
                labels: vm.yAxisTeacherLabel,
                datasets: [{
                    label:  'Rating',
                    backgroundColor: "#6699ff",
                    data: vm.xAxisRatingLabel
                }]
            };

            if (ctx != null) {
                if (vm.barChart2) {
                    vm.barChart2.destroy();
                }
                vm.barChart2 = new Chart(ctx, {
                    type: 'horizontalBar',
                    data: vm.teacherRatingData,
                    options: {
                        elements: {
                            rectangle: {
                                borderWidth: 2,
                            }
                        },
                        responsive: true,
                        legend: {
                            position: 'right',
                        }
                    }
                });
            }

        };

        vm.teacherRating = function (teacherRatingData) {

            var xAxisRating = [];
            for (var key in teacherRatingData) {
                xAxisRating.push(parseFloat(teacherRatingData[key].rating).toFixed(2));
            }
            vm.xAxisRatingLabel = xAxisRating;

            var yAxisLabel = [];


            for (var key in teacherRatingData) {
                yAxisLabel.push(teacherRatingData[key].teacher);
            }

            vm.yAxisTeacherLabel = yAxisLabel;


            teacherRatingChart();


        };


        /***********************Teacher Rating End**********************************/


        /**************Student Rating Start********************************/

        var studentRatingChart = function () {
            var ctx = document.getElementById("studentRating");


            vm.ratingData = {
                labels: vm.yAxisStudentLabel,
                datasets: [{
                    label:  'Rating',
                    backgroundColor: "#6699ff",
                    data: vm.xAxisRatingLabel
                }]
            };

            if (ctx != null) {
                if (vm.barChart1) {
                    vm.barChart1.destroy();
                }
                vm.barChart1 = new Chart(ctx, {
                    type: 'horizontalBar',
                    data: vm.ratingData,
                    options: {
                        elements: {
                            rectangle: {
                                borderWidth: 2,
                            }
                        },
                        responsive: true,
                        legend: {
                            position: 'right',
                        }
                    }
                });
            }

        };

        vm.studentRating = function (studentRatingData) {

            var xAxisRating = [];
            for (var key in studentRatingData) {
                xAxisRating.push(parseFloat(studentRatingData[key].rating).toFixed(2));
            }
            vm.xAxisRatingLabel = xAxisRating;

            var yAxisLabel = [];


            for (var key in studentRatingData) {
                yAxisLabel.push(studentRatingData[key].student);
            }

            vm.yAxisStudentLabel = yAxisLabel;


            studentRatingChart();


        };


        vm.getOverallRating = function () {
            var reqMap = {
                "year": vm.year
            };
            reportSvcs.getOverallRating(reqMap, function (res) {
                vm.overallRating = res.data;
                vm.studentRating(vm.overallRating.studentRatingList);
                vm.teacherRating(vm.overallRating.teacherRatingList);

            }, function (err) {
                notifySvcs.error({
                    content: "Error occured."
                })
            });
        };


        /**********************Student Rating End***********************************/





    }]);

})(window, window.document, window.jQuery, window.angular);