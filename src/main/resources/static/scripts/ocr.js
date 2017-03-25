$(document).ready(function(){
	var thing = $("#loading").dialog('Analyzing betting slip...');
	var file = $('#bet-img')[0].src;
//	.split("data:image/jpeg;base64,").pop();
	$.ajax({
		type: 'GET',
		url: 'http://ec2-34-250-24-17.eu-west-1.compute.amazonaws.com:8080/ocr',
//		data: JSON.stringify({'img_data':file}),
		dataType: 'json',
		success: function(result) {
			$('#selection').val(result.selection);
			$('#time').val(result.time);
			$('#track').val(result.track);
			$('#odds').val(result.odds);
			thing = $.dialog("close");
		},
		error: function() {
			console.log("didn't work");
		}
	});
});