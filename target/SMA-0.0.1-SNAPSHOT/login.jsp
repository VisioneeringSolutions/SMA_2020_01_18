<html>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Suwayam Music</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

    <!-- Icons -->

    <link rel="stylesheet" href="bower_components/font-awesome/css/all.min.css"/>

    <link rel="icon" href="images/favicon.ico">
    <!--<link rel="stylesheet" href="bower_components/material-design-icons/iconfont/material-icons.css" />-->

    <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">--%>
    <%--<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>--%>
    <%--<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>--%>

    <!-- Third Party CSS -->
    <link rel="stylesheet" href="bower_components/bootstrap/dist/css/bootstrap.min.css"/>
    <%--<link rel="stylesheet" href="node_modules/angular-ui-bootstrap/dist/ui-bootstrap-csp.css"/>--%>

    <link href="https://fonts.googleapis.com/css?family=Quicksand|Roboto|Open+Sans" rel="stylesheet">
    <%--<link href="http://daneden.github.io/animate.css/animate.min.css" rel="stylesheet">--%>

    <!-- Template -->

    <!-- Custom CSS -->
    <link rel="stylesheet" href="custom/style/responsive.css"/>

    <link rel="stylesheet" href="custom/style/style.css"/>

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
    <script src="modules/auth/login.controller.js"></script>


    <script src="node_modules/bootbox/bootbox.min.js"></script>
    <script src="node_modules/bootbox/bootbox.locales.js"></script>
    <script src="node_modules/bootbox/bootbox.locales.min.js"></script>
    <script src="node_modules/bootbox/bootbox.all.min.js"></script>
    <script src="node_modules/bootbox/bootbox.js"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <![endif]-->

</head>
<body ng-controller="loginCtrl as login">
<!-- Header
    ================================================= -->
<div class="main-page-load modal-backdrop fade in text-center" style="display: block;">
    <div class="loader">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
    </div>
</div>
<div class="main-body">
    <div class="body-image">

       <%-- <div class="login-container col-lg-12 col-md-12 col-sm-12 col-xs-12 no-padding"
             id="login-container"
             ng-click="login.changeClassSignUp()">
            <div class="login-box center col-lg-5 col-md-5 col-sm-12 col-xs-12 no-padding">
                <div class="login-wrapper">
                    <img src="images/Suwayama1.png" style="display: block; margin: 0 auto;">
                    <div class=" col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <span class="welcome-text">Welcome to Suwayama Music !</span>
                        <button type="button" name="button"><span style="font-size: 15px">Sign-in</span></button>
                    </div>
                </div>
            </div>
        </div>--%>

        <%--<div class="login-form-container center animate col-lg-12 col-md-12 col-sm-12 col-xs-12"
             id="login-form-container">
            <div class="login-form-box col-lg-5 col-md-5 col-sm-12 col-xs-12">
                <form class="" method="post">
                    <div class="input-form username">
                        <input type="input" name="userName" id="userName" class="input-box font-family" style="color: #000"
                               ng-model="login.usrName">
                    </div>

                    <div class="input-form password">
                        <input type="Password" name="password" id="password" class="input-box font-family" style="color: #000"
                               ng-model="login.password">
                    </div>

                    <button type="button" name="button"
                            ng-click="login.signIn(login.usrName,login.password)">
                        Log-in</button>
              <div style="padding-top: 8%">
                    <p class="message text-center" style="color: white"><a href="{{login.baseUrl}}forgotPassword">Forgot Password</a></p>
               </div>
                </form>
            </div>
        </div>--%>

        <div class="login-container">
            <form autocomplete="off">
                <input id="username1" style="display:none" type="text" name="fakeusernameremembered"/>
                <input id="password1" style="display:none" type="password" name="fakepasswordremembered"/>
                <img src="images/Suwayama1.png" style="width: 100%; margin: 0 auto 20px auto;">

                <div class="login-form">

                    <input type="text"  id="userName"
                           class="input-box font-family" style="color: #000"
                           required=""
                           autocomplete="nope"
                           ng-model="login.usrName"/>
                    <label class="label-name">
                        <span class="user-name">Username</span>
                    </label>
                </div>

                <div class="login-form">
                    <input type="password" name="password"
                           id="password" class="input-box font-family" style="color: #000"
                           required=""
                           autocomplete="new-password"
                           ng-model="login.password"/>
                    <label class="label-name">
                        <span class="user-name">Password</span>
                    </label>
                </div>

                <button type="button" name="button"
                        ng-click="login.signIn(login.usrName,login.password)">
                    Log-in</button>
                <%--<div style="padding-top: 6%">
                    <p class="message text-center" style="color: white"><a href="{{login.baseUrl}}forgotPassword">Forgot Password</a></p>
                </div>--%>
            </form>



        </div>
    </div>
   <!--  <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 copyright">
        <span>Copyright 2019 <span >
            <a style="color: orange" href="http://visioneering.co.in/" target="_blank">VSPL</a>
        </span> All rights reserved.</span>
    </div> -->
</div>



<%--<footer id="footer">
    <div class="copyright">
        <p>Copyright @VSPL 2019. All rights reserved</p>
    </div>
</footer>--%>

<!--preloader-->
<%--<div id="spinner-wrapper">
    <div class="spinner"></div>
</div>--%>
<!--Header End-->

<script src="modules/auth/auth.bootstrap.js"></script>
</body>


</html>