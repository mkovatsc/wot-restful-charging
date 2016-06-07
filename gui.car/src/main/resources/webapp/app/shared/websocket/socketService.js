app.factory('socketService', function ($window, $timeout, $log) {
  // TODO switch to Socket.io?
  // TODO Config -> provider()
  var socket = new WebSocket('ws://localhost:8091');

  var handlers = {};

  socket.onopen = function () {
    $log.info('Socket successfully opened.');
  };

  socket.onclose = function () {
    $log.info('Socket closed.');
  };

  socket.onerror = function (error) {
    $log.error('Socket error.');
  };

  socket.onmessage = function (evt) {
    $log.debug('Socket received: ' + evt.data);

    var msg;
    try {
      msg = JSON.parse(evt.data);
    } catch (e) {
      $log.info('No valid JSON message received.');
    }

    if (msg != null) {
      if (msg.hasOwnProperty('type') && msg.type in handlers) {
        handlers[msg.type](msg.content);
      } else {
        $log.error('No callback registered for received message!');
      }
    } else {
      if (evt.data == 'Ping!') { // Keepalive
        waitAndSend(socket, 'Pong!');
      }
    }
  };

  var waitAndSend = function (socket, msg) {
    $timeout(function () {
      if (socket.readyState == 1) {
        socket.send(msg);
      } else {
        waitAndSend(msg);
      }
    }, 10); // TODO max retries?
  };

  sockServ = {
    addHandler: function (msgType, handler) {
      handlers[msgType] = handler;
    },
    send: function (msg) {
      waitAndSend(socket, msg);
    }
  };

  return sockServ;
});
