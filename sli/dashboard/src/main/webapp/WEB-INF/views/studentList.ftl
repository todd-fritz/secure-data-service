<html>
<head>
<!-- Frameworks -->
<script type="text/javascript" src="static/js/3p/jquery-1.7.1.js"></script>
<script type="text/javascript" src="static/js/3p/raphael-min.js"></script>
<!-- Internal js -->
<script type="text/javascript" src="static/js/populationSelect.js"></script>
<script type="text/javascript" src="static/js/dashboardUtil.js"></script>
<script type="text/javascript" src="static/js/fuelGaugeWidget.js"></script>
<script type="text/javascript" src="static/js/lozengeWidget.js"></script>

<#-- TODO: find better way to pass freemarker variable to javascript -->
<script type="text/javascript">
var instHierarchy = ${instHierarchy};
$.ajaxSetup ({cache: false});
</script>

<link rel="stylesheet" type="text/css" href="static/css/common.css" media="screen" />
</head>
<body onLoad="populateInstHierarchy()">
<div id="container">

    <div id="header">
		<#include "header.ftl">
    </div>
    
    <div id="banner">
        <h1>
            SLI Dashboard - List of Students
        </h1>
    </div>
    <div id="content">
    
        <div id="populationSelect">
            <span id="edorgDiv"></span>
            <span id="schoolDiv"></span>
            <span id="courseDiv"></span>
            <span id="sectionDiv"></span>
        </div>
    
        <div id="listView">
            <span id="studentDiv"></span>
        </div>
    </div>
    
</div>

</body>
</html>
