app.controller('stateController', function ($scope, statusService) {

  // TODO Live update not working
  $scope.isActive = function (state) {
    return (statusService.se.data != null) && (statusService.se.data.currentState === state);
  };
});
