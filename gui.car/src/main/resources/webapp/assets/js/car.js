var Car = function(emulator) {

  this.emulator = emulator;

};

Car.prototype =  {

  // Basic model description
  name : "",
  uuid : "",
  battery : {
    capacity : 0,       // kWh
    soc : 0,            // State of charge
    R_C : 0,            // charge = U*(1-e^-(t/R*C)) with t in minutes / 10
    charging : false
  },
  plugged_in : false,
  ready_charge : false,
  charging : {
    voltage : {
      AC : 0,
      DC : 0
    },
    rate : {
      AC : [0],
      DC : [0]
    },
    complete : false
  }

};
