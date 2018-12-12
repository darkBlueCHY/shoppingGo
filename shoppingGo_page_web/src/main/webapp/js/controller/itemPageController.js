app.controller('itemPageController',function($scope){
	
	$scope.choicedSpec = {};
	// $scope.specSize 由ng-init FreeMarker值传入
	$scope.itemList=[];

	$scope.buyCount = 1;
	$scope.countNumber=function(number){
		$scope.buyCount = $scope.buyCount + number;
		if ($scope.buyCount < 1) $scope.buyCount = 1;
	}
	$scope.$watch('itemListStr',function(newValue){
		if (angular.isString(newValue)) {
			$scope.itemList = JSON.parse(newValue);
		}
	}) 
	
	$scope.choiceSpec=function(specName,optionName){
		if ($scope.choicedSpec[specName] == optionName) {
			$scope.choicedSpec[specName] = null;
			$scope.selectedSKU = undefined;
		} else {
			$scope.choicedSpec[specName] = optionName;
		}
	}
	
	$scope.$watch('choicedSpec', function(newValue){
		var index = 0;
		for (var key in $scope.choicedSpec) {
			index ++ ;
			if (! newValue[key]) return;
		}
		if (!$scope.specSize || index < $scope.specSize) {
			return;
		} else {
			$scope.choicedSKU();
		}
	},true)
	
	//将item中的spec、brandJson数据转换为JS对象
	$scope.$watch('itemList',function(newValue){
		if (newValue) {
			for (var index = 0; index < $scope.itemList.length; index++) {
				$scope.itemList[index].spec = JSON.parse($scope.itemList[index].spec);
				if ($scope.itemList[index].isDefault == '1') {
					for (var key in $scope.itemList[index].spec) {
						$scope.choicedSpec[key] = $scope.itemList[index].spec[key];
					}
				}
			}
		}
	})
    $scope.choicedSKU=function(){
        for (var index = 0 ; index < $scope.itemList.length ; index ++) {
            var flag = true;
			for (var option in $scope.itemList[index].spec) {
                if ($scope.itemList[index].spec[option] != $scope.choicedSpec[option]) {
					flag = false;
					break;
                }
            }
			if (flag) {
				$scope.selectedSKU = $scope.itemList[index];
				break;
			}
        }
    }
	$scope.addToCart=function(){
		alert($scope.buyCount + "   " + $scope.selectedSKU.id);
	}
})