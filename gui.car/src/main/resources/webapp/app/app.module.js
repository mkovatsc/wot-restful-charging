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
      action : function() {
        cars.selected.plugged_in = true;
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
        powerDeliveryDoneW : 'weldingDetection',
        powerDeliveryDoneS : 'sessionStop'
      }
    },
    currentDemand : {
      transitions : {
        currentDemand : 'currentDemand',
        currentDemandDone : 'powerDelivery'
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
