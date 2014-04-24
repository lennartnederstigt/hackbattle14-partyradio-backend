'use strict';

var APP_VERSION = 'v0.0.1';
var API_VERSION = 'v0.0.0';
// var NG_VERSION = angular.version.full + ' (' + angular.version.codeName + ')';

angular
	.module('hackbattle14PartyradioFrontendApp', [
		'ngRoute',
		'angular-websocket'
	])
	.config(function ($routeProvider) {
		$routeProvider
			.when('/main', {
				templateUrl: 'views/main.html',
				controller: 'MainCtrl'
			})
			.when('/join', {
				templateUrl: 'views/join.html',
				controller: 'JoinCtrl'
			})
			.when('/suggest', {
				templateUrl: 'views/suggest.html',
				controller: 'SuggestCtrl'
			})
			.otherwise({
				redirectTo: '/suggest'
			});
	})
	.config(function(WebSocketProvider){
    WebSocketProvider
      .prefix('')
      .uri('ws://localhost:8000');
	})
	.run(function($rootScope) {
		$rootScope.API_VERSION = API_VERSION;
		$rootScope.APP_VERSION = APP_VERSION;
	});
