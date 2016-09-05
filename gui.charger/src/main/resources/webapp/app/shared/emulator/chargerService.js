app.factory('chargerService', function ($log, $rootScope, $interval, $timeout, socketService) {
  var charger = function (args) {
    this.status = {
      se: {},
      ev: {},
      cableCheck: '',
      voltageAdaption: ''
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

        if ('description' in data && data['description'] == 'pluggedIn') {
          // TODO Start cable check
          that.status.cableCheck = 'running';

          // Tell CoAP server about that change
          if (typeof that.config.socket != 'undefined') { // TODO external function!
            var data = {
              action: 'updateCableCheckStatus',
              cableCheckStatus: 1
            };
            that.config.socket.send('ACTION', data);
          }

          $timeout(function () {
            that.status.cableCheck = 'finished';

            // Tell CoAP server about that change
            if (typeof that.config.socket != 'undefined') { // TODO external function!
              var data = {
                action: 'updateCableCheckStatus',
                cableCheckStatus: 2
              };
              that.config.socket.send('ACTION', data);
            }
            $rootScope.$apply();
          }, 5000);
        } else if ('description' in data && data['description'] == 'unplugged') {
          that.status = {
            se: {},
            ev: {},
            cableCheck: ''
          };
          $rootScope.$apply();
        } else if ('description' in data && data['description'] == 'targetVoltageSet') {
          var tmpVol = data.targetVoltage;

          if (that.status.voltageAdaption == '') { // TODO Completely random right now
            $timeout(function () { // TODO Stop if car wants to leave, so that it can be unplugged

              // Tell CoAP server about voltage change
              if (typeof that.config.socket != 'undefined') { // TODO external function!
                var data = {
                  action: 'updatePresentVoltage',
                  presentVoltage: tmpVol / 2
                };
                that.config.socket.send('ACTION', data);
              }
            }, 2000).then(function () {
              return $timeout(function () {

                // Tell CoAP server about voltage change
                if (typeof that.config.socket != 'undefined') { // TODO external function!
                  var data = {
                    action: 'updatePresentVoltage',
                    presentVoltage: tmpVol
                  };
                  that.config.socket.send('ACTION', data);
                }

                that.status.voltageAdaption = 'finished';
                $rootScope.$apply();
              }, 2000);
            });
          }

          that.status.voltageAdaption = 'running';
        }

        $rootScope.$apply();
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
