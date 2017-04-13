$(document).ready(function($) {
	
    $(".clickable").click(function() {
        window.location = $(this).attr("href");        
    });
    
    $('#table_').DataTable( {
    	"order": [[ 0, "desc"]]
    });  
    
    var userTable = $('#user-table').DataTable();
    
    $('#race-table').DataTable();
    
    var username;
    
    $.contextMenu({
        selector: 'td.context-menu',
        trigger: 'right',
        items: {
        	remove: {
        		name: "Delete User",
        		callback : function(key, opt) {        	
        			var username = opt.$trigger[0].innerText;
        			$.confirm({
        				title: 'Delete user?',
        				content: 'Do you wish to delete the user account for user ' + username + '?',
        				buttons: {        					
        					confirm: {
        						text: 'Delete',
            					btnClass: 'btn-red',
        						action: function() {
        							if (username != $('#username')[0].outerText) {
        								$.post('/users/delete/' + username, function(){
            								var tableRow = $("td").filter(function() {
            								    return $(this).text() == username;
            								}).closest("tr");
            								tableRow.fadeOut();
            								tableRow.remove();  
            							})
        							} else {
        								$.dialog("Can't delete currently logged in user!");
        							}        							
        						}
        					},
        					cancel: function() {        						
        					}
        				}
        			});
        		}    		
        	}, 
        	reset: {
    			name: 'Reset Password',
    			callback : function(key, opt) {
    				username = opt.$trigger[0].innerText;
    				console.log(username);
    				$('#modal-btn')[0].click();
    				$('#passwd').focus();
    			}
    		}    
        }
    });
    
    $('#save-new-pass-btn').click(function() {
    	var error = validatePasswordFields(); 
    	if (error != "") {
    		$('#modal-error').text(error);
    	} else {
    		// close modal and display dialog
    		$('#close-btn')[0].click();
    		
    		$.ajax({
				type: 'POST',
				url: 'api/user/' + username,
				dataType: 'json',
				data: {'password':$('#passwd').val()},
				success: function(result) {					
					$.dialog({
						title: 'Success!',
						content: 'Password updated.',
						autocancel: '500'
					});
				},
				error: function() {
					$.dialog({
						title: 'Failed!',
						content: 'Sorry, could not update password.',
						autocancel: '500'
					});
				}
			});     		
    	}
    });    
});

function validatePasswordFields() {
	var errors = "";
	var errorFree = true;

	if ($('#passwd').val().length < 8) {
		errors += "Password must be 8 characters minimum.";
		errorFree = false;
	}
	
	if ($('#passwd').val().length > 100) {
		errors += "Please must not exceed 100 characters.";
		errorFree = false;
	}	
	
	if ($('#passwd').val() != $('#confirm').val()) {
		if (errorFree) {
			errors += "Passwords do not match.";
		}
	}	 
	
	return errors;
}

$("#passwd").change(function() {
	$("#passwd").text("");
});