
/*
 * GET home page.
 */

exports.loginFacebook = function(req, res){

	// redirect to page
	res.sendfile('./public/main.html');
};

exports.loginSuccess = function(req, res){
	
	// redirect to page
	res.sendfile('./public/make_activity.html');
};