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
        }
    },
    
    photos : {
        
        _cache : new Array(), // key = tag name, value = collection with photos
        
        
        _getPhoto : function(farm_id, server_id, photo_id, secret, size)
        {
            return "http://farm" + farm_id + ".staticflickr.com/" + server_id + "/" + photo_id + "_" + secret + "_" + size + ".jpg";
        },
        
        _getPhotoUrl : function(owner_id, photo_id)
        {
            return "http://www.flickr.com/photos/" + owner_id + "/" + photo_id;
        },
    
    
        search : function(tag, min_upload_date, max_upload_date, per_page, success)
        {
            // whether in cache?
            if(tag in FlickrApi.photos._cache){
                 success(FlickrApi.photos._cache[tag]);
                 return;
            }
        
            // load by URL
            var url = FlickrApi._getUrl("flickr.photos.search", "&per_page=" + per_page + "&sort=interestingness-desc&tags=" + tag + "&min_upload_date=" + min_upload_date + "&max_upload_date=" + max_upload_date);
            
            $.ajax({
                dataType: "jsonp",
                url: url,
                jsonpCallback: "jsonFlickrApi",
                success : function(data, textStatus, jqXHR){
                    FlickrApi.photos._cache[tag] = data.photos.photo;
                    success(data.photos.photo);
                }        
            })
        }
        
        
        
    },
    
    
    _getUrl : function(method, parameters)
    {
        return "http://api.flickr.com/services/rest/?method=" + method + "&api_key=" + this.API_KEY + "&format=json&" + parameters;
    
    },
    
    
    _getIconUrl : function(userId, iconFarm, iconServer){
        return "http://farm" + iconFarm + ".staticflickr.com/" + iconServer + "/buddyicons/" + userId + ".jpg";
    
    }
    
}
