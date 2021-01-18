<%--
 <%
  response.setHeader("Cache-Control","no-cache");
  response.setHeader("Cache-Control","no-store");
  response.setHeader("Pragma","no-cache");
  response.setDateHeader ("Expires", 0);

 %>

<%@ include file="login.jsp" %>--%>




 <html>
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>Suwayama Music</title>
        <!-- Tell the browser to be responsive to screen width -->
        <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

        <!-- Icons -->
        <link rel="icon" href="images/nemi-n-logo-blue.png">
        <%--<link rel="icon" href="AdminLTE-2.3.3/dist/img/favicon.png">
        <link rel="stylesheet" href="bower_components/font-awesome/css/font-awesome.min.css" />--%>
        <!--<link rel="stylesheet" href="bower_components/material-design-icons/iconfont/material-icons.css" />-->

        <!-- Third Party CSS -->
        <link rel="stylesheet" href="bower_components/bootstrap/dist/css/bootstrap.min.css" />
        <link rel="stylesheet" href="node_modules/angular-ui-bootstrap/dist/ui-bootstrap-csp.css" />
        <link href="http://daneden.github.io/animate.css/animate.min.css" rel="stylesheet">

        	<!-- Custom CSS -->
        	<link rel="stylesheet" href="custom/style/style.css" />

        	<script src="node_modules/moment/moment.js"></script>
        	<script src="bower_components/jquery/dist/jquery.min.js"></script>
        	<script src="bower_components/bootstrap/dist/js/bootstrap.min.js"></script>

                <script src="bower_components/angular/angular.min.js"></script>

                <!-- Custom Scripts -->

                <script src="custom/js/script.js"></script>

                <!-- Modules Scripts -->
                <script src="modules/auth/auth.module.js"></script>
                <script src="modules/auth/auth.model.js"></script>
                <script src="modules/auth/auth.service.js"></script>
                <script src="modules/auth/logout.controller.js"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <![endif]-->

        </head>
        <body class="hold-transition login-page" cz-shortcut-listen="true" ng-controller="logoutCtrl as logout">

                <div class="main-page-load modal-backdrop fade in text-center"style="display: block;">
                        <div class="loader"></div>
                        Loading...
                </div>

                <!-- Redirect To Login -->

        <script src="modules/auth/auth.bootstrap.js"></script>


        </body>
</html>
