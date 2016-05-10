app.controller('SidenavController', function ($scope, $timeout, $mdSidenav, $mdUtil) {
  $scope.toggleLeft = buildToggler('left');

  function buildToggler(navID) {
    var debounceFn =  $mdUtil.debounce(function(){
          $mdSidenav(navID)
            .toggle();
        },300);
    return debounceFn;
  }
});
