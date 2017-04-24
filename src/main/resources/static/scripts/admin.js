var alphaPattern = /^[A-z]+$/;
var alphaNumPattern = /^\w+$/;

//parse URL parameter and set active tab
var activeTab;
var url = window.location.href;
activeTab = url.split('?').pop().split('=').pop();

if (activeTab == 2) {
	$('#1').removeClass("active");
	$('#l1').removeClass("active");
	$('#2').addClass("active");
	$('#l2').addClass("active");
}

else if (activeTab == 3) {
	$('#1').removeClass("active");
	$('#l1').removeClass("active");
	$('#3').addClass("active");
	$('#l3').addClass("active");
}

else if (activeTab == 4) {
	$('#1').removeClass("active");
	$('#l1').removeClass("active");
	$('#4').addClass("active");
	$('#l4').addClass("active");
}


//validate add race form
$("#addRace").click(function() {
	var errors = false;
	var errorMessage = "";
	var trackError = false;
	
	if ($("#track").val() == "") {
		$("#track").addClass("error");
		errors = true;
		trackError = true;
		errorMessage += "Please enter a track name.<br>";
	}
	
	if (!alphaPattern.test($("#track").val())) {
		$("#track").addClass("error");
		errors = true;
		if (!trackError) {
			trackError = true;
			errorMessage += "Track name must contain only alphabetic characters.<br>";
		}
	}
	
	if ($("#track").val().length > 24){
		$("#track").addClass("error");
		errors = true;
		if (!trackError) {
			trackError = true;
			errorMessage += "Track name must not exceed 24 characters in length.<br>";
		}
	}
	
	$("#track").change(function() {
		$("#track").removeClass("error");
	});
	
	if ($("#time").val() == "") {
		$("#time").addClass("error");
		errors = true;
		errorMessage += "Please enter a time.<br>";
	}
	
	$("#time").change(function() {
		$("#time").removeClass("error");
	});
	
	if ($("#runners").val() < 2) {
		$("#runners").addClass("error");
		errors = true;
		errorMessage += "Minimum of 2 horses per race.<br>";
	}
	
	if ($("#runners").val() > 40) {
		$("#runners").addClass("error");
		errors = true;
		errorMessage += "Maximum runners in a race is 40.<br>";
	}
	
	$("#runners").change(function() {
		$("#runners").removeClass("error");
	});
	
	if (errors) {
		event.preventDefault();
		$.dialog({
			title: 'Errors!',
			content: errorMessage
		});
	}
});

//validate add user form
$("#addUser").click(function() {
	var errors = false;
	var errorMessage = "";
	var userNameError = false;
	var passwordError = false;
	
	if ($("#user").val() == "") {
		$("#user").addClass("error");
		errors = true;
		userNameError = true;
		errorMessage += "Please enter a value for username.<br>";
	}
	
	if ($("#user").val().length > 24) {
		$("#user").addClass("error");
		errors = true;
		if (!userNameError)  
			errorMessage += "Username cannot exceed 24 characters.<br>";
	}
	
	$("#user").change(function() {
		$("#user").removeClass("error");
	});
	
	if ($("#password").val() == "") {
		$("#password").addClass("error");
		errors = true;
		errorMessage += "Please enter a password.<br>";
		passwordError = true;
	}

	if ($("#password").val().length < 8) {
		$("#password").addClass("error");
		errors = true;
		if (!passwordError) {
			errorMessage += 'Password must be 8 characters in length or greater.<br>';
			passwordError = true;
		}
	}
	
	if ($("#password").val().length > 100) {
		$("#password").addClass("error");
		errors = true;
		errorMessage += 'Password cannot exceed 100 characters.<br>';
	}

	$("#password").change(function() {
		$("#password").removeClass("error");
	});
	
	if (errors) {
		event.preventDefault();
		$.dialog({
			title: 'Errors!',
			content: errorMessage
		});
	}
});


