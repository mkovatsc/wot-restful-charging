app.controller('actionController', function ($scope, $rootScope, tickerService, socketService) {

  $scope.togglePlug = function() {
    $rootScope.car.plugged_in = !$rootScope.car.plugged_in;
    if (!$rootScope.car.plugged_in) {
      tickerService.reset();
      // TODO Doesn't refresh on frontend on reset
    }
    socketService.send('{"type" : "EVENT", "content" : { "pluggedIn" : ' + $rootScope.car.plugged_in + '} }');
  };

  var registerHandler = function(json) {
    $rootScope.car.uuid = json.uuid;
    $scope.$apply();
  };

  socketService.addHandler('REGISTER', registerHandler);

});
