 //控制层 
app.controller('itemSearchController' ,function($scope,$location,itemSearchService){
    $scope.flushSearchEntity = function(){
        $scope.entity={
            categoryList : [],
            brandList : []
        }
        $scope.searchEntity={
            pageNumber : 1,
            spec:{}
        };
        if ($location.search()['keywords']) {
            $scope.searchEntity.keywords = $location.search()['keywords'];
            $scope.itemSearch();
        }
    }



    $scope.keywordsIsBrand = function(){
        for (var index = 0; index < $scope.entity.brandList.length; index++) {
            if ($scope.searchEntity.keywords.indexOf($scope.entity.brandList[index].text) > -1) {
                $scope.searchEntity.brand = $scope.entity.brandList[index].text;
                return;
            }
        }
    }

    $scope.nextPage=function (noReverse) {
        if (noReverse) {
            $scope.searchEntity.pageNumber ++;
        } else {
            $scope.searchEntity.pageNumber --;
        }
    }

    $scope.checkNumber=function(str) {
        var number = parseInt(str);
        if (number){
            if (number > 0 && number < $scope.entity.totalPages + 1) {
                $scope.searchEntity.pageNumber = number;
            } else {
                alert("输入页数不在有效范围")
            }
        } else {
            $scope.jumpPage = null;
        }
    }

    $scope.createPageList=function(){
        $scope.pageList = []; //null,1,2,3,4,5 {pageNumber: , active : true/false}
        var firstPage = 1;
        var lastPage = $scope.entity.totalPages;
        if ($scope.entity.totalPages < 6) { //不足6页
        } else if ($scope.searchEntity.pageNumber < 4) {
            lastPage = 5;
        } else if ($scope.searchEntity.pageNumber > $scope.entity.totalPages - 3) {
            firstPage = $scope.entity.totalPages - 4;
        } else {        //中间5个
            firstPage = $scope.searchEntity.pageNumber - 2;
            lastPage = $scope.searchEntity.pageNumber + 2
        }
        for (var index = firstPage; index < lastPage + 1; index++) {
            $scope.pageList.push(index);
        }
    }


    $scope.itemSearch = function (){
        itemSearchService.itemSearch($scope.searchEntity).success(
            function (response) {
				$scope.entity = response;
                $scope.keywordsIsBrand();
                $scope.createPageList();
            })
	}

	//添加过滤选项
	$scope.addSearchOption = function (optionName, optionValue, optionKey) {
        if (optionName == "spec") {
            $scope.searchEntity.spec[optionKey] = optionValue;
        } else {
            $scope.searchEntity[optionName] = optionValue;
        }
    }
    //移除过滤选项
    $scope.removeSearchOption = function (optionName, optionKey) {
        if (optionName == "spec") {
            delete $scope.searchEntity.spec[optionKey];
        } else {
            if (optionName == "category") {
                $scope.searchEntity.spec = {};
                $scope.searchEntity.brand = null;
                $scope.searchEntity.category = null;
            }
            $scope.searchEntity[optionName] = null;
        }
    }
    $scope.$watch('searchEntity',function (newValue,oldValue) {
        if (newValue.keywords && newValue.keywords == oldValue.keywords && oldValue.pageNumber) {
            $scope.itemSearch();
        }
    },true)
});	
