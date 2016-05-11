app.controller('tickerController', function ($scope, $interval, tickerService, $log) {

  $scope.tickCount = tickerService.tickCount();
  $scope.tickingSpeed = tickerService.tickingSpeed;
  $scope.tickingSpeeds = tickerService.tickingSpeeds;

  $scope.startTicking = tickerService.startTicking;
  $scope.stopTicking= tickerService.stopTicking;
  $scope.tick = tickerService.tick;
  $scope.tickDisabled = tickerService.tickDisabled;

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
