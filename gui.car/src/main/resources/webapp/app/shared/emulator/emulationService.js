app.factory('emulationService', function ($log, $rootScope, $timeout, $q) {
  var emulator = function (args) {

    // Default config values
    this.config = {
      timeout: 1000,
      speedup: 1.0, // TODO maybe combine with cycle timeout
      car: undefined
    };

    // Assign given config values
    if (typeof args != 'undefined') {
      var key;
      for (key in this.config) {
        if (typeof args[key] != 'undefined') {
          this.config[key] = args[key];
        }
      }
    }
  };

  emulator.prototype = {
    isRunning: false,
    cycles: 0,
    interrupts: [],

    // Start emulation
    start: function () {
      if (!this.isRunning && typeof this.config.car != 'undefined') {
        this.isRunning = true;
        this.emulation = $timeout(this.chainTimeouts(), this.config.timeout);
      }
    },

    // Chain emulation steps
    chainTimeouts: function () {
      var that = this;

      return function () {
        that.emulate();
        if (that.isRunning) {
          $q.when(that.emulation).then(function () {
            that.emulation = $timeout(that.chainTimeouts(), that.config.timeout);
          });
        }
      };
    },

    // Process one cycle
    emulate: function () {
      // TODO car could be undefined at some points
      var car = this.config.car;

      if (this.interrupts.length > 0) {
        var interrupt = this.interrupts.shift();
        interrupt(); // TODO maybe inject some dependency
      } else if (typeof car.state == 'undefined' && !car.plugged_in) {
        car.plugIn(this.config.speedup);
      } else {
        switch (car.state) {
          case 'pluggedIn':
            angular.forEach(car.links, function(value, key) {
              if (typeof(value.rt) != 'undefined' && value.rt == 'ev') {
                car.follow(value.href);
                car.changeState('unregistered');
                return;
              }
            });
            break;
          case 'unregistered':
            // TODO Generally: Sometime one has to wait until a desired form appears!
            angular.forEach(car.forms, function(value, key) {
              if (key == 'next') {
                car.submitForm(value.href, value.method, value.accepts);
                car.changeState('registered'); // TODO Who says we are already there?!
                return;
              }
            });
            break;
          case 'registered':
            if ('charge' in car.links) {
              car.follow(car.links.charge['href']);
              car.changeState('chargeInit');
            } else if ('wait' in car.links) { // TODO The following statements hold for every state -> function!
              car.follow(car.links.wait['href']); // TODO Applies to every access on 'links' and 'forms': Might not be refreshed yet!
            } else if ('next' in car.links) {
              car.follow(car.links.next['href']);
            } else if ('next' in car.forms) {
              car.submitForm(car.forms.next['href'], car.forms.next['method'], car.forms.next['accepts']);
            } else {
              // TODO Well, we're screwed!
            }
            break;
          case 'chargeInit':
            if ('next' in car.forms) {
              car.submitForm(car.forms.next['href'], car.forms.next['method'], car.forms.next['accepts']);
              car.changeState('chargeReady');
            } else if ('wait' in car.links) {
              car.follow(car.links.wait['href']);
            } else if ('next' in car.links) {
              car.follow(car.links.next['href']);
            } else if ('next' in car.forms) { // TODO already covered above, so this doesn't qualify for an own state!
              car.submitForm(car.forms.next['href'], car.forms.next['method'], car.forms.next['accepts']);
            } else {
              // TODO Well, we're screwed!
            }
            break;
          case 'chargeReady':
            if ('next' in car.forms) {
              car.charging.currentDemand = car.charging.rate.DC[0]; // TODO

              car.submitForm(car.forms.next['href'], car.forms.next['method'], car.forms.next['accepts']);
              car.changeState('charging');
            } else if ('wait' in car.links) {
              car.follow(car.links.wait['href']);
            } else if ('next' in car.links) {
              car.follow(car.links.next['href']);
            } else if ('next' in car.forms) { // TODO already covered above, so this doesn't qualify for an own state!
              car.submitForm(car.forms.next['href'], car.forms.next['method'], car.forms.next['accepts']);
            } else {
              // TODO Well, we're screwed!
            }
            break;
          case 'charging':
            if ('continue' in car.forms) {
              if (car.battery.soc < 100) {
                car.battery.soc++;
                car.charging.currentDemand = car.charging.rate.DC[0] - (car.charging.rate.DC[0] * (car.battery.soc / 100));

                car.submitForm(car.forms.continue['href'], car.forms.continue['method'], car.forms.continue['accepts']);
                car.changeState('charging');
              } else {
                car.charging.currentDemand = 0;
                car.changeState('chargingFinished');
              }
            } else if ('wait' in car.links) {
              car.follow(car.links.wait['href']);
            } else if ('next' in car.links) {
              car.follow(car.links.next['href']);
            } else if ('next' in car.forms) {
              car.submitForm(car.forms.next['href'], car.forms.next['method'], car.forms.next['accepts']);
            } else {
              // TODO Well, we're screwed!
            }
            break;
          case 'chargingFinished':
            if ('stop' in car.forms) {
              car.submitForm(car.forms.stop['href'], car.forms.stop['method'], car.forms.stop['accepts']);
              car.changeState('sessionStop');
            } else if ('wait' in car.links) {
              car.follow(car.links.wait['href']);
            } else if ('next' in car.links) {
              car.follow(car.links.next['href']);
            } else if ('next' in car.forms) {
              car.submitForm(car.forms.next['href'], car.forms.next['method'], car.forms.next['accepts']);
            } else {
              // TODO Well, we're screwed!
            }
            break;
          case 'sessionStop':
            if ('leave' in car.forms) {
              car.submitForm(car.forms.leave['href'], car.forms.leave['method'], car.forms.leave['accepts']);
              car.changeState(undefined);
              this.stop(); // Stop emulation
            } else if ('wait' in car.links) {
              car.follow(car.links.wait['href']);
            } else if ('next' in car.links) {
              car.follow(car.links.next['href']);
            } else if ('next' in car.forms) {
              car.submitForm(car.forms.next['href'], car.forms.next['method'], car.forms.next['accepts']);
            } else {
              // TODO Well, we're screwed!
            }
            // TODO Unplug the car?
            break;
          default:
            console.log('No action defined for this state. [' + car.state + ']'); // TODO proper handling
        }
      }

      this.cycles++;
    },

    // Add interrupt functions
    addInterrupt: function (interrupt) {
      this.interrupts.push(interrupt);
    },

    // Stop emulation
    stop: function () {
      $timeout.cancel(this.emulation);
      this.isRunning = false;

      // TODO Remove existing asynchronous handlers
    },

    // Reset the emulator
    reset: function () {
      this.stop();
      this.cycles = 0;

      var car = this.config.car;
      if (typeof car != 'undefined') {
        car.reset();
      }
    }
  };

  return emulator;
});
