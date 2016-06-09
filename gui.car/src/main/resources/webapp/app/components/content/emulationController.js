app.controller('emulationController', function ($log, $rootScope, $scope, carService, emulationService) {

  // Create a new car
  var car = new carService({
    socketaddr: 'ws://localhost:8091'
  });
  $rootScope.car = car; // Assign it to $rootScope for global state access

  // Create a new emulator for the car
  var emulator = new emulationService({
    timeout: 500,
    speedup: 1.0,
    car: car
  });

  // Define some $scope functions
  $scope.start = function () {
    emulator.start();
  };

  // Catch events and handle them
  $rootScope.$on('carStateChanged', function (event) {
    $scope.currentState = car.state;
    $scope.$apply();

    $log.info('Car state changed to: ' + car.state);
  });
});
