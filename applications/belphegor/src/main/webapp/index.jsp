<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.Iterator"%>
<%@ page import="org.jasig.cas.client.authentication.AttributePrincipal"%>
<%
 
AttributePrincipal principal = (AttributePrincipal)request.getUserPrincipal();

session.setAttribute("casPrincipal", principal);
session.setAttribute("username", request.getRemoteUser());

Map attributes = principal.getAttributes();

Iterator attributeNames = attributes.keySet().iterator();
for (; attributeNames.hasNext();) {
	  String attributeName = (String) attributeNames.next();
      Object attributeValue = attributes.get(attributeName);
      System.out.println(attributeName + "-->" + attributeValue);
      session.setAttribute(attributeName, attributeValue);
}
String redirectURL = request.getContextPath() + "/belphegor.html";
response.sendRedirect(redirectURL);
%>
