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
		
		//ocr for stake on upload page
		if (~$(location).attr('href').indexOf('upload')) {
			$.ajax({
				type: 'POST',
				url: 'https://ec2-34-250-24-17.eu-west-1.compute.amazonaws.com/ocr/stake',
				dataType: 'json',
				data: {'imgData':imgData},
				success: function(result) {
					if (!$.isNumeric(result.ocr.stake))
						$('#stake').addClass('error');
					 else 
						$('#stake').val(result.ocr.stake);

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
		
		//ocr on translate page
		else {
			$.ajax({
				type: 'POST',
				url: 'https://ec2-34-250-24-17.eu-west-1.compute.amazonaws.com/ocr',
				dataType: 'json',
				data: {'imgData':imgData},
				success: function(result) {//					
					$('#time').val(result.ocr.time + ':00');
					var timeVal = $('#time').val();
					var timeObj = $('#timesList').find("option[value='" + timeVal + "']");
					if (timeObj != null && timeObj.length > 0) {
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
					if ($('#odds').val() == 'No text detected in odds section!')
						$('#odds').addClass('error');
					
					$("#odds").change(function() {
						$("#odds").removeClass("error");
					});
					
					$("#selection").change(function() {
						$("#odds").removeClass("error");
					});
					
					$("#track").change(function() {
						$("#odds").removeClass("error");
					});
					
					$("#time").change(function() {
						$("#odds").removeClass("error");
					});
					
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
	}	
});