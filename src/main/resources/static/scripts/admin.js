var alphaPattern = /^[A-z]+$/;
var alphaNumPattern = /^\w+$/;

var activeTab;
var url = window.location.href;
activeTab = url.split('?').pop().split('=').pop();
console.log(activeTab);

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

//validate add race form
$("#addRace").click(function() {
	var errors = false;
	
	if ($("#track").val() == "") {
		$("#track").addClass("error");
		errors = true;
	}
	
	if (!alphaPattern.test($("#track").val())) {
		$("#track").addClass("error");
		errors = true;
	}
	
	$("#track").change(function() {
		$("#track").removeClass("error");
	});
	
	if ($("#time").val() == "") {
		$("#time").addClass("error");
		errors = true;
	}
	
	$("#time").change(function() {
		$("#time").removeClass("error");
	});
	
	if ($("#runners").val() < 1) {
		$("#runners").addClass("error");
		errors = true;
		$.dialog("Can't have a race with 0 runners");
	}
	
	$("#runners").change(function() {
		$("#runners").removeClass("error");
	});
	
	if (errors)
		event.preventDefault();
});

//validate add user form
$("#addUser").click(function() {
	var errors = false;
	
	if ($("#username").val() == "") {
		$("#username").addClass("error");
		errors = true;
	}
	
	if (!alphaNumPattern.test($("#username").val())) {
		$("#username").addClass("error");
		errors = true;
	}
	
	$("#username").change(function() {
		$("#username").removeClass("error");
	});
	
	if ($("#password").val() == "") {
		$("#password").addClass("error");
		errors = true;
	}

	if ($("#password").val().length < 8) {
		$("#password").addClass("error");
		errors = true;
		$.dialog('Password must be 8 characters in length or greater');
	}

	$("#password").change(function() {
		$("#password").removeClass("error");
	});
	
	if (errors)
		event.preventDefault();	
});


