angular.module("ChargerUI")
  .factory("socketService", function ($log) {
    var socket = new WebSocket("ws://localhost:8081"); // TODO Config -> provider()
    var callback = {}

    socket.onopen = function() {
      $log.info("Socket successfully opened.");
    };

    socket.onerror = function(error) {
      $log.error("Socket error: " + error);
    };

    socket.onmessage = function(evt) {
      $log.debug("Socket received: " + evt.data);

      var msg = JSON.parse(evt.data);
      if (msg.hasOwnProperty('type') && msg.type in callback) {
        callback[msg.type](msg.content);
      } else {
        $log.error("No callback registered for received message!");
      }
    }

    sockServ = {
      addHandler: function (msgType, handler) {
        callback[msgType] = handler;
      }
    };

    return sockServ;
  });
