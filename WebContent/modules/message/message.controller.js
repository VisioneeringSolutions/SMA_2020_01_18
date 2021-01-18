/**
 * Created by dell on 17-06-19.
 */

(function (window, document, $, angular) {

    var messageApp = angular.module('messageApp');

    messageApp.controller("messageCtrl", ["$scope", "$state", "messageModel", "$rootScope", "localStorage", "messageData", "messageSvcs", "notifySvcs","$interval", function ($scope, $state, messageModel, $rootScope, localStorage, messageData, messageSvcs, notifySvcs,$interval) {

        var vm = this;
        vm.baseImgUrl = window.sInstances.origin + "/ImgData/SMA/";
        vm.userKey = localStorage.get("userKey");
        vm.role = localStorage.get("role");
        vm.date = moment(new Date()).format("YYYY-MM-DD") + "";

        vm.showContactBlock = function(){
            if(document.getElementById("add-contact").style.display == "block"){
                document.getElementById("add-contact").style.display = "none";
            }else{
                document.getElementById("add-contact").style.display = "block";
            }

        };

        vm.showAddedContact = function(){
            document.getElementById("message-header-mb").style.height = "100px";


            if(document.getElementById("added-contact-list-mb").style.display == "block"){
                document.getElementById("added-contact-list-mb").style.display = "none";
            }else{
                document.getElementById("added-contact-list-mb").style.display = "block";
            }

            if(document.getElementById("chat-box-mb").style.display == "block"){
                document.getElementById("chat-box-mb").style.display = "none";
            }else{
                document.getElementById("chat-box-mb").style.display = "block";
            }

            if(document.getElementById("chat-box-send-mb").style.display == "block"){
                document.getElementById("chat-box-send-mb").style.display = "none";
            }else{
                document.getElementById("chat-box-send-mb").style.display = "block";
            }

            if(document.getElementById("search-contact-mb").style.display == "block"){
                document.getElementById("search-contact-mb").style.display = "none";
            }else{
                document.getElementById("search-contact-mb").style.display = "block";
            }
            if(document.getElementById("add-contact-mb").style.display == "block"){
                document.getElementById("add-contact-mb").style.display = "none";
            }else{
                document.getElementById("add-contact-mb").style.display = "block";
            }

        };

        vm.showChatBox = function(instance, index){

            document.getElementById("message-header-mb").style.height = "65px";
            if(document.getElementById("chat-box-mb").style.display == '' &&
                document.getElementById("chat-box-mb").style.display == '' &&
                document.getElementById("chat-box-send-mb").style.display == ''){

                document.getElementById("added-contact-list-mb").style.display = "none";
                document.getElementById("chat-box-mb").style.display = "block";
                document.getElementById("chat-box-send-mb").style.display = "block";
                document.getElementById("search-contact-mb").style.display = "none";
                document.getElementById("add-contact-mb").style.display = "none";

            }
            else{
                if(document.getElementById("added-contact-list-mb").style.display == "block"){
                    document.getElementById("added-contact-list-mb").style.display = "none";
                }else{
                    document.getElementById("added-contact-list-mb").style.display = "block";
                }


                if(document.getElementById("chat-box-mb").style.display == "block"){
                    document.getElementById("chat-box-mb").style.display = "none";
                }else{
                    document.getElementById("chat-box-mb").style.display = "block";
                }

                if(document.getElementById("chat-box-send-mb").style.display == "block"){
                    document.getElementById("chat-box-send-mb").style.display = "none";
                }else{
                    document.getElementById("chat-box-send-mb").style.display = "block";
                }

                if(document.getElementById("search-contact-mb").style.display == "block"){
                    document.getElementById("search-contact-mb").style.display = "none";
                }else{
                    document.getElementById("search-contact-mb").style.display = "block";
                }

                if(document.getElementById("add-contact-mb").style.display == "block"){
                    document.getElementById("add-contact-mb").style.display = "none";
                }else{
                    document.getElementById("add-contact-mb").style.display = "block";
                }
            }

            vm.getMessage(instance, index);
        };
        vm.selectedIndex = 0;

        console.log("messageData :: ", messageData);
        vm.availableContactList = messageData.AvailableContactList;
        vm.contactList = messageData.ContactList;
        vm.receiverData = messageData.ContactList[0];

        vm.getMessage = function (instance, index) {
            console.log("fn clled");

            vm.messageData = '';
            vm.selectedIndex = index;
            vm.receiverData = instance;
            $rootScope.messageCount -= instance.unreadCount;
            instance.unreadCount = 0;

            var reqMap = {
                "messageID": vm.receiverData['messageID'],
                "userKey": localStorage.get("userKey")+""
            };
            messageSvcs.getMessageByUser(reqMap, function (res) {
                vm.messageArr = res.data;
                /*console.log(angular.element(document.getElementById('pageTop')).offsetTop);
                 window.scrollTo(0, angular.element(document.getElementById('pageTop')).offsetEnd);*/
            }, function (err) {

            });

        };

        vm.getFirstUserMessages = function () {
            if(vm.receiverData){
                vm.getMessage(vm.receiverData, 0);
            }

        }();

        vm.myConfig = {
            create: true,
            valueField: 'phone',
            labelField: 'fullName',
            plugins: ['remove_button'],
            delimiter: '|',
            placeholder: 'Add:',
            searchField: ['fullName', 'phone'],
            render: {
                item: function (item, escape) {
                    return '<div>' +
                        (item.fullName ? '<span class="fullName">' + escape(item.fullName) + '</span>' : '') +
                        '</div>';
                },
                option: function (item, escape) {
                    return '<div>' +
                        '<span class="fullName">' + escape(item.fullName) + '</span>' + '</br>' +
                        (item.phone ? '<span class="phone">' + escape(item.phone) + '</span>' : '') +
                        '</div>';
                }
            }
        };
        vm.contactArr = [];
        function getContactList(callback) {
            for (var k in vm.availableContactList) {
                console.log(vm.availableContactList[k].phone);
                console.log(vm.contactList.includes(vm.availableContactList[k].phone));
                if (vm.contacts.includes(vm.availableContactList[k].phone) == true) {
                    vm.contactArr.push({
                        "userKey": localStorage.get("userKey"),
                        "role": localStorage.get("role"),
                        "receiver": vm.availableContactList[k].primaryKey + "",
                        "receiverRole": vm.availableContactList[k].role
                    });
                }
            }
            callback(vm.contactArr);
        }

        vm.addContact = function () {
            getContactList(function (response) {
                console.log("response :: ", response);
                if (response.length > 0) {
                    var reqMap = {
                        "contactList": response
                    };
                    messageSvcs.addContactList(reqMap, function (res) {
                        if (res.data == 'Success') {
                            notifySvcs.success({
                                content: "Contact added successfully."
                            });
                            $state.reload();
                        }
                    }, function (err) {
                        notifySvcs.error({
                            content: "Error Occured !!"
                        });
                    });
                } else {
                    notifySvcs.info({
                        content: "Select atleast one contact."
                    });
                }

            });
        };

        var inputMessage = document.getElementById("inputMessage");
        inputMessage.addEventListener("keyup", function (event) {
            // Number 13 is the "Enter" key on the keyboard
            if (event.keyCode === 13) {
                event.preventDefault();
                document.getElementById("sendButton").click();
            }
        });

        vm.sendMessage = function () {
            console.log("call : ", new Date().getTime());
            if (vm.messageData != undefined || vm.messageData != null || vm.messageData != '') {
                var reqMap = {
                    "className": "EOMessageHistory",
                    "message": vm.messageData,
                    "sender": localStorage.get("userKey"),
                    "senderClassName": localStorage.get("role"),
                    "receiver": vm.receiverData.receiver,
                    "receiverClassName": vm.receiverData.receiverClassName,
                    "messageID": vm.receiverData.messageID,
                    "eoMessage": vm.receiverData.eoMessage
                };
                console.log("reqMap :: ", reqMap);
                messageSvcs.createUserMessage(reqMap, function (res) {
                    if (res.data == 'Success') {
                        vm.messageData = '';
                        var reqMap = {
                            "messageID": vm.receiverData['messageID'],
                            "userKey": localStorage.get("userKey")+""
                        };
                        messageSvcs.getMessageByUser(reqMap, function (res) {
                            vm.messageArr = res.data;
                        }, function (err) {

                        });

                        var reqMap2 = {
                            userKey: localStorage.get("userKey"),
                            role: localStorage.get("role")
                        };
                        messageSvcs.getContactListByUserKey(reqMap2, function (res) {
                            vm.contactList = res.data;
                        });
                    }
                }, function (err) {

                });
            } else {
                notifySvcs.info({
                    content: "Write something into message box."
                });
            }
        };


        function getData(callback) {
            var reqMap2 = {
                userKey: localStorage.get("userKey"),
                role: localStorage.get("role")
            };
            messageSvcs.getContactListByUserKey(reqMap2, function (res) {
                vm.contactList = res.data;
                callback("Success");
            });

        }

        vm.autoRefresh = function () {
            var tmpMessage = vm.messageData;
            var reqMap = {
                "messageID": vm.receiverData['messageID'],
                "userKey": localStorage.get("userKey")+""
            };
            messageSvcs.getMessageByUser(reqMap, function (res) {
                vm.messageArr = res.data;
                getData(function (res) {
                    vm.messageData = tmpMessage;
                });
            }, function (err) {

            });

        };

        var promise;


        $scope.start = function() {
            // stops any running interval to avoid two intervals running at the same time
            $scope.stop();

            // store the interval promise
           /* window.setInterval(function() {
                var elem = document.getElementById('chat-box-mb');
                elem.scrollTop = elem.scrollHeight;
            }, 5000);*/
            promise = $interval(vm.autoRefresh, 30000);
        };

        $scope.stop = function() {
            $interval.cancel(promise);
        };

        // starting the interval by default
        $scope.start();



        $scope.$on('$destroy', function() {
            $scope.stop();
        });





    }]);

})(window, window.document, window.jQuery, window.angular);
