app.controller('actionController', function ($scope, $rootScope, tickerService, socketService, emulationService) {

  $scope.togglePlug = function() {
    $rootScope.car.plugged_in = !$rootScope.car.plugged_in;

    socketService.send('{"type" : "EVENT", "content" : { "pluggedIn" : ' + $rootScope.car.plugged_in + '}}');

    if (!$rootScope.car.plugged_in) {
      tickerService.reset(); // TODO Doesn't refresh on frontend on reset
      emulationService.reset();
    } else {
      socketService.send('{"type" : "ACTION", "content" : { "notify" : "charger"}}');
      emulationService.next('pluggedIn');
    }

  };

  var registerHandler = function(json) {
    $rootScope.car.uuid = json.uuid;
    $scope.$apply();
  };
  socketService.addHandler('REGISTER', registerHandler);

  var tickAction = function() {
    socketService.send('{"type":"STATUS","content":{"stateOfCharge":' + Math.floor($rootScope.car.battery.soc * 100) + ',"maximumVoltageLimit":400,"maximumCurrentLimit":' + $rootScope.car.charging.rate.DC[0] + ',"targetVoltage":1,"targetCurrent":1,"chargingComplete":false}}');
  }
  tickerService.addCallback(tickAction);

});
