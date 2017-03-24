var oddsPattern = /\d+\/\d+/;
var alphaPattern = /^[A-z]+$/;
var alphaNumPattern = /^[-\w\s]+$/;
var numPattern = /^\d*\.?\d*$/;

//validate translate form
$("#translateSubmit").click(function() {
	var errorFree = true;
	var errorMessage = "";
	var trackError = false;
	var oddsError = false;
	
	if ($("#race").val() == "") {
		$("#race").addClass("error");
		errorFree = false;
		errorMessage += "Please enter a valid race time.<br>";
	}
	
	$("#race").change(function() {
		$("#race").removeClass("error");
	});

	if ($("#track").val() == "") {
		$("#track").addClass("error");
		errorFree = false;
		trackError = true;
		errorMessage += "Please enter a valid track name.<br>";
	}
	
	if (!alphaPattern.test($("#track").val())) {
		$("#track").addClass("error");
		errorFree = false;
		if (!trackError)
			errorMessage += "Please enter a valid track name containing alphabetical characters.<br>";
	}
	
	$("#track").change(function() {
		$("#track").removeClass("error");
	});
	
	if ($("#selection").val() == "") {
		$("#selection").addClass("error");
		errorFree = false;
		errorMessage += "Please enter a valid selection.<br>";
	}
	
	$("#selection").change(function() {
		$("#selection").removeClass("error");
	});
	
	if ($("#odds").val() == "" || $("#odds").val() == "0/0<br>") {
		$("#odds").addClass("error");
		errorFree = false;
		oddsError = true;
		errorMessage += "Please enter valid odds.<br>";
	}
	
	if (!oddsPattern.test($("#odds").val())) {
		$("#odds").addClass("error");
		errorFree = false;
		if (!oddsError) {
			errorMessage += 'Invalid Format entered for odds - please follow the format of number/number e.g. 2/1<br>';
			oddsError = true;
		}
	}
	
	if ($("#odds").val().indexOf("/0") >= 0) {
		$("#odds").addClass("error");
		errorFree = false;
		if (!oddsError) {
			errorMessage += "Odds cannot end in '/0'<br>";
			oddsError = true;
		}
	}
	
	if ($("#odds").val().split("/")[0].length > 8 || $("#odds").val().split("/")[0].length > 8) {
		$("#odds").addClass("error");
		errorFree = false;
		if (!oddsError) 
			errorMessage += "Value entered for odds cannot exceed 8 digits in length on either side of the fraction.<br>";
	} 
	
	//validate that comboboxes contain valid values
	var horseVal = $('#selection').val();
	var horseObj = $('#horseList').find("option[value='" + horseVal + "']");
	if (horseObj != null && horseObj.length > 0) {
		console.log("valid selection");
	} else {
		errorFree = false;
		errorMessage += "The horse you entered is not a valid selection.<br>";
	}
	
	var timeVal = $('#time').val();
	var timeObj = $('#timesList').find("option[value='" + timeVal + "']");
	if (timeObj != null && timeObj.length > 0) {
		console.log("valid selection");
	} else {
		errorFree = false;
		errorMessage += "The time you entered is not a valid selection.<br>";
	}
	
	var trackVal = $('#track').val();
	var trackObj = $('#tracksList').find("option[value='" + trackVal + "']");
	if (trackObj != null && trackObj.length > 0) {
		console.log("valid selection");
	} else {
		errorFree = false;
		errorMessage += "The track you entered is not a valid selection.<br>";
	}
	
	$("#odds").change(function() {
		$("#odds").removeClass("error");
	});
	
	if (!errorFree) {
		event.preventDefault();
		$.dialog({
			title: 'Errors!',
			content: errorMessage
		});
	}
});

//validate edit bet form
$("#editSubmit").click(function() {
	var errorFree = true;
	var errorMessage = "";
	var trackError = false;
	var oddsError = false;
	
	if ($("#stake").val() == 0.0) {
		$("#stake").addClass("error");
		errorFree = false;
		errorMessage += 'Stake must be greater than 0.<br>';
	}
	
	if ($("#stake").val() > 100000) {
		$("#stake").addClass("error");
		errorFree = false;
		errorMessage += 'Stake cannot exceed 100,000.<br>';
	}
	
	$("#stake").change(function() {
		$("#stake").removeClass("error");
	});

	if ($("#time").val() == "") {
		$("#time").addClass("error");
		errorFree = false;
		errorMessage += "Please enter a valid race time.<br>";
	}
	
	$("#time").change(function() {
		$("#time").removeClass("error");
	});
	
	if ($("#track").val() == "") {
		$("#track").addClass("error");
		errorFree = false;
		trackError = true;
		errorMessage += "Please enter a valid track name.<br>";
	}
	
	if (!alphaPattern.test($("#track").val())) {
		$("#track").addClass("error");
		errorFree = false;
		if (!trackError)
			errorMessage += "Please enter a valid track name containing alphabetical characters.<br>";
	}
	
	$("#track").change(function() {
		$("#track").removeClass("error");
	});
	
	if ($("#selection").val() == "") {
		$("#selection").addClass("error");
		errorFree = false;
		errorMessage += "Please enter a valid selection.<br>";
	}
	
	$("#selection").change(function() {
		$("#selection").removeClass("error");
	});
	
	if ($("#odds").val() == "" || $("#odds").val() == "0/0") {
		$("#odds").addClass("error");
		errorFree = false;
		oddsError = true;
		errorMessage += "Please eneter valid odds.<br>";
	}
	
	if (!oddsPattern.test($("#odds").val())) {
		$("#odds").addClass("error");
		errorFree = false;
		if (!oddsError) {
			errorMessage += 'Invalid Format entered for odds - please follow the format of number/number e.g. 2/1<br>';
			oddsError = true;
		}
	}
	
	if ($("#odds").val().indexOf("/0") >= 0) {
		$("#odds").addClass("error");
		errorFree = false;
		if (!oddsError) {
			errorMessage += "Odds cannot end in '/0'";
			oddsError = true;
		}
	}
	
	if ($("#odds").val().split("/")[0].length > 8 || $("#odds").val().split("/")[0].length > 8) {
		$("#odds").addClass("error");
		errorFree = false;
		if (!oddsError) 
			errorMessage += "Value entered for odds cannot exceed 8 digits in length on either side of the fraction.<br>";
	} 
	
	if (!errorFree) {
		event.preventDefault();
		$.dialog({
			title: 'Errors!',
			content: errorMessage
		});
	}
});

