var numPattern = /^\d*\.?\d*$/;
var alphaPattern = /^[A-z]+$/;

//get user account balance via ajax
var json;
function getCreditJson(user) {
	return Promise.resolve($.ajax({
		type: "GET",
		dataType: "json",
		url: "/api/account/" + user,
		success: function(data) {
			json = data;
		}
	}));
}

//store user credit info on page load
var user;
$(document).ready(function() {
	user = $("#customername").val();
	console.log(user);
	getCreditJson(user);
});

//withdraw button
$("#withdraw").click(function() {
	var errors = false;
	
	if ($("#amount").val() == "") {
		$("#amount").addClass("error");
		$.dialog('Please enter a valid amount.');
		errors = true;
	}
	
	if ($("#amount").val() == 0) {
		$("#amount").addClass("error");
		if (!errors) {
			$.dialog('0 is not a valid amount.');
			errors = true;
		}
	}
	
	if ($("#amount").val() > 100000) {
		$("#amount").addClass("error");
		$.dialog('Transaction value cannot exceed €100,000.');
		errors = true;
	}
	
	if (!numPattern.test($("#amount").val())) {
		$("#amount").addClass("error");
		$.dialog('Please enter a valid amount.');
		errors = true;
	}
	
	$("#amount").change(function() {
		$("#amount").removeClass("error");
	});
	
	if(!errors) {
			$.confirm({
		    title: 'Withdraw?',
		    content: 'New balance will be: ' + (json.credit.toFixed(2) - $("#amount").val()),
		    buttons: {
		        confirm: function () {
		        	var txType = $("<input>").attr("type", "hidden").attr("name", "withdraw").val("withdraw");
		        	$('#updateBalance').append($(txType));
		        	$('#updateBalance').submit();
		        },
		        cancel: function () {
		        	event.preventDefault();
		        }
		    }
		});
	}
});

//deposit button
$("#deposit").click(function() {
	var errors = false;
	
	if ($("#amount").val() == "") {
		$("#amount").addClass("error");
		$.dialog('Please enter a valid amount.');
		errors = true;
	}
	
	if ($("#amount").val() == 0) {
		$("#amount").addClass("error");
		if (!errors) {
			$.dialog('0 is not a valid amount.');
			errors = true;
		}
	}
	
	if ($("#amount").val() > 100000) {
		$("#amount").addClass("error");
		$.dialog('Transaction value cannot exceed €100,000.');
		errors = true;
	}
	
	
	$("#amount").change(function() {
		$("#amount").removeClass("error");
	});

	if(!errors) {
			$.confirm({
		    title: 'Deposit?',
		    content: 'New balance will be: ' + (parseFloat(json.credit) + parseFloat($("#amount").val())).toFixed(2),
		    buttons: {
		        confirm: function () {
		        	var txType = $("<input>").attr("type", "hidden").attr("name", "deposit").val("deposit");
		        	$('#updateBalance').append($(txType));
		        	$('#updateBalance').submit();
		        },
		        cancel: function () {
		        	event.preventDefault();
		        }
		    }
		});
	}
});

//validate edit customer form
$("#editCustomer").click(function() {
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