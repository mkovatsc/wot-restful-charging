angular.module('ChargerUI')
  .controller('statusController', function ($scope, $log, socketService) {
    var statusHandler = function(json) {
      $scope.se = json.se;
      $scope.ev = json.ev;
      $scope.$apply();
    };

    socketService.addHandler('status', statusHandler);
  });
