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
    // TODO Place this function somewhere else
    var soc = function(t) { // t in minutes
      return 1 - Math.pow(Math.E, -((t / 10) / $rootScope.car.battery.R_C));
    };
    $rootScope.car.battery.soc = soc($scope.tickCount / 60);
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
