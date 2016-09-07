app.factory('chargerService', function ($log, $rootScope, $interval, $timeout, socketService) {
  var charger = function (args) {
    this.status = {
      se: {},
      ev: {},
      cableCheck: '',
      voltageAdaption: ''
    };

    this.runningProcs = {}; // Keeps the promisess

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
        that.status.se = data.se;
        that.status.ev = data.ev;
        $rootScope.$apply();
      });

      this.config.socket.addHandler('EVENT', function (data) { // TODO This section needs a complete rework, just hacked together!
        if ('description' in data && data['description'] == 'pluggedIn') {
          that.status.cableCheck = 'running';

          // Tell CoAP server about that change
          if (typeof that.config.socket != 'undefined') { // TODO external function!
            var data = {
              action: 'updateCableCheckStatus',
              cableCheckStatus: 1
            };
            that.config.socket.send('ACTION', data);
          }

          that.runningProcs['cableCheck'] = $timeout(function () {
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
          if ('cableCheck' in that.runningProcs) {
            $timeout.cancel(that.runningProcs['cableCheck']);
            that.status.cableCheck = '';

            // Tell CoAP server about that changes
            if (typeof that.config.socket != 'undefined') { // TODO external function!
              var data = {
                action: 'updateCableCheckStatus',
                cableCheckStatus: 0
              };
              that.config.socket.send('ACTION', data);
            }
          }

          that.status = {
            se: {},
            ev: {},
            cableCheck: ''
          };
          $rootScope.$apply();
        } else if ('description' in data && data['description'] == 'targetVoltageSet') {
          var tmpVol = data.targetVoltage;

          if (tmpVol != that.status.se.presentVoltage) { // TODO Completely random right now
            that.status.voltageAdaption = 'running';

            $timeout(function () { // TODO Stop if car wants to leave, so that it can be unplugged

              // Tell CoAP server about voltage change
              if (typeof that.config.socket != 'undefined') { // TODO external function!
                var data = {
                  action: 'updatePresentVoltage',
                  presentVoltage: (tmpVol > that.status.se.presentVoltage ? tmpVol / 2 : that.status.se.presentVoltage / 2)
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
        } else if ('description' in data && data['description'] == 'targetCurrentSet') {

          // TODO At the moment it delivers what is requested straight away
          if (typeof that.config.socket != 'undefined') { // TODO external function!
            var data = {
              action: 'updatePresentCurrent',
              presentCurrent: data.targetCurrent
            };
            that.config.socket.send('ACTION', data);
          }
        }

        $rootScope.$apply();
      });
    }
  };

  charger.prototype = {

  };

  return charger;
});
