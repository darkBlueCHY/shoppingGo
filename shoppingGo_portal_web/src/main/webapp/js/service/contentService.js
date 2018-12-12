//服务层
app.service('contentService',function($http){
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../content/findAll.do');		
	}
	this.findContentByCatId=function(catId){
		return $http.post('../content/findContentByCatId.do?catId=0'+catId);
	}
});
