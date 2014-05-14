<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Stellenbosch IVR</title>

    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/"/>

    <link href="resources/css/vmmc.css" rel="stylesheet">

    <link href="resources/css/bootstrap-3.0.2.css" rel="stylesheet" media="screen">
    <link href="resources/css/bootstrap-theme-3.0.2.css" rel="stylesheet">
</head>
<body>

<div class="container">

    <div class="header">
        <ul class="nav nav-pills pull-right">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="j_spring_cas_security_logout">Logout</a>
        </ul>
        <h2><img class="ohsc-logo" src="resources/img/logo.png"></h2>
        <h3 class="muted" id="topWelcome">IVR Systems Dashboard</h3>
    </div>

    <hr>

    <br>

    <div class="row">

        <div class="col-md-2">

            <h4>
                <a href="#" class="active reportLink" id="side_btn">
                    <button type="button" class="btn btn-primary" onclick="ShowImportPage()" id="side_btn">Import
                        Contacts
                    </button>
                </a>
            </h4>


            <h4>
                <a href="#" class="active reportLink">
                    <button type="button" class="btn btn-primary" onclick="ShowReportsPage()" id="side_btn">Reports
                    </button>
                </a>
            </h4>

        </div>

        <div class="col-md-2">
        </div>

        <div class="col-md-8">
            <div><h2>Error</h2></div>
            <p>
                <c:out value="${errorMessage}"/>
            </p>
            <hr>
        </div>

    </div>
    <div class="footer">
        <p>&copy; Cell-Life (NPO) - 2014</p>
    </div>

</div>

</body>
</html>
