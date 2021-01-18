<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Suwayama Music</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Icons -->
    <link rel="icon" href="images/nemi-n-logo-blue.png">
    <!-- Third Party CSS -->
    <link rel="stylesheet" href="bower_components/bootstrap/dist/css/bootstrap.min.css"/>
    <!-- Custom CSS -->
    <link rel="stylesheet" href="custom/style/style.css"/>
    <script src="bower_components/jquery/dist/jquery.min.js"></script>
    <script src="bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
    <script src="bower_components/angular/angular.min.js"></script>
    <script src="node_modules/bootbox/bootbox.min.js"></script>

    <!-- Custom Scripts -->
    <script src="custom/js/script.js"></script>
    <!-- Modules Scripts -->
    <script src="modules/auth/auth.module.js"></script>
    <script src="modules/auth/auth.model.js"></script>
    <script src="modules/auth/auth.service.js"></script>
    <script src="modules/auth/forgotPassword.controller.js"></script>
</head>
<body class="hold-transition login-page" cz-shortcut-listen="true" ng-controller="forgotPasswordCtrl as forgotPassword">
<div class="main-page-load modal-body text-center" style="display: block;" >
    <div class="loader">
        <span></span>
        <span></span>
        <span></span>
        <span></span>
    </div>
</div>
<div class="container">
    <div class="modal-body text-center">
        <div class="col-md-6 col-md-offset-3">
            <div class="box box-primary">
                <div class="box-body">
                    <div class="col-md-12">
                        <form name="register-form"  ng-submit="forgotPassword.forgotPassSubmit(forgotPassword.emailorphone)">
                        <div class="panel-body">
                        <div class="text-center">
                            <h3><i class="fa fa-lock fa-4x lock-icon"></i></h3>
                            <h2 class="text-center text-forgot">Forgot Password?</h2>
                            <p>You can reset your password here.</p>
                            <div class="panel-body">
                                <div class="form-group">
                                    <div class="input-group">
                                        <span class="input-group-addon user-box-icon"><i
                                                class="glyphicon glyphicon-envelope color-blue"></i></span>
                                        <input id="userId" name="userId"
                                               placeholder="Enter your username" class="form-control" type="text" required
                                               ng-model="forgotPassword.userId">
                                    </div>
                                </div>
                                <div class="modal-footer col-md-12">
                                    <div class="col-md-6 col-md-offset-3">
                                        <button type="submit" class="btn btn-primary btn-block btn-flat" ng-click="forgotPassword.submitValue(forgotPassword.userId,forgotPassword.description)" >
                                            Reset Password
                                        </button>
                                    </div>
                                </div>
                                <div>
                                    <p><a href="{{forgotPassword.baseUrl}}login"><b>Back to Registration page</b></a>
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="modules/auth/auth.bootstrap.js"></script>
</body>
</html>