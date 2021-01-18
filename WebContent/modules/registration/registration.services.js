/**
 * Created by Kundan on 25-Apr-19.
 */


(function (window, document, $, angular) {

    angular.module('registrationApp').factory("registrationSvcs", ["http", "$rootScope", function (http, $rootScope) {

        var createUser = function (model, successCallback, errorCallback) {
            http.createAjaxRegistrationUser(model, successCallback, errorCallback);
        };
        var getStudentUser = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationStudentUser(model, successCallback, errorCallback);
        };
        var getStudentsUserByPk = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationStudentsUserByPk(model, successCallback, errorCallback);
        };
        var getPhoneDetailsByUserPk = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationPhoneDetailsByUserPk(model, successCallback, errorCallback);
        };
        var updateDeleteReasonByStdPk = function (model, successCallback, errorCallback) {
            http.updateAjaxRegistrationDeleteReasonByStdPk(model, successCallback, errorCallback);
        };
        var getTeacherUser = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationTeacherUser(model, successCallback, errorCallback);
        };
        var getTeacherUserByPk = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationTeacherUserByPk(model, successCallback, errorCallback);
        };
        var createCourse = function (model, successCallback, errorCallback) {
            http.createAjaxRegistrationCourse(model, successCallback, errorCallback);
        };
        var getCourseList = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationCourseList(model, successCallback, errorCallback);
        };
        var getCourseByPk = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationCourseByPk(model, successCallback, errorCallback);
        };
        var deleteCourseByPk = function (model, successCallback, errorCallback) {
            http.deleteAjaxRegistrationCourseByPk(model, successCallback, errorCallback);
        };
        var getMusicRoomList = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationMusicRoomList(model, successCallback, errorCallback);
        };
        var getMusicRoomByPk = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationMusicRoomByPk(model, successCallback, errorCallback);
        };
        var deleteMusicRoomByPk = function (model, successCallback, errorCallback) {
            http.deleteAjaxRegistrationMusicRoomByPk(model, successCallback, errorCallback);
        };
        var getBatchList = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationBatchList(model, successCallback, errorCallback);
        };
        var getBatchByPk = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationBatchByPk(model, successCallback, errorCallback);
        };
        var deleteBatchByPk = function (model, successCallback, errorCallback) {
            http.deleteAjaxRegistrationBatchByPk(model, successCallback, errorCallback);
        };
        var getBatchesForStudent = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationBatchesForStudent(model, successCallback, errorCallback);
        };
        var createBatchesForStudent = function (model, successCallback, errorCallback) {
            http.createAjaxRegistrationBatchesForStudent(model, successCallback, errorCallback);
        };
        var getMusicAndCategoryType = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationMusicAndCategoryType(model, successCallback, errorCallback);
        };
        var createBatchAndStudentBatch = function (model, successCallback, errorCallback) {
            http.createAjaxRegistrationBatchAndStudentBatch(model, successCallback, errorCallback);
        };
        var validateUserName = function (model, successCallback, errorCallback) {
            http.validateAjaxRegistrationUserName(model, successCallback, errorCallback);
        };
        var getMaxPrimaryKey = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationMaxPrimaryKey(model, successCallback, errorCallback);
        };
        var getColorCodeForTeacher = function (model, successCallback, errorCallback) {
            http.getAjaxColorCodeForTeacher(model, successCallback, errorCallback);
        };
        var checkCourseDeletionForStudent = function (model, successCallback, errorCallback) {
            http.checkAjaxTimeSlotCourseDeletionForStudent(model, successCallback, errorCallback);
        };
        var deleteBatchAndStudentBatch = function (model, successCallback, errorCallback) {
            http.deleteAjaxRegistrationBatchAndStudentBatch(model, successCallback, errorCallback);
        };
        var getMusicTypeList = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationMusicTypeList(model, successCallback, errorCallback);
        };
        var createMusicType = function (model, successCallback, errorCallback) {
            http.createAjaxRegistrationMusicType(model, successCallback, errorCallback);
        };
        var getMusicTypeByPk = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationMusicTypeByPk(model, successCallback, errorCallback);
        };
        var deleteMusicTypeByPk = function (model, successCallback, errorCallback) {
            http.deleteAjaxRegistrationMusicTypeByPk(model, successCallback, errorCallback);
        };
        var getMusicCategoryList = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationMusicCategoryList(model, successCallback, errorCallback);
        };
        var createMusicCategory = function (model, successCallback, errorCallback) {
            http.createAjaxRegistrationMusicCategory(model, successCallback, errorCallback);
        };
        var getMusicCategoryByPk = function (model, successCallback, errorCallback) {
            http.getAjaxRegistrationMusicCategoryByPk(model, successCallback, errorCallback);
        };
        var deleteMusicCategoryByPk = function (model, successCallback, errorCallback) {
            http.deleteAjaxRegistrationMusicCategoryByPk(model, successCallback, errorCallback);
        };
        var resetPasswordByAdmin = function (model, successCallback, errorCallback) {
            http.resetAuthPasswordByAdmin(model, successCallback, errorCallback);
        };

        return {
            createUser : createUser,
            getStudentUser : getStudentUser,
            getStudentsUserByPk : getStudentsUserByPk,
            getPhoneDetailsByUserPk : getPhoneDetailsByUserPk,
            updateDeleteReasonByStdPk : updateDeleteReasonByStdPk,
            getTeacherUser : getTeacherUser,
            getTeacherUserByPk :getTeacherUserByPk,
            createCourse : createCourse,
            getCourseList : getCourseList,
            getCourseByPk : getCourseByPk,
            deleteCourseByPk : deleteCourseByPk,
            getMusicRoomList : getMusicRoomList,
            getMusicRoomByPk : getMusicRoomByPk,
            deleteMusicRoomByPk : deleteMusicRoomByPk,
            getBatchList : getBatchList,
            getBatchByPk : getBatchByPk,
            deleteBatchByPk : deleteBatchByPk,
            getBatchesForStudent : getBatchesForStudent,
            createBatchesForStudent : createBatchesForStudent,
            getMusicAndCategoryType : getMusicAndCategoryType,
            createBatchAndStudentBatch : createBatchAndStudentBatch,
            validateUserName : validateUserName,
            getMaxPrimaryKey : getMaxPrimaryKey,
            getColorCodeForTeacher : getColorCodeForTeacher,
            checkCourseDeletionForStudent : checkCourseDeletionForStudent,
            deleteBatchAndStudentBatch: deleteBatchAndStudentBatch,
            getMusicTypeList: getMusicTypeList,
            createMusicType: createMusicType,
            getMusicTypeByPk : getMusicTypeByPk,
            deleteMusicTypeByPk : deleteMusicTypeByPk,
            getMusicCategoryList : getMusicCategoryList,
            createMusicCategory: createMusicCategory,
            getMusicCategoryByPk : getMusicCategoryByPk,
            deleteMusicCategoryByPk : deleteMusicCategoryByPk,
            resetPasswordByAdmin : resetPasswordByAdmin,
        }

    }]);

})(window, window.document, window.jQuery, window.angular);