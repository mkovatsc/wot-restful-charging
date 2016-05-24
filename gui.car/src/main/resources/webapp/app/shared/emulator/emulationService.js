app.factory("emulationService", function ($log, $rootScope, stateMachine) {

  var instance = {};

  instance.duration = 0; // Simulated duration of current state (ms)
  instance.defaultSequence = { // TODO Ordered?!
    'pluggedIn'                     : 0,
    'chargeParameterDiscoveryDone'  : 30,
    'cableCheckDone'                : 23000,
    'preChargeDone'                 : 3800,
    'powerDeliveryDone'             : 600,
    'currentDemandDone'             : 100,
    'powerDeliveryDoneW'            : 2200,
    'weldingDetectionDone'          : 0
  };

  var update = function() {
    stateMachine.getCurrentState().then(function(res) {
      $rootScope.currentState = res;
      $log.info("Current state: " + res + " (" + instance.duration + "ms)");
    });
    stateMachine.available().then(function(res) {
      $rootScope.transitions = res;
      $rootScope.transition = res[0]; // TODO Doesn't work every time
    });
  }

  instance.next = function(state) {
    stateMachine.send(state);
    update();
  }

  instance.reset = function() {
    stateMachine.initialize();
    update();
  }

  update();

  return instance;

});
