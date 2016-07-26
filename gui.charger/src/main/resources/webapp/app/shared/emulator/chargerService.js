app.factory('chargerService', function ($log, $rootScope, $interval, socketService) {
  var charger = function (args) {
    this.status = {
      se: {},
      ev: {},
      cableCheck: ''
    };

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
      this.config.socket.addHandler('STATUS', function (data) {
        $log.info(data); // TODO DEBUG

        that.status.se = data.se;
        that.status.ev = data.ev;
        $rootScope.$apply();
      });
      this.config.socket.addHandler('EVENT', function (data) { // TODO This section needs a complete rework, just hacked together!
        $log.info(data); // TODO DEBUG

        if ('pluggedIn' in data && data['pluggedIn']) {
          // TODO Start cable check
          that.status.cableCheck = 'running';
          $rootScope.$apply();
        } else if ('pluggedIn' in data && !data['pluggedIn']) {
          that.status = {
            se: {},
            ev: {},
            cableCheck: ''
          };
          $rootScope.$apply();
        }
      });
    }
  };

  charger.prototype = {

    // Set charger to EV target values
    prepare: function (speedup, voltage, current) {
      var that = this;
      $interval(function () {
        if (that.status.se.voltage <= that.status.ev.targetVoltage - 100)
          that.status.se.voltage += 100;
      }, 500, 5);
    }
  };

  return charger;
});
