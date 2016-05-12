app.controller('actionController', function ($scope, $rootScope, tickerService) {

  $scope.togglePlug = function() {
    $rootScope.car.plugged_in = !$rootScope.car.plugged_in;
    if (!$rootScope.car.plugged_in) {
      tickerService.reset();
      // TODO Doesn't refresh on frontend on reset
    }
  };

});
