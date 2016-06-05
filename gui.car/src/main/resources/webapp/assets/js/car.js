var Car = function() {

};

Car.prototype =  {

  // Basic model description (default: BMW i3)
  name : "BMW i3",
  uuid : "",
  state : undefined,
  battery : {
    capacity : 18.8,    // kWh
    soc : 5,            // State of charge
    R_C : 1.55,         // charge = U*(1-e^-(t/R*C)) with t in minutes / 10
    charging : false
  },
  plugged_in : false,
  ready_charge : false,
  charging : {
    voltage : {
      AC : 230,
      DC : 400
    },
    rate : {
      AC : [12, 16, 32],
      DC : [125]
    },
    complete : false
  },

  // Additional state information
  runningProc : undefined,

  // Additional functionality
  // Plug the car in
  plugIn : function() {
    console.log("Plugging in the car."); // TODO
    this.plugged_in = true;
    this.state = 'pluggedIn';
  },

  // Charge parameter discovery
  doChargeParameterDiscovery : function() {
    console.log("Running charge parameter discovery."); // TODO

    // TODO construction used very often, maybe offload? or generic do()?
    if (typeof this.runningProc == 'undefined') {
      var that = this;
      this.runningProc = setTimeout(function() {
        that.state = 'chargeParameterDiscoveryDone';
        that.runningProc = undefined;
      }, 30); // TODO 30ms in real-time, scale for simulation!
    }
  },

  // Cable check
  doCableCheck : function() {
    console.log("Running the cable check."); // TODO

    if (typeof this.runningProc == 'undefined') {
      var that = this;
      this.runningProc = setTimeout(function() {
        that.state = 'cableCheckDone';
        that.runningProc = undefined;
      }, 23000); // TODO 23s in real-time, scale for simulation!
    }
  },

  // Pre charge
  doPreCharge : function() {
    console.log("Running the pre charge routine."); // TODO

    if (typeof this.runningProc == 'undefined') {
      var that = this;
      this.runningProc = setTimeout(function() {
        that.state = 'preChargeDone';
        that.runningProc = undefined;
      }, 3800); // TODO 3.8s in real-time, scale for simulation!
    }
  },

  // Power delivery
  doPowerDelivery : function() {
    console.log("Asking for power delivery."); // TODO

    if (typeof this.runningProc == 'undefined') {
      var that = this;
      this.runningProc = setTimeout(function() {
        if (that.battery.soc == 100 && that.charging.complete) {
          that.state = 'powerDeliveryDoneW';
        } else {
          that.ready_charge = true; // TODO
          that.state = 'powerDeliveryDone';
        }
        that.runningProc = undefined;
      }, 600); // TODO 600ms in real-time, scale for simulation!
    }
  },

  // Current demand
  doCurrentDemand : function() {
    console.log("Sending current demand."); // TODO

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
  doWeldingDetection : function() {
    console.log("Performing welding detection."); // TODO

    if (typeof this.runningProc == 'undefined') {
      var that = this;
      this.runningProc = setTimeout(function() {
        that.state = 'weldingDetectionDone';
        that.runningProc = undefined;
      }, 2200); // TODO 2.2s in real-time, scale for simulation!
    }
  },

  // Stop session
  doStopSession : function() {
    console.log("Stopping the session"); // TODO

    if (typeof this.runningProc == 'undefined') {
      var that = this;
      this.runningProc = setTimeout(function() {
        that.state = 'sessionStop';
        that.runningProc = undefined;
      }, 1000); // TODO realistic value in real-time? scale for simulation!
    }
  },

  // Unplug the car
  unplug : function() {
    console.log("Unplugging car."); // TODO
    this.plugged_in = false;
    this.state = undefined;
  },

  // Reset the car's state
  reset : function() {
    this.state = undefined;
    this.battery.soc = 5;
    this.battery.charging = false;
    this.plugged_in = false;
    this.ready_charge = false;
    this.charging.complete = false;
  }
};
