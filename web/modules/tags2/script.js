/**
 * Chart with tags
 * Must be defined here because of the onmouseover calls.
 */
var tagsChart;


/**
 * Page with top tags
 */
FlickrGraphr.modules["tags2"] = {

    METHOD : "our", // our, their
    
    title : "Tags",
    
    TAGS_PER_BATCH : 40,
    alreadyLoadedTags : 0,
    
    /**
     * Method called when the module is loaded
     */
    load : function(){

        this.alreadyLoadedTags = 0;
        
        var module = this;
    
        $("#topnav").html('');
        
        this.prepareScoreGraph();
        
        
        // searching
        $("#tagSearchInput").keypress(function(e) {
            if(e.keyCode == 13) {
                // searches the tag
                module.searchScore($("#tagSearchInput").val());  
            }
        });
        $("#tagSearchButton").button();
        $("#tagSearchButton").click(function(){
            // searches the tag
            module.searchScore($("#tagSearchInput").val());   
            return false;
        });
        
        
        // top tags
        $("#loadTopRecentTagsButton").button();
        $("#loadTopRecentTagsButton").click(function(){
            // searches the tag
            module.fetchTopRecentTags(module.alreadyLoadedTags);   
            return false;
        });
        this.fetchTopRecentTags(0);
        
    },
    
    /**
     * Page source, is overriden by the template.html file, if available
     */
    template : "",
    
    /**
     * Array with tag names in the graph
     */
    tagInfo : new Array(),
    
    /**
     * Array with dates (last 14 days)
     */
    dates : new Array(),
    
    /**
     * Loads the score for TAG NAME
     *
     * @param {string} tagName
     */
    searchScore : function(tagName)
    {
        var module = this;
        $.ajax({
			url: FlickrGraphr.API_URL + FlickrGraphr.API_TAG_SCORE,
			data : {
                tag: tagName,
				method : module.METHOD
			},
            type : "get",
			success: function(result){
                
                if(result.stat == FlickrGraphr.stat.OK){
                    var data = result.data;
                    
                    if(data.length == 0){
                        FlickrGraphr.messageBox("No data found", "No data found for tag: " + tagName);
                        return;
                    }
                    
                    module.addScoresToGraph(data);
                    
                    $("#tagSearchInput").val("");
                    
                }else{
                    FlickrGraphr.messageBox("Response fail", "Error while loading data: " + result.error.message);
                }
				
			},
            error: function(jqXHR, textStatus, errorThrown){
                FlickrGraphr.messageBox("Response fail", "Error while loading data from API (tag: " + tagName + "): " + errorThrown);
            },
			dataType: "json"
		});
    },
    
    
    /**
     * Loads the top tags and then, for each tag loads score
     *
     * @param {string} tagName
     */
    fetchTopRecentTags : function(skip){
        var module = this;
        $.ajax({
			url: FlickrGraphr.API_URL + FlickrGraphr.API_INTERESTING_TAGS,
			data : {
                start: skip,
				records : module.TAGS_PER_BATCH
			},
            type : "get",
			success: function(result){
                
                if(result.stat == FlickrGraphr.stat.OK){
                    var data = result.data;
                    
                    if(data.length == 0){
                        FlickrGraphr.messageBox("No data found", "No more tags found, loading the first tags.");
                        module.alreadyLoadedTags = 0;
                        module.fetchTopRecentTags(0);
                        return;
                    }
                    
                    module.createTagCloud(data);
                    
                }else{
                    FlickrGraphr.messageBox("Response fail", "Error while loading data: " + result.error.message);
                }
				
			},
            error: function(jqXHR, textStatus, errorThrown){
                FlickrGraphr.messageBox("Response fail", "Error while loading data from API");
            },
			dataType: "json"
		});
        module.alreadyLoadedTags += module.TAGS_PER_BATCH;
    
    
    },
    
    createTagCloud : function(data){
    
        var module = this;
        var tags = [];
        var maxscore = data.length * 20;
        for(var i in data){
            tags.push({tag: data[i].tag, count: maxscore});
            maxscore = maxscore / 1.5;
        }
        
        
        //var tags = [{tag: "computers", count: 56}, {tag: "mobile" , count :12}, ... ];
        
        
        $("#tagCloud").tagCloud(tags,{
            click: function(tag, event){
                module.searchScore(tag);            
            }        
        });

    
    },
    
    /**
     * Prepares the graph
     */
    prepareScoreGraph : function(){
          
        this.dates = new Array();
        this.tagInfo = new Array();
        
        // START DATE - 14 days back
        var startDate = new Date();
        startDate.setDate(startDate.getDate() - 14);
        
        // END DATE - now
        var endDate = new Date();
        
        // counts the dates in the interval
        for(var date = new Date(startDate.getTime()); date <= endDate; date.setDate(date.getDate() + 1))
        {
            this.dates.push(new Date(date.getTime()));
        }
        
        // data table
        this.dataTable = new google.visualization.DataTable();

        // add first column, date
        this.dataTable.addColumn({
            type : 'date',
            label : 'Date'
        });
        
        // add dates
        for(var i in this.dates){
            var dat = this.dates[i];
            
            // empty row
            var row = new Array();
            
            // add date to row
            row.push(dat);
            
            this.dataTable.addRow(row);
        }
        
      
        // Create and draw the visualization.
        tagChart = new google.visualization.LineChart(document.getElementById('visualization'));
        
        var module = this;
       

        
        function tagChartSelectHandler() {
        
            var selectedItem = tagChart.getSelection()[0];
          
            if(selectedItem)
            {
             
                var tag = module.tagInfo[selectedItem.column - 1];
                module.tooltipLoading();
                var min_upload_date = Math.round(startDate / 1000);
                var max_upload_date = Math.round(endDate / 1000);
                
                // 16 per page
                FlickrApi.photos.search(tag.tag, min_upload_date, max_upload_date, 16, function(photos){
                    module.showTooltip(tag.tag, photos);
                });
            }
        }

        
        // adding the handlers
        google.visualization.events.addListener(tagChart, 'select', tagChartSelectHandler);
    },
    
    /**
     * Shows the tooltip
     * The format is exactly the same as specified in the API
     */
    showTooltip : function(tag, photos){
     
        
        var tooltip = "";
        tooltip += '<h2> ' + tag + " interesting photos</h2>\n";
        
        
        for(var i in photos){
            
            var photo = photos[i];
            
            // s = square
            var photoSquare = FlickrApi.photos._getPhoto(photo.farm, photo.server, photo.id, photo.secret, "s");
            var photoUrl = FlickrApi.photos._getPhotoUrl(photo.owner, photo.id);
            
            tooltip += "<a class=\"photo\" target=\"_blank\" href=\"" + photoUrl + "\" title=\"" + photo.title + "\"><img src=\"" + photoSquare+ "\" alt=\"" + photo.title + "\" title=\"" + photo.title + "\" /></a>";
        
        }        
        
        $("#tagDetail").html(tooltip);
        
    
    },
    
    tooltipLoading : function(){
        $("#tagDetail").html("Loading photos for selected tag <img src=\"images/loading.gif\" alt=\"Loading...\">");
    },
    
    /**
     * Internal function, called when the ajax requests finishes
     */
	addScoresToGraph : function(scores){
    
        // fetch name form the first score
        this.dataTable.addColumn('number', scores[0].tag);
        
        // tag info
        this.tagInfo.push({tag : scores[0].tag});
        
        var col = this.dataTable.getNumberOfColumns() - 1;
        var rowsFound = new Array();

        
        // for each score, add row
        for(var i in scores)
        {
            score = scores[i];
            
            // match scores with dates in the table
            for(var row = 0; row < this.dates.length; row++)
            {
                // already found row
                if(jQuery.inArray(row, rowsFound) != -1) continue;
            
                // reset value
                this.dataTable.setValue(row, col, 0);
                var dateFromTable = this.dates[row].format("yyyy-mm-dd");
                
                // if found, set value, zero otherwise
                if(score.date == dateFromTable){
                    rowsFound.push(row);
                    this.dataTable.setValue(row, col, score.score);
                    break;
                }
            }            
        }
      
        // redraw chart
        tagChart.draw(this.dataTable, {
            curveType: "none",
            width: 700,
            height: 350,
            chartArea:{
                width: '80%',
                height: '80%',
                left: '0'
            },
            legend:{
                position:"right"
            },
            vAxis : {
                textPosition : "none"
            }
        });
	}
}