describe('Testing the StatusController', function() {

    beforeEach(module('ChargerUI'));

    var $controller;

    beforeEach(inject(function(_$controller_){
      $controller = _$controller_;
    }));

    describe('$scope', function() {
      var $scope, controller;

      beforeEach(function() {
        $scope = {};
        controller = $controller('statusController', { $scope: $scope });
      });

      it('updates the status of the supply equipment', inject(function(statusService) {
        var se_data = { a : "b", c : 12.3, d : false};

        statusService.se.set(se_data);
        expect($scope.se.data.a).toEqual("b");
        expect($scope.se.data.c).toEqual(12.3);
        expect($scope.se.data.d).toEqual(false);
      }));

      it('updates the status of the electric vehicle', inject(function(statusService) {
        var ev_data = { aa : "bb", cc : 32.1, dd : true};

        statusService.ev.set(ev_data);
        expect($scope.ev.data.aa).toEqual("bb");
        expect($scope.ev.data.cc).toEqual(32.1);
        expect($scope.ev.data.dd).toEqual(true);
      }));

    });

});
