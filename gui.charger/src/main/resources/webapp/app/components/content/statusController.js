app.controller('statusController', function ($scope, socketService, statusService) {
  $scope.se = statusService.se;
  $scope.ev = statusService.ev;

  var statusHandler = function(json) {
    statusService.saveSE(json.se);
    statusService.saveEV(json.ev);
    $scope.$apply();
  };

  socketService.addHandler('status', statusHandler);
});
