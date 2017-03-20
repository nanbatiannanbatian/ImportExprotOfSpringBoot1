$(function() {
	/*
	 * 2. 给注册按钮添加submit()事件，完成表单校验
	 */
	$("#submit").click(function() {
		var bool = true;
		$(":input").each(function() {
			var inputName = $(this).attr("name");
			if (!validateUsername()) {
				bool = false;
			}
			if(!validatePassword()){
				bool = false;
			}
		});
		if (bool == "true" || bool) {
			login();
		}
	});

	/*
	 * 3. 输入框得到焦点时隐藏错误信息
	 */
	$("#username").focus(function() {
		$("#passwordHasError").css("display", "none");
	});
	$("#password").focus(function() {
		$("#passwordHasError").css("display", "none");
	});

	/*
	 * 4. 输入框退出焦点时进行校验
	 */
/*	$(".form-password form-control").blur(function() {
		validatePassword();
	})
	$(".form-username form-control").blur(function() {
		validateUsername();
	})*/
});

/* 进行登录 */
function login() {
	var username = $("#username").val();
	var password = $("#password").val();
	$.ajax({
		url : "/login/login",
		data : {
			"username" : username,
			"password" : password
		},
		type : "get",
		dataType : "text",
		success : function(result) {
			if (result == "true") {
				window.location.href = "/question/getQuestionHtml";
			} else {
				
				$("#passwordHasError").css("display", "");
				$("#passwordHasError").css("color", "red");
				$("#passwordHasError").text(result);
			}

		},
	});
}


/*
 * 校验登录名
 */
function validateUsername() {
	var bool = true;
	/*$("#usernameError").css("display", "none");*/
	var value = $("#username").val();
	if (!value) {// 非空校验
		$("#passwordHasError").css("display", "");
		$("#passwordHasError").css("color", "red");
		$("#passwordHasError").text("请输入账户名或密码");
		bool = false;
	} else if (value.length < 3 || value.length > 20) {// 长度校验
		$("#passwordHasError").css("display", "");
		$("#passwordHasError").css("color", "red");
		$("#passwordHasError").text("用户名长度必须在3 ~ 20之间！");
		bool = false;
	}

	return bool;
}

/*
 * 校验密码
 */
function validatePassword() {
	var bool = true;
	/*$("#passwordError").css("display", "none");*/
	var value = $("#password").val();
	if (!value) {// 非空校验
		$("#passwordHasError").css("display", "");
		$("#passwordHasError").css("color", "red");
		$("#passwordHasError").text("请输入账户名或密码");
		bool = false;
	} else if (value.length < 3 || value.length > 20) {// 长度校验
		$("#passwordHasError").css("display", "");
		$("#passwordHasError").css("color", "red");
		$("#passwordHasError").text("密码长度必须在3 ~ 20之间！");
		bool = false;
	}
	return bool;
}
