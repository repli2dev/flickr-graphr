/**
 * Chart with uploaded photos per day
 * Must be defined here because of the onmouseover calls.
 */
var photosChart;


/**
 * Module with uploadedPhotos
 */
FlickrGraphr.modules["uploaded-photos"] = {

    title : "Uploaded photos",
    
    dataTable : false,

    loadUploadedPhotos : function()
    {
        var module = this;
        
        var startDate =  $('#uploadedPhotosDateFrom').val();
        var endDate =  $('#uploadedPhotosDateTo').val();

    
        // if not, fetch it
        $.ajax({
			url: FlickrGraphr.API_URL + FlickrGraphr.API_UPLOADED_PHOTOS,
			data : {
				"start-date": startDate,
				"end-date": endDate
			},
            type : "get",
			success: function(result){
                
                if(result.stat == FlickrGraphr.stat.OK){
                
                    var data = result.data;
                    
                    // if length = 0, generate zeros
                    if(data.length == 0){
                        FlickrGraphr.messageBox("No data", "No data found for this interval.");
                        return;
                    }
                    
                      
                
                    module.addScoresToGraph(result.data);
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
        var topnav = 'Choose range: <input type="text" name="dateFrom" id="uploadedPhotosDateFrom" value=""> - ';
        topnav += '<input type="text" name="dateFrom" id="uploadedPhotosDateTo" value=""> ';
        topnav += '<button id="uploadedPhotosRefreshButton">Set</button>';
        $("#topnav").html(topnav);
        
        // datepicking
        $("#uploadedPhotosDateFrom").datepicker({ dateFormat : 'yy-mm-dd'});
        $("#uploadedPhotosDateTo").datepicker({ dateFormat : 'yy-mm-dd'});
        
        // prepare dates now and previous week
        var prevDate = new Date();
        prevDate.setDate(prevDate.getDate() - 7);
        
        $('#uploadedPhotosDateFrom').datepicker("setDate",  prevDate);
        $('#uploadedPhotosDateTo').datepicker("setDate", new Date() );
        
        
        var module = this;
    
        // refresh
        $("#uploadedPhotosRefreshButton").button();
        $("#uploadedPhotosRefreshButton").click(function(){
            
            var start = new Date($('#uploadedPhotosDateFrom').val());
            var end = new Date($('#uploadedPhotosDateTo').val());
            
            if(end <= start){
                FlickrGraphr.messageBox("Invalid date", "The start and end date are in collision.");
                return;
            }
        
            module.prepareScoreGraph();
            module.loadUploadedPhotos();
            
        });
    
        // graph
        this.prepareScoreGraph();
        
        // load photos
        this.loadUploadedPhotos();
    },
    
    
    template : "",
    
    
    prepareScoreGraph : function(){
          
        var startDate = new Date($('#uploadedPhotosDateFrom').val());
        var endDate = new Date($('#uploadedPhotosDateTo').val());
        
        // data table
        this.dataTable = new google.visualization.DataTable();

        // add first column, date
        this.dataTable.addColumn({
            type : 'date',
            label : 'Date'
        });
        
        // second column - photos per date
        this.dataTable.addColumn({
            type : 'number',
            label : 'Uploaded photos per day'
        });
      
        // Create and draw the visualization.
        photosChart = new google.visualization.LineChart(document.getElementById('visualization'));
        
        var module = this;
       
    },
    
	/**
     * Internal function, called when the ajax requests finishes
     */
	addScoresToGraph : function(scores){
    
        // for each score, add row
        for(var i in scores)
        {
            var score = scores[i];
            //alert(new Date(score.date) + " score:" + score.score);
            this.dataTable.addRow([new Date(score.date), score.score]);
        }
      
        // redraw chart
        photosChart.draw(this.dataTable, {
            curveType: "none",
            width: 700,
            height: 400,
            chartArea:{
                width: '80%',
                height: '80%',
            },
            legend:{
                position:"top"
            }
        });
	}
    
    

}



      