Car.Emulator = function(args) {
  this.config = {
    timeout : 1000,
    car : undefined
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
  isRunning : false,
  interrupts : [],

  // Start emulation
  start : function() {
    if (!this.isRunning && this.config.car != undefined) {
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
    if (this.interrupts.length > 0) {
      var interrupt = this.interrupts.shift();
      interrupt();
    } else {
      console.log("HelloWorld!");
    }
  },

  // Add interrupt functions
  addInterrupt : function(interrupt) {
    this.interrupts.push(interrupt);
  },

  // Stop emulation
  stop : function() {
    clearTimeout(this.cycle);
    this.isRunning = false;
  }
};
