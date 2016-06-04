Car.Emulator = function(args) {

  this.config = {
    timeout : 1000
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
      this.cycle = setTimeout(this.chainTimeouts(), this.config.timeout);
    }
  },

  // Chain emulation steps
  chainTimeouts : function() {
    var that = this;

    return function() {
      that.emulate();
      that.cycle = setTimeout(that.chainTimeouts(), that.config.timeout);
    }
  },

  // Process one cycle
  emulate : function() {
    console.log("HelloWorld!");
  },

  // Stop emulation
  stop : function() {
    clearTimeout(this.cycle);
    this.isRunning = false;
  }

};
