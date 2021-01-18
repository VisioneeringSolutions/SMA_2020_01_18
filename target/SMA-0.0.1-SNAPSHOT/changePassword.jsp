<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>NEMI</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

    <!-- Icons -->
    <link rel="icon" href="images/nemi-n-logo-blue.png">
    <%--<link rel="icon" href="AdminLTE-2.3.3/dist/img/favicon.png">
    <link rel="stylesheet" href="bower_components/font-awesome/css/font-awesome.min.css"/>--%>
    <!--<link rel="stylesheet" href="bower_components/material-design-icons/iconfont/material-icons.css" />-->

    <!-- Third Party CSS -->
    <link rel="stylesheet" href="bower_components/bootstrap/dist/css/bootstrap.min.css"/>
    <link href="http://daneden.github.io/animate.css/animate.min.css" rel="stylesheet"/>

    <!-- Custom CSS -->
    <link rel="stylesheet" href="custom/style/style.css"/>

    <script src="bower_components/jquery/dist/jquery.min.js"></script>
    <script src="bower_components/bootstrap/dist/js/bootstrap.min.js"></script>

    <script src="bower_components/angular/angular.min.js"></script>

    <!-- Custom Scripts -->

    <script src="custom/js/script.js"></script>

    <!-- Modules Scripts -->
    <script src="modules/auth/auth.module.js"></script>
    <script src="modules/auth/auth.model.js"></script>
    <script src="modules/auth/auth.service.js"></script>
    <script src="modules/auth/changePassword.controller.js"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->


</head>
<body class="hold-transition login-page" cz-shortcut-listen="true" ng-controller="changePasswordCtrl as
            changePassword">
<div class="container-fluid">
    <div class="row">
        <div class="main-page-load modal-backdrop fade in text-center" style="display: block;">
            <div class="loader"></div>
            Loading...
        </div>

        <div class="login-box">
            <div class="login-logo">
                <a href="javascript:void(0);"><b>NEMI</b></a>
            </div>
            <!-- /.login-logo -->
            <div class="login-box-body">
                <p class="login-box-msg">Change Password</p>

                <form name="changePasswordForm" ng-submit="changePassword.submit($event,changePasswordForm)">
                    <div class="form-group has-feedback">
                        <input type="password" class="form-control" required ng-model="changePassword.password"
                               name="password"
                               id="password"
                               placeholder="New Password">
                        <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                    </div>
                    <div class="form-group has-feedback">
                        <input type="password" class="form-control" required ng-model="changePassword.cpassword"
                               name="cpassword"
                               id="cpassword"
                               placeholder="Confirm Password">
                        <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                    </div>

                    <div class="row">
                        <div class="col-md-6 col-md-offset-3">
                            <button type="submit" class="btn btn-primary btn-block btn-flat">
                                Change Password
                            </button>
                        </div>
                        <!-- /.col -->
                    </div>
                </form>

            </div>
            <!-- /.login-box-body -->
        </div>
    </div>
</div>
<script src="modules/auth/auth.bootstrap.js"></script>


</body>
</html>