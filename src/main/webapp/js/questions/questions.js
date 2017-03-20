/*分页所需*/
window.currentPage = 1;
window.totalRecord;
window.pageSize;
window.totalPage;
window.previousPage;
window.nextPage;
window.pageBar;
window.url = "/question/findAllByPage";
/* 按照标题分页 */
window.alltqtitle = "";
window.type = "";
$(function() {
	/* 显示刚刚注册的用户名 */
	showusername();
	/* 显示所有分页的试题数据 */
	
	hasQueryOrNotThenPaging();
	/* 显示题目类型下拉框 */
	showallselect();
	/* 全选框绑定 */

	$("#selectAll").click(function() {
		var bool = $("#selectAll").prop("checked");
		/* 所有条件复选框绑定 */
		checkedornot(bool);
	});

	/*
	 * jquery 1.7版以前使用live动态绑定事件 $("#testdiv ul li").live("click",function(){
	 * }); jquery 1.7版以后使用on动态绑定事件 $("#testdiv ul").on("click","li", function() {
	 * //do something here });
	 */
	$("#project").on("click", ":checkbox[name=checkboxBtn]", function() {
		/*
		 * 1.勾选 查看是否全部勾选
		 */
		var bool = $("#selectAll").prop("checked");
		var num = $(":checkbox[name=checkboxBtn]:checked").length;
		var allnum = $(":checkbox[name=checkboxBtn]").length;
		if ((num == allnum - 1 && !bool) || num == allnum) { /* 全部勾选 */
			$("#selectAll").prop("checked", true);
		} else { /* 全部不选 */
			$("#selectAll").prop("checked", false);
		}
	});

});

/* 所有条件复选框勾选或者取消 */
function checkedornot(bool) {
	$(":checkbox[name=checkboxBtn]").prop("checked", bool);
}

/* 显示所有分页记录 */
function allpagejson() {

	if (url == "/question/findAllByPage" || url == "") {
		findAllUseDeleteOrUpdate(currentPage);
	} else {
		findAllAfterQueryUseDeleteOrUpdate(currentPage);
	}
}

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

/* 显示所有分页查出来的数据通过jquery加到DOM里面 */
function showallpage(result) {
	/* 显示查询出来的数据 */
	showresult(result);

	currentPage = result.currentPage;
	totalRecord = result.totalRecord;
	pageSize = result.pageSize;
	totalPage = result.totalPage;
	previousPage = result.previousPage;
	nextPage = result.nextPage;
	pageBar = result.pageBar;
	url = result.url;
	/* 添加页面信息：当前页，查找的标题类型以及URL */
	addPaging();
	/* 显示所有按钮信息 */
	showbuttonhtml();
	/* 显示一共多少页 */
	showallpagenum();
	
	/*$("#showmessage").hide();*/
}

/* 查找所有类型在网页显现出来 */
function showselect(result) {
	var tbody = "";
	$.each(result, function(n, value) {
		var trs = "";
		trs += "<option value='" + value.tname + "'>" + value.tname
				+ "</option>";
		tbody += trs;
	});
	$("#select1").append(tbody);

	getPaging();
	var count = $("#select1 option").length;
	for (var i = 0; i < count; i++) {
		if ($("#select1").get(0).options[i].text == type) {
			$("#select1").get(0).options[i].selected = true;
			break;
		}
	}
}
/* 一共多少页在网页显现出来 */
function showallpagenum() {
	$("#select2").empty();
	var tbody = "";
	for (var i = 1; i <= totalPage; i++) {
		var trs = "";
		trs += "<option value='" + i + "'>" + i + "</option>";
		tbody += trs;
	}

	$("#select2").append(tbody);
}

function deleteonequestion() {
	var bool = $("#selectAll").prop("checked");
	var num = $(":checkbox[name=checkboxBtn]:checked").length;
	if (num == 0 || (num == 1 && bool)) {
		hidemessage("请选择删除选型", 8);
	} else {
		addPaging();
		if (confirm("确定删除所有选中记录吗？")) {
			var bool = $("#selectAll").prop("checked");
			var totalId = retuenTotalId(bool);
			totalId = totalId.substring(0, totalId.length - 1);
			deleteone(totalId);
			$("#selectAll").prop("checked",false);
		}
	}
}

function updateonequestion() {
	var bool = $("#selectAll").prop("checked");
	var num = $(":checkbox[name=checkboxBtn]:checked").length;
	if (num == 0 || (num == 1 && bool)) {
		hidemessage("请选择修改选型", 8);
	} else {
		addPaging();
		var bool = $("#selectAll").prop("checked");
		var totalId = retuenTotalId(bool);
		totalId = totalId.substring(0, totalId.length - 1);
		updatequestions(totalId);
	}
}

