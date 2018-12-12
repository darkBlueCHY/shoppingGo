 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.selectIds=[];
    }

    $scope.toPartenString=function(str,propName){
    	var list = JSON.parse(str);

    }
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 0,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	};

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}
	//将字符串转化为json并抽取出其中的一个字段形成以逗号间隔的字符串
    $scope.extractField = function(jsonStr,field){
		var list = JSON.parse(jsonStr);
		var result = "";
		for (var jndex = 0; jndex < list.length; jndex++){
			result = result + list[jndex][field] + ',';
		}
		return result.substring(0,result.length-1);
	}
	//将对象数组转换成主键数组
	$scope.extractId = function (objectList,idField) {
		var result=[];
		for (var index = 0 ; index < objectList.length; index++) {
			result.push(objectList[index][idField]);
		}
		return result;
    }
});	