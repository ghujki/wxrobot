function now() {return + new Date;} 
function getDeviceID() {return "e" + ("" + Math.random().toFixed(15)).substring(2, 17);}
function getCookie(e) {
	for (var t = e + "=",o = document.cookie.split(";"), n = 0; n < o.length; n++) { 
		for (var r = o[n];" " == r.charAt(0);) r = r.substring(1); 
			if ( - 1 != r.indexOf(t)) return r.substring(t.length, r.length)} return "";}
			
function getMsgId () {return (now() + Math.random().toFixed(3)).replace(".", "");}

function sendTextMessage(content,to) {
   var $scope1 = angular.element('.chat_item').scope();
   var local_id = getMsgId();
   var b = {"BaseRequest":{"Uin":$scope1.account.Uin,"Sid":getCookie("wxsid"),"Skey":$scope1.account.HeadImgUrl.substring($scope1.account.HeadImgUrl.lastIndexOf("=") + 1),
            "DeviceID":getDeviceID()},"Msg":{"Type":1,"Content":content,"FromUserName":$scope1.account.UserName,
            "ToUserName":to,"LocalID":local_id,"ClientMsgId":local_id},"Scene":0};
   $.ajax({
		url:'/cgi-bin/mmwebwx-bin/webwxsendmsg',
      	contentType:'application/json;charset=UTF-8',
      	type:'POST',
      	data:JSON.stringify(b),
      	success:function(ex){ 
      			var e = JSON.parse(ex);
      			if (e.MsgID == null || e.MsgID == '') {
      				alert('文字发送出错');
      			};
        },
		error:function(e){alert(e.responseText);}
   });
}

function sendImageMessage(mediaId,to) {
	var $scope1 = angular.element('.chat_item').scope();
   	var local_id = getMsgId();
   	var b = {"BaseRequest":{"Uin":$scope1.account.Uin,"Sid":getCookie("wxsid"),"Skey":$scope1.account.HeadImgUrl.substring($scope1.account.HeadImgUrl.lastIndexOf("=") + 1),
            "DeviceID":getDeviceID()},"Msg":{"Type":3,"MediaId":mediaId,"FromUserName":$scope1.account.UserName,
            "ToUserName":to,"LocalID":local_id,"ClientMsgId":local_id},"Scene":0};
   	$.ajax({
		url:'/cgi-bin/mmwebwx-bin/webwxsendmsgimg?fun=async&f=json',
      	contentType:'application/json;charset=UTF-8',
      	type:'POST',
      	data:JSON.stringify(b),
      	success:function(ex){ 
      			var e = JSON.parse(ex);
      			if (e.MsgID == null || e.MsgID == '') {
      				alert('图片发送出错,可能资源已过期,需要重新设置图片');
      			};
        },
		error:function(e){alert(e.responseText);}
   });
}