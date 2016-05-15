var app = angular.module('CarUI', ['ngMaterial', 'ngRoute', 'FSM']);

app.config(['stateMachineProvider', function(stateMachineProvider) {
  
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
      }
    },
    cableCheck : {
      transitions : {
        cableCheck : 'cableCheck',
        cableCheckDone : 'preCharge'
      }
    },
    preCharge : {
      transitions : {
        preCharge : 'preCharge',
        preChargeDone : 'powerDelivery'
      }
    },
    powerDelivery : {
      transitions : {
        chargeParameterDiscovery : 'chargeParameterDiscovery',
        powerDeliveryDone : 'currentDemand',
        powerDeliveryStopW : 'weldingDetection',
        powerDeliveryStopS : 'sessionStop'
      }
    },
    currentDemand : {
      transitions : {
        currentDemand : 'currentDemand',
        currentDemandStop : 'powerDelivery'
      }
    },
    weldingDetection : {
      transitions : {
        weldingDetection : 'weldingDetection',
        weldingDetectionDone : 'sessionStop'
      }
    },
    sessionStop : {

    }
  });

}]);

app.run(['stateMachine', function(stateMachine) {
    stateMachine.initialize();
}]);
