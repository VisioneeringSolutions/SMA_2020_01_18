/**
 * Created by dell on 30-05-19.
 */
(function (window, document, $, angular) {

    var reportApp = angular.module('reportApp');

    reportApp.controller("ratingCtrl", ["$scope", "$rootScope", "reportSvcs", "$state", "notifySvcs", "localStorage", "$timeout", function ($scope, $rootScope, reportSvcs, $state, notifySvcs, localStorage, $timeout) {
        $rootScope.pageHeader = "";
        $rootScope.pageDescription = "";

        var vm = this;

        vm.yearArray = window.sInstances.yearArray;
        console.log("vm.yearArray :: ", vm.yearArray);

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


        /**************Student Rating Start********************************/

        var studentRatingChart = function () {
            var ctx = document.getElementById("studentRating");


            vm.ratingData = {
                labels: vm.xAxisLabel,
                datasets: [{
                    label:  'Student Rating',
                    tension: 0.000001,
                    borderColor: "#f9687f",
                    backgroundColor: "#f9687f",
                    fill: false,
                    data: vm.yAxisStudentLabelRating,
                    yAxisID: 'y-axis'
                }]
            };


            var chartOptions = {
                scales: {
                    xAxes: [vm.xAxisLabel],
                    yAxes: [{id: "y-axis"}
                    ]
                }
            };
            if (ctx != null) {
                if (vm.barChart1) {
                    vm.barChart1.destroy();
                }
                vm.barChart1 = new Chart(ctx, {
                    type: 'line',
                    data: vm.ratingData,
                    options: chartOptions
                });
            }

        };

        vm.studentRating = function (studentRatingData) {

            var xAxis = [];
            for (var key in studentRatingData) {
                xAxis.push(studentRatingData[key].month);
            }
            vm.xAxisLabel = xAxis;

            var yAxisStudentRating = [];


            for (var key in studentRatingData) {
                yAxisStudentRating.push(parseFloat(studentRatingData[key].maxRating).toFixed(2));
            }

            vm.yAxisStudentLabelRating = yAxisStudentRating;


            studentRatingChart();


        };


        vm.getStudentRating = function () {
            var reqMap = {
                "studentPk": vm.eoStudentUser,
                "musicPk": vm.musicPk,
                "year": vm.year
            };
            reportSvcs.getStudentRatingByMusicTypeAndPk(reqMap, function (res) {
                vm.studentRatingData = res.data;
                vm.studentRating(vm.studentRatingData);

            }, function (err) {

            });
        };


        /**********************Student Rating End***********************************/


        /***********Teacher Ratings start*********************************/

        var teacherRatingChart = function () {
            var ctx = document.getElementById("teacherRating");
            console.log("ctx : ",ctx);


            vm.ratingData = {
                labels: vm.xAxisLabel,
                datasets: [{
                    label:  'Teacher Rating',
                    tension: 0.000001,
                    borderColor: "#f9687f",
                    backgroundColor: "#f9687f",
                    fill: false,
                    data: vm.yAxisTeacherLabelRating,
                    yAxisID: 'y-axis'
                }]
            };


            var chartOptions = {
                scales: {
                    xAxes: [vm.xAxisLabel],
                    yAxes: [{id: "y-axis"}
                    ]
                }
            };
            if (ctx != null) {
                if (vm.barChart1) {
                    vm.barChart1.destroy();
                }
                vm.barChart1 = new Chart(ctx, {
                    type: 'line',
                    data: vm.ratingData,
                    options: chartOptions
                });
            }

        };

        vm.teacherRating = function (teacherRatingData) {

            var xAxis = [];
            for (var key in teacherRatingData) {
                xAxis.push(teacherRatingData[key].month);
            }
            vm.xAxisLabel = xAxis;


            var yAxisTeacherRating = [];


            for (var key in teacherRatingData) {
                yAxisTeacherRating.push(parseFloat(teacherRatingData[key].maxRating).toFixed(2));
            }

            vm.yAxisTeacherLabelRating = yAxisTeacherRating;


            teacherRatingChart();


        };

        vm.getTeacherRating = function () {
            var reqMap = {
                "teacherPk": vm.eoTeacherUser,
                "musicPk": vm.musicPk,
                "year": vm.year
            };
            reportSvcs.getTeacherRatingByMusicTypeAndPk(reqMap, function (res) {
                    vm.teacherRatingData = res.data;
                    vm.teacherRating(vm.teacherRatingData);


            }, function (err) {

            });
        };

        /***********************Teacher Rating End**********************************/


    }]);

})(window, window.document, window.jQuery, window.angular);