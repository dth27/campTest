<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TravelPlan info</title>

    <link rel="stylesheet" type="text/css" href="/css/myTravelplansLook.css">
    <link href="https://fonts.googleapis.com/css?family=Lobster|Open+Sans+Condensed:300" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
</head>
<body>
<p class="headTitle">Your travelplan</p>
<form method="get" action="/onetravel">
<p class = "loggedIn">You are logged in as: <b class="username">${username}</b></p>
<div class="container-fluid">
    <nav class="navbar navbar-light" style="background-color:#42453D" data-spy="affix" data-offset-top="197">
        <div class="container-fluid">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand" href="goToNotendasida"><img src="/css/logo3.png" alt="Dispute Bills">
                </a>
            </div>
            <div id="navbar1" class="navbar-collapse collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="listofcamps">Campsites</a></li>
                    <li><a href="getTravelItems">My Travelplans</a></li>
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">Info<span class="caret"></span></a>
                        <ul class="dropdown-menu" role="menu">
                            <li><a href="accountInfo">My information</a></li>
                            <li><a href="UserReviews">My reviews</a></li>
                            <li class="divider"></li>
                            <li class="dropdown-header">About</li>
                            <li><a href="aboutCampALot">About Camp'A'Lot</a></li>
                            <li><a href="help">Help</a></li>
                        </ul>
                    </li>
                </ul>
                <a class = "right" href = "logOut">Log out</a>
            </div>
            <!--/.nav-collapse -->
        </div>
        <!--/.container-fluid -->
    </nav>
</div>
</form>
<div class ="maincontainer">
    <div class="textContainer">
        <h2>Your Travelplans!</h2>
        <p class="travelplantext">Wow you've got a lot! Check them out and edit them by pressing the name of the one you want to change.</p>
        <p class="travelplantext">You can always add new campsites to your travelplan by selecting the campsite of your choice from the list of campsites.</p>
    </div>
    <div class="formContainer">
    <form method="get" action="/onetravel">
        <table class="table table-hover" border="1">
         <thead>
         <tr>
             <th>Travelplan</th>
             <th>Delete</th>
         </tr>
        </thead>
            <c:forEach var="travelitems" items="${travelplanitems}">
                <tr>
                    <td><button name="travelname" value="${travelitems.travelplanname}">${travelitems.travelplanname}</button></td>
                    <td><button class = "delete" name="planName" type = "submit" value = "${travelitems.travelplanname}" onclick="form.action='deletePlan'; method='post';"></button></td>
                </tr>
            </c:forEach>
        </table>
    </form>
    </div>
</div>


<div class="bottom-nav">
    Created By Diljá, Kristín, Ólöf og Sandra
</div>
</body>
</html>
