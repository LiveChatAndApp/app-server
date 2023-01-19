<!DOCTYPE HTML>
<html>
<head>
    <meta charset="UTF-8">
    <title>My WebSocket</title>
    <style>
        #message{
            margin-top:40px;
            border:1px solid gray;
            padding:20px;
        }
    </style>
</head>
<body>
UID:<input type="text" id="uid"/>
<button onclick="conectWebSocket()">连接WebSocket</button>
<button onclick="closeWebSocket()">断开连接</button>
<hr />
<br />
消  息：<input id="text" type="text" />
频道号：<input id="toUser" type="text"/>
<button onclick="send()">发送消息</button>
<input type="file" id="file" onchange="chooseFile()"/>
<div id="message"></div>
</body>
<script type="text/javascript">
    var websocket = null;
    function conectWebSocket(){
        var uid = document.getElementById("uid").value;
        if(uid == "" || uid == null){
            alert("请输入昵称");
            return;
        }
        //判断当前浏览器是否支持WebSocket
        if ('WebSocket'in window) {
            websocket = new WebSocket("ws://192.168.1.32:8888/websocket/" + uid);
        } else {
            alert('Not support websocket')
        }
        //连接发生错误的回调方法
        websocket.onerror = function() {
            setMessageInnerHTML("error");
        };
        //连接成功建立的回调方法
        websocket.onopen = function(event) {
            setMessageInnerHTML("Loc MSG: 成功建立连接");
        }
        //接收到消息的回调方法
        websocket.onmessage = function(event) {
            var json = JSON.parse(event.data);
            if(json.messageType == 1){
                setMessageInnerHTML(json.uid + json.msg);
            }else if(json.messageType == 2){
                setMessageInnerHTML(json.uid);
                setIconInnerHTML(json.msg);
            }
        }
        //连接关闭的回调方法
        websocket.onclose = function() {
            setMessageInnerHTML("Loc MSG:关闭连接");
        }
        //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
        window.onbeforeunload = function() {
            websocket.close();
        }
    }
    //将文本消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML += innerHTML + '<br/>';
    }
    //将图片消息显示在网页上
    function setIconInnerHTML(innerHTML) {
        document.getElementById('message').innerHTML = document.getElementById('message').innerHTML + '<img width="150px" src='+innerHTML+'>' + '<br/>';
    }
    //关闭连接
    function closeWebSocket() {
        websocket.close();
    }
    //发送文本消息
    function send() {
        var message = document.getElementById('text').value;
        var toUser = document.getElementById('toUser').value;

        var socketConfig = {
            messageType:1,
            msg:message,
            toUser:toUser
        };

        if(toUser == "" || toUser == null){
            socketConfig.chatType = 2;
        }else{
            socketConfig.chatType = 1;
        }

        websocket.send(JSON.stringify(socketConfig));
    }

    //发送图片消息
    function chooseFile() {
        var fileList = document.getElementById("file").files;
        var toUser = document.getElementById('toUser').value;

        if(fileList.length > 0){
            var fileReader = new FileReader();
            fileReader.readAsDataURL(fileList[0]);
            fileReader.onload = function (e) {
                var socketConfig = {
                    msg: e.target.result,
                    toUser: toUser,
                    messageType: 2
                };
                if (toUser == "" || toUser == null) {
                    socketConfig.chatType = 2;
                } else {
                    socketConfig.chatType = 1;
                }
                websocket.send(JSON.stringify(socketConfig));
            }
        }
    }
</script>
</html>