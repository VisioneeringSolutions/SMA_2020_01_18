/**
 * Created by SumitJangir on 6/5/16.
 */

(function (window, document, $, angular) {

    angular.module('commonApp').factory("http", ["$http", "storeSvcs", "$rootScope", function ($http, storeSvcs, $rootScope) {

        var result = {};

        var http = new function () {

            this.request = function (map, successCallback, errorCallback) {
                $http(map).then(function (response) {
                    if (successCallback)
                        successCallback(response);
                }, function (err) {
                    if (errorCallback)
                        errorCallback(err);
                });
            };

        }();

        result.request = http.request;

        var lookupArray = ["MusicType", "CategoryType", "ClassDuration"];

        angular.forEach(lookupArray, function (value, key) {

            result["get" + value] = function (successCallback, errorCallback) {
                var k = value;
                http.request(
                    {
                        url: $rootScope.baseAPIUrl + "ajax/getObject",
                        method: "POST",
                        data: {objName: "LK" + k}
                    },
                    successCallback,
                    errorCallback
                );
            }
        });

        var controllersDef = {
            "auth":{
                "resetPassword":{
                    name: "Password",
                    type: "reset",
                    method: "POST"
                },
                "resetPasswordByAdmin":{
                    name: "PasswordByAdmin",
                    type: "reset",
                    method: "POST"
                }
            },
            "ajax": {
                "createObject": {
                    name: "Object",
                    type: "create",
                    method: "POST"
                },
                "updateObject": {
                    name: "Object",
                    type: "update",
                    method: "POST"
                },
                "createImgObject": {
                    name: "ImageObject",
                    type: "create",
                    method: "POST"
                },
                "updateImgObject": {
                    name: "ImageObject",
                    type: "update",
                    method: "POST"
                },

                "getStaff": {
                    name: "Staff",
                    type: "get",
                    method: "POST"
                },
                "getStaffByPk": {
                    name: "StaffByPk",
                    type: "get",
                    method: "POST"
                },
                "getStateByCountryID": {
                    name: "StateByCountryID",
                    type: "get",
                    method: "POST"
                },
                "getCityByStateID": {
                    name: "CityByStateID",
                    type: "get",
                    method: "POST"
                },
                "getInstitute": {
                    name: "Institute",
                    type: "get",
                    method: "POST"
                },
                "getInstituteByPk": {
                    name: "InstituteByPk",
                    type: "get",
                    method: "POST"
                },
                "getBatchListForTeacher": {
                    name: "BatchListForTeacher",
                    type: "get",
                    method: "POST"
                },
                "getStudentListForTeacher": {
                    name: "StudentListForTeacher",
                    type: "get",
                    method: "POST"
                },
                "createFeedbackByTeacher": {
                    name: "FeedbackByTeacher",
                    type: "create",
                    method: "POST"
                },
                "createRatings": {
                    name: "Ratings",
                    type: "create",
                    method: "POST"
                },
                "getAttributeForRating": {
                    name: "AttributeForRating",
                    type: "get",
                    method: "POST"
                },
                "getDateAndTimeForBatch": {
                    name: "DateAndTimeForBatch",
                    type: "get",
                    method: "POST"
                },
                "getStudentRating": {
                    name: "StudentRating",
                    type: "get",
                    method: "POST"
                },
                "createNews": {
                    name: "News",
                    type: "create",
                    method: "POST"
                },
                "getNewsList": {
                    name: "NewsList",
                    type: "get",
                    method: "POST"
                },
                "getNewsByPk": {
                    name: "NewsByPk",
                    type: "get",
                    method: "POST"
                },
                "getHomeWorkData": {
                    name: "HomeWorkData",
                    type: "get",
                    method: "POST"
                },
                "getHomeWrkList": {
                    name: "HomeWrkList",
                    type: "get",
                    method: "POST"
                },
                "deleteImageForNews": {
                    name: "ImageForNews",
                    type: "delete",
                    method: "POST"
                },
                "getObject": {
                    name: "MusicType",
                    type: "get",
                    method: "POST"
                },
                "deleteNewsByPk": {
                    name: "NewsByPk",
                    type: "delete",
                    method: "POST"
                },
                "createNewsForAdmin": {
                    name: "NewsForAdmin",
                    type: "create",
                    method: "POST"
                },
                "createQueryForm":{
                    name: "QueryForm",
                    type: "create",
                    method: "POST"
                },
                "getQueryForm":{
                    name: "QueryForm",
                    type: "get",
                    method: "POST"
                },
                "moveQryFormFn":{
                    name: "QryFormFn",
                    type: "move",
                    method: "POST"
                },
                "getQueryStudentsByPk":{
                    name: "QueryStudentsByPk",
                    type: "get",
                    method: "POST"
                },
                "notConvertedStudFn":{
                    name: "ConvertedStudFn",
                    type: "not",
                    method: "POST"
                },
                "getFollowUpDate":{
                    name: "FollowUpDate",
                    type: "get",
                    method: "POST"
                },
                "getPhoneNumberList": {
                    name: "PhoneNumberList",
                    type: "get",
                    method: "POST"
                },
                "createCompetition": {
                    name: "Competition",
                    type: "create",
                    method: "POST"
                },
                "getCompetition": {
                    name: "Competition",
                    type: "get",
                    method: "POST"
                },
                "getCompetitionByPk": {
                    name: "CompetitionByPk",
                    type: "get",
                    method: "POST"
                },
                "getCompListForStud": {
                    name: "CompListForStud",
                    type: "get",
                    method: "POST"
                },
                "getCompListForTeacher": {
                    name: "CompListForTeacher",
                    type: "get",
                    method: "POST"
                },
                "getAllFollowUpDate": {
                    name: "AllFollowUpDate",
                    type: "get",
                    method: "POST"
                },
                "getProfileImage": {
                    name: "ProfileImage",
                    type: "get",
                    method: "POST"
                },
                "getCompListForTeacherPk": {
                    name: "CompListForTeacherPk",
                    type: "get",
                    method: "POST"
                },
                "getCompListForStudByPk": {
                    name: "CompListForStudByPk",
                    type: "get",
                    method: "POST"
                },
                "getColorCodeForTeacher": {
                    name: "ColorCodeForTeacher",
                    type: "get",
                    method: "POST"
                },
                "getTrialStudents":{
                    name: "TrialStudents",
                    type: "get",
                    method: "POST"
                },
                "updateTrialStudents":{
                    name: "TrialStudents",
                    type: "update",
                    method: "POST"
                }
            },
            "ajaxRegistration": {
                "createUser": {
                    name: "User",
                    type: "create",
                    method: "POST"
                },
                "getStudentUser": {
                    name: "StudentUser",
                    type: "get",
                    method: "POST"
                },
                "getStudentsUserByPk": {
                    name: "StudentsUserByPk",
                    type: "get",
                    method: "POST"
                },
                "getPhoneDetailsByUserPk": {
                    name: "PhoneDetailsByUserPk",
                    type: "get",
                    method: "POST"
                },
                "updateDeleteReasonByStdPk": {
                    name: "DeleteReasonByStdPk",
                    type: "update",
                    method: "POST"
                },
                "getTeacherUser": {
                    name: "TeacherUser",
                    type: "get",
                    method: "POST"
                },
                "getTeacherUserByPk": {
                    name: "TeacherUserByPk",
                    type: "get",
                    method: "POST"
                },
                "createCourse": {
                    name: "Course",
                    type: "create",
                    method: "POST"
                },
                "getCourseList": {
                    name: "CourseList",
                    type: "get",
                    method: "POST"
                },
                "getCourseByPk": {
                    name: "CourseByPk",
                    type: "get",
                    method: "POST"
                },
                "deleteCourseByPk": {
                    name: "CourseByPk",
                    type: "delete",
                    method: "POST"
                },
                "getMusicRoomList": {
                    name: "MusicRoomList",
                    type: "get",
                    method: "POST"
                },
                "getMusicTypeList": {  //vishuja
                    name: "MusicTypeList",
                    type: "get",
                    method: "POST"
                },
                "getMusicCategoryList": {  //vishuja
                    name: "MusicCategoryList",
                    type: "get",
                    method: "POST"
                },
                "getMusicTypeByPk": {  //vishuja
                    name: "MusicTypeByPk",
                    type: "get",
                    method: "POST"
                },
                "getMusicCategoryByPk": {  //vishuja
                    name: "MusicCategoryByPk",
                    type: "get",
                    method: "POST"
                },
                "getMusicRoomByPk": {
                    name: "MusicRoomByPk",
                    type: "get",
                    method: "POST"
                },
                "deleteMusicRoomByPk": {
                    name: "MusicRoomByPk",
                    type: "delete",
                    method: "POST"
                },
                "deleteMusicTypeByPk": {
                    name: "MusicTypeByPk",
                    type: "delete",
                    method: "POST"
                },
                "deleteMusicCategoryByPk": {
                    name: "MusicCategoryByPk",
                    type: "delete",
                    method: "POST"
                },
                "getBatchList": {
                    name: "BatchList",
                    type: "get",
                    method: "POST"
                },
                "getBatchByPk": {
                    name: "BatchByPk",
                    type: "get",
                    method: "POST"
                },
                "deleteBatchByPk": {
                    name: "BatchByPk",
                    type: "delete",
                    method: "POST"
                },
                "getBatchesForStudent": {
                    name: "BatchesForStudent",
                    type: "get",
                    method: "POST"
                },
                "createBatchesForStudent": {
                    name: "BatchesForStudent",
                    type: "create",
                    method: "POST"
                },
                "getMusicAndCategoryType": {
                    name: "MusicAndCategoryType",
                    type: "get",
                    method: "POST"
                },
                "updateUser": {
                    name: "User",
                    type: "update",
                    method: "POST"
                },
                "updateLoginUserData": {    /*rashmi update profile*/
                    name: "LoginUserData",
                    type: "update",
                    method: "POST"
                },
                "updateProfileObject": {
                    name: "ProfileObject",
                    type: "update",
                    method: "POST"
                },
                "getManagementUser": {
                    name: "ManagementUser",
                    type: "get",
                    method: "POST"
                },
                "getStudentUserData": {
                    name: "StudentUserData",
                    type: "get",
                    method: "POST"
                },
                "getTeacherUserData": {
                    name: "TeacherUserData",
                    type: "get",
                    method: "POST"
                },
                "createDetailsForCourse": {
                    name: "DetailsForCourse",
                    type: "create",
                    method: "POST"
                },
                "getBatchSeq": {
                    name: "BatchSeq",
                    type: "get",
                    method: "POST"
                },
                "validateUserName": {
                    name: "UserName",
                    type: "validate",
                    method: "POST"
                },
		        "createBatchAndStudentBatch": {
                    name: "BatchAndStudentBatch",
                    type: "create",
                    method: "POST"
                },
		        "getMaxPrimaryKey": {
                    name: "MaxPrimaryKey",
                    type: "get",
                    method: "POST"
                },
		        "deleteBatchAndStudentBatch": {
                    name: "BatchAndStudentBatch",
                    type: "delete",
                    method: "POST"
                },
                "createMusicType": {
                    name: "MusicType",
                    type: "create",
                    method: "POST"
                },
                "createMusicCategory": {
                    name: "MusicCategory",
                    type: "create",
                    method: "POST"
                },
                "validateStudentId": {
                    name: "StudentId",
                    type: "validate",
                    method: "POST"
                },
                "getTrialStudentUser": {
                    name: "TrialStudentUser",
                    type: "get",
                    method: "POST"
                },
            },
            "ajaxTimeSlot": {
                "createTimeSlot": {
                    name: "TimeSlot",
                    type: "create",
                    method: "POST"
                }, "getTimeSlotForAdmin": {
                    name: "TimeSlotForAdmin",
                    type: "get",
                    method: "POST"
                }, "getTimeSlotForTeacher": {
                    name: "TimeSlotForTeacher",
                    type: "get",
                    method: "POST"
                }, "createTimeSlotForTeacher": {
                    name: "TimeSlotForTeacher",
                    type: "create",
                    method: "POST"
                }, "getTimeSlotForStudent": {
                    name: "TimeSlotForStudent",
                    type: "get",
                    method: "POST"
                }, "getTimeSlotCount": {
                    name: "TimeSlotCount",
                    type: "get",
                    method: "POST"
                }, "getBatchByStudentPk": {
                    name: "BatchByStudentPk",
                    type: "get",
                    method: "POST"
                }, "getStudentBatchByTecherPk": {
                    name: "StudentBatchByTecherPk",
                    type: "get",
                    method: "POST"
                }, "getTimeSlotDetailsForMobileStd": {
                    name: "TimeSlotDetailsForMobileStd",
                    type: "get",
                    method: "POST"
                }, "getTimeSlotDetailsForMobileTech": {
                    name: "TimeSlotDetailsForMobileTech",
                    type: "get",
                    method: "POST"
                }, "sendEmailOnNewSlotCreation": {
                    name: "EmailOnNewSlotCreation",
                    type: "send",
                    method: "POST"
                }, "getSlotLengthForCancellation": {
                    name: "SlotLengthForCancellation",
                    type: "get",
                    method: "POST"
                }, "checkCourseDeletionForStudent": {
                    name: "CourseDeletionForStudent",
                    type: "check",
                    method: "POST"
                }
            },
            "ajaxSalary": {
                "getTeacherForSalary": {
                    name: "TeacherForSalary",
                    type: "get",
                    method: "POST"
                }, "createSalary": {
                    name: "Salary",
                    type: "create",
                    method: "POST"
                }, "getTeacherForSalaryByMonth": {
                    name: "TeacherForSalaryByMonth",
                    type: "get",
                    method: "POST"
                }, "createSalaryByMonthForSlip": {
                    name: "SalaryByMonthForSlip",
                    type: "create",
                    method: "POST"
                }, "getSalaryByTeacherPk": {
                    name: "SalaryByTeacherPk",
                    type: "get",
                    method: "POST"
                }
            },
            "ajaxDashboard": {
                "getStudentRating": {
                    name: "StudentRating",
                    type: "get",
                    method: "POST"
                },
                "getStudentRatingByPk": {
                    name: "StudentRatingByPk",
                    type: "get",
                    method: "POST"
                },
                "getTeacherRating": {
                    name: "TeacherRating",
                    type: "get",
                    method: "POST"
                },
                "getTeacherRatingByPk": {
                    name: "TeacherRatingByPk",
                    type: "get",
                    method: "POST"
                }
            },
            "ajaxFeedback": {
                "getBatchListForStudent": {
                    name: "BatchListForStudent",
                    type: "get",
                    method: "POST"
                },
                "getSlotForStudent": {
                    name: "SlotForStudent",
                    type: "get",
                    method: "POST"
                },
                "getTeacherList": {
                    name: "TeacherList",
                    type: "get",
                    method: "POST"
                },
                "getAttrForRating": {
                    name: "AttrForRating",
                    type: "get",
                    method: "POST"
                },
                "createRatingsForTeacher": {
                    name: "RatingsForTeacher",
                    type: "create",
                    method: "POST"
                }
            },
            "ajaxReport": {
                "getStudentUser": {
                    name: "StudentUser",
                    type: "get",
                    method: "POST"
                },
                "getTeacherUser": {
                    name: "TeacherUser",
                    type: "get",
                    method: "POST"
                },
                "getStudentRatingByMusicTypeAndPk": {
                    name: "StudentRatingByMusicTypeAndPk",
                    type: "get",
                    method: "POST"
                },
                "getTeacherRatingByMusicTypeAndPk": {
                    name: "TeacherRatingByMusicTypeAndPk",
                    type: "get",
                    method: "POST"
                },
                "getSalaryDetails": {
                    name: "SalaryDetails",
                    type: "get",
                    method: "POST"
                },
                "getMusicSessionDetails": {
                    name: "MusicSessionDetails",
                    type: "get",
                    method: "POST"
                },
                "getPaymentDetails": {
                    name: "PaymentDetails",
                    type: "get",
                    method: "POST"
                },
                "getOverallTurnOver": {
                    name: "OverallTurnOver",
                    type: "get",
                    method: "POST"
                },
                "getOverallGrossProfit": {
                    name: "OverallGrossProfit",
                    type: "get",
                    method: "POST"
                },
                "getTurnOverByInstrument": {
                    name: "TurnOverByInstrument",
                    type: "get",
                    method: "POST"
                },
                "getTurnOverByStudent": {
                    name: "TurnOverByStudent",
                    type: "get",
                    method: "POST"
                },
                "getOverallRating": {
                    name: "OverallRating",
                    type: "get",
                    method: "POST"
                },
                "getnotconvertedlist": {
                    name: "NotConvertStudent",
                    type: "get",
                    method: "POST"
                },
                "getStudentDetailsForInvoice": {
                    name: "StudentDetailsForInvoice",
                    type: "get",
                    method: "POST"
                }

            },
            "ajaxInvoice": {
                "createInvoice": {
                    name: "Invoice",
                    type: "create",
                    method: "POST"
                },"getStudentForInvoice": {
                    name: "StudentForInvoice",
                    type: "get",
                    method: "POST"
                },"getInvoiceByStudentPk": {
                    name: "InvoiceByStudentPk",
                    type: "get",
                    method: "POST"
                },"getMaxInvoiceNo": {
                    name: "MaxInvoiceNo",
                    type: "get",
                    method: "POST"
                },"createFreeTextInvoiceByPk": {
                    name: "FreeTextInvoiceByPk",
                    type: "create",
                    method: "POST"
                },
                "getFreeTextInvoiceByPk": {
                    name: "FreeTextInvoiceByPk",
                    type: "get",
                    method: "POST"
                },
                "getInvoiceDateWiseDetails": {
                    name: "InvoiceDateWiseDetails",
                    type: "get",
                    method: "POST"
                }

            },
            "ajaxMessage": {
                "getMessageList": {
                    name: "MessageList",
                    type: "get",
                    method: "POST"
                },
                "getAvailableContactList": {
                    name: "AvailableContactList",
                    type: "get",
                    method: "POST"
                },
                "getContactListByUserKey": {
                    name: "ContactListByUserKey",
                    type: "get",
                    method: "POST"
                },
                "addContactList": {
                    name: "ContactList",
                    type: "add",
                    method: "POST"
                },
                "createUserMessage": {
                    name: "UserMessage",
                    type: "create",
                    method: "POST"
                },
                "getMessageByUser": {
                    name: "MessageByUser",
                    type: "get",
                    method: "POST"
                }
            },
            "ajaxPdf":{
                "createInvoicePdf":{
                    name: "InvoicePdf",
                    type: "create",
                    method: "POST"
                },"createSalaryPdf":{
                    name: "SalaryPdf",
                    type: "create",
                    method: "POST"
                },"createTimeTablePdf":{
                    name: "TimeTablePdf",
                    type: "create",
                    method: "POST"
                },
                "getStudentInvoicePdf": {
                    name: "GetStudentInvoicePdf",
                    type: "get",
                    method: "POST"
                },
                "expenseSheetPdf": {
                    name: "SheetPdf",
                    type: "expense",
                    method: "POST"
                }
            },
            "ajaxExpenses":{
                "createExpenses":{
                    name: "Expenses",
                    type: "create",
                    method: "POST"
                },
                "getRegisterExpenses": {
                    name: "RegisterExpenses",
                    type: "get",
                    method: "POST"
                },
                "getExpensesByMonth": {
                    name: "ExpensesByMonth",
                    type: "get",
                    method: "POST"
                },
                "createExpensesByMonth": {
                    name: "ExpensesByMonth",
                    type: "create",
                    method: "POST"
                },
                "getDataForExpenseSheet": {
                    name: "DataForExpenseSheet",
                    type: "get",
                    method: "POST"
                }
            }


        };

        angular.forEach(controllersDef, function (controllerDefVal, controllerDefKey) {

            angular.forEach(controllerDefVal, function (val, key) {
                var k = controllerDefKey;
                k = k.replace(k.charAt(0), k.charAt(0).toUpperCase());

                result[val.type + k + val.name] = function (modelData, successCallback, errorCallback) {

                    var clsKey = controllerDefKey,
                        methodKey = key,
                        value = val;

                    http.request(
                        {
                            url: $rootScope.baseAPIUrl + clsKey + "/" + methodKey,
                            method: value.method,
                            data: modelData
                        },
                        successCallback,
                        errorCallback
                    );
                }

            });

        });

        return result;

    }]);

})(window, window.document, window.jQuery, window.angular);