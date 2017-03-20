window.type = "";
$(function() {
	/* 显示刚刚注册的用户名 */
	showusername();
	/* 把下面的两个函数放入一个函数中确保执行顺序 */
	showAllSelectAndIsUpdateOrNot();
	/*
	 * 显示题目类型下拉框 showallselect(); 判断是update的话就显示数据 isUpdateOrNot();
	 */
	$("#submit").click(function() {

		var bool = true;
		$(".layui-input").each(function() {
			var inputName = $(this).attr("name");
			if (!invokeValidateFunction(inputName)) {
				bool = false;
			}
		});
		if (bool == "true" || bool) {
			if (confirm("确定提交所填写内容？")) {
				addonequestion();
			}
		}

		return false;
	});
	$("#submit1").click(function() {
		var bool = true;
		$(".layui-input").each(function() {
			var inputName = $(this).attr("name");
			if (!invokeValidateFunction(inputName)) {
				bool = false;
			}
		});
		if (bool == "true" || bool) {
			if (confirm("确定修改所有选项为所填写内容？")) {
				updateonequestion();
			}
		}

		return false;
	});

	/*
	 * 3. 输入框得到焦点时隐藏错误信息
	 */
	$(".layui-input").focus(function() {
		var inputName = $(this).attr("name");
		$("#" + inputName + "Error").css("display", "none");
	});

	/*
	 * 4. 输入框退出焦点时进行校验
	 */
	$(".layui-input").blur(function() {
		var inputName = $(this).attr("name");
		invokeValidateFunction(inputName);
	})
});
/* 查找所有类型在网页显现出来 */
function showallselect() {
	$.ajax({
		url : "/question/findallType",
		type : "get",
		dataType : "json",
		success : function(result) {
			showselect(result);
		},
	});
}

/* 查找所有类型在网页显现出来 */
function showselect(result) {

	var tbody = "";
	$.each(result, function(n, value) {
		if (value.tname != type) {
			var trs = "";
			trs += "<option value='" + value.tname + "'>" + value.tname
					+ "</option>";
			tbody += trs;
		} else {
			var trs = "";
			trs += "<option value='" + value.tname + "' selected='selected'>"
					+ value.tname + "</option>";
			tbody += trs;
		}
	});
	$("#select").append(tbody);
}

function addonequestion() {
	/* 添加一个数据后回到第一页显示 */
	currentPage = 1;

	url = "/question/findAllByPage";
	title = "";

	var tqtitle = $("#p_title").val();
	var optiona = $("#p_optiona").val();
	var optionb = $("#p_optionb").val();
	var optionc = $("#p_optionc").val();
	var optiond = $("#p_optiond").val();
	var question = $("#p_question").val();
	var typename = $('#select option:selected').text();
	$.ajax({
		url : "/question/addone",
		data : {
			"tqtitle" : tqtitle,
			"optiona" : optiona,
			"optionb" : optionb,
			"optionc" : optionc,
			"optiond" : optiond,
			"question" : question,
			"type.tname" : typename
		},
		type : "get",
		dataType : "text",
		success : function(result) {
			if (result == "输入的字符长度超过标准！请删减后再输入") {
				$("#showmessage").text(result);
			} else {
				window.location.href = "/question/getQuestionHtml";
			}

		},
	});
}

function updateonequestion() {
	var tqtitle = $("#p_title").val();
	var optiona = $("#p_optiona").val();
	var optionb = $("#p_optionb").val();
	var optionc = $("#p_optionc").val();
	var optiond = $("#p_optiond").val();
	var question = $("#p_question").val();
	var typename = $('#select option:selected').text();
	$.ajax({
		url : "/question/updateone",
		data : {
			"tqtitle" : tqtitle,
			"optiona" : optiona,
			"optionb" : optionb,
			"optionc" : optionc,
			"optiond" : optiond,
			"question" : question,
			"type.tname" : typename
		},
		type : "get",
		dataType : "text",
		success : function(result) {
			window.location.href = "/question/getQuestionHtml";
		},
	});
}

function showusername() {
	$.ajax({
		url : "/login/findusername",
		type : "get",
		dataType : "text",
		success : function(result) {
			$("#username").text(result);
			$("#username").css("color", "red");
			$("#leavelogin").css("color", "blue");
		},
	});
}

/* 返回所有的主键ID并且以_拼接 */
function retuenTotalId(bool) {
	var totalId = "";
	$(":checkbox[name=checkboxBtn][checked=true]").each(function() {
		if (!bool) {
			var id = $(this).val();
			totalId = totalId + id + "_";
		}
		bool = "";
	});
	return totalId;
}
/* 通过当前页数查找所有试题 */
function findAllUseDeleteOrUpdate(currentPage) {
	$.ajax({
		url : "/question/findAllByPage",
		data : {
			"currentPage" : currentPage
		},
		type : "get",
		dataType : "json",
		success : function(result) {
			totalPage = result.totalPage;
			showallpage(result);
		},
	});
}
/* 通过当前页数按照类型标题查找所有试题 */
function findAllAfterQueryUseDeleteOrUpdate(currentPage) {
	$.ajax({
		url : "/question/findTqtitleAndTypeByPage",
		data : {
			"currentPage" : currentPage,
			"findtype" : type,
			"findtitle" : alltqtitle
		},
		type : "get",
		dataType : "json",
		success : function(result) {
			showallpage(result);
		},
	});
}
/* 判断是否通过模糊查询采用不同分页策略修改或者删除 */
function hasQueryOrNotThenPaging(result) {
	if (url == "/question/findAllByPage" || url == "") {
		allpagejson();
	} else {
		findAllAfterQueryUseDeleteOrUpdate(currentPage);
	}
	alert(result);
}
/* 退出登录 */
function leavelogin() {
	$.ajax({
		url : "/login/leavelogin",
		type : "get",
		dataType : "text",
		success : function(result) {
			if (result == "成功退出") {
				window.location.href = "/login/showlogin";
			} else {
				alert(result);
			}
		},
	});
}
function inputIsToLongOrNot(optiona, optionb, optionc, optiond, question,
		tqtitle) {
	if (optiona.length >= 255) {

	}
}

