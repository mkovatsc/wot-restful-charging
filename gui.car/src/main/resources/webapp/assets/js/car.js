var Car = function() {

};

Car.prototype =  {

  // Basic model description (default: BMW i3)
  name : "BMW i3",
  uuid : "",
  battery : {
    capacity : 18.8,       // kWh
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

  // Additional functionality
  // Plug the car in
  plugIn : function() {
    console.log("Plugging in the car."); // TODO
    this.plugged_in = true;
  },

  // Unplug the car
  unplug : function() {
    console.log("Unplugging car."); // TODO
    this.plugged_in = false;
  },

  // Reset the cars state
  reset : function() {
    this.battery.soc = 5;
    this.battery.charging = false;
    this.plugged_in = false;
    this.ready_charge = false;
    this.charging.complete = false;
  }
};
