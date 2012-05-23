/**
 * Flickr API
 */
var FlickrApi = {

    API_KEY : "ed6204a240a139921dbbc30c1a1e6f65",
    
    people : {
        
        _cache : new Array(), // key = flickr ID, value = person Object
    
        getInfo : function(userId, success)
        {
            // whether in cache?
            if(userId in FlickrApi.people._cache){
                 success(FlickrApi.people._cache[userId]);
                 return;
            }
        
            // load by URL
            var url = FlickrApi._getUrl("flickr.people.getInfo", "user_id=" + userId);
            
            $.ajax({
                dataType: "jsonp",
                url: url,
                jsonpCallback: "jsonFlickrApi",
                success : function(data, textStatus, jqXHR){
                    FlickrApi.people._cache[userId] = data.person;
                    success(data.person);
                }        
            })
        },
    },
    
    
    _getUrl : function(method, parameters)
    {
        return "http://api.flickr.com/services/rest/?method=" + method + "&api_key=" + this.API_KEY + "&format=json&" + parameters;
    
    },
    
    
    _getIconUrl : function(userId, iconFarm, iconServer){
        return "http://farm" + iconFarm + ".staticflickr.com/" + iconServer + "/buddyicons/" + userId + ".jpg";
    
    }
    
}
