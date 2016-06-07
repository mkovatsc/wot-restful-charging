var Car = function (args) {
  this.connector = undefined;

  // Regex for proper websocket URL
  var re = /^((ws\:\/\/)\S+\:([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))$/g;

  // Only try do initialize connector if provided URL is valid
  if ('socketaddr' in args && args['socketaddr'].match(re)) {
    this.connector = new WebSocket(args['socketaddr']);
    var that = this;
    setTimeout(function () {
      if (that.connector.readyState != 1) {
        this.connector = undefined;
      }
    }, 1000); // Give it a second to get ready
  }
};

Car.prototype = {

  // Basic model description (default: BMW i3)
  name: 'BMW i3',
  uuid: '',
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
  runningProc: undefined,

  // Plug the car in
  plugIn: function (speedup) {
    console.log('Plugging in the car.'); // TODO
    this.plugged_in = true;
    this.state = 'pluggedIn';
  },

  // Charge parameter discovery
  doChargeParameterDiscovery: function (speedup) {
    var timeout = Math.floor(30 / speedup); // TODO find a more elegant way!

    // TODO construction used very often, maybe offload? or generic do()?
    if (typeof this.runningProc == 'undefined') {
      console.log('Running charge parameter discovery.'); // TODO

      var that = this;
      this.runningProc = setTimeout(function () {
        that.state = 'chargeParameterDiscoveryDone';
        that.runningProc = undefined;
      }, timeout); // TODO 30ms in real-time
    }
  },

  // Cable check
  doCableCheck: function (speedup) {
    var timeout = Math.floor(23000 / speedup);

    if (typeof this.runningProc == 'undefined') {
      console.log('Running the cable check.'); // TODO

      var that = this;
      this.runningProc = setTimeout(function () {
        that.state = 'cableCheckDone';
        that.runningProc = undefined;
      }, timeout); // TODO 23s in real-time
    }
  },

  // Pre charge
  doPreCharge: function (speedup) {
    var timeout = Math.floor(3800 / speedup);

    if (typeof this.runningProc == 'undefined') {
      console.log('Running the pre charge routine.'); // TODO

      var that = this;
      this.runningProc = setTimeout(function () {
        that.state = 'preChargeDone';
        that.runningProc = undefined;
      }, timeout); // TODO 3.8s in real-time
    }
  },

  // Power delivery
  doPowerDelivery: function (speedup) {
    var timeout = Math.floor(600 / speedup);

    if (typeof this.runningProc == 'undefined') {
      console.log('Asking for power delivery.'); // TODO

      var that = this;
      this.runningProc = setTimeout(function () {
        if (that.battery.soc == 100 && that.charging.complete) {
          that.state = 'powerDeliveryDoneW';
        } else {
          that.ready_charge = true; // TODO
          that.state = 'powerDeliveryDone';
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
      this.state = 'currentDemand';
    } else {
      this.battery.charging = false; // TODO maybe define a routine
      this.ready_charge = false;
      this.charging.complete = true;
      this.state = 'currentDemandDone';
    }
  },

  // Welding detection
  doWeldingDetection: function (speedup) {
    var timeout = Math.floor(2200 / speedup);

    if (typeof this.runningProc == 'undefined') {
      console.log('Performing welding detection.'); // TODO

      var that = this;
      this.runningProc = setTimeout(function () {
        that.state = 'weldingDetectionDone';
        that.runningProc = undefined;
      }, timeout); // TODO 2.2s in real-time
    }
  },

  // Stop session
  doStopSession: function (speedup) {
    var timeout = Math.floor(1000 / speedup);

    if (typeof this.runningProc == 'undefined') {
      console.log('Stopping the session'); // TODO

      var that = this;
      this.runningProc = setTimeout(function () {
        that.state = 'sessionStop';
        that.runningProc = undefined;
      }, timeout); // TODO realistic value in real-time?
    }
  },

  // Unplug the car
  unplug: function (speedup) {
    console.log('Unplugging car.'); // TODO
    this.plugged_in = false;
    this.state = undefined;
  },

  // Reset the car's state
  reset: function () {
    this.state = undefined;
    this.battery.soc = 5;
    this.battery.charging = false;
    this.plugged_in = false;
    this.ready_charge = false;
    this.charging.complete = false;
  },

  // Send message to the connected charger
  sendMsg: function (msg) {
    if (typeof this.connector != 'undefined') {
      this.connector.send(msg);
    }
  },

  // Send a command to the connected charger
  sendCmd: function (cmd) {
    // TODO just a wrapper
  }
};
