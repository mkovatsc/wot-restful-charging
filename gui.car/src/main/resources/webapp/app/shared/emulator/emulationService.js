app.factory("emulationService", function ($log, $rootScope, stateMachine) {

  var instance = {};

  instance.update = function() {
    stateMachine.getCurrentState().then(function(res) {
      $rootScope.currentState = res;
      $log.info("Current state: " + res);
    });
    stateMachine.available().then(function(res) {
      $rootScope.transitions = res;
      $rootScope.transition = res[0]; // TODO Doesn't work every time
    });
  }

  instance.next = function(state) {
    stateMachine.send(state);
    instance.update();
  }

  instance.reset = function() {
    stateMachine.initialize();
    instance.update();
  }

  instance.update();

  return instance;

});
