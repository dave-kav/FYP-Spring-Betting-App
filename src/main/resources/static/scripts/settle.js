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