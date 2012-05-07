/**
 * Chart with top users
 * Must be defined here because of the onmouseover calls.
 */
var topUsersChart;


/**
 * Module with topUsers
 */
FlickrGraphr.modules["topUsers"] = {

    title : "Top users",
    
    apiUrl : "topUsers",
    
    
    load : function(){
    
        // TOP NAVIGATION
        var topnav = 'Choose range: <input type="text" name="dateFrom" id="topUsersDateFrom" value=""> - ';
        topnav += '<input type="text" name="dateFrom" id="topUsersDateTo" value=""> ';
        topnav += '<button id="topUsersRefresh">Refresh</button>';
        $("#topnav").html(topnav);
        
        // datepicking
        $("#topUsersDateFrom").datepicker({ dateFormat : 'yy-mm-dd'});
        $("#topUsersDateTo").datepicker({ dateFormat : 'yy-mm-dd'});
        
        // prepare dates now and previous week
        var prevDate = new Date();
        prevDate.setDate(prevDate.getDate() - 7);
        
        $('#topUsersDateFrom').datepicker("setDate",  prevDate);
        $('#topUsersDateTo').datepicker("setDate", new Date() );
        
        
        var module = this;
    
    
        // refresh
        $("#topUsersRefresh").button();
        $("#topUsersRefresh").click(function(){
            
            
            module.fetchTopScores($("#topUsersDateFrom").val(), $("#topUsersDateTo").val());            
            return false;
        });
        
        // fetch the top score with the default values
        module.fetchTopScores($("#topUsersDateFrom").val(), $("#topUsersDateTo").val());
    },
    
    
    template : "",
    
    
    
    /**
     * Called when user chooses the date and clicks on button
     * Also called when page starts with default values
     */
    fetchTopScores : function(startDate, endDate){
		
        var module = this;
        

		$.ajax({
			url: FlickrGraphr.apiUrl + this.apiUrl,
			data : {
				startDate: startDate,
				endDate: endDate
			},
            type : "get",
			success: function(result){
				module.pasteDataToPage(result);
			},
            error: function(jqXHR, textStatus, errorThrown){
                alert("Error while loading data from API: " + errorThrown);
            },
			dataType: "json"
		});
	},
	
	/**
     * Internal function, called when the ajax requests finishes
     */
	pasteDataToPage : function(entries){
    
        // prepare values
        var owners = new Array();
        var dates = new Array();
        var entriesByDates = new Array();
        
        // parse owners and scores
        for(var i in entries){
            var entry = entries[i];
            if((jQuery.inArray(entry.ownerid, owners)) == -1){
                owners.push(entry.ownerid);
            }
            
            var date = entry.date;
            
            // does the date exist in scores?
            // if not, create a new array
            if(!(date in entriesByDates)){
                entriesByDates[date] = new Array();
                dates.push(date);
            }
            
            // score
            entriesByDates[date].push(entry);
            
        }
        
        
        // data table
        var data = new google.visualization.DataTable();
        // add first column, date
        data.addColumn({
            type : 'date',
            label : 'Date'
        });
        
        // add owners and their tooltip
        for(var i in owners){
            var owner = owners[i];
            
            // owners
            data.addColumn({
                type : 'number',
                label : owner
            });
            // tooltip - in fact, the next column - ignored when the graph is drawn
            data.addColumn({
                type : 'string',
                role : 'tooltip',
                label : owner + ' details'
            });
        }
        // add entries (scores)
        for(var date in entriesByDates){
            var entriesInDay = entriesByDates[date];
            
            // empty row
            var row = new Array();
            
            // splits YYYY-MM-DD, and makes the JS object from it
            var parts = date.split("-");
            var dateparsed = new Date(parts[0], parts[1], parts[2]);
            
            // add date to row
            row.push(dateparsed);
            
            // for each entry, add value and tooltip with the photo
            for(var i in entriesInDay){
                var entry = entriesInDay[i];
                
                // score
                row.push(entry.score);
                
                // tooltip - HTML not available, yet
                var tooltip ="";
                tooltip += 'User: ' + entry.ownerid + "\n";
                tooltip += 'Score: ' + entry.score;
                row.push(tooltip);
            }
            
            // add the row to the table
            data.addRow(row);
        }
        
      
        // Create and draw the visualization.
        topUsersChart = new google.visualization.LineChart(document.getElementById('visualization'));
        
        topUsersChart.draw(data, {
            curveType: "none",
            width: 700,
            height: 350,
            chartArea:{
                width: '80%',
                height: '80%'
            },
            legend:{
                position:"top"
            }
        });
        
        // when user moves a mouse on the point
        // e = {column: ***, row: ***}
        function topUsersChartOverHandler(e) {
        
            if(!(e.row in dates)){
                return;
            }
            
            // divided by 2 because the "non-visible columns", the tooltip columns
            // -1 because the first is the date
            var entry = entriesByDates[dates[e.row]][(e.column - 1) / 2];
            FlickrGraphr.modules["topUsers"].showTooltip(entry);
        }


        function topUsersChartOutHandler() {
            FlickrGraphr.modules["topUsers"].hideTooltip();
        }
        
        // adding the handlers
        google.visualization.events.addListener(topUsersChart, 'onmouseover', topUsersChartOverHandler);
        google.visualization.events.addListener(topUsersChart, 'onmouseout', topUsersChartOutHandler);
	},
    
    /**
     * Shows the tooltip
     * The format is exactly the same as specified in the API
     */
    showTooltip : function(entry){
    
        var link = FlickrGraphr.getPhotoUrl(entry.photoid, entry.ownerid);
        var linkStream = FlickrGraphr.getPhotostreamUrl(entry.ownerid);
        
        var tooltip = 'User: <strong>' + entry.ownerid + "</strong><br />\n";
        tooltip += 'Score: <strong>' + entry.score + "</strong><br />\n";
        tooltip += 'Photo ID: <strong><a href="' + link + '">' + entry.photoid + "</a></strong><br />\n";
        tooltip += 'Photostream: <strong><a href="' + linkStream + '">' + linkStream + "</a></strong><br />\n";
        
        $("#topUsersInfo").html(tooltip);
        
    
    },
    
    hideTooltip : function(){
    }   


}



      