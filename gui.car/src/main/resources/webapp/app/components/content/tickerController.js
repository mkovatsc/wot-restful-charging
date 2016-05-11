app.controller('tickerController', function ($scope, $rootScope, $interval, tickerService) {

  $rootScope.car = cars.bmw_i3;

  $scope.tickCount = tickerService.tickCount();
  $scope.tickingSpeed = tickerService.tickingSpeed;
  $scope.tickingSpeeds = tickerService.tickingSpeeds;

  $scope.startTicking = tickerService.startTicking;
  $scope.stopTicking= tickerService.stopTicking;
  $scope.tick = tickerService.tick;
  $scope.isRunning = tickerService.isRunning;

  $scope.$watch("tickCount", function() {
    $rootScope.car.battery.soc = $rootScope.car.charging.status(1, $scope.tickCount);
  });

  tickerService.addCallback(function(){
    $scope.tickCount = tickerService.tickCount();
  });

  $scope.$on('$destroy', function() {
    tickerService.stopTicking();
  });

  $scope.$watch("tickingSpeed", function(){
    tickerService.updateTicking($scope.tickingSpeed);
  });

});
