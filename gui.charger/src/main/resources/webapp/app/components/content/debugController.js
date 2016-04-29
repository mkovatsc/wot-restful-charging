angular.module('ChargerUI')
  .controller('debugController', function ($scope, $log, socketService, debugService) {
    $scope.debugmsgs = debugService.messages;

    var debugHandler = function(json) {
      var currentdate = new Date();
      var datetime = ('0' + currentdate.getHours()).slice(-2) + ":"
                      + ('0' + currentdate.getMinutes()).slice(-2) + ":"
                      + ('0' + currentdate.getSeconds()).slice(-2); // TODO format string

      debugService.pushMsg(datetime, json.message);
      $scope.$apply();
    };

    socketService.addHandler('debug', debugHandler);
  });
