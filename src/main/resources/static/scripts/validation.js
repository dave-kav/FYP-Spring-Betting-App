var oddsPattern = /\d+\/\d+/;
var alphaPattern = /^[A-z]+$/;
var alphaNumPattern = /^[-\w\s]+$/;
var numPattern = /^\d*\.?\d*$/;

//validate translate form
$("#translateSubmit").click(function() {
	var errorFree = true;
	
	if ($("#race").val() == "") {
		$("#race").addClass("error");
		errorFree = false;
	}
	
	$("#race").change(function() {
		$("#race").removeClass("error");
	});

	if ($("#track").val() == "") {
		$("#track").addClass("error");
		errorFree = false;
	}
	
	$("#track").change(function() {
		$("#track").removeClass("error");
	});
	
	if (!alphaPattern.test($("#track").val())) {
		$("#track").addClass("error");
		errorFree = false;
	}
	
	if ($("#selection").val() == "") {
		$("#selection").addClass("error");
		errorFree = false;
	}
	
	if (!alphaNumPattern.test($("#selection").val())) {
		$("#selection").addClass("error");
		errorFree = false;
	}
	
	$("#selection").change(function() {
		$("#selection").removeClass("error");
	});
	
	if ($("#odds").val() == "" || $("#odds").val() == "0/0") {
		$("#odds").addClass("error");
		errorFree = false;
	}
	
	if (!oddsPattern.test($("#odds").val())) {
		$("#odds").addClass("error");
		errorFree = false;
		$.dialog('Invalid Format entered for odds - please follow the format of number/number e.g. 2/1');
	}
	
	$("#odds").change(function() {
		$("#odds").removeClass("error");
	});
	
	if (!errorFree)
		event.preventDefault();
});

//validate edit bet form
$("#editSubmit").click(function() {
	var errorFree = true;
	
	if ($("#stake").val() == "") {
		$("#stake").addClass("error");
		errorFree = false;
	}
	
	$("#stake").change(function() {
		$("#stake").removeClass("error");
	});

	if ($("#time").val() == "") {
		$("#time").addClass("error");
		errorFree = false;
	}
	
	$("#time").change(function() {
		$("#time").removeClass("error");
	});
	
	if ($("#track").val() == "") {
		$("#track").addClass("error");
		errorFree = false;
	}
	
	$("#track").change(function() {
		$("#track").removeClass("error");
	});
	
	if ($("#selection").val() == "") {
		$("#selection").addClass("error");
		errorFree = false;
	}
	
	$("#selection").change(function() {
		$("#selection").removeClass("error");
	});
	
	if ($("#odds").val() == "" || $("#odds").val() == "0/0") {
		$("#odds").addClass("error");
		errorFree = false;
	}
	
	if (!oddsPattern.test($("#odds").val())) {
		$("#odds").addClass("error");
		errorFree = false;
		$.dialog('Invalid Format entered for odds - please follow the format of number/number e.g. 2/1');
	}
	
	$("#odds").change(function() {
		$("#odds").removeClass("error");
	});
	
	if (!errorFree)
		event.preventDefault();
});

//validate add customer form
$("#customerSubmit").click(function(){
	var errorFree = true;
	
	if ($("#firstName").val() == "") {
		$("#firstName").addClass("error");
		errorFree = false;
	}
	
	if (!alphaPattern.test($("#firstName").val())) {
		$("#firstName").addClass("error");
		errorFree = false;
	}
	
	$("#firstName").change(function() {
		$("#firstName").removeClass("error");
	});

	if ($("#lastName").val() == "") {
		$("#lastName").addClass("error");
		errorFree = false;
	}
	
	if (!alphaPattern.test($("#lastName").val())) {
		$("#lastName").addClass("error");
		errorFree = false;
	}
	
	$("#lastName").change(function() {
		$("#lastName").removeClass("error");
	});
	
	if ($("#dob").val() == "") {
		$("#dob").addClass("error");
		errorFree = false;
	}
	
	$("#dob").change(function() {
		$("#dob").removeClass("error");
	});

	if ($("#username").val() == "") {
		$("#username").addClass("error");
		errorFree = false;
	}
	
	$("#username").change(function() {
		$("#username").removeClass("error");
	});

	if ($("#password").val() == "") {
		$("#password").addClass("error");
		errorFree = false;
	}

	if ($("#password").val().length < 8) {
		$("#password").addClass("error");
		errorFree = false;
		$.dialog('Password must be 8 characters in length or greater');
	}

	$("#password").change(function() {
		$("#password").removeClass("error");
	});
	
	if (!errorFree)
		event.preventDefault();	
});


//validate stake on upload button
$("#uploadSubmit").click(function(){
	var errorFree = true;
	
	if ($("#stake").val() == 0.0) {
		$("#stake").addClass("error");
		errorFree = false;
		$.dialog('Stake must be greater than 0');
	}
	
	$("#stake").change(function() {
		$("#stake").removeClass("error");
	});
	
	if (!errorFree)
		event.preventDefault();	
});