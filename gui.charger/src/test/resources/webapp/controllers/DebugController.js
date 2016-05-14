describe('Testing the DebugController', function() {

    beforeEach(module('ChargerUI'));

    var $controller;

    beforeEach(inject(function(_$controller_){
      $controller = _$controller_;
    }));

    describe('$scope.debugmsgs', function() {
      var $scope, controller;

      beforeEach(function() {
        $scope = {};
        controller = $controller('debugController', { $scope: $scope });
      });

      it('adds a debug message to the list', inject(function(debugService) {
        var currentdate = new Date();
        var datetime = ('0' + currentdate.getHours()).slice(-2) + ":"
                        + ('0' + currentdate.getMinutes()).slice(-2) + ":"
                        + ('0' + currentdate.getSeconds()).slice(-2);
        var testMsg = "This is a test!";

        expect($scope.debugmsgs.length).toEqual(0);

        debugService.pushMsg(datetime, testMsg);
        expect($scope.debugmsgs.length).toEqual(1);
        expect($scope.debugmsgs[0].time).toEqual(datetime);
        expect($scope.debugmsgs[0].message).toEqual(testMsg);
      }));

    });

});
