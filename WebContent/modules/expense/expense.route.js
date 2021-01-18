/**
 * Created by vishuja
 */
(function (window, document, $, angular) {
    var expenseApp = angular.module("expenseApp");

    expenseApp.run(function (amMoment) {
    });
    expenseApp.config(['$provide', '$stateProvider', '$urlRouterProvider', function ($provide, $stateProvider, $urlRouterProvider) {

        function onEnter($rootScope, $state) {

        }
        function onExit($rootScope, $state) {
            $rootScope.pageHeader = "";
            $rootScope.pageDescription = "";
        }

        $stateProvider
            .state('expense', {
                url: "/expense",
                abstract: true,
                templateUrl: 'modules/expense/expense.html',
                controller: 'expenseCtrl',
                controllerAs: 'expense',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            })
            .state('expense.registerexpenses', {
                url: "/register-expenses",
                data: {
                    parentUrl: "/expense"
                },
                resolve: {
                    queryFormData: ["$q", "expenseSvcs","registrationSvcs", function ($q, expenseSvcs,registrationSvcs) {
                        var deferred = $q.defer();
                        async.parallel({
                                ExpenseList: function (callback) {
                                    var reqMap  = {

                                    };
                                    expenseSvcs.getRegisterExpenses(reqMap,function (res) {
                                        callback(null, res.data);
                                    });
                                }
                            },
                            function (err, results) {
                                //console.log(results);
                                deferred.resolve(results);
                            });
                        return deferred.promise;
                    }]
                },
                templateUrl: 'modules/expense/registerexpenses/registerexpenses.html',
                controller: 'registerExpensesCtrl',
                controllerAs: 'registerExpenses',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            }).state('expense.addexpenses', {
                url: "/add-expenses",
                data: {
                    parentUrl: "/expense"
                },
                resolve: {
                    expenseData: ["$q", "expenseSvcs","registrationSvcs", function ($q, expenseSvcs,registrationSvcs) {
                        var deferred = $q.defer();
                        async.parallel({
                                /*ExpenseList: function (callback) {
                                    var reqMap  = {

                                    };
                                    expenseSvcs.getExpensesByMonth(reqMap,function (res) {
                                        callback(null, res.data);
                                    });
                                }*/
                            },
                            function (err, results) {
                                //console.log(results);
                                deferred.resolve(results);
                            });
                        return deferred.promise;
                    }]
                },
                templateUrl: 'modules/expense/addexpenses/addexpenses.html',
                controller: 'addExpensesCtrl',
                controllerAs: 'addExpenses',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            }).state('expense.expensesheet', {
                url: "/expense-sheet",
                data: {
                    parentUrl: "/expense"
                },
                resolve: {
                    expenseData: ["$q", "expenseSvcs","registrationSvcs", function ($q, expenseSvcs,registrationSvcs) {
                        var deferred = $q.defer();
                        async.parallel({
                                /*ExpenseSheetData: function (callback) {
                                    var reqMap  = {

                                    };
                                    expenseSvcs.getExpensesByMonth(reqMap,function (res) {
                                        callback(null, res.data);
                                    });
                                }*/
                            },
                            function (err, results) {
                                //console.log(results);
                                deferred.resolve(results);
                            });
                        return deferred.promise;
                    }]
                },
                templateUrl: 'modules/expense/expensesheet/expensesheet.html',
                controller: 'expenseSheetCtrl',
                controllerAs: 'expenseSheet',
                onEnter: ["$rootScope", "$state", onEnter],
                onExit: ["$rootScope", "$state", onExit]
            });
    }]);

})(window, window.document, window.jQuery, window.angular);