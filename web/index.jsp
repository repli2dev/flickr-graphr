<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>

<html>
	<head>
		<title>flickr-graphr</title>
        <link type="text/css" href="css/flick/jquery-ui-1.8.20.custom.css" rel="stylesheet" />
        <link type="text/css" href="css/layout.css" rel="stylesheet" />
        <link type="text/css" href="css/styles.css" rel="stylesheet" />
		<script src="js/plugins/date.format.js"></script>
		<script src="js/utils.js"></script>
		<script src="js/jquery-1.7.2.min.js"></script>
		<script src="js/jquery-ui-1.8.20.custom.min.js"></script>
		<script src="js/plugins/jquery.ba-hashchange.min.js"></script>
		<script src="js/plugins/jquery.tagcloud-2.js"></script>
       <script type="text/javascript" src="https://www.google.com/jsapi"></script>
       <script type="text/javascript">
            google.load('visualization', '1.0', {'packages':['corechart']});
        </script>
		<script src="js/flickr-api.js"></script>
		<script src="js/flickr-graphr.js"></script>
		<script src="js/autorun.js"></script>
	</head>
	<body>
        <div id="messages">
            <div id="messages-inner">
            
        
            </div>
        </div>
        <div id="header">
            <h1>graph<span class="pink">r</span></h1>
            
            <div id="topnav">
            </div>
        </div>
		<div id="nav">
            <ul>
                <li><a href="#">Home</a></li>
                <li><a href="#users">Users</a></li>
                <li><a href="#tags2">Tags</a></li>
                <li><a href="#uploaded-photos">Photos</a></li>
            </ul>
       </div>
       
       <div id="module-template">
       
       
       </div>
		
		
	</body>
</html>	
		
		

