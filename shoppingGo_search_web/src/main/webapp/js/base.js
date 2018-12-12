var app=angular.module('shoppingGo',[]);

// app.filter("trustHtml",["$sce",function ($sce) {
//     return function (data) {
//         return $sce.trustAsHtml(data);
//     }
// }])

app.filter('trustHtml',['$sce',function($sce){
    return function(data){
        return $sce.trustAsHtml(data);
    }
}]);