<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">
	var grid, btns, url, ids = [], test = [];
	var testId;
	var custNoTemp = identNoTemp = lastUpdateSysTemp = "";
	$(init);

	function init() {
		url = "${ctx}/ecif/customer/custsplitrecordapphis/list.json";
		initGrid();
		searchForm();
		initButtons();
		addSearchButtons("#search", grid, "#searchbtn");
	}
	
	// 创建表单搜索按钮：搜索、高级搜索
	addSearchButtons = function(form, grid, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '搜索',
				icon : 'search3',
				width : '50px',
				click : function() {
					var flag = true;
					/* if($('#reserveCustNo').val() != ""){
						if($('#reserveCustNo').val().length > 32){
							flag = false;
							alert("保留客户号不能大于32位");
						}
					} */
					if($('#mergedCustNo').val() != ""){
						if($('#mergedCustNo').val().length > 32){
							flag = false;
							alert("被合并客户号不能大于32位");
						}
					}
					if(flag){
						var rule = BIONE.bulidFilterGroup(form);
						if (rule.rules.length) {
							grid.set('parms', {
								condition : JSON2.stringify(rule)
							});
							grid.set('newPage', 1);
						} else {
							grid.set('parms', {
								condition : ''
							});
							grid.set('newPage', 1);
						}
						/* @Revision 20130704182000 liucheng2@yuchengtech.com
						 * 防止grid进行ajax取数时session失效而无法正确获取数据； */
						if (!grid.loading || grid.loading!=true) {
							var opurl = grid.options.url;
							var ctx = "/";
							if (opurl) {
								ctx = "/" + opurl.split("/")[1] + "/";
							}
							$.ajax({
								cache : false,
								async : true,
								url : ctx + "/bione/common/getComboxData.json",
								dataType : 'json',
								data : {
									"paramTypeNo" : ""
								},
								type : grid.options.type,
								complete : function(XMLHttpRequest) {
									BIONE.isSessionAlive(XMLHttpRequest);
								},
								success : function() {
									grid.loadData();
								}
							});
						} else {
							BIONE.showError("查询进行中，请勿重复查询！");
						}
						/* @Revision 20130704182000 END */
						custNoTemp =  "";//$('#reserveCustNo').val();
						identNoTemp = $('#mergedCustNo').val();
						lastUpdateSysTemp = $('#approvalStat').val();
					}
				}
			});
			BIONE.createButton({
				appendTo : btnContainer,
				text : '重置',
				icon : 'refresh2',
				width : '50px',
				click : function() {
					$(":input", form)
							.not(":submit, :reset,:hidden,:image,:button, [disabled]")
							.each(function() {
								$(this).val("");
							});
					$(":input[ltype=combobox]", form)
					.each(function() {
						$(this).val("");
					});
				}
			});
		}
	};
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [/* {
				display : "保留客户号",
				name : "reserveCustNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "=",
					field : "c.reserveCustNo"
				}
			},  */{
				display : "被合并客户号",
				name : "mergedCustNo",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "=",
					field : "c.mergedCustNo"
				}
			}, {
				display : "审批状态",
				name : "approvalStatBox",
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
					valueFieldID : 'approvalStat',
					url : "${ctx}/ecif/customer/custsplitrecordapphis/getComBoBox.json",
					ajaxType : "get"
				},
				attr : {
					op : "=",
					field : "c.approvalStat"
				}
			}]
		});
	}

	function initGrid() {

		grid = $("#maingrid").ligerGrid(
				{
					width : '100%',
					columns : [
								/*{
									display : '拆分标识',
									name : 'splitRecordId',
									align : 'center',
									width : '14%',
									minWidth : '10%'
								},{
									display : '保留客户号',
									name : 'reserveCustNo',
									align : 'center',
									width : '13%',
									minWidth : '10%'
								}, */{
									display : '被合并客户号',
									name : 'mergedCustNo',
									align : 'center',
									width : '13%',
									minWidth : '10%'
								},{
									display : '拆分申请人',
									name : 'splitOperator',
									align : 'center',
									width : '13%',
									minWidth : '10%'
								},{
									display : '拆分申请时间',
									name : 'splitOperTimeDate',
									align : 'center',
									width : '13%',
									minWidth : '10%',
									type : "date",
									format : 'yyyy-MM-dd hh:mm:ss'
								},{
									display : '导入提交人',
									name : 'importOperator',
									align : 'center',
									width : '13%',
									minWidth : '10%'
								},{
									display : '导入提交时间',
									name : 'importOperTime',
									align : 'center',
									width : '13%',
									minWidth : '10%',
									type : "date",
									format : 'yyyy-MM-dd hh:mm:ss'
								},{
									display : '审批人',
									name : 'approvalOperator',
									align : 'center',
									width : '13%',
									minWidth : '10%'
								},{
									display : '审批时间',
									name : 'approvalTime',
									align : 'center',
									width : '13%',
									minWidth : '10%',
									type : "date",
									format : 'yyyy-MM-dd hh:mm:ss'
								},{
									display : '审批状态',
									name : 'approvalStat',
									align : 'center',
									width : '13%',
									minWidth : '10%',
									render : RenderApprovalStatus
								},{
									display : '审批意见',
									name : 'approvalNote',
									align : 'center',
									width : '13%',
									minWidth : '10%'
								}],
					checkbox : false,
					//delayLoad :true,
					usePager : true,
					isScroll : false,
					rownumbers : true,
					alternatingRow : true,//附加奇偶行效果行
					colDraggable : true,
					dataAction : 'server',//从后台获取数据
					method : 'post',
					url : url,
					sortName : 'importOperTime', //第一次默认排序的字段
					sortOrder : 'desc',
					toolbar : {}
				});
	}

	function initButtons() {
		btns = [
		/*动态维护功能按钮*/
		{
			text : '下载客户拆分审批记录',
			click : file_down,
			icon : 'export',
			operNo : 'file_down'
		}];
		BIONE.loadToolbar(grid, btns, function() { });
	}
	
	function RenderApprovalStatus(rowdata){
		return BIONE.paramTransformer(rowdata.approvalStat, 
				'${ctx}/ecif/customer/custsplitrecordapphis/getCodeMapApprovalStatus.json', '');
	}
	
	function doDownload(file) {
		if(file==null||file==""){
			BIONE.tip("下载失败。");
			return;
		}
		var form = $('<form/>').attr({
			target: '',
			method: 'post',
			action: '${ctx}/ecif/customer/custsplitrecordapphis/export.xls'
		}).css('display', 'none');
		var input = $('<input/>').attr({
			type: 'hidden',
			name: 'file',
			value: file
		});
		$('body').append(form);
		form.append(input);
		form.submit();
		form.remove();
	}
	
	function file_down() {
		var rows = grid.data.Rows;
		if(rows.length <= 0) {
			BIONE.tip("表格中没有数据！");
			return;
		}
		var timestamp=new Date().getTime();
		$.ajax({
			url: '${ctx}/ecif/customer/custsplitrecordapphis/getReportFile?'+timestamp,
			type: 'get',
			data: {
				repo: "10",
				//others: others
				custNo: custNoTemp,
				identNo: identNoTemp,
				lastUpdateSys: lastUpdateSysTemp
			},
			//success: doDownload
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("下载中，请稍候...");
			},
			complete : function(XMLHttpRequest) {
				BIONE.loading = false;
				BIONE.hideLoading();
				BIONE.isSessionAlive(XMLHttpRequest);
			},
			success: doDownload,
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	/* window.document.onkeydown = function(e) {
		e = !e ? window.event : e;
		var key = window.event ? e.keyCode : e.which;
		if (key == 13) {
			var btns = $(".l-btn");
			for(var i=0;i<btns.length;i++){
				if(btns[i].innerText=='搜索'){
					btns[i].click();
				}
			}
		}
	}; */
	window.document.onkeydown = function(e) {
		e = !e ? window.event : e;
		var key = window.event ? e.keyCode : e.which;
		if (key == 13) {
			var btns = $(".l-btn");
			for(var i=0;i<btns.length;i++){
				var offset = btns[i].innerText.indexOf('搜索');
				if (offset != -1) {
					btns[i].click();
				}
			}
		}
	};
</script>
</head>
<body>
</body>
</html>