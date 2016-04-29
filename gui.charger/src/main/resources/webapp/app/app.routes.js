app.config(function ($routeProvider) {
  $routeProvider.when("/", {
    templateUrl: "app/components/content/dashboardView.html",
    name: "Dashboard"
  }).when("/test", {
    templateUrl: "app/components/content/testView.html",
    name: "Test"
  }).otherwise({
    redirectTo: "/"
  });
});
