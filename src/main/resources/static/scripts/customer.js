var numPattern = /^\d*\.?\d*$/;

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
		errors = true;
	}
	
	if (!numPattern.test($("#amount").val())) {
		$("#amount").addClass("error");
		alert("wrong format - numbers only");
		errors = true;
	}
	
	$("#amount").change(function() {
		$("#amount").removeClass("error");
	});
	
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
	
	if (errors == true)
    	event.preventDefault();

});

//deposit button
$("#deposit").click(function() {
	event.preventDefault();
	
	if ($("#amount").val() == "") {
		$("#amount").addClass("error");
	}
	
	if (!numPattern.test($("#amount").val())) {
		$("#amount").addClass("error");
		alert("wrong format - letters only");
	}
	
	$("#amount").change(function() {
		$("#amount").removeClass("error");
	});

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
	        }
	    }
	});
});

//validate edit customer form
$("#editCustomer").click(function() {
	var errorFree = true;
	
	if (!errorFree)
		event.preventDefault();
});