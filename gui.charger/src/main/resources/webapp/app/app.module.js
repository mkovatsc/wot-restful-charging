var app = angular.module('ChargerUI', ['ngMaterial']);

// Fetch configuration from the server
app.run(function($rootScope, $http, $window) {
  $http({
    method: 'GET',
    url: '/config'
  }).then(function successCallback(response) {
      $rootScope.socketPort = response.data.socketPort;
      $rootScope.autoConfigSuccessful = true;
    }, function errorCallback(response) {
      $window.alert('Auto-configuration failed!');
    });
});