/* 分页模糊查询 */
function findquestion() {
	$("#selectAll").removeAttr("checked", "checked");
	currentPage = 1;
	url = "/question/findTqtitleAndTypeByPage";
	var findtitle = $("#findtitle").val();
	var findtype = $('#select1 option:selected').text();
	alltqtitle = findtitle;
	type = findtype;
	/* 记录查询标题类型以供查询后分页所用 */
	if (findtitle.length > 20) {
		$("#showmessage").text("您输入的字符过长！");
		tsotsi($('#showmessage'), 8);
	} else {
		addPaging();
		if ((findtitle == null || findtitle == "")
				&& (findtype == null || findtype == "")) {
			alltqtitle = "";
			type = "";
			url = "/question/findAllByPage";
		}
		findAllAfterQueryUseDeleteOrUpdate(currentPage);
	}

}

/**
 * 所有ajax复用相关方法
 */

function deleteone(totalId) {
	$.ajax({
		url : "/question/deleteone",
		data : {
			"totalId" : totalId
		},
		type : "get",
		dataType : "text",
		success : function(result) {
			hasQueryOrNotThenPaging();
			/* 显示提示信息 */
			getmessage();
		},
	});
}
function updatequestions(totalId) {
	$.ajax({
		url : "/question/saveTotalIdAndQuestionAndCurrentPage",
		data : {
			"totalId" : totalId
		},
		type : "get",
		dataType : "text",
		success : function(result) {
			window.location.href = "/question/showupdateHtml";
		},
	});
}
/* 添加提示信息 */
function getmessage() {
	$.ajax({
		url : "/question/getmessage",
		type : "get",
		dataType : "text",
		success : function(result) {
			if (result == "" || result == null || result == "查询成功") {
			} else {
				hidemessage(result, 8);
			}
		},
	});
}
/* 查看导入导出页面 */
function showimexprotHtml() {
	$.ajax({
		url : "/question/showimexprotHtml",
		type : "get",
		dataType : "text",
		success : function(result) {
			if (result == "" || result == null || result == "查询成功") {
			} else {
				hidemessage(result, 8);
			}
		},
	});
}
/* 查看所有页面 */
function refresh() {
	currentPage = 1;
	url = "/question/findAllByPage";
	alltqtitle = "";
	type = "";
	$.ajax({
		url : "/question/addPaging",
		data : {
			"alltqtitle" : alltqtitle,
			"url" : url,
			"currentPage" : currentPage,
			"tname" : type
		},
		type : "get",
		dataType : "text",
		success : function(result) {
			window.location.href = "/question/getQuestionHtml";
		},
	});
}
/* 加载分页策略 */
function getPaging() {
	$.ajax({
		url : "/question/getPaging",
		type : "get",
		dataType : "json",
		success : function(result) {
			url = result.url;
			alltqtitle = result.alltqtitle;
			type = result.tname;
			currentPage = result.currentPage;
		},
	});
}
/* 添加分页策略到session中 */
function addPaging() {
	$.ajax({
		url : "/question/addPaging",
		data : {
			"alltqtitle" : alltqtitle,
			"url" : url,
			"currentPage" : currentPage,
			"tname" : type
		},
		type : "get",
		dataType : "text",
		success : function(result) {
		},
	});
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
/* 通过当前页数查找所有试题 */
function findAllUseDeleteOrUpdate(currentPageof) {
	$.ajax({
		url : "/question/findAllByPage",
		data : {
			"currentPage" : currentPageof
		},
		type : "get",
		dataType : "json",
		success : function(result) {
			currentPage = currentPageof;
			addPaging();
			totalPage = result.totalPage;
			showallpage(result);
		},
	});
}
/* 通过当前页数按照类型标题查找所有试题 */
function findAllAfterQueryUseDeleteOrUpdate(currentPageof) {
	$.ajax({
		url : "/question/findTqtitleAndTypeByPage",
		data : {
			"currentPage" : currentPageof,
			"findtype" : type,
			"findtitle" : alltqtitle
		},
		type : "get",
		dataType : "json",
		success : function(result) {
			currentPage = currentPageof;
			addPaging();
			totalPage = result.totalPage;
			showallpage(result);
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


/**
 * 所有其他复用方法
 */
/* 每个按钮添加分页查询地址 */
function showpage(obj) {
	$("#selectAll").removeAttr("checked", "checked");
	if (url == "/question/findAllByPage") {
		findalladdbutton(obj);
	} else {
		findtqtitleaddbutton(obj);
	}
}
/* 查找全部记录的时候给按钮添加响应方法 */
function findalladdbutton(obj) {

	if (obj.id == "previousPage") {
		findAllUseDeleteOrUpdate(previousPage);
	} else if (obj.id == "nextPage") {
		findAllUseDeleteOrUpdate(nextPage);
	} else {
		findAllUseDeleteOrUpdate(pageBar[obj.id]);
	}
}
/* 模糊查找记录的时候给按钮添加响应方法 */
function findtqtitleaddbutton(obj) {
	if (obj.id == "previousPage") {
		findAllAfterQueryUseDeleteOrUpdate(previousPage);
	} else if (obj.id == "nextPage") {
		findAllAfterQueryUseDeleteOrUpdate(nextPage);
	} else {
		findAllAfterQueryUseDeleteOrUpdate(pageBar[obj.id]);
	}
}
/* 每个按钮添加显示文字 */
function showbuttonhtml() {
	var x = 0;
	if (pageBar != null) {
		$.each(pageBar, function(n, value) {
			if (pageBar[n] == currentPage) {
				$("#" + n).show();
				$("#" + n).text("" + pageBar[n]);
				$("#" + n).removeClass("btn btn-success btn-sm");
				$("#" + n).addClass("btn btn-warning btn-sm");
				x++;
			} else {
				$("#" + n).show();
				$("#" + n).text("" + pageBar[n]);
				$("#" + n).removeClass("btn btn-warning btn-sm");
				$("#" + n).addClass("btn btn-success btn-sm");
				x++;

			}
		});
	}
	for (var i = x; i < 10; i++) {
		$("#" + i).hide();
	}
	$("#previousPage").show();
	$("#nextPage").show();
	if (currentPage == 1) {
		$("#previousPage").hide();
	}
	if (currentPage == totalPage) {
		$("#nextPage").hide();
	}
	$("#allpage").text(totalPage);
	$("#currentPage").text(currentPage);
}

function go() {

	var findpagenum = $('#select2 option:selected').text();
	currentPage = findpagenum;
	addPaging();
	hasQueryOrNotThenPaging();
}

/* 返回所有的主键ID并且以_拼接 */
function retuenTotalId(bool) {
	var totalId = "";
	$(":checkbox[name=checkboxBtn]:checked").each(function() {
		if (!bool) {
			var id = $(this).val();
			totalId = totalId + id + "_";
		}
		bool = "";
	});
	return totalId;
}

/* 判断是否通过模糊查询采用不同分页策略修改或者删除 */
function hasQueryOrNotThenPaging() {
	$("#showmessage").show();
	$("#showmessage").text("正在加载数据请稍后!");
	
	$.ajax({
		url : "/question/getPaging",
		type : "get",
		dataType : "json",
		success : function(result) {
			url = result.url;
			alltqtitle = result.alltqtitle;
			$("#findtitle").val(alltqtitle);
			type = result.tname;
			currentPage = result.currentPage;
			if (url == "/question/findAllByPage" || url == "") {
				findAllUseDeleteOrUpdate(currentPage);
			} else {
				findAllAfterQueryUseDeleteOrUpdate(currentPage);
			}
			/* 把原来的类型以及标题填入 */
			var count = $("#select1 option").length;
			for (var i = 0; i < count; i++) {
				if ($("#select1").get(0).options[i].text == type) {
					$("#select1").get(0).options[i].selected = true;
					break;
				}
			}
		},
	});

}

function inputIsToLongOrNot(optiona, optionb, optionc, optiond, question,
		tqtitle) {
	if (optiona.length >= 255) {

	}
}

/**
 * 把传过来的信息隐藏固定秒数
 * 
 * @param totalId
 */
function hidemessage(result, num) {
	$("#showmessage").show();
	$("#showmessage").text(result);
	tsotsi($('#showmessage'), num);
}

function toAddHtml() {

	window.location.href = "/question/showaddHtml";
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
/* 显示表格的试题数据 */
function showresult(result) {
	var tbody = "";
	$.each(result.beanList, function(n, value) {
		if (value != null) {
			var trs = "";
			trs += "<tr><td>" + "<input value=" + value.tqid
					+ " type='checkbox' name='checkboxBtn'/>" + "</td> <td>"
					+ value.tqtitle + "</td> <td>" + value.optiona
					+ "</td> <td>" + value.optionb + "</td> <td>"
					+ value.optionc + "</td> <td>" + value.optiond
					+ "</td> <td>" + value.question + "</td> <td>"
					+ value.type.tname + "</td></tr>";
			tbody += trs;
		}
	});
	$("#project").empty();
	$("#project").append(tbody);
	/* 本页没有数据则显示前一页数据，都没有则提示无数据 */
	if (tbody == null || tbody == "") {
		if (currentPage >= 2) {
			currentPage -= 1;
			addPaging();
			hasQueryOrNotThenPaging();
		} else {
			$("#showmessage").text("对不起本页没有你要找的记录");
			tsotsi($('#showmessage'), 8);
		}
	}
	if (tbody != null && tbody != "") {
		$("#showmessage").hide();
	}
}
