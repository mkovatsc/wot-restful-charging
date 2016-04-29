app.factory("statusService", function () {
  var seStatus = {};
  var evStatus = {};

  seStatus.set = function (data) {
    seStatus.data = data;
  };

  evStatus.set = function (data) {
    evStatus.data = data;
  };

  return {
    se: seStatus,
    ev: evStatus
  };
});
