 //控制层 
app.controller('goodsController' ,function($scope,$controller,$timeout,$location,goodsService,itemCatService,typeTemplateService,specificationService,uploadService){
	$controller('baseController',{$scope:$scope});//继承

    $scope.flushEntity=function(){
        $scope.entity = {
            goods : {
            	isEnableSpec : "1",
                brandId : null
			},
            goodsDesc : {
                specificationItems:[],
                itemImages:[]
            },
            itemList : []
        };
        $scope.noSpecSKU = {
            status : "1",
            isDefault : "1"
        };
    }
    $scope.flushEntity();

    $scope.entity.goods.id = $location.search()['id'];
	if ($scope.entity.goods.id) {
        goodsService.findOne($scope.entity.goods.id).success(
        	function (response) {
				$scope.entity = response;
				$scope.entity.goodsDesc.specificationItems = JSON.parse(response.goodsDesc.specificationItems);
				$scope.entity.goodsDesc.itemImages = JSON.parse(response.goodsDesc.itemImages)
				for (var index = 0 ; index < response.itemList.length; index++) {
					$scope.entity.itemList[index].spec = JSON.parse(response.itemList[index].spec);
				}
				editor.html($scope.entity.goodsDesc.introduction);
				if ($scope.entity.goods.isEnableSpec){
                    $scope.noSpecSKU = $scope.entity.itemList[0];
				}
        	})
	}

	//添加图片
    $scope.uploadPicture=function(){
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.message;
                } else {
                    alert(response.message);
                }
            }
        )
    }
    //保存图片到specExtens中
    $scope.saveImage=function(){
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
        $scope.image_entity = {};
        $scope.file=null;
    }
    //从goods的图片列表中删除指定图片
    $scope.removeItemImage=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }
    //初始化一级itemCat
	itemCatService.findByParentId(0).success(
		function (response) {
			$scope.itemCatList_1=response;
		});
	//二级itemCat
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
        if (!angular.isUndefined(newValue)) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCatList_2 = response;
                    if (oldValue) {
                    	$scope.itemCatList_3 = undefined;
                        $scope.entity.goods.category2Id = undefined;
                        $scope.entity.goods.category3Id = undefined;
                        $scope.entity.goods.brandId = null;
					}
                });
        }
    })		//三级itemCat
	$scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        if (!angular.isUndefined(newValue)) {
            itemCatService.findByParentId(newValue).success(
                function (response) {
                    $scope.itemCatList_3 = response;
                    if (oldValue) {
                        $scope.entity.goods.category3Id = undefined;
                        $scope.entity.goods.brandId = null;
                    }
                });
        }
	})			//查询brand
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
		if (!angular.isUndefined(newValue)) {
			if (! $scope.entity.goods.id) {
                $scope.entity.goods.typeTemplateId=$scope.findTypeIdByCatId(newValue);
			}
			typeTemplateService.findOne($scope.entity.goods.typeTemplateId).success(
				function (response) {
					$scope.brandList=JSON.parse(response.brandIds);
					var objList = JSON.parse(response.specIds);
					specificationService.findSpecificationByIds($scope.extractId(objList,"id")).success(
						function (response) {
                            $scope.specificationList = response;
                        }
					);
				});
		}
    })
	$scope.findTypeIdByCatId=function (catId) {
        for (var index = 0; index < $scope.itemCatList_3.length; index++){
            if ($scope.itemCatList_3[index].id == catId) {
            	return $scope.itemCatList_3[index].typeId;
			}
		}
    }
	//将checkbox中的勾选情况记录到event对象中
    $scope.changeSpecItems=function($event,specName,optionName){
		if ($event.target.checked) {
            for (var index = 0; index < $scope.entity.goodsDesc.specificationItems.length; index++){
                if ($scope.entity.goodsDesc.specificationItems[index].attributeName == specName) {
                    $scope.entity.goodsDesc.specificationItems[index].attributeValue.push(optionName);
                    return;
				}
            }
            $scope.entity.goodsDesc.specificationItems.push({
				attributeName : specName,
                attributeValue : [optionName]
            })
		} else {
            for (var index = 0; index < $scope.entity.goodsDesc.specificationItems.length; index++){
            	if ($scope.entity.goodsDesc.specificationItems[index].attributeName == specName) {
            		if ($scope.entity.goodsDesc.specificationItems[index].attributeValue.length == 1) {
            			$scope.entity.goodsDesc.specificationItems.splice(index, 1);
            		} else {
                    	$scope.entity.goodsDesc.specificationItems[index].attributeValue.splice($scope.entity.goodsDesc.specificationItems[index].attributeValue.indexOf(optionName), 1);
                	}
                    return;
                }
            }
		}
	}
	//回显Checked
    $scope.isSpecChecked=function(specValue){
		for (var index = 0; index < $scope.entity.goodsDesc.specificationItems.length; index++) {
            for (var jndex = 0; jndex < $scope.entity.goodsDesc.specificationItems[index].attributeValue.length; jndex++) {
                if ($scope.entity.goodsDesc.specificationItems[index].attributeValue[jndex] == specValue) {
                    return true;
                }
			}
		}
		return false;
	}

	//生成SKUlist
	var echoSKUList = true;
	$scope.$watch('entity.goodsDesc.specificationItems',function (newValue,oldValue) {
		if (!angular.isUndefined(newValue)) {
			if (angular.isNumber($scope.entity.goods.id) && echoSKUList) {
				echoSKUList = false;
				return;
			} else {
				var specItemNums = [];
				var counter = [];
				for (var index = 0; index < $scope.entity.goodsDesc.specificationItems.length; index++) {
					specItemNums.push($scope.entity.goodsDesc.specificationItems[index].attributeValue.length - 1);
					counter.push(0);
				}
				$scope.entity.itemList = [];
				if (newValue.length == 0) { return; }
				for (var index=0;; index++) {
					$scope.entity.itemList.push({
						spec:{},
						status : "1" ,
						isDefault : "0"
					});
					for (var jndex=0; jndex < counter.length; jndex++) {
						$scope.entity.itemList[index].spec[$scope.entity.goodsDesc.specificationItems[jndex].attributeName] = $scope.entity.goodsDesc.specificationItems[jndex].attributeValue[counter[jndex]];
					}
					for (var jndex=counter.length - 1;; jndex--) {
						if (counter[jndex] < specItemNums[jndex]) {
							counter[jndex] = counter[jndex] + 1;
							break;
						} else {
							counter[jndex] = 0;
						}
						if (jndex == 0) return;
					}
				}
			}
		}
    },true);
	//isDefault 保持只有一个勾选！
	$scope.holdOneChecked=function($event,holdIndex) {
		if ($event.target.checked) {
			for (var index = 0; index < $scope.entity.itemList.length; index++){
				if (holdIndex != index) {
                    $scope.entity.itemList[index].isDefault="0";
				}
			}
		}
	}

	//读取列表数据绑定到表单中
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}
		);
	}

	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){
		var serviceObject;//服务层对象
		if ($scope.entity.goods.isEnableSpec == "0") {
            $scope.entity.itemList = [$scope.noSpecSKU];
		}
        $scope.entity.goodsDesc.introduction=editor.html();
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity );//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//清空页面
                    location.href="./goods.html";
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
