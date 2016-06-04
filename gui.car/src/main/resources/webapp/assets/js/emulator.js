Car.Emulator = function(args) {

  this.config = {
    interval : 1000
  };

  if (typeof args != 'undefined') {
    var key;
    for (key in this.config) {
      if (typeof args[key] != 'undefined') {
        this.config[key] = args[key];
      }
    }
  }

};

Car.Emulator.prototype = {

  // Emulate one cylce
  emulate : function() {

  }

};
