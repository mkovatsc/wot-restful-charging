app.factory("socketService", function ($window, $timeout, $log) {
  // TODO switch to Socket.io?
  // TODO Config -> provider()
  var socket = new WebSocket("ws://localhost:8091");
  $timeout(function() {
    if (socket.readyState == 3) {
      $window.alert("Socket could not be opened.");
    }
  }, 2500);

  var handlers = {};

  socket.onopen = function() {
    $log.info("Socket successfully opened.");
  };

  socket.onclose = function() {
    $log.info("Socket closed.");
  }

  socket.onerror = function(error) {
    $log.error("Socket error.");
  };

  socket.onmessage = function(evt) {
    $log.debug("Socket received: " + evt.data);

    var msg;
    try {
      msg = JSON.parse(evt.data);
    } catch (e) {
      $log.info("No valid JSON message received.");
    }

    if (msg != null) {
      if (msg.hasOwnProperty('type') && msg.type in handlers) {
        handlers[msg.type](msg.content);
      } else {
        $log.error("No callback registered for received message!");
      }
    } else {
      if (evt.data == "Ping!") { // Keepalive
        socket.send("Pong!");
      }
    }
  }

  sockServ = {
    addHandler: function (msgType, handler) {
      handlers[msgType] = handler;
    },
    send: function(msg) {
      if (socket.readyState == 1) {
        $log.debug("Sending message to server: " + msg);
        socket.send(msg);
      } else {
        $log.error("Message could not be sent. Socket not ready.");
      }
    }
  };

  return sockServ;
});
