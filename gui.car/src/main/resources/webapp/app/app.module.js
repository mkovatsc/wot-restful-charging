var app = angular.module('CarUI', ['ngMaterial']);

app.config(function($mdThemingProvider) {
  $mdThemingProvider.theme('default')
    .primaryPalette('green')
    .accentPalette('grey');
});

// Fetch configuration from the server
app.run(function($rootScope, $http, $window) {
  $http({
    method: 'GET',
    url: '/config'
  }).then(function successCallback(response) {
      $rootScope.socketPort = response.data.socketPort;
      $rootScope.carModel = response.data.carModel;
      $rootScope.autoConfigSuccessful = true;
    }, function errorCallback(response) {
      $window.alert('Auto-configuration failed!');
    });
});
