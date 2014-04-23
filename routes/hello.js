

exports.rick = function(req, res){

	var antwoord = { antwoord : "hoi..."};
	// antwoord
	res.write(JSON.stringify(antwoord));
	return res;
};