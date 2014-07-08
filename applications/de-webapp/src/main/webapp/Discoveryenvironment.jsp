<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.iplantc.de.server.DiscoveryEnvironmentMaintenance"%>
<%@page import="org.iplantc.de.server.DiscoveryEnvironmentProperties"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

// Fetch the DE configuration settings.
ServletContext ctx = getServletConfig().getServletContext();
DiscoveryEnvironmentProperties props = DiscoveryEnvironmentProperties.getDiscoveryEnvironmentProperties(ctx);

// Redirect the user to the maintenance page if the DE is under maintenance.
DiscoveryEnvironmentMaintenance deMaintenance = new DiscoveryEnvironmentMaintenance(props.getMaintenanceFile());
if (deMaintenance.isUnderMaintenance()) {
    session.invalidate();
    response.sendRedirect("");
}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<html>
<head>

<!--                                                               -->
<!-- Consider inlining CSS to reduce the number of requested files -->
<!--                                                               -->
<link type="image/x-icon" rel="shortcut icon" href="images/favicon.ico">
<link type ="text/css" rel="stylesheet" href="./introjs.min.css">
<link type="text/css" rel="stylesheet" href="./codemirror.css">



<!-- set by i18n code -->
<title></title>

<!--                                           -->
<!-- This script loads your compiled module.   -->
<!-- If you add any GWT meta tags, they must   -->
<!-- be added before this line.                -->
<!--                                           -->


<script type="text/javascript" language="javascript"
	src="discoveryenvironment/discoveryenvironment.nocache.js"></script>
<script type="text/javascript" language="javascript"
	src="scripts/intro.min.js"></script>
<script type="text/javascript" language="javascript"
	src="scripts/codemirror.js"></script>
<script type="text/javascript" language="javascript"
	src="scripts/javascript.js"></script>
<script type="text/javascript" language="javascript"
	src="scripts/shell.js"></script>
<script type="text/javascript" language="javascript"
	src="scripts/nexus.js"></script>
	<script type="text/javascript" language="javascript"
	src="scripts/perl.js"></script>
	<script type="text/javascript" language="javascript"
	src="scripts/python.js"></script>
	<script type="text/javascript" language="javascript"
	src="scripts/r.js"></script>
		<script type="text/javascript" language="javascript"
	src="scripts/markdown.js"></script>
			<script type="text/javascript" language="javascript"
	src="scripts/matchbrackets.js"></script>
			<script type="text/javascript" language="javascript"
	src="scripts/closebrackets.js"></script>

<%
	out.println("<p style='position:absolute;top:45%; left:48%  margin-top: 45%; margin-left: 48%;'>Loading...Please wait!</p><img style='position:absolute;top:50%; left:50%  margin-top: 50%; margin-left: 50%;' src='./images/loading_spinner.gif'/>");
    if (props.isProduction()) {
%>
    <!-- Google analytics -->
    <script type="text/javascript">
        var _gaq = _gaq || [];

        _gaq.push(['_setAccount', 'UA-16039757-1']);

        _gaq.push(['_trackPageview']);
        (function() {

            var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;

            ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';

            var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);

        })();

    </script>
<%
    }
%>
</head>

<!--                                           -->
<!-- The body can have arbitrary html, or      -->
<!-- you can leave the body empty if you want  -->
<!-- to create a completely dynamic UI.        -->
<!--                                           -->
<body>
	<!-- include for history support -->
	<iframe src="javascript:''" id="__gwt_historyFrame" 
		style="position: absolute; width: 0; height: 0; border: 0">
	</iframe>


</html>
