var cars = {
  bmw_i3 : {
    name : "BMW i3",
    uuid : "",
    battery : {
      capacity : 18.8,    // kWh
      soc : 1,            // State of charge
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
    }
  }
};
