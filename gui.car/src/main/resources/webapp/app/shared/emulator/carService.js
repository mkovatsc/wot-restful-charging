app.factory('carService', function ($rootScope, socketService) {
  var car = function (args) {
    this.uuid = undefined;

    // Navigation for RESTful interface
    this.location = '';
    this.actions = [];

    this.config = {
      socketaddr: undefined
    };

    // Regex for proper websocket URL
    var re = /^((ws\:\/\/)\S+\:([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))$/g;

    // Only accept valid socket URLs
    if ('socketaddr' in args && args['socketaddr'].match(re)) {
      this.config.socketaddr = args['socketaddr'];

      // Create new socket and register default handlers
      this.config.socket = new socketService({socketaddr: this.config.socketaddr});
      var that = this;
      this.config.socket.addHandler('KEEPALIVE', function () {
        that.config.socket.send('KEEPALIVE', {});
      });
      this.config.socket.addHandler('REGISTER', function (data) {
        that.uuid = data.uuid;
        $rootScope.$apply();
      });
      this.config.socket.addHandler('DEBUG', function (data) { // TODO
        console.log(data);
      });
    }
  };

  car.prototype = {

    // Basic model description (default: BMW i3)
    name: 'BMW i3',
    state: undefined,
    battery: {
      capacity: 18.8, // kWh
      soc: 5, // State of charge
      R_C: 1.55, // charge = U*(1-e^-(t/R*C)) with t in minutes / 10
      charging: false
    },
    plugged_in: false,
    ready_charge: false,
    charging: {
      voltage: {
        AC: 230,
        DC: 400
      },
      rate: {
        AC: [12, 16, 32],
        DC: [125]
      },
      complete: false
    },

    // Additional state information
    connector: undefined,
    runningProc: undefined,

    // Plug the car in
    plugIn: function (speedup) {
      console.log('Plugging in the car.'); // TODO
      this.plugged_in = true;

      // Try to connect to the charger
      if (typeof this.config.socket != 'undefined') {
        var data = {
          pluggedIn: true,
          soc: this.battery.soc,
          chargingType: 'DC',
          maxVoltage: this.charging.voltage.DC,
          maxCurrent: Math.max.apply(null, this.charging.rate.DC)
        };
        this.config.socket.send('EVENT', data);
        this.changeState('pluggedIn'); // TODO First wait for answer from charger, $apply() exception
      } else {
        console.log('No connector for charger defined.'); // TODO
      }
    },

    // TODO Check available actions
    checkAvailableActions: function (speedup) {
      console.log('Checking for available actions.');

      // Register a handler to wait asynchronously for an answer
      if (!this.config.socket.hasHandler('ANSWER')) { // TODO external function?
        var that = this;
        this.config.socket.addHandler('ANSWER', function (data) {
          console.log('Answer received.');
          
          that.location = data.location;
          that.actions = data.actions;

          if (data.actions.indexOf('charge') != -1) {
            that.changeState('readyToCharge');
            that.config.socket.clearHandler('ANSWER');
          }
        });
      }

      if (typeof this.config.socket != 'undefined') { // TODO external function!
        var data = {
          action: 'checkAvailableActions'
        };
        this.config.socket.send('ACTION', data);
      }
    },

    // Set the desired target voltage
    setTargetVoltage: function (speedup) {
      console.log('Setting target voltage.');

      if (typeof this.config.socket != 'undefined') { // TODO external function!
        var data = {
          action: 'setTargetVoltage',
          targetVoltage: this.charging.voltage.DC
        }; // TODO Maybe submit resource URIs
        this.config.socket.send('ACTION', data);
      }

      this.changeState('targetVoltageSet');
    // TODO Don't execute steps multiple times if we are still waiting for an answer! (see other steps as well)
    },

    // Lookup charging procedure
    lookupChargingProcess: function (speedup) {
      console.log('Looking up charging procedure');

      // Register a handler to wait asynchronously for an answer
      if (!this.config.socket.hasHandler('ANSWER')) { // TODO external function?
        var that = this;
        this.config.socket.addHandler('ANSWER', function (data) {
          console.log('Answer received.');
          if (data.actions.length > 1) { // TODO Not just "self"
            that.changeState('chargingProcess');
            that.config.socket.clearHandler('ANSWER');
          }
        });
      }

      if (typeof this.config.socket != 'undefined') { // TODO external function, even with rate limit?!
        var data = {
          action: 'lookupChargingProcess'
        };
        this.config.socket.send('ACTION', data);
      }
    },

    // Stop the charging process
    stopChargingProcess: function (speedup) {
      console.log('Stopping charging process');

      if (typeof this.config.socket != 'undefined') { // TODO external function, even with rate limit?!
        var data = {
          action: 'stopChargingProcess'
        };
        this.config.socket.send('ACTION', data);
      }

      this.changeState('pluggedIn');
    },

    // Charge parameter discovery
    doChargeParameterDiscovery: function (speedup) {
      this.changeState('chargeParameterDiscovery');
      var timeout = Math.floor(30 / speedup); // TODO find a more elegant way!

      // TODO construction used very often, maybe offload? or generic do()?
      if (typeof this.runningProc == 'undefined') {
        console.log('Running charge parameter discovery.'); // TODO

        if (typeof this.config.socket != 'undefined') { // TODO external function!
          var data = {
            action: 'chargeParameterDiscovery',
            soc: this.battery.soc,
            maxVoltage: this.charging.voltage.DC,
            maxCurrent: Math.max.apply(null, this.charging.rate.DC)
          }; // TODO maybe missing energy transfer type
          this.config.socket.send('ACTION', data);
        }

        // TODO Register handler for incoming messages?

        // TODO Split timeouts into intervals, regular messages can then be triggered
        var that = this;
        this.runningProc = setTimeout(function () { // TODO use AngularJS library for all timeouts
          that.changeState('chargeParameterDiscoveryDone');
          that.runningProc = undefined;
        }, timeout); // TODO 30ms in real-time
      }
    },

    // Cable check
    doCableCheck: function (speedup) {
      this.changeState('cableCheck');
      var timeout = Math.floor(23000 / speedup);

      if (typeof this.runningProc == 'undefined') {
        console.log('Running the cable check.'); // TODO

        if (typeof this.config.socket != 'undefined') {
          var data = {
            action: 'cableCheck',
            soc: this.battery.soc
          };
          this.config.socket.send('ACTION', data);
        }

        // TODO Handler for success message

        var that = this;
        this.runningProc = setTimeout(function () {
          that.changeState('cableCheckDone');
          that.ready_charge = true; // TODO if successful?
          that.runningProc = undefined;
        }, timeout); // TODO 23s in real-time
      }
    },

    // Pre charge
    doPreCharge: function (speedup) {
      var that = this;
      if (this.state == 'cableCheckDone') {
        this.config.socket.addHandler('SEVALUES', function (data) {
          if (that.state == 'preCharge' && data.voltage == that.charging.voltage.DC) { // TODO Allow also for narrow voltage range?
            that.changeState('preChargeDone');
          }
        });
      }

      this.changeState('preCharge');
      var timeout = Math.floor(3800 / speedup);

      if (typeof this.runningProc == 'undefined') {
        console.log('Running the pre charge routine.'); // TODO

        if (typeof this.config.socket != 'undefined') {
          var data = {
            action: 'preCharge',
            targetVoltage: this.charging.voltage.DC,
            targetCurrent: 0
          };
          this.config.socket.send('ACTION', data);
        }
      }
    },

    // Power delivery
    doPowerDelivery: function (speedup) {
      this.changeState('powerDelivery');
      var timeout = Math.floor(600 / speedup);

      if (typeof this.runningProc == 'undefined') {
        console.log('Asking for power delivery.'); // TODO

        if (typeof this.config.socket != 'undefined') {
          var data = {
            action: 'powerDelivery',
            chargingComplete: this.charging.complete,
            readyToCharge: this.ready_charge
          };
          this.config.socket.send('ACTION', data);
        }

        // TODO Handler?

        var that = this;
        this.runningProc = setTimeout(function () {
          if (that.battery.soc == 100 || that.charging.complete) {
            that.changeState('powerDeliveryDoneW');
          } else {
            that.ready_charge = true; // TODO
            that.changeState('powerDeliveryDone');
          }
          that.runningProc = undefined;
        }, timeout); // TODO 600ms in real-time
      }
    },

    // Current demand
    doCurrentDemand: function (speedup) {
      console.log('Sending current demand.'); // TODO

      // TODO base on time / cycles
      if (this.battery.soc < 100) {
        this.battery.charging = true;
        this.battery.soc++;
        this.changeState('currentDemand');

        if (typeof this.config.socket != 'undefined') {
          var data = {
            action: 'currentDemand',
            soc: this.battery.soc,
            targetVoltage: this.charging.voltage.DC,
            targetCurrent: this.charging.rate.DC[0] - (this.charging.rate.DC[0] * (this.battery.soc / 100)), // TODO Derive from formula!
            chargingComplete: this.charging.complete
          };
          this.config.socket.send('ACTION', data);
        }
      } else {
        this.battery.charging = false; // TODO maybe define a routine
        this.ready_charge = false;
        this.charging.complete = true;
        //this.changeState('currentDemandDone');
        this.changeState('sessionStop');

        if (typeof this.config.socket != 'undefined') {
          var data = {
            action: 'stopCharging',
            soc: this.battery.soc,
            targetVoltage: this.charging.voltage.DC,
            targetCurrent: 0,
            chargingComplete: this.charging.complete
          };
          this.config.socket.send('ACTION', data);
        }
      }
    },

    // Welding detection
    doWeldingDetection: function (speedup) {
      this.changeState('weldingDetection');
      var timeout = Math.floor(2200 / speedup);

      if (typeof this.runningProc == 'undefined') {
        console.log('Performing welding detection.'); // TODO

        if (typeof this.config.socket != 'undefined') {
          var data = {
            action: 'weldingDetection'
          };
          this.config.socket.send('ACTION', data);
        }

        // TODO Handler?

        var that = this;
        this.runningProc = setTimeout(function () {
          that.changeState('weldingDetectionDone');
          that.runningProc = undefined;
        }, timeout); // TODO 2.2s in real-time
      }
    },

    // Stop session
    doStopSession: function (speedup) {
      // TODO introduce new state?
      var timeout = Math.floor(1000 / speedup);

      if (typeof this.runningProc == 'undefined') {
        console.log('Stopping the session'); // TODO

        if (typeof this.config.socket != 'undefined') {
          var data = {
            action: 'sessionStop'
          };
          this.config.socket.send('ACTION', data);
        }

        // TODO Handler?

        var that = this;
        this.runningProc = setTimeout(function () {
          that.changeState('sessionStop');
          that.runningProc = undefined;
        }, timeout); // TODO realistic value in real-time?
      }
    },

    // Unplug the car
    unplug: function (speedup) {
      console.log('Unplugging car.'); // TODO
      this.plugged_in = false;
      if (typeof this.config.socket != 'undefined') {
        this.config.socket.send('EVENT', {pluggedIn: false});
      }
      this.changeState(undefined);
    },

    // Reset the car's state
    reset: function () {
      clearTimeout(this.runningProc);
      this.runningProc = undefined; // TODO there must be a cleaner solution!
      this.unplug(1);
      this.battery.soc = 5;
      this.battery.charging = false;
      this.plugged_in = false;
      this.ready_charge = false;
      this.charging.complete = false;
    },

    // Change state of the car
    changeState: function (newState, args) {
      if (this.state != newState) {
        this.state = newState;
        $rootScope.$broadcast('carStateChanged', args);
      }
    }
  };

  return car;
});
