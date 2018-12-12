 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承

    $scope.selectIds=[];

	$scope.flushPage=function(){
		if ($scope.grade == 1) {
            $scope.findByParentId(0);
		} else if ($scope.grade == 2) {
			$scope.findByParentId($scope.entity_1.id);
		} else {
            $scope.findByParentId($scope.entity_2.id);
        }
	}

	$scope.flushEntity=function(){
		if ($scope.grade == 2) {
			$scope.entity={parentId : $scope.entity_1.id}
		} else if ($scope.grade == 3) {
            $scope.entity={parentId : $scope.entity_2.id}
		} else {
			$scope.entity={parentId : 0}
		}
		$scope.selectedType=null;
    };

	$scope.initTypeName=function (){
        typeTemplateService.select2TypeNameList().success(
        	function (response) {
				$scope.typeNameList={
					data : response,
                    placeholder : "选择类型"
				};
        })
	}

	$scope.setGrade=function(grade,obj){
		$scope.grade=grade;
		if (grade == 2) {
			$scope.entity_1 = obj;
		} else if (grade == 3) {
			$scope.entity_2 = obj;
		}
	}

	$scope.findByParentId=function(parentId){
		itemCatService.findByParentId(parentId).success(
			function (response) {
				$scope.list=response;
                $scope.selectIds=[];
            }
		)
	}

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;
				$scope.selectedType = {
					"id" : response.id,
					"text" : response.typeName
				}
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加
		}

		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.flushPage();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.flushPage();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
