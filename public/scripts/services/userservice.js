'use strict';

angular.module('hackbattle14PartyradioFrontendApp')
	.service('userService', function UserService() {
		var username = '';

		this.setUsername = function(name) {
			console.log('userService: setUsername to ' + name);
			username = name;
		};
		this.getUsername = function() {
			return username;
		};
	});
