var express = require('express');
var spotify = require('spotify');
var app = express();

app.use(express.logger());

var port = process.env.PORT || 5000;
app.listen(port, function() {
  console.log("Listening on " + port);
});

app.get('/', function(req, res) {
  res.sendfile('public/index.html');
})

app.use(express.static(__dirname + '/public'));