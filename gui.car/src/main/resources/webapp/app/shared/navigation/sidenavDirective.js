app.directive('navigation', function (sidenavService) {
  return {
    restrict: "E",
    replace: true,
    templateUrl: "app/shared/navigation/sidenavTemplate.html",
    controller: function ($scope) {
      $scope.routes = sidenavService.routes;
      $scope.activeRoute = sidenavService.activeRoute;
    }
  };
});
