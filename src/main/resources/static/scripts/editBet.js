$(document).ready(function($) {
	
	// payout button confirm dialog
	$('#payout-btn').click(function(){
		event.preventDefault();
		$.confirm({
			title: "Payout",
			content: "Do you want to payout this bet?",
			buttons: {
				confirm: {
					btnClass: 'btn-warning',
					text: 'Yes',
					action: function() {
						$('#payout-btn').fadeOut()
						
						// update bet via ajax
						$.ajax({
							type: 'POST',
							url: '/api/bet/',
							dataType: 'json',
							data: {'betID': $('#betID').val()},
							success: function() {
								// show success dialog
								$.confirm({
									title: "Payout",
									type: 'green',
									content: "Please payout " + $('#winnings').val() + ". Press enter to close.",
									buttons: {
										closeFunc: {
											text: 'Close',
											btnClass: 'btn-green',
											keys: ['enter']
										}
									},
								});
							},
							error: function() {
								// show success dialog
								$.confirm({
									title: "Error",
									type: 'red',
									content: "Could not payout this bet!. Press enter to close.",
									buttons: {
										closeFunc: {
											text: 'Close',
											btnClass: 'btn-red',
											keys: ['enter']
										}
									},
								});
							}
						});					
					}
				},
				cancel: {					
				}
			}
		});
	});
	
	// update button confirm dialog
	
	
	
});