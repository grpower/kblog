var oniuiUrl = "res/vendors/avalon/";

avalon.config({
	baseUrl:"/",
	paths:{
		
		//jquery
		jquery:'/res/vendors/jquery/jquery-2.1.4',//指定本地文件时，后缀.js可省略
		
		//avalon功能组件模块
		mmRequest:'/res/vendors/avalon/mmRequest/mmRequest',
		mmHistory:'/res/vendors/avalon/mmHistory/mmHistory',
		mmPromise:'/res/vendors/avalon/mmPromise/mmPromise',
		mmRouter:'/res/vendors/avalon/mmRouter/mmRouter',
		mmState:'/res/vendors/avalon/mmState/mmState',
		//avalon.oniui组件模块
		carousel : oniuiUrl+"carousel/avalon.carousel",
		dialog : oniuiUrl+"dialog/avalon.dialog",
		textbox : oniuiUrl+"textbox/avalon.textbox",
		pager : oniuiUrl+"pager/avalon.pager",
		cookie : oniuiUrl+"cookie/avalon.cookie",
		validation : oniuiUrl+"validation/avalon.validation",
		dropdown : oniuiUrl+"dropdown/avalon.dropdown",
		button : oniuiUrl+"button/avalon.button",
		simplegrid : oniuiUrl+"simplegrid/avalon.simplegrid",
		tab : oniuiUrl+"tab/avalon.tab",
		checkboxlist : oniuiUrl+"checkboxlist/avalon.checkboxlist",
		smartgrid: oniuiUrl+"smartgrid/avalon.smartgrid",
		datepicker: oniuiUrl+"datepicker/avalon.datepicker",
		spinner: oniuiUrl+"spinner/avalon.spinner",
		browser: oniuiUrl+"browser/avalon.browser",
		loading: oniuiUrl+"loading/avalon.loading",
		
		//bootstrap
		bootstrap:'/res/vendors/bootstrap/js/bootstrap.min',
		
		//其他工具类js
		base64:'/res/js/common/base64'
		
	},
	priority:['jquery','text','css'],//指定需要优先加载的模块
	shim:{//帮助RequireJS加载非AMD兼容的模块
		jquery:{
			exports:"jQuery"//暴露jQuery变量给requirejs
		},
		avalon:{
			exports:"avalon"
		},
		bootstrap:{//bootstrap依赖jquery,但是他是非AMD兼容模块，requirejs无法感知这个依赖关系，所以需要手动做一些处理。
			//require会在所有依赖模块加载完成之后再调用回调函数，但是，各个模块的加载是异步的，除非定义了依赖关系，否则加载顺序是不确定的；
			//如果某些非AMD兼容模块自身并没有能够定义依赖关系，但事实上却依赖其他模块，那么可以通过shim来声明依赖关系，从而保证加载顺序
			deps:["jquery"]//声明bootstrap依赖jquery
		}
	}
});


