app.controller('statusController', function ($scope, socketService, statusService) {
  $scope.se = statusService.se;
  $scope.ev = statusService.ev;

  var statusHandler = function(json) {
    $scope.se.set(json.se);
    $scope.ev.set(json.ev);
    $scope.$apply();
  };

  //socketService.addHandler('STATUS', statusHandler);
});
