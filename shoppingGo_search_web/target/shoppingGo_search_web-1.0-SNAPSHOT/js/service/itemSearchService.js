//服务层
app.service('itemSearchService',function($http){

	//从Solr中读取商品信息
	this.itemSearch=function (requestEntity) {
		return $http.post('../itemSearch/search.do',requestEntity);
    }
});
