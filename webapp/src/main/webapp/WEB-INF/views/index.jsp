<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Stellenbosch VMMC</title>

    <link href="resources/css/bootstrap-3.0.2.css" rel="stylesheet" media="screen">
    <link href="resources/css/bootstrap-theme-3.0.2.css" rel="stylesheet">
    <link href="resources/css/vmmc.css" rel="stylesheet">

    <script type="text/javascript" src="resources/js/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="resources/js/jquery-ui-1.9.1.min.js"></script>
    <script type="text/javascript" src="resources/js/bootstrap-3.0.2.js"></script>
</head>

<body>

<div class="container">

    <div class="header">
        <ul class="nav nav-pills pull-right">
            <li class="active"><a href="http://sol.cell-life.org/vmmc/">Home</a></li>
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


        <div class="col-md-8 form">

            <div id="welcomeMessage"><h2>Welcome to the IVR Systems Dashboard.</h2></div>

            <div id="importPage">

                <h3>Add Contacts To Campaign</h3>
                <ul>
                    <li>Note that imported files must be CSV format
                    <li>Columns must be labelled MSISDN and Password
                    <li>Numbers must begin with 27 (not 0) and must be 11 characters in length
                    <li>Special characters (e.g the + symbol) will cause the import to fail
                </ul>

                <br>

                <div id="successMessage" class="alert alert-success">(placeholder)</div>

                <div id="failureMessage" class="alert alert-danger">(placeholder)</div>

                <form role="form" class="form-horizontal" enctype="multipart/form-data" id="uploadForm">

                    <div class="form-group">
                        <label for="campaignName" class="col-sm-2 control-label">Campaign Name:</label>

                        <div class="col-sm-4">
                            <select id="campaignName" name="campaignName" class="form-control" required>
                                <c:forEach items="${campaigns}" var="campaign">
                                    <option>${campaign.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="file" class="col-sm-2 control-label">File:</label>

                        <div class="col-sm-4">
                            <input type="file" id="file" name="file" class="form-control" required/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-offset-2 col-sm-10">
                            <button id="uploadButton" class="btn btn-default" type="submit">Submit
                            </button>
                        </div>
                    </div>
                </form>

            </div>

            <div id="chooseReport">
                <h3>Which report would you like to generate?</h3>
                <a href="#Overall" onclick="ShowReportsPage1()">
                    <h4>Overall report</h4>
                </a>
                <a href="#MSISDN" onclick="ShowReportsPage2()">
                    <h4>MSISDN report</h4>
                </a>
            </div>

            <div id="ReportsPage1">
                <h4>Overall reports generator here</h4>
            </div>

            <div id="ReportsPage2">
                <h4>MSISDN reports generator here</h4>
            </div>

        </div>
    </div>

    <hr>

    <div class="footer">
        <p>&copy; Cell-Life (NPO) - 2014</p>
    </div>

</div>
<script>

    $(document).ready(function () {

        $("#successMessage").hide();
        $("#failureMessage").hide();

        $('#uploadForm').submit(function(event){

            event.preventDefault();

            $("#uploadButton").prop('disabled', true);

            var fd = new FormData(document.getElementById("uploadForm"));

            $.ajax({
                url: 'service/campaign/' + document.querySelector('#campaignName').value + '/contacts',
                type: 'POST',
                data: fd,
                async: false,
                cache: false,
                contentType: false,
                processData: false,
                success: function (data) {

                    $("#uploadButton").prop('disabled', false);

                    if (data.length > 0) {
                        var errorString = "";
                        $.each(data, function (key, val) {
                            errorString = errorString + "<li>" + val.msisdn + ". Reason:  " + val.reasonForFailiure + "</li>";
                        });
                        document.getElementById("failureMessage").innerHTML = "Some contacts could not be added. Contacts that failed: <ul>" + errorString + "</ul>";
                        $("#failureMessage").show();
                        $("#successMessage").hide();

                    } else {

                        document.getElementById("successMessage").innerHTML = "Contacts added successfully.";
                        $("#successMessage").show();
                        $("#failureMessage").hide();

                    }

                },
                error: function (jqXHR, textStatus, errorThrown) {

                    $("#uploadButton").prop('disabled', false);

                    document.getElementById("failureMessage").innerHTML = "Error code: " + jqXHR.status + "<br> Message: " + jqXHR.statusText;
                    $("#failureMessage").show();
                    $("#successMessage").hide();
                }
            });

        });

    });

    $(document).ready(function () {
        $("#successMessage").hide();
        $("#failureMessage").hide();
        $("#importPage").hide();
        $("#chooseReport").hide();
        $("#topWelcome").hide();
        $("#ReportsPage1").hide();
        $("#ReportsPage2").hide();

    });
    function ShowImportPage() {
        $("#importPage").show();
        $("#chooseReport").hide();
        $("#topWelcome").show();
        $("#welcomeMessage").hide();
        $("#ReportsPage1").hide();
        $("#ReportsPage2").hide();
    }


    function ShowReportsPage() {
        $("#importPage").hide();
        $("#chooseReport").show();
        $("#topWelcome").show();
        $("#welcomeMessage").hide();
        $("#ReportsPage1").hide();
        $("#ReportsPage2").hide();
    }

    function ShowReportsPage1() {
        $("#importPage").hide();
        $("#chooseReport").hide();
        $("#topWelcome").show();
        $("#welcomeMessage").hide();
        $("#ReportsPage1").show();
        $("#ReportsPage2").hide();
    }

    function ShowReportsPage2() {
        $("#importPage").hide();
        $("#chooseReport").hide();
        $("#topWelcome").show();
        $("#welcomeMessage").hide();
        $("#ReportsPage1").hide();
        $("#ReportsPage2").show();
    }

</script>

</body>

</html>
