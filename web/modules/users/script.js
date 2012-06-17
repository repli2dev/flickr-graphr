/**
 * Chart with top users
 * Must be defined here because of the onmouseover calls.
 */
var usersChart = {};


/**
 * Module with topUsers
 */
FlickrGraphr.modules["users"] = {

    // Title shown in the browser
    title : "Users",
    
    // Array with scores - key: userId => Array(score1, score2)...;
    scores : new Array(), 

    // Users which are displayed
    usersToDisplay : new Array(),
    
    // User info - all users
    userInfo : new Array(),
    
    // Users display names - key = userId
    displayNames : new Array(),
    
    // Selected dates
    dates : new Array(),
    
    // Source table
    dataTable : false,
    
    // module template - overriden by template.html
    template : "",
    
    // timer for drawing
    drawTimeout : {},
    
    /**
     * Prepares the module
     *
     */
    load : function(){
        
        // clears the previous data
        this.scores = new Array();
        this.usersToDisplay = new Array();
        this.userInfo = new Array();
        this.dates = new Array();
        this.dataTable = false;
        
        
        
        // TOP NAVIGATION
        var topnav = 'Choose range: <input type="text" name="dateFrom" id="topUsersDateFrom" value=""> - ';
        topnav += '<input type="text" name="dateFrom" id="topUsersDateTo" value=""> ';
        topnav += '<button id="topUsersRefreshButton">Set</button>';
        $("#topnav").html(topnav);
        
        // datepicking
        $("#topUsersDateFrom").datepicker({ dateFormat : 'yy-mm-dd', maxDate: "-1D"});
        $("#topUsersDateTo").datepicker({ dateFormat : 'yy-mm-dd', maxDate: "-1D"});
        
        // prepare dates now and previous week
        var prevDate = new Date();
        prevDate.setDate(prevDate.getDate() - 9);
        
        var nowDate = new Date();
        nowDate.setDate(nowDate.getDate() - 2);
        
        $('#topUsersDateFrom').datepicker("setDate",  prevDate);
        $('#topUsersDateTo').datepicker("setDate", nowDate );
        
        
        var module = this;
    
        // refresh
        $("#topUsersRefreshButton").button();
        $("#topUsersRefreshButton").click(function(){
            
            var start = new Date($('#topUsersDateFrom').val());
            var end = new Date($('#topUsersDateTo').val());
            
            if(end <= start){
                FlickrGraphr.setMessage("Invalid date - The start and end date are in collision.", "error");
                return;
            }
        
        
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
            var date2 = new Date($('#topUsersDateTo').val());
            module.loadTopUsers(date2.format("yyyy-mm-dd"));            
            
            
            // OLD METHOD
            // the date is the center of the two dates
            //  var date1 = new Date($('#topUsersDateFrom').val());   
            // var date = new Date((date2.getTime() - date1.getTime()) / 2 + date1.getTime());
            
            
            return false;
        });
        
        // search
        $("#userSearchInput").keypress(function(e) {
            if(e.keyCode == 13) {
                // searches the user
                module.searchScore($("#userSearchInput").val());  
            }
        });
        $("#userSearchButton").button();
        $("#userSearchButton").click(function(){
            // searches the user
            module.searchScore($("#userSearchInput").val());   
            return false;
        });
        
        // graph
        this.prepareScoreGraph();
        
    },
    
    
    

    /**
     * Loads user's score and adds it to the graph
     *
     * @param {string} userId
     */
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
                        module.displayNames[userId] = userId;
                    }else{
                        module.displayNames[userId] = data[0].displayName;
                    }
                    
                    module.addScoresToGraph(data);
                }
				
			},
            error: function(jqXHR, textStatus, errorThrown){
                FlickrGraphr.setMessage("Error while loading data from API: " + textStatus, "error");
            },
			dataType: "json"
		});
    },
    
    /**
     * If user already in graph - do not load
     *
     * @param {string} userId
     * @return {boolean} True if user loaded, false otherwise
     */
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
    
    
    /**
     * Searches the user
     * If user found, it will add them to the graph
     *
     * @param {string} searchString
     */
    searchScore : function(searchString)
    {
        if(searchString == ""){
            FlickrGraphr.setMessage("String to search cannot be empty.", "error");
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
                        FlickrGraphr.setMessage("User has no score yet.", "error");
                        return;
                    }
                    
                    $("#userSearchInput").val("");
                      
                    // user already loaded?
                    if(module.checkWhetherUserAlreadyLoaded(data[0].userId)){
                        FlickrGraphr.setMessage(data[0].displayName + " is already in the graph.", "status");
                        return;
                    }               
                    
                    
                    
                    FlickrGraphr.setMessage(data[0].displayName + "'s score added to the graph.", "status");
                    
                    module.displayNames[data[0].userId] = data[0].displayName;
                    
                    module.usersToDisplay.push(data[0]);
                    
                
                    module.addScoresToGraph(result.data);
                }else if(result.error.code == FlickrGraphr.ERROR_USER_NOT_EXISTS){
                    FlickrGraphr.setMessage("User does not exist.", "error");
                }
				
			},
            error: function(jqXHR, textStatus, errorThrown){
                FlickrGraphr.setMessage("Error while loading data from API: " + textStatus, "error");
            },
			dataType: "json"
		});
    },
    
    
   
    /**
     * Loads the top users for the date and adds them to the graph
     *
     * @param {string} date Date in format YYYY-MM-DD
     */    
    loadTopUsers : function(date){
        
        var module = this;
        
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
                                name = module.displayNames[result.data[i].userId];
                            }
                            FlickrGraphr.setMessage(name + " is already in the graph.", "status");
                            continue;
                        }                   
                    
                        module.addUserToDisplay(result.data[i]);
                    }
                    
                }           
				
			},
            error: function(jqXHR, textStatus, errorThrown){
                FlickrGraphr.setMessage("Error while loading data from API: " + textStatus, "error");
            },
			dataType: "json"
		});
    
    },
    
    
    /**
     * Adds a user to display
     
     * @param {string} date Date in format YYYY-MM-DD
     */
    
    addUserToDisplay : function(userObject){
        this.usersToDisplay.push(userObject);
        this.loadScore(userObject.userId);
        this.displayNames[userObject.userId] = userObject.displayName;
    },
    
    
    
   
    /**
     * Generates zero score for the user
     * @param {string} userId
     * @return {object} score object
     */    
    generateZeroScores : function(userId){
        return {
            userId : userId,
            displayName : userId,
            score: 0
        }    
    },
    
    
    /**
     * Prepares the graph
     */
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
            var date = this.dates[i];
            
            // empty row
            var row = new Array();
            
            // add date to row
            row.push(date);
            
            this.dataTable.addRow(row);
        }
        
      
        // Create and draw the visualization.
        usersChart = new google.visualization.LineChart(document.getElementById('visualization'));
        
        var module = this;
       

        
        function usersChartSelectHandler() {
        
            var selectedItem = usersChart.getSelection()[0];
            if(selectedItem)
            {
             
                var user = module.userInfo[selectedItem.column - 1];
                module.tooltipLoading();
                FlickrApi.people.getInfo(user.userId, function(person){
                    module.showTooltip(person);
                });
            }
        }

        
        // adding the handlers
        google.visualization.events.addListener(usersChart, 'select', usersChartSelectHandler);
       
        
    
    
    },
    
	/**
     * Internal function, called when the ajax requests finishes
     * Adds scores to the table
     *
     * @param {array} scores Array with scores objects
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
      
        clearTimeout(this.drawTimer);
        //$("#visualization").html(FlickrGraphr.loadingWidget("chart"));
        this.drawTimer = setTimeout("FlickrGraphr.modules[\"users\"].redrawChart();", 100);
	},
    
    /**
     * Redraws the chart
     */
    redrawChart : function()
    {
        // redraw chart
        usersChart.draw(this.dataTable, {
            curveType: "none",
            width: 760,
            height: 350,
            chartArea:{
                width: '750px',
                height: '80%',
                left: '90'
            },
            legend:{
                position:"right"
            },
            vAxis : {
                textPosition : "out",
                title : "Score"
            }
        });
    
    },
    
    /**
     * Shows the tooltip
     * The format is exactly the same as specified in the API
     * @param {object} person Person object
     */
    showTooltip : function(person){
     
        var iconurl = FlickrApi._getIconUrl(person.nsid, person.iconfarm, person.iconserver);
        
        
        var tooltip = "";
        tooltip += '<h2> ' + person.username._content + "</h2>\n";
        var realname = "";
        if(typeof person.realname != 'undefined')
        {
            realname = person.realname._content;
        }else{
            realname = person.username._content;
        }
        tooltip += "<div class=\"iconwrapper\"><img alt=\"" + realname + "\" src=\"" + iconurl + "\"></div>\n";
        tooltip += '<p><strong>Name:</strong> ' + realname + "</p>\n";
        
        if(typeof person.location != 'undefined')
        {
            tooltip += '<p><strong>Location:</strong> ' + person.location._content + "</p>\n";
        }
        tooltip += '<p><strong>User ID:</strong> ' + person.id + "</p>\n";
        tooltip += '<p><strong>Photostream:</strong> <a href="' + person.photosurl._content + '">' + person.photosurl._content  + "</a></p>\n";
        
        
        $("#userDetail").html(tooltip);
        
    
    },
    
    /** 
     * Loading user details
     */
    tooltipLoading : function(){
        $("#userDetail").html(FlickrGraphr.loadingWidget("user details"));
    },
    
    /**
     * Returns empty score for the userId and date
     * @param {string} userId
     * @param {string} date YYYY-MM-DD
     * @return {object} Score object
     */
    getEmptyScore : function(userId, date){
        return {
            userId : userId,
            date : date        
        }    
    }

}



      