//validate add customer form
$("#customerSubmit").click(function(){
	var errorFree = true;
	var errorMessage = "";
	var firstNameError = false;
	var lastNameError = false;
	var userNameError = false;
	var passwordError = false;
	
	if ($("#firstName").val() == "") {
		$("#firstName").addClass("error");
		errorFree = false;
		firstNameError = true;
		errorMessage += "Please enter a value for first name.<br>";
	}
	
	if (!alphaPattern.test($("#firstName").val())) {
		$("#firstName").addClass("error");
		errorFree = false;
		if (!firstNameError) {
			firstNameError = true;
			errorMessage += "First name can contain only letters.<br>";
		}
	}
	
	if ($("#firstName").val().length > 20) {
		$("#firstName").addClass("error");
		errorFree = false;
		errorMessage += "First name cannot exceed 20 characters.<br>";
	}
	
	$("#firstName").change(function() {
		$("#firstName").removeClass("error");
	});

	if ($("#lastName").val() == "") {
		$("#lastName").addClass("error");
		errorFree = false;
		firstNameError = true;
		errorMessage += "Please enter a value for last name.<br>";
	}
	
	if (!alphaPattern.test($("#lastName").val())) {
		$("#lastName").addClass("error");
		errorFree = false;
		if (!firstNameError) {
			firstNameError = true;
			errorMessage += "Last name can contain only letters.<br>";
		}
	}
	
	if ($("#lastName").val().length > 20) {
		$("#lastName").addClass("error");
		errorFree = false;
		errorMessage += "Last name cannot exceed 20 characters.<br>";
	}
	
	$("#lastName").change(function() {
		$("#lastName").removeClass("error");
	});
	
	if ($("#dob").val() == "") {
		$("#dob").addClass("error");
		errorFree = false;
		errorMessage += "Please enter a value for 'Date of Birth'.<br>";
	}
	
	//validate customer is over 18
	var today = new Date();
	var dob = new Date($("#dob").val());
	var diff = Math.abs(today - dob);
	var years = diff / 31536000000;
	
	if (years < 18) {
		$("#dob").addClass("error");
		errorFree = false;
		errorMessage += "Customer is too young to open a betting account! Must be 18+<br>";
	}
	
	$("#dob").change(function() {
		$("#dob").removeClass("error");
	});

	if ($("#username").val() == "") {
		$("#username").addClass("error");
		errorFree = false;
		userNameError = true;
		errorMessage += "Please enter a value for username.<br>";
	}
	
	if ($("#username").val().length > 24) {
		$("#username").addClass("error");
		errorFree = false;
		if (!userNameError)  
			errorMessage += "Username cannot exceed 24 characters.<br>";
	}
	
	$("#username").change(function() {
		$("#username").removeClass("error");
	});

	if ($("#password").val() == "") {
		$("#password").addClass("error");
		errorFree = false;
		errorMessage += "Please enter a password.<br>";
		passwordError = true;
	}

	if ($("#password").val().length < 8) {
		$("#password").addClass("error");
		errorFree = false;
		if (!passwordError) {
			errorMessage += 'Password must be 8 characters in length or greater.<br>';
			passwordError = true;
		}
	}
	
	if ($("#password").val().length > 100) {
		$("#password").addClass("error");
		errorFree = false;
		errorMessage += 'Password cannot exceed 100 characters.<br>';
	}

	$("#password").change(function() {
		$("#password").removeClass("error");
	});
	
	if (!errorFree) {
		event.preventDefault();
		$.dialog({
			title: 'Errors!',
			content: errorMessage
		});
	}
});


//validate stake on upload button
$("#uploadSubmit").click(function(){
	var errorFree = true;
	
	if ($("#stake").val() == 0.0) {
		$("#stake").addClass("error");
		errorFree = false;
		$.dialog('Stake must be greater than 0<br>');
	}
	
	if ($("#stake").val() > 100000) {
		$("#stake").addClass("error");
		errorFree = false;
		$.dialog('Stake cannot exceed 100,000<br>');
	}
	
	$("#stake").change(function() {
		$("#stake").removeClass("error");
	});
	
	if (!errorFree)
		event.preventDefault();	
});