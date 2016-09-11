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

  function processState(car, type, relation, nextState) {
    $log.info('Calling ' + arguments.callee.name + '(<car>, ' + type + ', ' + relation + ', ' + nextState + ')');

    if (type == 'link' && relation in car.links) {
      car.follow(car.links[relation].href);
      car.changeState(nextState);
    } else if (type == 'form' && relation in car.forms) {
      car.submitForm(car.forms[relation].href, car.forms[relation].method, car.forms[relation].accepts);
      car.changeState(nextState);
    } else if ('wait' in car.links) {
      car.follow(car.links.wait['href']); // TODO Applies to every access on 'links' and 'forms': Might not be refreshed yet!
    } else if ('next' in car.links) {
      car.follow(car.links.next['href']);
    } else if ('next' in car.forms) {
      car.submitForm(car.forms.next['href'], car.forms.next['method'], car.forms.next['accepts']);
    } else {
      // TODO Well, we're screwed!
      $log.warn('Well, we are stuck! CarState: ' + car.state);
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
            processState(car, 'form', 'register', 'registered');
            break;
          case 'registered':
            processState(car, 'link', 'charge', 'charging');
            break;
          case 'charging':
            if (car.battery.soc < 100) {
              car.battery.soc++; // TODO Base on time rather than cylces?
              car.charging.currentDemand = car.charging.rate.DC[0] - (car.charging.rate.DC[0] * (car.battery.soc / 100));
              processState(car);
            } else {
              car.charging.currentDemand = 0;
              car.changeState('chargingFinished');
            }
            break;
          case 'chargingFinished':
            processState(car, 'form', 'stop', 'sessionStop');
            break;
          case 'sessionStop':
            processState(car, 'form', 'leave', undefined);
            if (typeof(car.state) == 'undefined') {
              this.stop(); // Stop emulation
            }
            // TODO Unplug the car?
            break;
          default:
            $log.warn('No action defined for this state. [' + car.state + ']'); // TODO proper handling
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
