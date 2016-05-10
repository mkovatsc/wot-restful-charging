app.config(function ($routeProvider) {
  $routeProvider.when("/", {
    templateUrl: "app/components/content/dashboardView.html",
    name: "Dashboard"
  }).otherwise({
    redirectTo: "/"
  });
});
