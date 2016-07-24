app.controller('emulationController', function ($log, $rootScope, $scope, chargerService, debugService) {

  // Create a new charger
  var charger = new chargerService({
    socketaddr: 'ws://localhost:8081'
  });
  $rootScope.charger = charger;

  // Create and add debug message handler to charger
  var debugHandler = function(json) {
    var currentdate = new Date();
    var datetime = ('0' + currentdate.getHours()).slice(-2) + ":"
                    + ('0' + currentdate.getMinutes()).slice(-2) + ":"
                    + ('0' + currentdate.getSeconds()).slice(-2); // TODO format string

    debugService.pushMsg(datetime, json.message);
    $rootScope.$apply();
  };

  charger.config.socket.addHandler('DEBUG', debugHandler);
  $rootScope.debugmsgs = debugService.messages;

});
