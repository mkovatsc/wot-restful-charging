app.factory('carService', function ($log, $rootScope, socketService) {
  var car = function (args) {
    this.uuid = undefined;

    // Navigation for RESTful interface
    this.href = '/.well-known/core';
    this.nowayback = false; // TODO true as soon as a form was sent
    this.links = {};
    this.forms = {};

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

      this.config.socket.addHandler('DEBUG', function (data) {
        $log.debug(data);
      });

      this.config.socket.addHandler('DISCOVER', function (data) {
        if (data !== null) {
          that.links = data.links;
          that.forms = {};
          $rootScope.$apply(); // TODO
        }
      });

      this.config.socket.addHandler('REDIRECT', function (data) {
        if (data !== null) {
          that.href = data;
          if (data == '/.well-known/core') { // TODO ugly hack
            that.plugIn();
          } else {
            that.follow(data);
          }
        }
      });

      this.config.socket.addHandler('LINKS', function (data) {
        if (data !== null) {
          that.links = data;
        } else {
          that.links = {};
        }
        $rootScope.$apply(); // TODO
      });

      this.config.socket.addHandler('FORMS', function (data) {
        if (data !== null) {
          that.forms = data;
        } else {
          that.forms = {};
        }
        $rootScope.$apply(); // TODO
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
      currentDemand : 0,
      complete: false
    },

    // Additional state information
    connector: undefined,
    runningProc: undefined,

    // Plug the car in
    plugIn: function (speedup) {
      $log.info('Plugging in the car.'); // TODO
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
        $log.debug('No connector for charger defined.'); // TODO
      }
    },

    // Follow a link
    follow: function (href) {
      $log.info('Following: ' + href);
      this.href = href;

      if (typeof this.config.socket != 'undefined') { // TODO external function!
        var data = {
          action: 'follow',
          href: href
        };
        this.config.socket.send('ACTION', data);
      }
    },

    // Submit a form
    submitForm: function (href, method, accepts) {
      $log.info('Submitting form: ' + method + ' ' + href + ' (' + accepts + ')');

      this.nowayback = true; // TODO

      if (typeof this.config.socket != 'undefined') { // TODO external function!
        var data = {
          action: 'submitForm',
          href: href,
          method: method
        };

        // Fill in form values > TODO generic solution?
        switch (accepts) {
            case 'application/register+json':
              data.soc = this.battery.soc;
              data.chargingType = 'DC';
              data.maxVoltage = this.charging.voltage.DC;
              data.maxCurrent = Math.max.apply(null, this.charging.rate.DC);
              break;
            case 'application/chargeinit+json':
              data.targetVoltage = this.charging.voltage.DC;
              break;
            case 'application/charge+json':
              data.soc = this.battery.soc;
              data.targetCurrent = this.charging.currentDemand;
              break;
            default:
              // TODO
        }

        this.config.socket.send('ACTION', data);
      }
    },

    // Set the desired target voltage
    setTargetVoltage: function (speedup) {
      $log.info('Setting target voltage.');

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
      $log.info('Looking up charging procedure');

      if (Object.keys(this.links).length > 1) { // TODO Not just "self"
        this.changeState('chargingProcess');
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
      $log.info('Stopping charging process');

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
        $log.info('Running charge parameter discovery.'); // TODO

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
        $log.info('Running the cable check.'); // TODO

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
        $log.info('Running the pre charge routine.'); // TODO

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
      $log.info('Asking for power delivery.'); // TODO

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
      $log.info('Sending current demand.'); // TODO

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
        $log.info('Performing welding detection.'); // TODO

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
        $log.info('Stopping the session'); // TODO

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
      $log.info('Unplugging car.'); // TODO

      this.href = '/.well-known/core'; // TODO Better way?
      this.nowayback = false;
      this.links = {};
      this.forms = {};

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
