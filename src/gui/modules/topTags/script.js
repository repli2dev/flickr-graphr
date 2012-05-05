/**
 * Page with top tags
 */
FlickrGraphr.modules["topTags"] = {

    title : "Top tags",
    
    /**
     * Method called when the module is loaded
     */
    load : function(){
    
        $("#topnav").html('');
    
    },
    
    /**
     * Page source, is overriden by the template.html file, if available
     */
    template : ""
}