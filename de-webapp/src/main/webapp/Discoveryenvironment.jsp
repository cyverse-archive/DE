<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.iplantc.de.server.DiscoveryEnvironmentMaintenance"%>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.Properties" %>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(application);
Properties props = (Properties) webApplicationContext.getBean("deProperties");
String maintFile = props.getProperty("org.iplantc.discoveryenvironment.maintenance-file");
DiscoveryEnvironmentMaintenance deMaintenance = new DiscoveryEnvironmentMaintenance(maintFile);
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
<script type="text/javascript" language="javascript"
	src="scripts/Markdown.Converter.js"></script>
<script type="text/javascript" language="javascript"
	src="scripts/Markdown.Sanitizer.js"></script>
<script type="text/javascript" language="javascript"
    src="scripts/handlebars.js"></script>
<%
	out.println("<p style='position:absolute;top:45%; left:48%  margin-top: 45%; margin-left: 48%;'>Loading...Please wait!</p><img style='position:absolute;top:50%; left:50%  margin-top: 50%; margin-left: 50%;' src='./images/loading_spinner.gif'/>");
	boolean isProduction = Boolean.parseBoolean(props.getProperty("org.iplantc.discoveryenvironment.environment.prod-deployment"));
    if (isProduction) {
%>
    <!-- Google analytics -->
    <script type="text/javascript">
        var _gaq = _gaq || [];

        _gaq.push(['_setAccount', 'UA-57745299-1']);

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
