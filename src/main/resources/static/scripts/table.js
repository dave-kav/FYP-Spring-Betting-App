$(document).ready(function($) {
	
    $(".clickable").click(function() {
        window.location = $(this).attr("href");        
    });
    
    $('#table_').DataTable( {
    	"order": [[ 0, "desc"]]
    });  
    
    $('.datepicker').datepicker();
});

