 //控制层 
app.controller('contentController' ,function($scope,contentService){

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		contentService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
    $scope.contentList = [];
	$scope.findContentByCatId=function(catId) {
        contentService.findContentByCatId(catId).success(
            function (response) {
                $scope.contentList[catId] = response;
            })
    }
    $scope.goSearch = function(){
		location.href = "http://localhost:9006/search.html#?keywords=" + $scope.keywords;
	}
});	
