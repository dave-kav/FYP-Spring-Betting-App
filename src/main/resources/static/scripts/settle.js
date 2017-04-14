var dialog_content;

function getContent() {
	var message = "You have selected " + $('#winner').val() + " as the winner.<br>";
	if ($('#place0').val() !== undefined) {
		message += "You have chosen " + $('#place0').val() + " as placed second.<br>"; 
	} 
	if ($('#place1').val() !== undefined) {
		message += "You have chosen " + $('#place1').val() + " as placed third.<br>"; 
	} 
	if ($('#place2').val() !== undefined) {
		message += "You have chosen " + $('#place2').val() + " as placed fourth.<br>"; 
	} 
	return message;
}

$('#settle-race-btn').click(function() {
	event.preventDefault();
	dialog_content = getContent();
	$.confirm({
		title: "Settle " + $('p.lead.text-center').text() + "?",
		content: dialog_content,
		buttons: {		
			confirm: {
				btnClass: 'btn-warning',
				action: function() { 
					$('form').submit();
				}
			},			
			cancel: function() {
				event.preventDefault();
			}
		}
	});
});

$('#edit-settle-btn').click(function() {
	event.preventDefault();
	$.confirm({
		type: 'red',
		title: "Reset this race?",
		content: "Please proceed with extreme caution! Choosing to reset this race will result "
			+ "in all unpaid bets being unsettled - this includes bets placed via the mobile app."
			+ "Any bets paid out in store will have been paid in error and the shop will be at a deficit.",
		buttons: {
			confirm: {
				btnClass: 'btn-red',
				action: function() {
					$.confirm({
						type: 'red',
						title: "Reset this race?",
						content: "Are you really sure??",
						buttons: {
							confirm: {
								btnClass: 'btn-red',
								action: function() {
									$('#edit-form').submit();
								}
							},
							cancel: function() {								
							}
						}
					});
				}
			},
			cancel: function() {
				$('#edit-form').submit();
			}
		}
	});
});