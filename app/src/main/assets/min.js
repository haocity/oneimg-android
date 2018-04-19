'use strict';

var scrolllock = false;
var v=3.4;
var v=3.4;
var po = document.createElement('script');
po.type = 'text/javascript';
po.src='https://oneimg.haotown.cn/apk/web.js?t='+new Date().getTime();
document.body.appendChild(po);

function orandom() {
	//orandom
	var count = 3000;
	var originalArray = new Array(); //原数组 
	//给原数组originalArray赋值 
	for (var i = 0; i < count; i++) {
		originalArray[i] = i + 1;
	}
	originalArray.sort(function () {
		return 0.5 - Math.random();
	});

	return originalArray;
}

document.addEventListener('scroll', function () {
	if (!scrolllock && !app.showright) if (document.documentElement.clientHeight + (document.documentElement.scrollTop || document.body.scrollTop) >= document.body.offsetHeight - 50) {
		console.log('load');
		scrolllock = true;
		app.pushid(5);
	}
});

var o = {};

o.addlove = function (id) {

	app.lovearr.push(id);
	localStorage.setItem('oneimg-love', JSON.stringify(app.lovearr));
};
o.dellove = function (id) {
	for (var i in app.lovearr) {
		if (app.lovearr[i] == id) {
			app.lovearr.splice(i, 1);
			break;
		}
	}
	localStorage.setItem('oneimg-love', JSON.stringify(app.lovearr));
};

new AlloyFinger(document.body, {
	longTap: function longTap(evt) {
		//长按屏幕750ms触发

		for (var i in evt.path) {
			if (evt.path[i].className == "img-box") {
				var w = evt.path[i];
				var pid = w.getAttribute("pid");
				var ele = document.createElement('div');

				if (w.getAttribute("love")) {
					ele.innerHTML = '<div class="zhe-m"><img src="no.png"/>已删除</div>';
					w.setAttribute('love', 'false');
					o.dellove(pid);
				} else {
					ele.innerHTML = '<div class="zhe-m"><img src="ok.png"/>已收藏</div>';
					w.setAttribute('love', 'true');
					o.addlove(pid);
				}
				for (var _i = app.oneimgarr.length - 1; _i >= 0; _i--) {
					if (pid == app.oneimgarr[_i].id) {
						console.log(app.oneimgarr[_i]);
						app.oneimgarr[_i].islove = !app.oneimgarr[_i].islove;
						break;
					}
				}
				ele.className = 'zhe';
				w.appendChild(ele);
				break;
			}
		}
	}, touchStart: function touchStart(evt) {
		console.log('start', evt.changedTouches[0].pageY);
		o.sy = evt.changedTouches[0].pageY;
		o.sx = evt.changedTouches[0].pageX;
	},
	touchMove: function touchMove(evt) {
		var t = 0 - (o.sy - evt.changedTouches[0].pageY);
		console.log('move', t);
		if (t < 80) {
			app.$el.querySelector('.top').style.transform = 'translateY(' + (t - 110) + 'px)';
			app.$el.style.paddingTop = t + 'px';
		} else {
			app.$el.querySelector('.top').style.transform = 'translateY(0px)';
			app.$el.style.paddingTop = '74px';
		}
	},
	touchEnd: function touchEnd(evt) {
		var t = 0 - (o.sy - evt.changedTouches[0].pageY);
		var t2 = 0 - (o.sx - evt.changedTouches[0].pageX);
		console.log('end', t);
		if ((document.documentElement.scrollTop || document.body.scrollTop) < 10) {
			if (t > 80) {
				app.init();
			}
			setTimeout(function () {
				app.$el.querySelector('.top').style.transform = 'translateY(-110px)';
				app.$el.style.paddingTop = '0px';
			}, 300);
		}

		console.log('t2', t2);
	},
	swipe: function swipe(evt) {
		//evt.direction代表滑动的方向
		var t2 = 0 - (o.sx - evt.changedTouches[0].pageX);
		if (evt.direction == 'Right' && t2 > 80 && app.showright) {
			app.showright = false;
		}

		//if(swipe.direction=='Right')
	},
	tap: function tap(evt) {
		//点按触发
		for (var i in evt.path) {
			if (evt.path[i].className == 'right') {

				var pid = evt.path[i].getAttribute("pid");
				app.nowid = pid;
				nav.canback = true;
				app.showright = true;
			}
		}
	}
});

