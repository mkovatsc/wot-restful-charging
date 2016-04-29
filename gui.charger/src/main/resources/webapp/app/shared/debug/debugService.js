angular.module("ChargerUI")
  .factory("debugService", function (socketService) {
    var debugmsgs = [];

    return {
      messages: debugmsgs,
      pushMsg: function (datetime, msg) {
        debugmsgs.unshift({time : datetime, message : msg});
      }
    };
  });
