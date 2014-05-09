<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Stellenbosch VMMC Reports</title>

    <c:set var="url">${pageContext.request.requestURL}</c:set>
    <base href="${fn:substring(url, 0, fn:length(url) - fn:length(pageContext.request.requestURI))}${pageContext.request.contextPath}/"/>

    <link href="resources/css/bootstrap-3.0.2.css" rel="stylesheet" media="screen">
    <link href="resources/css/bootstrap-theme-3.0.2.css" rel="stylesheet">

    <script type="text/javascript" src="resources/js/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="resources/js/jquery-ui-1.9.1.min.js"></script>
    <script type="text/javascript" src="resources/js/bootstrap-3.0.2.js"></script>
</head>

<body>

<div class="container">

    <div class="header">
        <ul class="nav nav-pills pull-right">
            <li class="active"><a href="${pageContext.request.contextPath}">Home</a></li>
            <li><a href="j_spring_cas_security_logout">Logout</a>
        </ul>
        <h2><img src="resources/img/logo.png"></h2>

        <h3 class="muted">Reports</h3>
    </div>

    <hr>

    <h3>Select a Report</h3>
    <br>

    <div class="row">

        <div class="col-md-4">

            <c:forEach items="${reports}" var="report">
                <a href="#${report.id}" class="active reportLink">
                    <h4>${report.label}</h4>
                </a>
            </c:forEach>

        </div>

        <div class="col-md-8 form">

        </div>
    </div>

    <hr>

    <div class="footer">
        <p>&copy; Cell-Life (NPO) - 2013</p>
    </div>

</div>

<script>
    $(document).ready(function () {

        $(".reportLink").click(function (e) {

            e.preventDefault();

            var reportId = $(this).attr('href') + '';
            reportId = reportId.replace('#', '');

            $.get(
                    "service/getHtml",
                    {reportId: reportId},
                    function (data) {
                        $(".form").html(data);

                        $(".form").on('submit', function (e) {
                            e.preventDefault();
                            window.location = "service/downloadReport" + "?reportId=" + reportId + "&" + $("form").serialize();
                        });
                    }
            );

        });

    });

</script>

</body>
</html>