angular.module("ChargerUI")
  .config(function ($routeProvider) {
    $routeProvider.when("/", {
      templateUrl: "app/components/content/dashboardView.html",
    }).when("/test", {
      templateUrl: "app/components/content/testView.html",
    }).otherwise({
      redirectTo: "/"
    });
  });
