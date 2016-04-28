angular.module("ChargerUI")
  .factory("socketService", function ($log) {
    var socket = new WebSocket("ws://localhost:8081"); // TODO Config -> provider()

    socket.onopen = function() {
      $log.info("Socket successfully opened.");
    };

    socket.onerror = function(error) {
      $log.error("Socket error: " + error);
    };

    socket.onmessage = function(msg) {
      $log.debug("Socket received: " + msg.data);
    }

    return {
      socket: function () {
        return socket;
      }
    };
  });
