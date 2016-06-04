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

  // Local flags
  isRunning : false,

  // Start emulation
  start : function() {
    var that = this;

    if (!this.isRunning) {
      this.isRunning = true;

      this.cycle = setInterval(function () {
        that.emulate();
      }, this.config.interval);
    }
  },

  // Process one cycle
  emulate : function() {
    console.log("HelloWorld!");
  },

  // Stop emulation
  stop : function() {
    clearInterval(this.cycle);
    this.isRunning = false;
  }

};
