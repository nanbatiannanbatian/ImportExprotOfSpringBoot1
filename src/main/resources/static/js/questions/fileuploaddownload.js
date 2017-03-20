/**
 * 上传 submitUploadCsv()函数需要用到questions.js里面的隐藏字体的函数
 */
function uploadFile() {
	$("#parent").show();
	$("#son").show();
	$("#showmessage").show();
	$("#son").html(0 + "%");
	$("#son").css("width", 0 + "%");
	$("#showmessage").text("正在上传请稍等");

	var pic = $("#pic").get(0).files[0];
	var formData = new FormData();
	formData.append("file", pic);
	/**
	 * 必须false才会避开jQuery对 formdata 的默认处理 XMLHttpRequest会对 formdata 进行正确的处理
	 */
	$.ajax({
		type : "POST",
		url : "/fileup/upload",
		data : formData,
		processData : false,
		// 必须false才会自动加上正确的Content-Type
		contentType : false,
		xhr : function() {
			var xhr = $.ajaxSettings.xhr();
			if (onprogress && xhr.upload) {
				xhr.upload.addEventListener("progress", onprogress, false);
				return xhr;
			}
		}
	});
}
/**
 * 侦查附件上传情况 ,这个方法大概0.05-0.1秒执行一次
 */
function onprogress(evt) {
	var loaded = evt.loaded; // 已经上传大小情况
	var tot = evt.total; // 附件总大小
	var per = Math.floor(100 * loaded / tot); // 已经上传的百分比
	$("#son").html(per + "%");
	$("#son").css("width", per + "%");
	if (per == "100") {
		/* setTimeout("hidemessage1(2)", 2000); */
		$("#parent").show();
		$("#son").show();
		$("#son").html(0 + "%");
		$("#son").css("width", 0 + "%");
		$("#showmessage").text("数据上传到数据库请稍等！");
		setTimeout("finalUploadOrNot()", 10);
		/* hidemessage("请刷新页面得到插入的数据",8); */
	}
}
/* 解析上传文件的后缀名 */
function submitUploadCsv() {
	var file = $("input[name='pic']").val();
	var filename = file.replace(/.*(\/|\\)/, "");
	var fileExt = filename.split('.');
	var filesplit = fileExt[1];
	if (filesplit == "csv") {
		uploadFile();
	} else {
		hidemessage("请选择CSV格式的文件", 8);
	}
}
/* 判断是否上传结束 */
function finalUploadOrNot() {
	$.ajax({
		url : "/fileup/finalUploadOrNot",
		type : "get",
		dataType : "json",
		success : function(result) {
			if (result.beginUpLoadOrOnt == "begin") {
				setTimeout("finalUploadOrNot()", 1000);
			} else if (result.beginUpLoadOrOnt == "Uploading") {
				var loaded = result.upLoadDealrow; // 已经上传大小情况
				var tot = result.upLoadAllrow-1; // 附件总大小
				var per = Math.floor(100 * loaded / tot); // 已经上传的百分比
				$("#son").html(per + "%");
				$("#son").css("width", per + "%");
				setTimeout("finalUploadOrNot()", 1000);
			} else if (result.beginUpLoadOrOnt == "final") {
				$("#son").html(100 + "%");
				$("#son").css("width", 100 + "%");
				hidemessage("上传成功,请刷新页面！", 8);
				hidemessage1(8);
			}
		},
	});
}
/**
 * 把传过来的信息隐藏固定秒数
 * 
 * @param totalId
 */
function hidemessage1(num) {
	tsotsi1($('#parent'), num);
}
/**
 * @comment item 为匹配的标签的jquery对象，t表示剩余时间 实现功能为t秒内，渐渐消失
 * @author tsotsi
 * @date 2014/07/06
 */
function tsotsi1(item, t) {
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

/**
 * 下载
 */
/* 试题csv下载 */
function download() {
	var msg = $('#export option:selected').attr('class');
	if (msg == "download3") {
		var num = $(":checkbox[name=checkboxBtn]:checked").length;
		var bool = $("#selectAll").prop("checked");
		if (num == 0 || (num == 1 && bool)) {
			hidemessage("请选择需要下载的试题", 8);
		} else {
			addPaging();
			var bool = $("#selectAll").prop("checked");
			var totalId = retuenTotalId(bool);
			totalId = totalId.substring(0, totalId.length - 1);
			downLoadQuestionPart(totalId, "csv");
		}
	} else if (msg == "download4") {
		var num = $(":checkbox[name=checkboxBtn]:checked").length;
		var bool = $("#selectAll").prop("checked");
		if (num == 0 || (num == 1 && bool)) {
			hidemessage("请选择需要下载的试题", 8);
		} else {
			addPaging();
			var bool = $("#selectAll").prop("checked");
			var totalId = retuenTotalId(bool);
			totalId = totalId.substring(0, totalId.length - 1);
			downLoadQuestionPart(totalId, "Excel");
		}
	} else if (msg == "download1") {
		downLoadQuestionAll("csv");
	} else if (msg == "download2") {
		downLoadQuestionAll("Excel");
	}
}

/* 下载全部试题的方法 */
function downLoadQuestionAll(type) {
	ShowImgWhereExport();

	var form = $("<form>"); // 定义一个form表单
	form.attr('style', 'display:none'); // 在form表单中添加查询参数
	form.attr('target', '');
	form.attr('method', 'post');
	form.attr('action', "/fileup/testDownload?type=" + type);
	$('body').append(form); // 将表单放置在web中
	form.submit();
	/* 设置什么时候查看是否下载完毕取消等待图像 */
	setTimeout("finalDownloadOrNot()", 2000);
}
/* 下载本业部分试题的方法 */
function downLoadQuestionPart(totalId, type) {
	ShowImgWhereExport();

	var form = $("<form>"); // 定义一个form表单
	form.attr('style', 'display:none'); // 在form表单中添加查询参数
	form.attr('target', '');
	form.attr('method', 'post');
	form.attr('action', "/fileup/testDownload?type=" + type + "&totalId="
			+ totalId);
	$('body').append(form); // 将表单放置在web中
	form.submit();
	setTimeout("finalDownloadOrNot()", 10);
}

function ShowImgWhereExport() {
	$('#waitAndShow').show();
	$('#waitAndShow').text("正在下载");
	$('#showwaitAndShowImg').show();
	/* alert($('#waitAndShowImg').attr("src")); */
}

/* 判断是否下载结束 */
function finalDownloadOrNot() {
	$.ajax({
		url : "/fileup/finalDownloadOrNot",
		type : "get",
		dataType : "text",
		success : function(result) {
			if (result != "true" || !result) {
				$('#waitAndShow').hide();
				$('#showwaitAndShowImg').hide();
			} else {
				setTimeout("finalDownloadOrNot()", 1000);
			}
		},
	});

}