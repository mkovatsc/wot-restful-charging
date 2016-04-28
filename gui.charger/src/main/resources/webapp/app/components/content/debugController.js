angular.module('ChargerUI')
  .controller('debugController', function ($scope, $log, socketService) {
    $scope.socket = socketService.socket;
  });
