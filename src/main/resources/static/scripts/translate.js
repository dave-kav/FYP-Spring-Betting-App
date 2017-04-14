//set value of track field upon selection of time
$('#time').bind('input', function(){
	if ($('#time').val() != "") {
		$('#track').val("");
		$('#selection').val("");
		Promise.resolve($.ajax({
			type: "GET",
			dataType: "JSON",
			url: "/api/race/" + $('#time').val(),
			success: function(data) {
				$('#track').val(data.track);
				$('#horseList').empty();
				for (var i = 0; i < data.allHorses.length; i++) {
					$('#horseList').append($("<option></option>")
							.attr("value", data.allHorses[i].name)
							.attr("label", data.allHorses[i].number));
				}
	        }
		}));
	}
});

//set value of time and race upon selection of horse
$('#selection').bind('input', function(){
	Promise.resolve($.ajax({
		type: "GET",
		dataType: "JSON",
		url: "/api/race/horse/" + $('#selection').val(),
		success: function(data) {
			$('#track').empty();
			$('#track').val(data.track);
			$('#time').val(data.time);
        }
	}));
});

$('#track').bind('input', function(){
	Promise.resolve($.ajax({
		type: "GET",
		dataType: "JSON",
		url: "/api/race/track/" + $('#track').val(),
		success: function(data) {
			$('#timesList').empty();
			$('#selection').val("");
			$('#time').val("");
			for (var i = 0; i < data.times.length; i++) {
				$('#timesList').append($("<option></option>").attr("value", data.times[i]));
			}
			$('#horseList').empty();
			for (var i = 0; i < data.times.length; i++) {
				$('#horseList').append($("<option></option>")
						       .attr("value", data.allHorses[i].name)
						       .attr("label", data.allHorses[i].number));
			}
        }
	}));
});

$('#selection').click(function() {
	if ($('#selection').val() == 'No text detected in selection section!') {
		$('#selection').val("");
		$('#selection').removeClass("error");
	}
});

$('#odds').click(function() {
	if ($('#odds').val() == 'No text detected in odds section!') {
		$('#odds').val("");
		$('#odds').removeClass("error");
	}
});