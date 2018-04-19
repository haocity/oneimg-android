let scrolllock=false
		
		function orandom(){
			//orandom
			let count=3000; 
			let originalArray=new Array;//原数组 
			//给原数组originalArray赋值 
			for (let i=0;i<count;i++){
				originalArray[i]=i+1 
			} 
			originalArray.sort(function(){ return 0.5 - Math.random(); }); 

			return originalArray
		}

		document.addEventListener('scroll',()=>{
			if(!scrolllock&&!app.showright)
			if(document.documentElement.clientHeight + (document.documentElement.scrollTop||document.body.scrollTop ) >= document.body.offsetHeight-50) {
				console.log('load')
				scrolllock=true
				app.pushid(5)

			}
		

		})

		

	let o={};
	

	

	o.addlove=function(id){

		app.lovearr.push(id);
		localStorage.setItem('oneimg-love', JSON.stringify(app.lovearr));
	}
	o.dellove=function(id){
		for (var i in app.lovearr) {
			if (app.lovearr[i] == id) {
				app.lovearr.splice(i, 1);
				break;
			}
		}
		localStorage.setItem('oneimg-love', JSON.stringify(app.lovearr));
	}


	new AlloyFinger(document.body, {
	longTap: function longTap(evt) {
		//长按屏幕750ms触发
		
		for (var i in evt.path) {
			if (evt.path[i].className == "img-box") {
				let w=evt.path[i]
				let pid=w.getAttribute("pid")
				let ele=document.createElement('div')
				
				if(w.getAttribute("love")){
					ele.innerHTML = '<div class="zhe-m"><img src="no.png"/>已删除</div>'
					w.setAttribute('love','false')
					o.dellove(pid)
				}else{
					ele.innerHTML = '<div class="zhe-m"><img src="ok.png"/>已收藏</div>'
					w.setAttribute('love','true')
					o.addlove(pid)
				}
				for (let i = app.oneimgarr.length - 1; i >= 0; i--) {
					if(pid==app.oneimgarr[i].id){
						console.log(app.oneimgarr[i])
						app.oneimgarr[i].islove=!app.oneimgarr[i].islove
						break
					}	
				}
				ele.className='zhe'
				w.appendChild(ele)
				break
			}
		}
		
	},touchStart: function (evt) {
		console.log('start',evt.changedTouches[0].pageY);
		o.sy=evt.changedTouches[0].pageY;
		o.sx=evt.changedTouches[0].pageX;
	},
    touchMove: function (evt) {
    	let t=0-(o.sy-evt.changedTouches[0].pageY)
    	console.log('move',t);
    	if(t<80){
    		app.$el.querySelector('.top').style.transform=`translateY(${t-110}px)`;
    		app.$el.style.paddingTop=t+'px'
    	}else{
    		app.$el.querySelector('.top').style.transform=`translateY(0px)`;
    		app.$el.style.paddingTop='74px'
    	}
     },
    touchEnd:  function (evt) { 
    	let t=0-(o.sy-evt.changedTouches[0].pageY)
    	let t2=0-(o.sx-evt.changedTouches[0].pageX)
    	console.log('end',t)
    	if((document.documentElement.scrollTop||document.body.scrollTop)<10){
	    	if(t>80){
	    		app.init()
	    		
	    	}
	    	setTimeout(()=>{
	    		app.$el.querySelector('.top').style.transform=`translateY(-110px)`;
	    		app.$el.style.paddingTop='0px'
	    	},300)
    	}

    	console.log('t2',t2)

    },
	swipe: function swipe(evt) {
		//evt.direction代表滑动的方向
		let t2=0-(o.sx-evt.changedTouches[0].pageX)
		if(evt.direction=='Right'&&t2>80&&app.showright){
			app.showright=false
		}
		
		//if(swipe.direction=='Right')

	},
	tap: function tap(evt) {
		//点按触发
		for (var i in evt.path) {
			if (evt.path[i].className=='right') {

				let pid=evt.path[i].getAttribute("pid");
				app.nowid=pid;
				nav.canback=true
				app.showright=true
				
				
				
			}
		}
	}
});

	let app = new Vue({
		  el: '.content',
		  data: {
		  	smallapi:'https://oneimg.haotown.cn/data/small.php?id=',
		  	nowi:0,
		  	imgnumber:0,
		  	randomarr:[],
		    oneimgarr: [],
		    nowid:0,
		    showright:false,
		    full:{
		    	show:true,
		    	sms:'正在努力加载中'
		    }
		  },watch:{
				showright:function(i){
					if(i){
						o.scrolltop=document.documentElement.scrollTop
						setback(1)
						setTimeout(()=>{
							document.querySelector('.hco-w').innerHTML=''
							new Hco('.hco-w', 'oneimg', app.nowid);
						},100)
						
					}else{
						document.documentElement.scrollTop=o.scrolltop
						nav.canback=false
						setback(0)
					}
				}
			},
		  methods: {
		  	load(){
		  		axios.get('https://oneimg.haotown.cn/data/')
				  .then(function (response) {
				    app.imgnumber=response.data;
				    console.log('num',app.imgnumber)
				    app.init();
				    app.full.show=false
				  })
				  .catch(function (error) {
				    console.log(error);
				     app.full.sms='网络可能出错了 <br> 请尝试联系ONEIMG.HAOTOWN.CN'
				 });
		  	},
		  	init(){
		  		this.nowi=0;

		  		if (localStorage.getItem("oneimg-love")) {
					this.lovearr = JSON.parse(localStorage.getItem("oneimg-love"));
				} else {
					this.lovearr = new Array();
				}
		  		this.oneimgarr=[];
		  		this.robj=[]
		  		let t;

		  		if(nav.islove){
					console.log('喜欢页面')
					t=this.lovearr.reverse()
				}else{
					t=orandom(this.imgnumber);
				}
		  		
		  		for (let i = 0; i < t.length; i++) {
		  			let islove=false;
		  			for (let x = 0; x < this.lovearr.length; x++) {
		  				if(t[i]==this.lovearr[x]){
		  					islove=true
		  					console.log(t[i],i)
		  					break
		  				}
		  			}
		  			this.robj.push({
		  				id:t[i],
		  				islove:islove
		  			})
		  		}

		  		this.imgheight=document.querySelector('.app').offsetWidth*0.5625
		  		this.pushid(5)
		  	},getnum(){
		  		console.log(this)
		  	},pushid(num){
		  		if(!num){
		  			num=5	
		  		}
		  		for (var i = 0; i < num; i++) {
		  			if(this.robj[this.nowi])
		  			this.oneimgarr.push({
		  				id:this.robj[this.nowi].id,
		  				islove:this.robj[this.nowi].islove
		  			})
		  			this.nowi++	
		  		}

		  		scrolllock=false

		  	}

		  }
		});
		app.load()
		 
		
		let nav = new Vue({
			el:'.nav-warp',
			data:{
				canback:false,
				navlist:false,
				mode:1,
				islove:false,
				smg:{
					show:false,
					smgmode:0
				},
				set:{
					show:false,
					smgmode:1,
					title:'设置',
					content:`<p>模式一 使用启动器设置壁纸 <br>启动器不支持的情况下启用模式二<br>
        模式二 启用图片裁剪后设置  <br>部分设备可能切图卡住 请切图后等待几秒 点击退出即可设置壁纸  <br>
        模式三 直接设置整张图片</p>

        `
				},about:{
					show:false,
					smgmode:3,
					title:'关于',
					content:`<p>最美的壁纸给最美的你 <br>
        一图壁纸是个小型的壁纸网站 <br>
        在这里你可以找到大量的动漫壁纸 <br>
        本站所有资源均来自网络 <br>
        如果对你造成侵权行为，请联系我 <br>
        E-maill:ureygt@gmail.com <br>
        By:疯狂减肥带 <br>
        感谢!</p>
        <p style="text-align: right; margin-top: 10px;">
        <span class="smg-btn" onclick='openurl("https://github.com/haocity")'>GITHUB</span> 
        <span class="smg-btn" onclick='openurl("https://www.haotown.cn")'>BLOG</span> 
        <span class="smg-btn"  onclick='openurl("https://oneimg.haotown.cn")'>WEBSITE</span></p>
					 `
				}
			},watch:{
				islove:()=>{
					app.init()
				}
			},methods: {
				getsmode(i){
					let t;
					if(i==0){
						t='零'
					}else if(i==1){
						t='一'
					}else if(i==2){
						t='二'
					}else if(i==3){
						t='三'
					}else if(i==4){
						t='四'
					}
					return '模式'+t;
				}
			}
		})

function $c(e) {
	return document.querySelector(e);
}
function toastMessage(message) {
	window.control.toastMessage(message);
}
function setback(id) {
	try {
		window.control.setback(id);
	} catch (e) {}
}
function alertMessage(message) {
	alert(message);
}
function startoneimg(url, id) {
	window.control.startoneimg(url, id);
}
function jschangermode(i){
	nav.mode=i;
	nav.smg.show=false
	window.control.jschangermode(i);
}
function openurl(i){
	try{
		nav.smg.show=false
		window.control.openurl(i)
	}catch(e){
		window.location.href=i
	}
	
}

function oneimg(id) {
	app.full.show=true;
	var url = "http://t4.haotown.cn/img/bj@" + id + ".jpg";
	console.log(id);
		try {
			startoneimg(url, id);
		} catch (e) {
			app.full.sms= '<br>不支持的客户端<br><a style="color:#fff;" href="http://www.coolapk.com/apk/cn.haotowm.oneimg.client" target="_blank">下载最新版</a>';
		}
	
}
		