app.factory('carService', function ($rootScope, socketService) {
  var car = function (args) {
    this.uuid = undefined;

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
        this.config.socket.send('EVENT', {what: 'pluggedIn'});
        this.changeState('pluggedIn'); // TODO First wait for answer from charger, $apply() exception
      } else {
        console.log('No connector for charger defined.'); // TODO
      }
    },

    // Charge parameter discovery
    doChargeParameterDiscovery: function (speedup) {
      this.changeState('chargeParameterDiscovery');
      var timeout = Math.floor(30 / speedup); // TODO find a more elegant way!

      // TODO construction used very often, maybe offload? or generic do()?
      if (typeof this.runningProc == 'undefined') {
        console.log('Running charge parameter discovery.'); // TODO

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

        var that = this;
        this.runningProc = setTimeout(function () {
          that.changeState('cableCheckDone');
          that.runningProc = undefined;
        }, timeout); // TODO 23s in real-time
      }
    },

    // Pre charge
    doPreCharge: function (speedup) {
      this.changeState('preCharge');
      var timeout = Math.floor(3800 / speedup);

      if (typeof this.runningProc == 'undefined') {
        console.log('Running the pre charge routine.'); // TODO

        var that = this;
        this.runningProc = setTimeout(function () {
          that.changeState('preChargeDone');
          that.runningProc = undefined;
        }, timeout); // TODO 3.8s in real-time
      }
    },

    // Power delivery
    doPowerDelivery: function (speedup) {
      this.changeState('powerDelivery');
      var timeout = Math.floor(600 / speedup);

      if (typeof this.runningProc == 'undefined') {
        console.log('Asking for power delivery.'); // TODO

        var that = this;
        this.runningProc = setTimeout(function () {
          if (that.battery.soc == 100 && that.charging.complete) {
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
      this.changeState('currentDemand');
      console.log('Sending current demand.'); // TODO

      // TODO base on time / cycles
      if (this.battery.soc < 100) {
        this.battery.charging = true;
        this.battery.soc++;
        this.changeState('currentDemand');
      } else {
        this.battery.charging = false; // TODO maybe define a routine
        this.ready_charge = false;
        this.charging.complete = true;
        this.changeState('currentDemandDone');
      }
    },

    // Welding detection
    doWeldingDetection: function (speedup) {
      this.changeState('weldingDetection');
      var timeout = Math.floor(2200 / speedup);

      if (typeof this.runningProc == 'undefined') {
        console.log('Performing welding detection.'); // TODO

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
      this.changeState(undefined);
    },

    // Reset the car's state
    reset: function () {
      clearTimeout(this.runningProc);
      this.runningProc = undefined; // TODO there must be a cleaner solution!
      this.changeState(undefined, {'reset': true});
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
    },

    // Send message to the connected charger
    sendMsg: function (msg) {
      if (typeof this.connector != 'undefined' && this.connector.readyState == 1) {
        this.connector.send(msg);
      }
    },

    // Send a command to the connected charger
    sendCmd: function (cmd) {
      // TODO just a wrapper
    }
  };

  return car;
});
