app.controller('itemPageController',function($scope,itemPageService){

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

    $scope.choicedSKU=function(){
        flag: for (var index = 0 ; index < $scope.itemList.length ; index ++) {
            for (var option in $scope.itemList[index].spec) {
                if ($scope.itemList[index].spec[option] != $scope.choicedSpec[option]) {
                    continue flag;
                }
            }
            $scope.selectedSKU = $scope.itemList[index];
        }
    };
})