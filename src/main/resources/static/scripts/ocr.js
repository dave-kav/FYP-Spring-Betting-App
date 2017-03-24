$(document).ready(function(){
	var file = $('#bet-img')[0].src;
//	.split("data:image/jpeg;base64,").pop();
	$.ajax({
		type: 'POST',
		url: 'http://ec2-34-250-24-17.eu-west-1.compute.amazonaws.com:8080/ocr',
		data: {img_data:file},
		dataType: 'json',
		success: function(result) {
			console.log(result);
		},
		error: function() {
			console.log("didn't work");
		}
	});
});