'use strict';

angular.module('hackbattle14PartyradioFrontendApp')
	.controller('EchonestCtrl', function ($scope, $http) {
		$scope.artist = '';

		$scope.$watch('artist', function() {
			console.log($scope.artist);
			if ($scope.artist !== '') {
				$http.get('http://developer.echonest.com/api/v4/song/search?api_key=IPGN04JAMI9DIDKKB&artist=' + $scope.artist).success(function(data) {
					$scope.songs = data.response.songs;
				});
			} else {
				$scope.songs = [];
			}
		});
	});
