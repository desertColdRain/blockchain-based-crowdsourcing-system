<!DOCTYPE html>
<html xmlns:th="http://www.w3.org/1999/xhtml">
<head lang="en">
    <meta charset="UTF-8">
    <title>系统登录 --基于区块链的移动众包匿名信任认证系统</title>
    <link type="text/css" rel="stylesheet" href="../css/style.css" />
    <script type="text/javascript" src="jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="jquery.cookie.js"></script>
    <script type="text/javascript">
        $(function(){
            $("#login").click(function(){
                var userName = $("#userName").val();
                var password = $("#userPassword").val();
                $.post("user/login",{"userName":userName,"password":password},function(result){
                    if(result != null){
                        if(result.success===true){ //登录成功
                            //保存用户名称到cookie,以便自动登录系统
                            var date = new Date();
                            //获取过期时间（单位：毫秒）
                            date.setTime(date.getTime()+parseInt($("#time").val())*1000);
                            //保存cookie信息
                            $.cookie("userName",result.data.userName,{expires:date,path:"/"});
                            //跳转到首页
                            location.href="user/frame";
                        }else{ //登录失败
                            //显示失败信息
                            $(".info").html(result.msg);
                        }
                    }
                },"json");
            })
        })
        $(function(){
            $("#register").click(function(){
                var userName = $("#userName").val();
                var password = $("#userPassword").val();
                $.post("user/register",{"userName":userName,"password":password},function(result){
                    if(result != null){
                        if(result.success===true){ //注册成功
                            //保存用户名称到cookie,以便自动登录系统
                            var date = new Date();
                            //获取过期时间（单位：毫秒）
                            date.setTime(date.getTime()+parseInt($("#time").val())*1000);
                            //保存cookie信息
                            $.cookie("userName",result.data.userName,{expires:date,path:"/"});
                            //跳转到首页
                            location.href="user/frame";
                        }else{ //登录失败
                            //显示失败信息
                            $(".info").html(result.msg);
                        }
                    }
                },"json");
            })
        })
    </script>
</head>
<body class="login_bg">
<section class="loginBox">
    <header class="loginHeader">
        <h1>基于区块链的移动众包匿名信任认证</h1>
    </header>
    <section class="loginCont">
        <form class="loginForm" action="user.html" name="actionForm" id="actionForm" method="post" >
            <div class="info"></div>
            <div class="inputbox">
                <label for="userName">用户名：</label>
                <input type="text" class="input-text" id="userName" name="userName" placeholder="请输入用户名"/>
            </div>
            <div class="inputbox">
                <label for="userPassword">密码：</label>
                <input type="password" id="userPassword" name="userPassword" placeholder="请输入密码"/>
            </div>
            <div class="inputbox">
                <label for="time">时间：</label>
                <select id="time">
                    <option value="60">1分钟</option>
                    <option value="3600">1小时</option>
                    <option value="86400">1天</option>
                    <option value="604800">7天</option>
                </select>
            </div>
            <div class="subBtn">
                <input type="button" id="login" value="登录"/>
                <input  type="button" id="reset" value="重置" />
                <a href="register.jsp" th:target="_self">注册</a>

            </div>

        </form>
    </section>
</section>
</body>
</html>