var cars = {
  bmw_i3 : {
    name : "BMW i3",
    uuid : "",
    battery : {
      capacity : 18.8,
      soc : 1,
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
      status : function(startedChargingAt, chargingTime) {
        // TODO Calculate baseline for charging
        bias = 51;
        // TODO Random function, derive model from actual data!
        return 0.222026 * Math.log(0.019663 * (bias + chargingTime));
      },
      complete : false
    }
  }
};
