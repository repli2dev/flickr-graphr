/**
 * File executed after the page is prepared
 */
$(document).ready(function(){
    
    // menu links as buttons
    $("#nav a").button();
    
    // change module    
    $(window).hashchange( function(){
    
        var hash = location.hash;
        
        if(hash == '' || hash == '#'){
            hash = 'index';
        }
        
        // Iterate over all nav links, setting the "selected" class as-appropriate.
        $('#nav a').each(function(){
            var that = $(this);
            that[ that.attr( 'href' ) === hash ? 'addClass' : 'removeClass' ]( 'graphr-active' );
        });
        
        // Change the module
        hash = hash.replace('#', '');
        FlickrGraphr.loadModule(hash);
        

    });
    
    // trigger the hashchange for the first time
    $(window).hashchange();
});