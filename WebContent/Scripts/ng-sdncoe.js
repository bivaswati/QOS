/**
 * AngularJS Script for index1.html
 */
(function() {
    var app = angular.module("sdnCoeApp", []);
    app.directive('loading', function() {
    return {
            restrict : 'E',
            replace:true,
            template: '<div class="loading"><img src="img/loader.gif" width="50" height="50" />Please Wait ! Looking up for the best server.</div>',
            link : function(scope, element, attr) {
                scope.$watch('loading', function (val) {
                      if (val){
                          $(element).show();
                      } else {
                          $(element).hide();
                      }
                  });
            }
        };

    });

    // This is my main controller
    app.controller("mySdnController", mySdnController);
    var placeHolder = {
        welcomeMessage : "SDN Video QOS",
        welcomeTag : "A Wipro SDN COE Initiative!",
    };

    function mySdnController($scope, $http, $window) {

        $scope.initVar = placeHolder;
        $scope.l_status = "Default User";
        $scope.player=document.getElementById("video1");

        var id = document.getElementById('link_status');
        id.style.display = 'none';
        // fetching the channel names and images to the view
        var success1 = function(response) {
            $scope.channelLstNames = response.data.channelListNames;
        };
        var onError1 = function(reason) {
            $scope.error1 = "Cannot fetch images from the files!";
        };
        $http.get('resources/ChannelNames.properties').then(success1, onError1);

        var success2 = function(response) {
            $scope.localWebServiceUrl = response.data.localWebServiceUrl;
            $scope.homePage = response.data.homePage;
            $scope.remoteWebServiceURL = response.data.remoteWebServiceURL;
        };
        var onError2 = function(reason) {
            $scope.error2 = "Cannot fetch data from the Config file Please check the file!";
        };

        $http.get('resources/Config.properties').then(success2, onError2);

        var success = function(response) {
            $scope.channelList = response.data.channelList;
        };
        var onError = function(reason) {
            $scope.error = "Cannot fetch data from the channelServe file Please check the file!";
        };

        $http.get('resources/ChannelServer.properties').then(success, onError);
        $scope.createchannelArray = function(chnlName) {
            $scope.loading = true;
            var container=document.getElementById("content");
            container.style.display = 'none';

            for (var i = 0; i < $scope.channelList.length; i++) {
                if (angular.equals(chnlName, $scope.channelList[i].channelName)) {
                    $scope.channelDataName = $scope.channelList[i].channelName;
                    $scope.channelDataURL = $scope.channelList[i].urls;
                    break;
                }
            }
            var dataObj = {
                "chnlName" : $scope.channelDataName,
                "serverURL" : $scope.channelDataURL,
                "webServiceURL" : $scope.remoteWebServiceURL
            }
            var addRequest = $http({
                method : 'POST',
                url : $scope.localWebServiceUrl,
                data : dataObj,
                headers : {
                    'Content-Type' : 'application/json; charset=UTF-8'
                }
            });

            addRequest.success(function(serverResponse, status, headers, config) {
                        $scope.connectToServer = serverResponse.toString();
                        if($scope.connectToServer.length != 0){
                        if(angular.equals($scope.connectToServer, "Please check the Remote Webservice")){
                            alert("Video URL Not Found Please check the remote connection!!");
                            $scope.loading = false;
                            $scope.redirect2();
                        } else {
                            $scope.loading = false;
                            $scope.launchPlayer($scope.connectToServer);
                        }

                        } else{
                            $scope.loading = false;
                            alert("Video URL Not Found!!");
                            $scope.redirect2();
                        }
                    });
            addRequest.error(function(serverResponse, status) {
                alert("Please check your local webservice - Status message: "
                        + status);
                $window.location.href = $scope.homePage;
            });

        };
        $scope.launchPlayer = function(connectToServer) {
            var plyr=document.getElementById("player");
            plyr.style.display = 'block';
            id.style.display = 'block';
            id.innerHTML = $scope.l_status ;
            var container=document.getElementById("content");
            container.style.display = 'none';
            $scope.player.src = connectToServer;
            $scope.player.load();
            $scope.player.play();
            $scope.getNotification();
        };

        $scope.redirect2 = function() {
        $scope.l_status = "Default User";
        $window.location.href = $scope.homePage;
        };


        $scope.getNotification = function() {
            var eventSource = new EventSource("EventPollServlet");
            eventSource.onmessage = function(event) {
                if(event.data == 'LINK_DOWN') {
                    document.getElementById('link_status').innerHTML = "Link Down" ;
                }
                if(event.data == 'LINK_RESTORED') {
                    document.getElementById('link_status').innerHTML = "Link Restored";
                    var myTimer = setInterval(
                                    function() {
                                    document.getElementById('link_status').innerHTML = $scope.l_status;
                                    clearInterval(myTimer);
                                    }
                                    ,4000);
                    $scope.player.load();
                    $scope.player.play();
                }
                if(event.data == 'CONGESTION_OCCURED') {
                    document.getElementById('link_status').innerHTML = "Congestion Occured" ;
                }
                if(event.data == 'CONGESTION_AVOIDED') {
                    document.getElementById('link_status').innerHTML = "Rerouted in NonCongested Path" ;
                    var myTimer_c = setInterval(
                    function() {
                        document.getElementById('link_status').innerHTML = $scope.l_status;
                        clearInterval(myTimer_c);
                    }
                    ,4000);
                    $scope.player.play();
                }
            };
        };

        $scope.sdnvideo = function(videoServer) {
            $scope.l_status = "Priority User";
            document.getElementById('link_status').innerHTML = $scope.l_status;

             var serverInput = {
                "chnlName" : $scope.channelDataName,
                "serverURL" : videoServer,
                "webServiceURL" : $scope.remoteWebServiceURL
             }
             var addRequest = $http({
                method : 'POST',
                url :"rest/sdncoe/getSdnVideo/",
                data : serverInput,
                headers : {
                   'Content-Type' : 'application/json'
                }
             });
             addRequest.success(function(serverResponse, status, headers, config) {

         });
         addRequest.error(function(serverResponse, status) {
         alert("Please check your local webservice - Status message: "+ status);
         });
         };

        $scope.onExit = function() {

            var dataObj = {
                    "remoteWebServiceURLDeleteIntent" : "http://10.0.0.100:8181/onos/qos/sdnc/deleteSession"
            }
            $http({
                method: 'POST',
                url: 'rest/sdncoe/deleteSession',
                data : dataObj,
                headers : {
                    'Content-Type' : 'application/json'
                }
            }).then(function successCallback(response) {


            }, function errorCallback(response) {
                alert('failure');
            });
            return('Window is about to close...');
                };

            $window.onbeforeunload =  $scope.onExit;
    };

}());