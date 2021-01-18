/**
 * Created by Kundan kumar on 18-Feb-20.
 */

(function (window, document, $, angular) {

    var salaryApp = angular.module('salaryApp');

    salaryApp.controller("detailedSalaryViewCtrl", ["$scope", "salaryModel","salarySvcs","metadata","notifySvcs","$state","localStorage","$uibModalInstance","sorting", function ($scope, salaryModel,salarySvcs,metadata,notifySvcs,$state,localStorage,$uibModalInstance,sorting) {

        var vm = this;


        vm.studentData = [];
        var tempData = metadata.data;

        vm.studentData = sorting.listOfObject(tempData, "ASC");

        vm.month = metadata.month;
        vm.year = metadata.year;
        vm.modalInstance = $uibModalInstance;
    }]);

})(window, window.document, window.jQuery, window.angular);
