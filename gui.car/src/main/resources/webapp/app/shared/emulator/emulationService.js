app.factory('emulationService', function ($log, $rootScope, $q) {
  var emulator = function (args) {

    // Default config values
    this.config = {
      timeout: 1000,
      speedup: 1.0, // TODO maybe combine with cycle timeout
      car: undefined
    };

    // Assign given config values
    if (typeof args != 'undefined') {
      var key;
      for (key in this.config) {
        if (typeof args[key] != 'undefined') {
          this.config[key] = args[key];
        }
      }
    }
  };

  emulator.prototype = {
    isRunning: false,
    cycles: 0,
    interrupts: [],

    // Start emulation
    start: function () {
      if (!this.isRunning && typeof this.config.car != 'undefined') {
        this.isRunning = true;
        this.emulation = setTimeout(this.chainTimeouts(), this.config.timeout);
      }
    },

    // Chain emulation steps
    chainTimeouts: function () {
      var that = this;

      return function () {
        that.emulate();
        if (that.isRunning) {
          $q.when(that.emulation).then(function () {
            that.emulation = setTimeout(that.chainTimeouts(), that.config.timeout);
          });
        }
      };
    },

    // Process one cycle
    emulate: function () {
      // TODO car could be undefined at some points
      var car = this.config.car;

      if (this.interrupts.length > 0) {
        var interrupt = this.interrupts.shift();
        interrupt(); // TODO maybe inject some dependency
      } else if (typeof car.state == 'undefined' && !car.plugged_in) {
        car.plugIn(this.config.speedup);
      } else {
        switch (car.state) {
          case 'pluggedIn':
            car.checkAvailableActions(this.config.speedup);
            break;
          case 'readyToCharge':
            car.setTargetVoltage(this.config.speedup);
            break;
          case 'targetVoltageSet':
            car.lookupChargingProcess(this.config.speedup);
            break;
          /*
                    case 'pluggedIn':
                      car.doChargeParameterDiscovery(this.config.speedup)
                      break
                    case 'chargeParameterDiscovery':
                      car.doChargeParameterDiscovery(this.config.speedup)
                      break
                    case 'chargeParameterDiscoveryDone':
                      car.doCableCheck(this.config.speedup)
                      break
                    case 'cableCheck':
                      car.doCableCheck(this.config.speedup)
                      break
                    case 'cableCheckDone':
                      car.doPreCharge(this.config.speedup)
                      break
                    case 'preCharge':
                      car.doPreCharge(this.config.speedup)
                      break
                    case 'preChargeDone':
                      car.doPowerDelivery(this.config.speedup)
                      break
                    case 'powerDelivery':
                      car.doPowerDelivery(this.config.speedup)
                      break
                    case 'powerDeliveryDone':
                      car.doCurrentDemand(this.config.speedup)
                      break
                    case 'currentDemand':
                      car.doCurrentDemand(this.config.speedup)
                      break
                    case 'currentDemandDone':
                      car.doPowerDelivery(this.config.speedup)
                      break
                    case 'powerDeliveryDoneS':
                      car.doStopSession(this.config.speedup)
                      break
                    case 'powerDeliveryDoneW':
                      car.doWeldingDetection(this.config.speedup)
                      break
                    case 'weldingDetection':
                      car.doWeldingDetection(this.config.speedup)
                      break
                    case 'weldingDetectionDone':
                      car.doStopSession(this.config.speedup)
                      break
          */
          case 'sessionStop':
            car.unplug(this.config.speedup);
            this.stop(); // Stop emulation after unplugging
            break;
          default:
            console.log('No action defined for this state. [' + car.state + ']'); // TODO proper handling
        }
      }

      this.cycles++;
    },

    // Add interrupt functions
    addInterrupt: function (interrupt) {
      this.interrupts.push(interrupt);
    },

    // Stop emulation
    stop: function () {
      clearTimeout(this.emulation);
      this.isRunning = false;
    },

    // Reset the emulator
    reset: function () {
      this.stop();
      this.cycles = 0;

      var car = this.config.car;
      if (typeof car != 'undefined') {
        car.reset();
      }
    }
  };

  return emulator;
});
