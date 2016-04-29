app.controller('stateController', function ($scope, $timeout) {
  var currentState;

  $scope.isActive = function (state) {
    return currentState === state;
  };
});
