app.factory('socketService', function ($timeout, $log) {
  var socket = function (args) {
    this.config = {
      socketaddr: undefined,
      handlers: {}
    };

    // Regex for proper websocket URL
    var re = /^((ws\:\/\/)\S+\:([0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))$/g;

    // Only accept valid socket URLs
    if ('socketaddr' in args && args['socketaddr'].match(re)) {
      this.config.socketaddr = args['socketaddr'];

      // Initialize websocket connection
      this.config.websocket = new WebSocket(this.config.socketaddr);

      // TODO Anything to do here other then some debugging?
      this.config.websocket.onopen = function () {
        $log.info('Socket successfully opened.');
      };

      this.config.websocket.onclose = function () {
        $log.info('Socket closed.');
      };

      this.config.websocket.onerror = function (error) {
        $log.error('Socket error.');
      };

      var that = this;
      var callHandlers = function (msgType, data) {
        angular.forEach(that.config.handlers[msgType], function (handler) { // TODO no handler(s) defined?
          handler(data);
        });
      };

      this.config.websocket.onmessage = function (evt) {
        $log.info(evt.data);
        var json = JSON.parse(evt.data); // TODO Catch if invalid JSON received
        callHandlers(json.type, json.data);
      };
    }
  };

  socket.prototype = {
    hasHandler: function(msgType) {
      return msgType in this.config.handlers;
    },

    addHandler: function (msgType, handler) {
      this.config.handlers[msgType] = this.config.handlers[msgType] || [];
      this.config.handlers[msgType].push(handler);
    },

    clearHandler: function(msgType) {
      delete this.config.handlers[msgType];
    },

    send: function (msgType, msgData) {
      var that = this;
      $timeout(function () {
        if (that.config.websocket.readyState == 1) {
          that.config.websocket.send(JSON.stringify({type: msgType, data: msgData}));
        } else {
          that.send(msgType, msgData);
        }
      }, 10); // TODO max retries?
    }
  };

  return socket;
});
