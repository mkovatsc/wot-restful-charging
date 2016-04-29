app.factory("debugService", function () {
  var debugmsgs = [];

  return {
    messages: debugmsgs,
    pushMsg: function (datetime, msg) {
      debugmsgs.unshift({time : datetime, message : msg});
    }
  };
});