/*
 * 输入input名称，调用对应的validate方法。 例如input名称为：loginname，那么调用validateLoginname()方法。
 */
function invokeValidateFunction(inputName) {
	inputName = inputName.substring(0, 1).toUpperCase()
			+ inputName.substring(1);
	var functionName = "validate" + inputName;
	return eval(functionName + "()");
}

/*
 * 校验试题标题
 */
function validateTitle() {
	var bool = true;
	$("#titleError").css("display", "none");
	var value = $("#p_title").val();
	if (!value) {// 非空校验
		$("#titleError").css("display", "");
		$("#titleError").css("color", "red");
		$("#titleError").text("标题不能为空！");
		bool = false;
	} else if (value.length > 1000) {// 长度校验
		$("#titleError").css("display", "");
		$("#titleError").css("color", "red");
		$("#titleError").text("标题字数太长超出限制！");
		bool = false;
	}

	return bool;
}

/*
 * 校验选项A
 */
function validateOptiona() {
	var bool = true;
	$("#optionaError").css("display", "none");
	var value = $("#p_optiona").val();
	if (!value) {// 非空校验
		$("#optionaError").css("display", "");
		$("#optionaError").css("color", "red");
		$("#optionaError").text("选项A不能为空！");
		bool = false;
	} else if (value.length > 255) {// 长度校验
		$("#optionaError").css("display", "");
		$("#optionaError").css("color", "red");
		$("#optionaError").text("选项A字数太长超出限制！");
		bool = false;
	}
	return bool;
}
/*
 * 校验选项B
 */
function validateOptionb() {
	var bool = true;
	$("#optionbError").css("display", "none");
	var value = $("#p_optionb").val();
	if (!value) {// 非空校验
		$("#optionbError").css("display", "");
		$("#optionbError").css("color", "red");
		$("#optionbError").text("选项B不能为空！");
		bool = false;
	} else if (value.length > 255) {// 长度校验
		$("#optionbError").css("display", "");
		$("#optionbError").css("color", "red");
		$("#optionbError").text("选项B字数太长超出限制！");
		bool = false;
	}
	return bool;
}
/*
 * 校验选项C
 */
function validateOptionc() {
	var bool = true;
	$("#optioncError").css("display", "none");
	var value = $("#p_optionc").val();
	if (!value) {// 非空校验
		$("#optioncError").css("display", "");
		$("#optioncError").css("color", "red");
		$("#optioncError").text("选项C不能为空！");
		bool = false;
	} else if (value.length > 255) {// 长度校验
		$("#optioncError").css("display", "");
		$("#optioncError").css("color", "red");
		$("#optioncError").text("选项C字数太长超出限制！");
		bool = false;
	}
	return bool;
}
/*
 * 校验选项D
 */
function validateOptiond() {
	var bool = true;
	$("#optiondError").css("display", "none");
	var value = $("#p_optiond").val();
	if (!value) {// 非空校验
		$("#optiondError").css("display", "");
		$("#optiondError").css("color", "red");
		$("#optiondError").text("选项D不能为空！");
		bool = false;
	} else if (value.length > 255) {// 长度校验
		$("#optiondError").css("display", "");
		$("#optiondError").css("color", "red");
		$("#optiondError").text("选项D字数太长超出限制！");
		bool = false;
	}
	return bool;
}
/*
 * 校验答案
 */
function validateQuestion() {
	var bool = true;
	$("#questionError").css("display", "none");
	var value = $("#p_question").val();
	if (!value) {// 非空校验
		$("#questionError").css("display", "");
		$("#questionError").css("color", "red");
		$("#questionError").text("答案不能为空！");
		bool = false;
	} else if (value.length > 255) {// 长度校验
		$("#questionError").css("display", "");
		$("#questionError").css("color", "red");
		$("#questionError").text("答案字数太长超出限制！");
		bool = false;
	}
	return bool;
}
/* 判断是update的话就显示数据 */
function isUpdateOrNot() {
	if (window.location.pathname == "/question/showupdateHtml") {
		$.ajax({
			url : "/question/getQuestion",
			type : "get",
			dataType : "json",
			success : function(result) {
				$("#p_title").val(result.tqtitle);
				$("#p_optiona").val(result.optiona);
				$("#p_optionb").val(result.optionb);
				$("#p_optionc").val(result.optionc);
				$("#p_optiond").val(result.optiond);
				$("#p_question").val(result.question);
				type = result.type.tname;
				showallselect();
			},
		});
	}
	showallselect();
}
function showAllSelectAndIsUpdateOrNot() {
	isUpdateOrNot();
	$("#selectAll").removeAttr("checked", "checked");
}

function givetalk() {
	$("#showmessage").text("请完成相关操作");
	tsotsi($('#showmessage'), 8);
}
/**
 * @comment item 为匹配的标签的jquery对象，t表示剩余时间 实现功能为10秒内，渐渐消失
 * @author tsotsi
 * @date 2014/07/06
 */
function tsotsi(item, t) {

	if (t >= 0) {
		item.css({

			filter : 'alpha(opacity=' + 10 * t + ')', /* ie 有效 */
			'-moz-opacity' : 0.1 * t, /* Firefox 有效 */
			opacity : 0.1 * t
		/* 通用，其他浏览器 有效 */

		});
		setTimeout(tsotsi, 500, item, t - 0.5);
	}
}