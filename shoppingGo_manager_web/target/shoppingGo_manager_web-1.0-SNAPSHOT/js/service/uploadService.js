app.service("uploadService",function ($http) {
    this.uploadFile=function () {
        var formData = new FormData();
        formData.append("file",file.files[0]);
        return $http({
            url : "../upload/uploadFile.do",
            method : "POST",
            data : formData,
            headers : {
                "Content-Type": undefined,
            },
            transformRequest : angular.identity
        });
    }
})
