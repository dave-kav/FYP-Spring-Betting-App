$(document).ready(function($) {
	
    $(".clickable").click(function() {
        window.location = $(this).attr("href");        
    });
    
    $('#table_').DataTable( {
    	"order": [[ 0, "desc"]]
    });  
    
    var userTable = $('#user-table').DataTable();
    
    $('#race-table').DataTable();
    
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
    				var username = opt.$trigger[0].innerText;
    				console.log(username);
    			}
    		}    
        }
    });
});

