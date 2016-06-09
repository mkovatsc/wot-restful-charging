app.controller('actionController', function ($scope, $rootScope, socketService) {
  $scope.togglePlug = function () {
    // TODO
  };

  var registerHandler = function (json) {
    // TODO
  };
  socketService.addHandler('REGISTER', registerHandler);
});
