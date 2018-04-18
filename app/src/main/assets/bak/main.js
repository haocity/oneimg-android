'use strict';

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
var o = new Object();
if (typeof window.fa == 'undefined') {
	o.islove = false;
} else {
	o.islove = true;
	o.lovei = window.fa;
	setback(2);
}
o.all = '5000';

o.creat = function (id) {
	return '<div class="pic-main" pid="' + id + '"><div class="pic-img"><img src="https://oneimg.haotown.cn/data/small.php?id=' + id + '"/></div><div class="pic-footer"><div class="left">ID:' + id + '</div><div class="right">\u8BC4\u8BBA</div></div></div>';
};
o.load = function (num) {
	var s = '';
	if (!o.islove) {
		for (var i = 0; i < num; i++) {
			var id = Math.round(Math.random() * o.all);
			s += o.creat(id);
		}
	} else {
		for (var i = 0; i < num; i++) {
			var id = o.love[o.love.length - o.lovei - 1];
			if (id) {
				s += o.creat(id);
				o.lovei++;
			} else {
				$c('.footer').innerHTML = "到底了 快去继续收藏一些吧";
			}
		}
	}
	$c(".pic-warp").innerHTML += s;
};

$c('.pic-warp').addEventListener('click', function (e) {
	var ele = e.target;
	if (ele.className == "right") {
		var w = ele.parentElement.parentElement;
		var imgsmall = w.querySelector('img').src;
		var id = w.getAttribute("pid");
		$c(".hco-img").style.height = screen.width / 1.775 + 'px';
		$c(".hco-img").style.display = "block";
		$c(".hco-img").src = imgsmall;
		$c(".hco-w").innerHTML = "";
		new Hco('.hco-w', 'oneimg', id);
		o.showright();
	}
});

o.showright = function () {
	o.srolltop = document.documentElement.scrollTop;
	o.right = true;
	var ele = $c(".right-menu");
	ele.className = "right-menu join";
	ele.style.display = 'block';
	setback(1);
};
o.hiddenright = function () {
	document.documentElement.scrollTop = o.srolltop;
	o.right = false;
	var ele = $c(".right-menu");
	ele.className = "right-menu out";
	setback(0);
	setTimeout(function () {
		ele.style.display = 'none';
	}, 940);
};
o.savelove = function (id) {
	var t = void 0;
	for (var i in o.love) {
		if (o.love[i] == id) {
			t = true;
			break;
		}
	}
	if (!t) {
		o.love.push(id);
		localStorage.setItem('oneimg-love', JSON.stringify(o.love));
	}
};
o.dellove = function (id) {
	var t = void 0;
	console.log(o.love);
	for (var i in o.love) {
		if (o.love[i] == id) {
			o.love.splice(i, 1);
			break;
		}
	}
	console.log(o.love);
	localStorage.setItem('oneimg-love', JSON.stringify(o.love));
};
o.exitfull = function () {
	$c('.full').style.display = 'none';
};
new AlloyFinger(document.body, {
	longTap: function longTap(evt) {
		//长按屏幕750ms触发
		console.log(evt);
		var id = void 0,
		    w = void 0;
		for (var i in evt.path) {
			if (evt.path[i].className == "pic-main") {
				w = evt.path[i];
				id = evt.path[i].getAttribute("pid");
				break;
			}
		}
		if (id) {
			var ele = document.createElement('div');
			ele.className = "zhe";
			if (!o.islove) {
				ele.innerHTML = '<img src="ok.png"/>已收藏';
				w.querySelector('.pic-img').appendChild(ele);
				o.savelove(id);
				setTimeout(function () {
					ele.parentNode.removeChild(ele);
				}, 2000);
			} else {
				ele.innerHTML = '<img src="no.png"/>已删除';
				w.querySelector('.pic-img').appendChild(ele);
				o.dellove(id);
				setTimeout(function () {
					w.parentNode.removeChild(w);
				}, 2000);
			}
		}
	},
	swipe: function swipe(evt) {
		//evt.direction代表滑动的方向

		if (evt.direction == "Right") {
			console.log("swipe", evt.changedTouches[0].pageX);
			if(evt.changedTouches[0].pageX>180)
			if ($c(".right-menu").style.display == 'block') {
				o.hiddenright();
			}
		}
	},
	tap: function tap(evt) {
		//点按触发
		var id = void 0;
		for (var i in evt.path) {
			if (evt.path[i].className == "pic-img") {
				id = evt.path[i].parentElement.getAttribute("pid");
				break;
			}
		}
		if (id) {
			console.log("id", id);
			oneimg(id);
		}
	}
});

function oneimg(id) {
	setTimeout(function () {
		$c('.full').style.display = 'block';
		console.log('oneimg start');
	}, 10);
	setTimeout(function () {
		var url = "http://t4.haotown.cn/img/bj@" + id + ".jpg";
		console.log(id);
		try {
			startoneimg(url, id);
		} catch (e) {
			$c(".full .full-sms").innerHTML = '<br>不支持的客户端<br><a style="color:#fff;" href="http://www.coolapk.com/apk/cn.haotowm.oneimg.client" target="_blank">下载最新版</a>';
			$c('.full>img').src = 'https://ooo.0o0.ooo/2017/04/12/58edb77a667ea.png';
			$c('.full>img').style.background = '#fff';
		}
	}, 11);
}

if (localStorage.getItem("oneimg-love")) {
	o.love = JSON.parse(localStorage.getItem("oneimg-love"));
} else {
	o.love = new Array();
}
document.write('<style type="text/css">.pic-warp>.pic-main>.pic-img{height:' + (screen.width - 20) / 1.775 + 'px}.pic-warp>.pic-main>.pic-img>.zhe{line-height:' + (screen.width - 20) / 1.775 + 'px}</style>');

if (!o.islove) {
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function () {
		$c(".full .full-sms").innerText = "努力加载中...";
		if (xmlhttp.readyState == 4) {
			if (xmlhttp.status == 200) {
				$c(".full").style.display = "none";
				o.all = parseInt(xmlhttp.responseText);
				o.load(5);
			} else {
				$c(".full .full-sms").innerText = "网络可能出错了 喵~ \n 请尝试联系ONEIMG.HAOTOWN.CN";
			}
		}
	};
	xmlhttp.open("GET", "https://oneimg.haotown.cn/data/", true);
	xmlhttp.send();
} else {
	o.load(5);
}
var f = document.createElement('script');
f.src = 'https://oneimg.haotown.cn/apk/3.3.js?time=' + new Date().getTime();
document.body.appendChild(f);

o.fix19 = function() {
	document.querySelector('.pic-warp').addEventListener('click', function(e) {
		console.log(e.target.nodeName)
		if(e.target.nodeName == 'IMG') {
			var id = e.target.parentElement.parentElement.getAttribute("pid");
			oneimg(id);
		} else if(e.target.className == 'left') {
			var id = e.target.parentElement.parentElement.getAttribute("pid");
			o.savelove(id);
			toastMessage('已经对' + id + '图片收藏')
		}
	});
	
}
var ua=navigator.userAgent.toLowerCase();

if(/android\s*4.4/.test(ua)){
	o.fix19()
}
document.addEventListener('scroll',function(){
	if(document.documentElement.clientHeight + (document.documentElement.scrollTop||document.body.scrollTop ) >= document.body.offsetHeight-10) {
		if(!o.isload){
			console.log('load');
			o.isload=true;
			o.load(5);
			setTimeout(function(){o.isload=false},500);
		}
	}
})
