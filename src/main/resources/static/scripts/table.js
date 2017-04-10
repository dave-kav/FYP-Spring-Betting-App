$(document).ready(function($) {
    $(".clickable").click(function() {
        window.location = $(this).attr("href");        
    });
    $('#table_').DataTable();
});

