/**
 * Module dependencies.
 */

var express = require('express');
var passport = require('passport');
var FacebookStrategy = require('passport-facebook').Strategy;
var mongoose = require('mongoose');
var http = require('http');
var login = require('./routes/login');
var write = require('./routes/write');

//connect to mongodb
mongoose.connect('mongodb://heroku_app24454838:eldena5910ft7mvodp451vt5ck@ds031328.mongolab.com:31328/heroku_app24454838');

// var User = mongoose.model('Activity', { 
// 	firstName: String,
// 	lastName: String,
// 	url: String,
// });

// var kitty = new Cat({ name: 'Zildjian' });
// kitty.save(function (err) {
//   if (err) // ...
//   console.log('meow meow');
// });

var app = express();
app.use(express.logger());

// all environments
app.set('port', process.env.PORT || 3000);
app.use(express.static(__dirname + '/public/welcome'));

http.createServer(app).listen(app.get('port'), function(){
	console.log('Express server listening on port ' + app.get('port'));
});

app.configure(function() {
	app.use(express.static('public'));
	app.use(express.cookieParser());
	app.use(express.bodyParser());
	app.use(express.session({ secret: 'keyboard cat' }));
	app.use(express.logger('dev'));
	app.use(express.methodOverride());
	app.use(passport.initialize());
	app.use(passport.session());
	app.use(app.router);
});

//routes

// app.get('/', function(req, res) {
// 	res.sendfile('./public/welcome/index.html'); // load the single view file (angular will handle the page changes on the front-end)
// });

// app.get('/main', function(req, res) {
// 	res.sendfile('./public/main.html'); // load the single view file (angular will handle the page changes on the front-end)
// });

// app.get('/make-activity', function(req, res) {
// 	res.sendfile('./public/make_activity.html'); // load the single view file (angular will handle the page changes on the front-end)
// });

app.get('/login/fb', login.loginFacebook);
app.get('/login/success', login.loginSuccess);

// Redirect the user to Facebook for authentication.  When complete,
// Facebook will redirect the user back to the application at
//     /auth/facebook
app.get('/auth/facebook', passport.authenticate('facebook', { scope: ['read_stream', 'email']} ));

// Facebook will redirect the user to this URL after approval.  Finish the
// authentication process by attempting to obtain an access token.  If
// access was granted, the user will be logged in.  Otherwise,
// authentication has failed.
app.get('/auth/facebook/callback',
	passport.authenticate('facebook', {
		successRedirect: '/main',
		failureRedirect: '/'
}));


