app.controller('emulationController', function ($log, $rootScope, $scope, carService, emulationService) {

  // Create a new car
  var car = new carService({
    socketaddr: 'ws://localhost:8091'
  });
  $rootScope.car = car; // Assign it to $rootScope for global state access

  // Create a new emulator for the car
  var emulator = new emulationService({
    timeout: 500,
    speedup: 2.5,
    car: car
  });

  // Plug in / Unplug the car from charger
  $scope.togglePlug = function () {
    if (!car.plugged_in) {
      car.plugIn(emulator.config.speedup);
    } else {
      emulator.stop();
      car.unplug(emulator.config.speedup);
    }
  };

  // Stop the charging process
  $scope.stopCharging = function () {
    if (car.plugged_in) {
      emulator.stop();
      car.stopChargingProcess(emulator.config.speedup);
    }
  };

  // Start the emulation
  $scope.start = function () {
    emulator.start();
  };

  // Stop / Pause the emulation
  $scope.stop = function () {
    emulator.stop();
  };

  // Reset the emulator
  $scope.reset = function () {
    emulator.reset();
  };

  // Catch events and handle them
  $rootScope.$on('carStateChanged', function (event, args) {
    $log.info('Car state changed to: ' + car.state);
  });
});
