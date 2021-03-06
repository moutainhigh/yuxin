<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template18.jsp">
<script type="text/javascript">
	var dialog;
	var custId = "${custId}";
	url = "${ctx}/ecif/orgevaluate/orgauth/list.json?custId=" + custId;
	$(function() {
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '认证类型',
				name : 'authType',
				align : 'center',
				width : 100,
				minWidth : 80
			}	, {
			display : "认证机构",
			name : "authOrg",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "认证结果",
			name : "authResult",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "证书名称",
			name : "certName",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "证书号码",
			name : "certNo",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "认证日期",
			name : "authDate",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "有效日期",
			name : "validDate",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "生效日期",
			name : "effectiveDate",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "失效日期",
			name : "expiredDate",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "最后更新系统",
			name : "lastUpdateSys",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "最后更新人",
			name : "lastUpdateUser",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "最后更新时间",
			name : "lastUpdateTm",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	, {
			display : "交易流水号",
			name : "txSeqNo",
			align : 'center',
			width : 100,
			minWidth : 80
	}
	],
			checkbox : false,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : url,
			sortName : 'custId',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			toolbar : {}
		});
	});
</script>
</head>
<body>
</body>
</html>

