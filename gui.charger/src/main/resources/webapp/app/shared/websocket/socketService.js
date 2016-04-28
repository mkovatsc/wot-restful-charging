angular.module("ChargerUI")
  .factory("socketService", function ($log) {
    // TODO switch to Socket.io?
    // TODO Config -> provider()
    var socket = new WebSocket("ws://localhost:8081");
    var sockReady = false;
    var handlers = {};

    socket.onopen = function() {
      $log.info("Socket successfully opened.");
      sockReady = true;
    };

    socket.onclose = function() {
      $log.info("Socket closed.");
      sockReady = false;
    }

    socket.onerror = function(error) {
      $log.error("Socket error: " + error);
      sockReady = false;
    };

    socket.onmessage = function(evt) {
      $log.debug("Socket received: " + evt.data);

      var msg = JSON.parse(evt.data);
      if (msg.hasOwnProperty('type') && msg.type in handlers) {
        handlers[msg.type](msg.content);
      } else {
        $log.error("No callback registered for received message!");
      }
    }

    sockServ = {
      addHandler: function (msgType, handler) {
        handlers[msgType] = handler;
      },
      send: function(msg) {
        if (sockReady) {
          $log.debug("Sending message to server: " + msg);
          socket.send(msg);
        } else {
          $log.error("Message could not be sent. Socket not ready.");
        }
      }
    };

    return sockServ;
  });
