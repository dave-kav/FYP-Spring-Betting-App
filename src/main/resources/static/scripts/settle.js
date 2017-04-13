$('#settle-race-btn').click(function() {
	event.preventDefault();
	$.confirm({
		title: "Settle " + $('p.lead.text-center').text() + "?",
		buttons: {
			confirm: function() {
				$('form').submit();
			},
			cancel: function() {
				event.preventDefault();
			}
		}
	});
});