'use strict';

angular.module('hackbattle14PartyradioFrontendApp')
	.controller('MainCtrl', function ($scope, userService, WebSocket) {
		$scope.username = userService.getUsername();


		// // make sure we have connection
		// WebSocket.onopen(function() {
		// 	console.log('websocket connection');

		// 	$scope.sendSuggestion = function() {
		// 		var object = JSON.stringify({event : "upvote"});
		// 		WebSocket.send(object);
		// 		$scope.suggestion = '';
		// 	};
		// });

		// WebSocket.onmessage(function(event) {
		// 	console.log('websocket message: ', event.data);
		// });

	});
