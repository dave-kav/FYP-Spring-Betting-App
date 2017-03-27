$(document).ready(function(){
	var messageDialog;
	if ($('#bet-img')[0] != undefined) {
		messageDialog = $.confirm({
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
			url: 'https://ec2-34-250-24-17.eu-west-1.compute.amazonaws.com/ocr',
			//below line is for dev. only, remove in prod.
//			url: 'http://localhost:8081/ocr',
			dataType: 'json',
			data: {'imgData':imgData},
//			contentType: 'application/json',
			success: function(result) {
				$('#track').val(result.ocr.track);
				var trackVal = $('#track').val();
				var trackObj = $('#tracksList').find("option[value='" + trackVal + "']");
				if (trackObj != null && trackObj.length > 0) {
					console.log("valid track");
				} else {
					$('#track').addClass('error');
				}
				
				$('#time').val(result.ocr.time + ':00');
				var timeVal = $('#time').val();
				var timeObj = $('#timesList').find("option[value='" + timeVal + "']");
				if (timeObj != null && timeObj.length > 0) {
					console.log("valid time");
					$('#time').trigger('input');
				} else {
					$('#time').addClass('error');
					console.log('time: error: ' + $('#time').val());
				}
				
				$('#selection').val(result.ocr.selection);
				var horseVal = $('#selection').val();
				var horseObj;
				if ($.isNumeric(horseVal)) {
					horseObj = $('#horseList').find("option[label='" + horseVal + "']");
					$('#selection').val(horseObj.val())
				} else {
					horseObj = $('#horseList').find("option[value='" + horseVal + "']");
				}
		
				if (horseObj != null && horseObj.length > 0) {
					console.log("valid selection");
				} else {
					$('#selection').addClass('error');
				}
				
				$('#odds').val(result.ocr.odds);
				
				messageDialog.$$ok.trigger('click');
			},
			error: function() {
				messageDialog.$$ok.trigger('click');
				$.dialog({
					title: 'Analysis Failed',
					content: 'Sorry, could not analyze this image.',
					autocancel: '500'
					});
			}
		});
	}	
});