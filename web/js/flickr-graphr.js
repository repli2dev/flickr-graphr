/**
 * Main class, which contains the basic information and settings
 * and handles the modules located in the "modules/" directory
 */
var FlickrGraphr = {
	
    // Map with all modules, the name is the key
    modules : new Array(),
    
    // Which module is active
    activeModule : false,
   
    // URL to the flickr-graphr API (relative path with ending slash)
	API_URL : "api/",
    
    // flickr-graphr API names
    
    API_USER_SCORE : "top-users", // adds user to the graph
    API_TOP_USERS_BY_DATE : "top-day-ids", // returns top IDs for the selected time
    API_TAG_SCORE : "top-tags", // for 2 weeks
    API_INTERESTING_TAGS : "interesting-tags", //  for 2 weeks
    API_UPLOADED_PHOTOS : "uploaded-photos", // photos uploaded in selected interval
    
    
    stat : {
        OK : "ok",
        FAIL : "fail"
    },
    
    ERROR_USER_NOT_EXISTS : 2,
    
    // base for the title
    TITLE_BASE : " | Graphr",
	
    /**
     * Loads the module
     *
     * @param {string} moduleName
     * @return {boolean} True if module loaded, false otherwise
     */
    loadModule : function(moduleName){
    
        // if already active, do nothing
        if(moduleName == this.activeModule){
            return true;
        }
        
       
        // if not preloaded
        if(!(moduleName in FlickrGraphr.modules))
        {
            // get module script
            $.ajax({
                url: 'modules/' + moduleName + '/script.js',
                dataType: 'script',
                async: false,
                success : function(){
                
                    // get template
                    $.ajax({
                        url: 'modules/' + moduleName + '/template.html',
                        dataType: 'text',
                        async: false,
                        success : function(data){
                            FlickrGraphr.modules[moduleName].template = data;
                        
                        },
                        error : function(jqXHR, textStatus, errorThrown)
                        {
                            // failed to get the template
                            alert(errorThrown + ' (' + moduleName + " teplate file)\n");
                        }
                    });
                },
                error : function(jqXHR, textStatus, errorThrown)
                {
                    // failed to get the module
                    alert(errorThrown + ' (modules/' + moduleName + '/script.js)\n');
                }
            });
        }
        
        // if not properly loaded
        if(!(moduleName in FlickrGraphr.modules)){
            alert("Module " + moduleName + " not found.");
            return false;
        }
        
        
        // set the module as active
        FlickrGraphr.activeModule = FlickrGraphr.modules[moduleName];
        
        // load the template file
        $("#module-template").html(FlickrGraphr.activeModule.template);
        
        // autoruns
        FlickrGraphr.activeModule.load();
        
        // set title
        document.title = FlickrGraphr.activeModule.title + FlickrGraphr.TITLE_BASE;
        
        return true;
        
    },
    
    /**
     * Returns the link to the HTML page with the photo
     * @param {string} photoid Photo ID
     * @param {string} owner Owner ID
     * @return {string} URL
     */
    getPhotoUrl : function(photoid, ownerid){
        return "http://flickr.com/photos/" + ownerid + "/" + photoid;
    },
    
    /**
     * Returns the link to the HTML page the user's photostream
     * @param {string} owner Owner ID
     * @return {string} URL
     */
    getPhotostreamUrl : function(ownerid){
        return "http://www.flickr.com/photos/" + ownerid;
    },
    
    /**
     * Returns the link to users interesting photos on flickriver
     * @param {string} owner Owner ID
     * @return {string} URL
     */
    getFlickRiverInterestingUrl : function(ownerid){
        return "http://www.flickriver.com/photos/" + ownerid + "/popular-interesting/";
    },
    
    
    /**
     * Displays a message
     * @param {string} title Title
     * @param {string} msg Message
     */
    messageBox: function(title, msg){
        var dialogId = "dialog-" + password(10);
        var dialog = "<div id=\"" + dialogId + "\" title=\"" + title + "\">";
        dialog += "<p>" + msg + "</p>";
        dialog += "</div>";
        $("body").append(dialog);
        $("#" + dialogId).dialog({
            modal: true,
			buttons: {
				OK: function() {
					$( this ).dialog( "close" );
				}
			}
        });
        
    }
    
    
    
}
