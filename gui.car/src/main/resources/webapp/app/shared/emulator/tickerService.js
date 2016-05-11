app.factory("tickerService", function ($interval) {
  var ticker;
  var callbacks = [];

  var tickCount = 0;
  var tickingSpeeds = [
      { name: 'Slow', value: 10000 },
      { name: 'Real-time', value: 1000 },
      { name: 'Fast', value: 100 }
  ];
  var tickingSpeed = tickingSpeeds[1].value;

  function triggerCallbacks() {
    tickCount++;
    angular.forEach(callbacks, function(callback) { callback() });
  };

  return {
    addCallback: function (callback) {
      callbacks.push(callback);
    },
    startTicking : function() {
      if (!angular.isDefined(ticker)){
        ticker = $interval(triggerCallbacks, tickingSpeed);
      }
    },
    updateTicking : function(interval) {
      tickingSpeed = interval;

      if (angular.isDefined(ticker)) {
        $interval.cancel(ticker);
        ticker = $interval(triggerCallbacks, tickingSpeed);
      }
    },
    tick: function() {
      if (!angular.isDefined(ticker)) {
        triggerCallbacks();
      }
    },
    stopTicking : function() {
      if (angular.isDefined(ticker)) {
        $interval.cancel(ticker);
        ticker = undefined;
      }
    },
    isRunning : function() {
      return angular.isDefined(ticker);
    },
    tickCount : function() {
      return tickCount;
    },
    tickingSpeed : tickingSpeed,
    tickingSpeeds : tickingSpeeds
  }

});
