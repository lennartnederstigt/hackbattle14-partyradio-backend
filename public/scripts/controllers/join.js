'use strict';

angular.module('hackbattle14PartyradioFrontendApp')
	.controller('JoinCtrl', function ($scope, $location, userService) {
		$scope.username = '';

		$scope.submitUsername = function() {
			if ($scope.username !== '') {
				userService.setUsername($scope.username);
				$location.path('/main');
			}
		};

		// function toQueryString(obj) {
		// 	var parts = [];
		// 	for (var i in obj) {
		// 		if (obj.hasOwnProperty(i)) {
		// 			parts.push(encodeURIComponent(i) + '=' + encodeURIComponent(obj[i]));
		// 		}
		// 	}
		// 	return parts.join('&');
		// }

	});

