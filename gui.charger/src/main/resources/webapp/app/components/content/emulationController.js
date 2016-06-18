app.controller('emulationController', function ($log, $rootScope, $scope, chargerService) {

  // Create a new charger
  var charger = new chargerService({
    socketaddr: 'ws://localhost:8081'
  });
  $rootScope.charger = charger;
});
