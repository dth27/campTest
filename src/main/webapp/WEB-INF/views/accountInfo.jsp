<%--
  Created by IntelliJ IDEA.
  User: Dottedsocks
  Date: 14/09/2017
  Time: 13:36
  To change this template use File | Settings | File Templates.
--%>
<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en">
<head>
    <title>My account</title>
    <link rel="stylesheet" type="text/css" href="/css/testing2.css?v=2">
    <link href="https://fonts.googleapis.com/css?family=Lobster|Open+Sans+Condensed:300" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</head>
<body>
<p class="headerTitle">My account</p>
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
<br><br>
<div class = "info">
    <h2>This is your account information!</h2>
    <div class="information">
        <p style="color: red">${passwordChange}</p>
        <p><b>Username: </b>${user.username}</p>
        <p><b>Email: </b>${user.email}</p>
        <form action="/changePassword">
            <button type="submit">Change password</button>
        </form>
    </div>
</div>
<br>

<div class="bottom-nav">
    Created By Diljá, Kristín, Ólöf og Sandra
</div>

</body>
</html>
