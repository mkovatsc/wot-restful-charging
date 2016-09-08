app.controller('emulationController', function ($log, $rootScope, $scope, $mdDialog, carService, emulationService) {

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

  // Pass the status of the emulator
  $scope.emulatorRunning = function () {
    return emulator.isRunning;
  };

  // Catch events and handle them
  $rootScope.$on('carStateChanged', function (event, args) {
    $log.info('Car state changed to: ' + car.state);
  });

  // TODO
  $scope.showLastResponse = function(event) {
    car.config.socket.addHandler('LASTRESPONSE', function (data) {
      if (data !== null) {
        $mdDialog.show({
          template:
          '<md-dialog layout="row" layout-align="center center" class="md-padding" aria-label="Last CoAP response">' +
          '  <md-dialog-content>' +
          '    <pre>{{ response }}</pre>' +
          '  </md-dialog-content>' +
          '</md-dialog>',
          locals: {
            response: angular.toJson(data.response, true)
          },
          controller: ['$scope', 'response', function($scope, response) {
            $scope.response = response;
          }],
          parent: angular.element(document.body),
          targetEvent: event,
          clickOutsideToClose:true
        });
      }
      car.config.socket.clearHandler('LASTRESPONSE');
    });
    car.getLastResponse();
  };

  // TODO
  $scope.follow = function (href) {
    if (car.plugged_in) {
      car.follow(href);
    }
  };

  // TODO
  $scope.submitForm = function (href, method, accepts) {
    if (car.plugged_in) {
      car.submitForm(href, method, accepts);
    }
  };

  // TODO
  $scope.observe = function (href) {
    if (car.plugged_in) {
      car.observe(href);
    }
  };

  // TODO
  $scope.cancelObserve = function (href) {
    if (car.plugged_in) {
      car.cancelObserve(href);
    }
  };

});
