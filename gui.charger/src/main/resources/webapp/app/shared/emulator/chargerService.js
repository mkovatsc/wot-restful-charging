app.factory('chargerService', function ($rootScope, socketService) {
  var charger = function (args) {
    this.status = {
      se: {},
      ev: {}
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
        that.status.se = data.se;
        that.status.ev = data.ev;
        $rootScope.$apply();
      });
    }
  };

  charger.prototype = {};

  return charger;
});
