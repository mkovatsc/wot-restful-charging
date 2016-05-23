var app = angular.module('CarUI', ['ngMaterial', 'ngRoute', 'FSM']);

app.config(['stateMachineProvider', function(stateMachineProvider) { // TODO Put somewhere else?

  stateMachineProvider.config({
    init : {
      transitions : {
        pluggedIn : 'chargeParameterDiscovery'
      }
    },
    chargeParameterDiscovery : {
      transitions : {
        chargeParameterDiscovery : 'chargeParameterDiscovery',
        chargeParameterDiscoveryDone : 'cableCheck'
      },
      action : ['emulationService', function(emulationService) {
        cars.selected.plugged_in = true;
        emulationService.duration = 30;
      }]
    },
    cableCheck : {
      transitions : {
        cableCheck : 'cableCheck',
        cableCheckDone : 'preCharge'
      },
      action : ['emulationService', function(emulationService) {
        emulationService.duration = 23000;
      }]
    },
    preCharge : {
      transitions : {
        preCharge : 'preCharge',
        preChargeDone : 'powerDelivery'
      },
      action : ['emulationService', function(emulationService) {
        emulationService.duration = 3800;
      }]
    },
    powerDelivery : {
      transitions : {
        chargeParameterDiscovery : 'chargeParameterDiscovery',
        powerDeliveryDone : 'currentDemand',
        powerDeliveryDoneW : 'weldingDetection',
        powerDeliveryDoneS : 'sessionStop'
      },
      action : ['emulationService', function(emulationService) {
        emulationService.duration = 600;
      }]
    },
    currentDemand : {
      transitions : {
        currentDemand : 'currentDemand',
        currentDemandDone : 'powerDelivery'
      },
      action : ['emulationService', function(emulationService) {
        emulationService.duration = 100;
      }]
    },
    weldingDetection : {
      transitions : {
        weldingDetection : 'weldingDetection',
        weldingDetectionDone : 'sessionStop'
      },
      action : ['emulationService', function(emulationService) {
        emulationService.duration = 2200;
      }]
    },
    sessionStop : {
      action : ['emulationService', function(emulationService) {
        emulationService.duration = 0;
      }]
    }
  });

}]);

app.run(['stateMachine', function(stateMachine) {
    stateMachine.initialize();
}]);
