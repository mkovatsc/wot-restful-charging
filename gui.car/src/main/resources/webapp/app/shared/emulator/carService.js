app.factory('carService', function ($log, $rootScope, socketService) {
  var car = function (args) {
    this.uuid = undefined;

    // Navigation for RESTful interface
    this.href = '/.well-known/core';
    this.nowayback = false; // TODO true as soon as a form was sent
    this.links = {};
    this.forms = {};
    this.observes = [];

    this.config = {
      socketaddr: undefined
    };

    // Regex for proper websocket URL
    var re = /^((ws\:\/\/)\S+\:([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))$/g;

    // Only accept valid socket URLs
    if ('socketaddr' in args && args['socketaddr'].match(re)) {
      this.config.socketaddr = args['socketaddr'];

      // Create new socket and register default handlers
      this.config.socket = new socketService({socketaddr: this.config.socketaddr});
      var that = this;

      this.config.socket.addHandler('KEEPALIVE', function () {
        that.config.socket.send('KEEPALIVE', {});
      });

      this.config.socket.addHandler('DEBUG', function (data) {
        $log.debug(data);
      });

      this.config.socket.addHandler('REDIRECT', function (data) {
        if (data !== null) {
          that.href = data;
          if (data == '/.well-known/core') { // TODO ugly hack
            that.plugIn();
          } else {
            that.follow(data);
          }
        }
      });

      this.config.socket.addHandler('LINKS', function (data) {
        if (data !== null) {
          that.links = data;
        } else {
          that.links = {};
        }
        $rootScope.$apply(); // TODO
      });

      this.config.socket.addHandler('FORMS', function (data) {
        if (data !== null) {
          that.forms = data;
        } else {
          that.forms = {};
        }
        $rootScope.$apply(); // TODO
      });
    }
  };

  car.prototype = {

    // Basic model description (default: BMW i3)
    name: 'BMW i3',
    state: undefined,
    battery: {
      capacity: 18.8, // kWh
      soc: 5, // State of charge
      R_C: 1.55, // charge = U*(1-e^-(t/R*C)) with t in minutes / 10
      charging: false
    },
    plugged_in: false,
    ready_charge: false,
    charging: {
      voltage: {
        AC: 230,
        DC: 400
      },
      rate: {
        AC: [12, 16, 32],
        DC: [125]
      },
      currentDemand : 0,
      complete: false
    },

    // Additional state information
    connector: undefined,
    runningProc: undefined,

    // Plug the car in
    plugIn: function (speedup) {
      $log.info('Plugging in the car.'); // TODO
      this.plugged_in = true;

      // Try to connect to the charger
      if (typeof this.config.socket != 'undefined') {
        var data = {
          pluggedIn: true,
          soc: this.battery.soc,
          chargingType: 'DC',
          maxVoltage: this.charging.voltage.DC,
          maxCurrent: Math.max.apply(null, this.charging.rate.DC)
        };
        this.config.socket.send('EVENT', data);
        this.changeState('pluggedIn'); // TODO First wait for answer from charger, $apply() exception
      } else {
        $log.debug('No connector for charger defined.'); // TODO
      }
    },

    // Follow a link
    follow: function (href) {
      $log.info('Following: ' + href);

      this.href = href;
      this.links = {}; // Reset links and forms, as they might no longer be valid
      this.forms = {};

      if (typeof this.config.socket != 'undefined') { // TODO external function!
        var data = {
          action: 'follow',
          href: href
        };
        this.config.socket.send('ACTION', data);
      }
    },

    // Submit a form
    submitForm: function (href, method, accepts) {
      $log.info('Submitting form: ' + method + ' ' + href + ' (' + accepts + ')');

      this.links = {}; // Reset links and forms, as they might no longer be valid
      this.forms = {};

      this.nowayback = true; // TODO

      if (typeof this.config.socket != 'undefined') { // TODO external function!
        var data = {
          action: 'submitForm',
          href: href,
          method: method
        };

        // Fill in form values > TODO generic solution? Retrieve media types from server?
        switch (accepts) {
            case 'application/x.register+json':
              data.soc = this.battery.soc;
              data.chargingType = 'DC';
              data.maxVoltage = this.charging.voltage.DC;
              data.maxCurrent = Math.max.apply(null, this.charging.rate.DC);
              break;
            case 'application/x.charge-init+json':
              data.targetVoltage = this.charging.voltage.DC;
              break;
            case 'application/x.charge+json':
              data.soc = this.battery.soc;
              data.targetCurrent = this.charging.currentDemand;
              break;
            default:
              // TODO
        }

        this.config.socket.send('ACTION', data);
      }
    },

    // Establish an observe relation
    observe: function (href) {
      if (typeof this.config.socket != 'undefined') { // TODO external function!
        var data = {
          action: 'observe',
          href: href
        };
        this.config.socket.send('ACTION', data);

        this.observes.push(href);
      }
    },

    // Cancel an observe relation
    cancelObserve: function (href) {
      if (typeof this.config.socket != 'undefined') { // TODO external function!
        var data = {
          action: 'cancelObserve',
          href: href
        };
        this.config.socket.send('ACTION', data);

        // Remove entry from observes
        var index = this.observes.indexOf(href);
        if (index != -1) {
          this.observes.splice(index, 1);
        }
      }
    },

    // Get last CoAP response from server
    getLastResponse: function() {
      if (typeof this.config.socket != 'undefined') { // TODO external function!
        var data = {
          action: 'lastResponse'
        };
        this.config.socket.send('ACTION', data);
      }
    },

    // Unplug the car
    unplug: function (speedup) {
      $log.info('Unplugging car.'); // TODO

      this.href = '/.well-known/core'; // TODO Better way?
      this.nowayback = false;
      this.links = {};
      this.forms = {};

      this.plugged_in = false;
      if (typeof this.config.socket != 'undefined') {
        this.config.socket.send('EVENT', {pluggedIn: false});
      }
      this.changeState(undefined);
    },

    // Reset the car's state
    reset: function () {
      clearTimeout(this.runningProc);
      this.runningProc = undefined; // TODO there must be a cleaner solution!
      this.unplug(1);
      this.battery.soc = 5;
      this.battery.charging = false;
      this.plugged_in = false;
      this.ready_charge = false;
      this.charging.complete = false;
    },

    // Change state of the car
    changeState: function (newState, args) {
      if (this.state != newState) {
        this.state = newState;
        $rootScope.$broadcast('carStateChanged', args);
      }
    }
  };

  return car;
});
