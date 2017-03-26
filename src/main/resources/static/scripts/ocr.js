$(document).ready(function(){
	if ($('#bet-img')[0] != undefined) {
		var thing = $.confirm({
			title: "",
			content: 'Please wait, analyzing betting slip image...',
			buttons: {
				ok : {
					action : function() {}
				}
	 		}
		});
		
		var img = $('#bet-img')[0].src;
		var imgData = JSON.stringify(img);
		
		$.ajax({
			type: 'POST',
			url: 'https://ec2-34-250-24-17.eu-west-1.compute.amazonaws.com:8080/ocr',
			//below line is for dev. only, remove in prod.
//			url: 'http://localhost:8081/ocr',
			dataType: 'json',
			data: {'imgData':imgData},
//			contentType: 'application/json',
			success: function(result) {
				console.log(result);
				console.log(result.ocr);
				
				$('#selection').val(result.ocr.selection);
				var horseVal = $('#selection').val();
				var horseObj = $('#horseList').find("option[value='" + horseVal + "']");
				if (horseObj != null && horseObj.length > 0) {
					console.log("valid selection");
				} else {
					$('#selection').addClass('error');
				}
				
				$('#time').val(result.ocr.time);
				var timeVal = $('#time').val();
				var timeObj = $('#timesList').find("option[value='" + timeVal + "']");
				if (timeObj != null && timeObj.length > 0) {
					console.log("valid selection");
				} else {
					$('#time').addClass('error');
				}
				
				$('#track').val(result.ocr.track);
				var trackVal = $('#track').val();
				var trackObj = $('#tracksList').find("option[value='" + trackVal + "']");
				if (trackObj != null && trackObj.length > 0) {
					console.log("valid selection");
				} else {
					$('#track').addClass('error');
				}
				
				$('#odds').val(result.ocr.odds);
				
				thing.$$ok.trigger('click');
			},
			error: function() {
				thing.$$ok.trigger('click');
				$.dialog({
					title: 'Analysis Failed',
					content: 'Sorry, could not analyze this image.',
					autocancel: '500'
					});
			}
		});
	}	
});