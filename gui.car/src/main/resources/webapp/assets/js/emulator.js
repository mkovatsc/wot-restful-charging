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
  cycles : 0,
  interrupts : [],

  // Start emulation
  start : function() {
    if (!this.isRunning && this.config.car != undefined) {
      this.isRunning = true;
      this.emulation = setTimeout(this.chainTimeouts(), this.config.timeout);
    }
  },

  // Chain emulation steps
  chainTimeouts : function() {
    var that = this;

    return function() {
      that.emulate();
      that.emulation = setTimeout(that.chainTimeouts(), that.config.timeout);
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

    this.cycles++;
  },

  // Add interrupt functions
  addInterrupt : function(interrupt) {
    this.interrupts.push(interrupt);
  },

  // Stop emulation
  stop : function() {
    clearTimeout(this.emulation);
    this.isRunning = false;
  },

  // Reset the emulator
  reset : function() {
    this.stop();
    this.cycles = 0;
  }
};
