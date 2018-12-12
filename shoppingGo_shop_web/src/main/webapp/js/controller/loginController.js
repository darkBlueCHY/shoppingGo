app.controller("loginController",function ($scope,loginService) {
    loginService.getLoginName().success(function (response) {
        $scope.loginName = response['loginName'];
    });
})