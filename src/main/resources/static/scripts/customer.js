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
	user = $("#username").val();
	getCreditJson(user);
});

//withdraw button
$("#withdraw").click(function() {
	var errors = false;
	
	if ($("#amount").val() == "") {
		$("#amount").addClass("error");
		$.dialog('Please enter a valid amount');
		errors = true;
	}
	
	if (!numPattern.test($("#amount").val())) {
		$("#amount").addClass("error");
		$.dialog('Please enter a valid amount');
		errors = true;
	}
	
	$("#amount").change(function() {
		$("#amount").removeClass("error");
	});
	
	if(!errors) {
			$.confirm({
		    title: 'Withdraw?',
		    content: 'New balance will be: ' + (json.credit - $("#amount").val()),
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
		errors = true;
		$.dialog('Please enter a valid amount');
	}
	
	if (!numPattern.test($("#amount").val())) {
		$("#amount").addClass("error");
		$.dialog('Please enter a valid amount');
		errors = true;
	}
	
	$("#amount").change(function() {
		$("#amount").removeClass("error");
	});

	if(!errors) {
			$.confirm({
		    title: 'Deposit?',
		    content: 'New balance will be: ' + (parseFloat(json.credit) + parseFloat($("#amount").val())),
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
	var errors = false;
	
	if ($("#firstName").val() == "") {
		$("#firstName").addClass("error");
		errors = true;
	}
	
	if (!alphaPattern.test($("#firstName").val())) {
		$("#firstName").addClass("error");
		errors = true;
	}
	
	$("#firstName").change(function() {
		$("#firstName").removeClass("error");
	});
	
	if ($("#lastName").val() == "") {
		$("#lastName").addClass("error");
		errors = true;
	}
	
	if (!alphaPattern.test($("#lastName").val())) {
		$("#lastName").addClass("error");
		errors = true;
	}
	
	$("#lastName").change(function() {
		$("#lastName").removeClass("error");
	});
	
	if ($("#dob").val() == "") {
		$("#dob").addClass("error");
		errors = true;
	}
	
	$("#dob").change(function() {
		$("#dob").removeClass("error");
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