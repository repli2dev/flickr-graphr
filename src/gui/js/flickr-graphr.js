/**
 * Main class, which contains the basic information and settings
 * and handles the modules located in the "modules/" directory
 */
var FlickrGraphr = {
	
    // Map with all modules, the name is the key
     modules : new Array(),
    
    // Which module is active
    activeModule : false,
   
    // URL to the flickr-graphr API (relative or absolute, with ending slash)
	apiUrl : "api/",
    
    // base for the title
    titleBase : " | Graphr",
	
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
        document.title = FlickrGraphr.activeModule.title + FlickrGraphr.titleBase;
        
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
        return "http://flickr.com/photos/" + ownerid;
    }
	
}
