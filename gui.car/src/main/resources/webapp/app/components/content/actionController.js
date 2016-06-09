app.controller('actionController', function ($scope, $rootScope, socketService) {
  var registerHandler = function (json) {
    // TODO
  };
  socketService.addHandler('REGISTER', registerHandler);
});
