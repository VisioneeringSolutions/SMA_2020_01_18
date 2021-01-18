/**
 * Created by Kundan Kumar on 25-Apr-19.
 */
(function (window, document, $, angular) {

    angular.module('registrationApp').factory("registrationModel", ["registrationSvcs", function (registrationSvcs) {

        var InstanceGenerator = new function (){
            var InstanceFns = {
                addStudent:function(){
                    this.className = "EOStudentUser";
                    this.firstName = "";
                    this.firstNameJap = "";
                    this.registrationAmount = "";
                    this.lastName = "";
                    this.lastNameJap = "";
                    this.gender = "Male";
                    this.enrollmentDate = "";
                    this.dateOfBirth = "";
                    this.email = "";
                    this.phone = "";
                    this.studentId = "";
                    this.musicPk = "";
                    this.userName = "";
                    this.isVisible = true;
                },
                updateStudent:function(){
                    this.className = "EOStudentUser";
                    this.firstName = "";
                    this.firstNameJap = "";
                    this.lastNameJap = "";
                    this.lastName = "";
                    this.gender = "Male";
                    this.enrollmentDate = "";
                    this.email = "";
                    this.phone = "";
                    this.registrationAmount = "";
                    this.addressLine1 = "";
                    this.addressLine2 = "";
                    this.dateOfBirth = "";
                    this.illness = "";
                    this.prescribedMedication = "";
                    this.alternateEmail = "";
                    this.alternatePhone = "";
                    this.guardianTitle = "";
                    this.guardianFirstName = "";
                    this.guardianLastName = "";
                    this.relationship = "";
                    this.guardianEmail = "";
                    this.guardianPhone = "";
                    this.guardianAddressLine1 = "";
                    this.guardianAddressLine2 = "";
                    this.studentId = "";
                    this.musicPk = "";
                },
                addTeacher:function(){
                    this.className = "EOTeacherUser";
                    this.title="";
                    this.firstNameJap = "";
                    this.lastNameJap = "";
                    this.firstName = "";
                    this.lastName = "";
                    this.gender = "Male";
                    this.joiningDate = "";
                    this.email = "";
                    this.phone = "";
                    this.teacherId = "";
                    this.userName = "";
                    this.colorCode = "";
                    this.salaryType = "Per session";
                },
                updateTeacher:function(){
                    this.className = "EOTeacherUser";
                    this.title = "";
                    this.firstNameJap = "";
                    this.lastNameJap = "";
                    this.firstName = "";
                    this.lastName = "";
                    this.gender = "Male";
                    this.qualification = "";
                    this.experience = "";
                    this.profile = "";
                    this.awards = "";
                    this.email = "";
                    this.phone = "";
                    this.alternateEmail = "";
                    this.alternatePhone = "";
                    this.addressLine1 = "";
                    this.addressLine2 = "";
                    this.joiningDate = "";
                    this.lkMusicType = "";
                    this.lkCategoryType = "";
                    this.teacherId = "";
                    this.musicCategoryPk = "";
                    this.colorCode = "";
                    this.salaryType = "";
                },
                course:function(){
                    this.className = "EOCourse";
                    this.courseCode = "";
                    this.courseName = "";
                    this.details = "";
                    this.url = "";
                    this.startDate = "";
                    this.session = "";
                    this.lkClassDuration = "";
                    this.feeType = "";
                    this.fees = "";
                    this.lkMusicType = "";
                    this.lkCategoryType = "";
                },
                musicRoom:function(){
                    this.className = "EOMusicRoom";
                    this.roomName = "";
                    this.roomId = "";
                    this.maxStudent = "";
                    this.details = "";
                },
                batch:function(){
                    this.className = "EOBatch";
                    this.batchName = "";
                    this.startDate = "";
                    this.status = "";
                    this.eoCourses = "";
                    this.batchId = "";
                    this.batchType = "";
                },
                studentBatch:function(){
                    this.className = "EOStudentBatch";
                    this.studentsPk = "";
                    this.eoBatch = "";
                    this.batchType = "";
                    this.eoStudentBatchMapping = [];
                },
                musicType:function(){
                    this.className = "LKMusicType";
                    this.musicType = "";
                }
            };
            this.create = function (type) {
                if (InstanceFns[type]) {
                    return new InstanceFns[type]();
                }
            };
        }();
        return {
            getInstance: InstanceGenerator.create
        }
    }]);

})(window, window.document, window.jQuery, window.angular);