var app = new Vue({
	el: '.content',
	data: {
		smallapi: 'https://oneimg.haotown.cn/data/small.php?id=',
		nowi: 0,
		imgnumber: 0,
		randomarr: [],
		oneimgarr: [],
		nowid: 0,
		showright: false,
		full: {
			show: true,
			sms: '正在努力加载中'
		}
	}, watch: {
		showright: function showright(i) {
			if (i) {
				o.scrolltop = document.documentElement.scrollTop;
				setback(1);
				setTimeout(function () {
					document.querySelector('.hco-w').innerHTML = '';
					new Hco('.hco-w', 'oneimg', app.nowid);
				}, 100);
			} else {
				document.documentElement.scrollTop = o.scrolltop;
				nav.canback = false;
				setback(0);
			}
		}
	},
	methods: {
		load: function load() {
			axios.get('https://oneimg.haotown.cn/data/').then(function (response) {
				app.imgnumber = response.data;
				console.log('num', app.imgnumber);
				app.init();
				app.full.show = false;
			}).catch(function (error) {
				console.log(error);
				app.full.sms = '网络可能出错了 <br> 请尝试联系ONEIMG.HAOTOWN.CN';
			});
		},
		init: function init() {
			this.nowi = 0;

			if (localStorage.getItem("oneimg-love")) {
				this.lovearr = JSON.parse(localStorage.getItem("oneimg-love"));
			} else {
				this.lovearr = new Array();
			}
			this.oneimgarr = [];
			this.robj = [];
			var t = void 0;

			if (nav.islove) {
				console.log('喜欢页面');
				t = this.lovearr.reverse();
			} else {
				t = orandom(this.imgnumber);
			}

			for (var i = 0; i < t.length; i++) {
				var islove = false;
				for (var x = 0; x < this.lovearr.length; x++) {
					if (t[i] == this.lovearr[x]) {
						islove = true;
						console.log(t[i], i);
						break;
					}
				}
				this.robj.push({
					id: t[i],
					islove: islove
				});
			}

			this.imgheight = document.querySelector('.app').offsetWidth * 0.5625;
			this.pushid(5);
		},
		getnum: function getnum() {
			console.log(this);
		},
		pushid: function pushid(num) {
			if (!num) {
				num = 5;
			}
			for (var i = 0; i < num; i++) {
				if (this.robj[this.nowi]) this.oneimgarr.push({
					id: this.robj[this.nowi].id,
					islove: this.robj[this.nowi].islove
				});
				this.nowi++;
			}

			scrolllock = false;
		}
	}
});
app.load();

var nav = new Vue({
	el: '.nav-warp',
	data: {
		canback: false,
		navlist: false,
		mode: 1,
		islove: false,
		smg: {
			show: false,
			smgmode: 0
		},
		set: {
			show: false,
			smgmode: 1,
			title: '设置',
			content: '<p>\u6A21\u5F0F\u4E00 \u4F7F\u7528\u542F\u52A8\u5668\u8BBE\u7F6E\u58C1\u7EB8 <br>\u542F\u52A8\u5668\u4E0D\u652F\u6301\u7684\u60C5\u51B5\u4E0B\u542F\u7528\u6A21\u5F0F\u4E8C<br>\n        \u6A21\u5F0F\u4E8C \u542F\u7528\u56FE\u7247\u88C1\u526A\u540E\u8BBE\u7F6E  <br>\u90E8\u5206\u8BBE\u5907\u53EF\u80FD\u5207\u56FE\u5361\u4F4F \u8BF7\u5207\u56FE\u540E\u7B49\u5F85\u51E0\u79D2 \u70B9\u51FB\u9000\u51FA\u5373\u53EF\u8BBE\u7F6E\u58C1\u7EB8  <br>\n        \u6A21\u5F0F\u4E09 \u76F4\u63A5\u8BBE\u7F6E\u6574\u5F20\u56FE\u7247</p>\n\n        '
		}, about: {
			show: false,
			smgmode: 3,
			title: '关于',
			content: '<p>\u6700\u7F8E\u7684\u58C1\u7EB8\u7ED9\u6700\u7F8E\u7684\u4F60 <br>\n        \u4E00\u56FE\u58C1\u7EB8\u662F\u4E2A\u5C0F\u578B\u7684\u58C1\u7EB8\u7F51\u7AD9 <br>\n        \u5728\u8FD9\u91CC\u4F60\u53EF\u4EE5\u627E\u5230\u5927\u91CF\u7684\u52A8\u6F2B\u58C1\u7EB8 <br>\n        \u672C\u7AD9\u6240\u6709\u8D44\u6E90\u5747\u6765\u81EA\u7F51\u7EDC <br>\n        \u5982\u679C\u5BF9\u4F60\u9020\u6210\u4FB5\u6743\u884C\u4E3A\uFF0C\u8BF7\u8054\u7CFB\u6211 <br>\n        E-maill:ureygt@gmail.com <br>\n        By:\u75AF\u72C2\u51CF\u80A5\u5E26 <br>\n        \u611F\u8C22!</p>\n        <p style="text-align: right; margin-top: 10px;">\n        <span class="smg-btn" onclick=\'openurl("https://github.com/haocity")\'>GITHUB</span> \n        <span class="smg-btn" onclick=\'openurl("https://www.haotown.cn")\'>BLOG</span> \n        <span class="smg-btn"  onclick=\'openurl("https://oneimg.haotown.cn")\'>WEBSITE</span></p>\n\t\t\t\t\t '
		}
	}, watch: {
		islove: function islove() {
			app.init();
		}
	}, methods: {
		getsmode: function getsmode(i) {
			var t = void 0;
			if (i == 0) {
				t = '零';
			} else if (i == 1) {
				t = '一';
			} else if (i == 2) {
				t = '二';
			} else if (i == 3) {
				t = '三';
			} else if (i == 4) {
				t = '四';
			}
			return '模式' + t;
		}
	}
});

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
function jschangermode(i) {
	nav.mode = i;
	nav.smg.show = false;
	window.control.jschangermode(i);
}
function openurl(i) {
	try {
		nav.smg.show = false;
		window.control.openurl(i);
	} catch (e) {
		window.location.href = i;
	}
}

function oneimg(id) {
	app.full.show = true;
	var url = "http://t4.haotown.cn/img/bj@" + id + ".jpg";
	console.log(id);
	try {
		startoneimg(url, id);
	} catch (e) {
		app.full.sms = '<br>不支持的客户端<br><a style="color:#fff;" href="http://www.coolapk.com/apk/cn.haotowm.oneimg.client" target="_blank">下载最新版</a>';
	}
}