/**
 * Chart with top users
 * Must be defined here because of the onmouseover calls.
 */
var usersChart;


/**
 * Module with topUsers
 */
FlickrGraphr.modules["users"] = {

    title : "Users",
    
    scores : new Array(), // key: userId => Array(score1, score2)...;

    usersToDisplay : new Array(),
    
    
    userInfo : new Array(),
    
    dates : new Array(),
    
    dataTable : false,

    loadScore : function(userId)
    {
        var module = this;
        
        var startDate =  $('#topUsersDateFrom').val();
        var endDate =  $('#topUsersDateTo').val();

    
        // if not, fetch it
        $.ajax({
			url: FlickrGraphr.API_URL + FlickrGraphr.API_USER_SCORE,
			data : {
                name: userId,
				"start-date": startDate,
				"end-date": endDate
			},
            type : "get",
			success: function(result){
                
                if(result.stat == FlickrGraphr.stat.OK){
                
                    var data = result.data;
                    
                    // if length = 0, generate zeros
                    if(data.length == 0){
                        data = module.generateZeroScores(userId);
                    }
                    
                      
                
                    module.addScoresToGraph(result.data);
                }
				
			},
            error: function(jqXHR, textStatus, errorThrown){
                FlickrGraphr.messageBox("Response fail", "Error while loading data from API (ID: " + userId + "): " + errorThrown);
            },
			dataType: "json"
		});
    },
    
    checkWhetherUserAlreadyLoaded : function(userId)
    {
        for(var i in this.usersToDisplay)
        {
            var userObject = this.usersToDisplay[i];
            if(userObject.userId == userId){
                return true;
            }
        }

        return false;
    },
    
    
    searchScore : function(searchString)
    {
        if(searchString == ""){
            FlickrGraphr.messageBox("Searching canceled", "String to search cannot be empty.");
            return;
        }
    
        var module = this;
        
        var startDate =  $('#topUsersDateFrom').val();
        var endDate =  $('#topUsersDateTo').val();

    
        // if not, fetch it
        $.ajax({
			url: FlickrGraphr.API_URL + FlickrGraphr.API_USER_SCORE,
			data : {
                name: searchString,
				"start-date": startDate,
				"end-date": endDate
			},
            type : "get",
			success: function(result){
                
                if(result.stat == FlickrGraphr.stat.OK){
                
                    var data = result.data;
                    
                    // if length = 0, generate zeros
                    if(data.length == 0){
                        FlickrGraphr.messageBox("No score found", "User has no score yet.");
                        return;
                    }
                    
                    $("#userSearchInput").val("");
                      
                    // user already loaded?
                    if(module.checkWhetherUserAlreadyLoaded(data[0].userId)){
                        FlickrGraphr.messageBox("User already in graph", data[0].displayName + " is already in the graph.");
                        return;
                    }               
                    
                    
                    
                    FlickrGraphr.messageBox("User loaded", data[0].displayName + "'s score added to the graph.");
                    module.usersToDisplay.push(data[0]);
                    
                
                    module.addScoresToGraph(result.data);
                }else if(result.error.code == FlickrGraphr.ERROR_USER_NOT_EXISTS){
                    FlickrGraphr.messageBox("Response fail", "User not exists.");
                }
				
			},
            error: function(jqXHR, textStatus, errorThrown){
                FlickrGraphr.messageBox("Response fail", "Error while loading data from API: " + errorThrown);
            },
			dataType: "json"
		});
    },
    
    
    
    load : function(){
    
        // TOP NAVIGATION
        var topnav = 'Choose range: <input type="text" name="dateFrom" id="topUsersDateFrom" value=""> - ';
        topnav += '<input type="text" name="dateFrom" id="topUsersDateTo" value=""> ';
        topnav += '<button id="topUsersRefreshButton">Set</button>';
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
        $("#topUsersRefreshButton").button();
        $("#topUsersRefreshButton").click(function(){
        
            module.prepareScoreGraph();
        
            for(var i in module.usersToDisplay)
            {
                var userObject = module.usersToDisplay[i];
                module.loadScore(userObject.userId);
            }

        
        });
    
        // top users
        $("#loadTopUsersButton").button();
        $("#loadTopUsersButton").click(function(){
            // loads top users to the table
            
            
            // the date is the center of the two dates
            var date1 = new Date($('#topUsersDateFrom').val());
            var date2 = new Date($('#topUsersDateTo').val());
            
            var date = new Date((date2.getTime() - date1.getTime()) / 2 + date1.getTime());
            
            
            module.loadTopUsers(date.format("yyyy-mm-dd"));            
            return false;
        });
        
        // search
        $("#userSearchButton").button();
        $("#userSearchButton").click(function(){
            // searches the user
            module.searchScore($("#userSearchInput").val());   
            
            return false;
        });
        
        // graph
        this.prepareScoreGraph();
        
    },
    
    
    template : "",
    
    
    loadTopUsers : function(date){
        
        var module = this;
        
        
         // if not, fetch it
        $.ajax({
			url: FlickrGraphr.API_URL + FlickrGraphr.API_TOP_USERS_BY_DATE,
			data : {
				date: date,
				count: 10
			},
            type : "get",
			success: function(result){
                
                if(result.stat == FlickrGraphr.stat.OK){
                    for(var i in result.data){
                    
                        
                        // user already loaded?
                        if(module.checkWhetherUserAlreadyLoaded(result.data[i].userId)){
                            var name = result.data[i].displayName;
                            if(typeof name == 'undefined'){
                                name = result.data[i].userId;
                            }
                            FlickrGraphr.messageBox("User already in graph", name + " is already in the graph.");
                            continue;
                        }                   
                    
                    
                    
                        module.addUserToDisplay(result.data[i]);
                    }
                    
                }           
				
			},
            error: function(jqXHR, textStatus, errorThrown){
                FlickrGraphr.messageBox("Response fail", "Error while loading data from API: " + errorThrown);
            },
			dataType: "json"
		});
    
    },
    
    
    
    
    addUserToDisplay : function(userObject){
        this.usersToDisplay.push(userObject);
        this.loadScore(userObject.userId);
    },
    
    
    
    
    
    generateZeroScores : function(userId){
        return {
            userId : userId,
            displayName : userId,
            score: 0
        }    
    },
    
    prepareScoreGraph : function(){
          
        this.dates = new Array();
        this.userInfo = new Array();
        var startDate = new Date($('#topUsersDateFrom').val());
        var endDate = new Date($('#topUsersDateTo').val());
        
      
        // counts the dates in the interval
        for(var date = startDate; date <= endDate; date.setDate(date.getDate() + 1))
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
        usersChart = new google.visualization.LineChart(document.getElementById('visualization'));
        
        /*usersChart.draw(this.dataTable, {
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
        
        */
        var module = this;
        // when user moves a mouse on the point
        // e = {column: ***, row: ***}
        function usersChartOverHandler(e) {
        
            var user = module.userInfo[e.column - 1];
            module.showTooltip(user);
        }


        function usersChartOutHandler() {
            FlickrGraphr.modules["users"].hideTooltip();
        }
        
        // adding the handlers
        google.visualization.events.addListener(usersChart, 'onmouseover', usersChartOverHandler);
        google.visualization.events.addListener(usersChart, 'onmouseout', usersChartOutHandler);
        
        
    
    
    },
    
	/**
     * Internal function, called when the ajax requests finishes
     */
	addScoresToGraph : function(scores){
    
        
        // add to global scores
        this.scores[scores[0].userId] = scores;
    
        // fetch name form the first score
        this.dataTable.addColumn('number', scores[0].displayName);
        
        // userinfo
        this.userInfo.push({ userId : scores[0].userId, displayName : scores[0].displayName});
        
        var col = this.dataTable.getNumberOfColumns() - 1;
        var rowsFound = new Array();
        
        
        // for each score, add row
        for(var i in scores)
        {
            score = scores[i];
            
            
           // var oldDate = score.date;
            
            // parse date to JS date
            var date = new Date(score.date);
            
            score.date = date;
            
            
            
            // match scores with dates in the table
            for(var row = 0; row < this.dates.length; row++)
            {
                // already found row
                if(jQuery.inArray(row, rowsFound) != -1) continue;
            
                // reset value
                this.dataTable.setValue(row, col, 0);
                var dateFromTable = this.dates[row].toString();
                
                // if found, set value, zero otherwise
                if(score.date.toString() == dateFromTable){
                    rowsFound.push(row);
                    this.dataTable.setValue(row, col, score.score);
                    break;
                }
            }            
        }
      
        // redraw chart
        usersChart.draw(this.dataTable, {
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
	},
    
    /**
     * Shows the tooltip
     * The format is exactly the same as specified in the API
     */
    showTooltip : function(user){
    
        //var link = FlickrGraphr.getPhotoUrl(entry.photoId, entry.userId);
        var linkStream = FlickrGraphr.getPhotostreamUrl(user.userId);
        var riverStream = FlickrGraphr.getFlickRiverInterestingUrl(user.userId);
        
        
        
        
        var tooltip = '<p><strong>User:</strong> ' + user.displayName + "</p>\n";
        tooltip += '<p><strong>User ID:</strong> ' + user.userId + "</p>\n";
        tooltip += '<p><strong>Photostream:</strong> <a href="' + linkStream + '">' + linkStream + "</a></p>\n";
        tooltip += '<p><strong>Flickriver:</strong> <a href="' + riverStream + '">' + riverStream + "</a></p>\n";
        
        $("#userDetail").html(tooltip);
        
    
    },
    
    hideTooltip : function(){
    },
    
    getEmptyScore : function(userId, date){
        return {
            userId : userId,
            date : date        
        }    
    }

}



      