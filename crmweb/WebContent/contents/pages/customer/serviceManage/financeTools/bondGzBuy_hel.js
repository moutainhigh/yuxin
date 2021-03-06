
Ext.onReady(function(){
	Ext.QuickTips.init();
    var czTypeStore = new Ext.data.ArrayStore({
        fields : ['key', 'value'],
        data : [['买入', '1'],['卖出', '2']]
    });
	var titleForm = new Ext.form.FormPanel({
		frame:true,
		labelAlign:'middle',
		region:'north',
		height:80,
		border: false,
		split:true,
		items:[{
			layout:'column',
			items:[{
				columnWidth:1,
				items:[{
					xtype:'displayfield',
					value:'国债买卖计算器',
					style: {
						marginTop:'20px',
						marginLeft:'200px',
						marginBottom:'20px'
					},
					anchor:'99%'
				},{
					xtype:'displayfield'
				}]
			}]
		}]
	});
	var inputForm = new Ext.form.FormPanel({
		labelWidth:200,
		frame:true,
		border: false,
		buttonAlign:'left',//按钮对齐方式
		labelAlign:'right',
		region:'center',
		split:true,
		items:[{
			layout:'column',
			items:[{
				columnWidth:.50,
				layout:'form',
				items:[{
                    xtype : 'combo',
                    fieldLabel : '操作选择',
                    mode:'local',
                    emptyText:'请选择',
                    store:czTypeStore,
                    triggerAction:'all',
                    valueField:'value',
                    editable : false,
                    displayField:'key',
                    id : 'czType',
                    name : 'czType',
                    value:'1',
                    anchor : '90%',
                    listeners : {
						'select' : function(combo) {
						}
					}
                },{
					xtype:'numberfield',
					id:'a',
					name:'a',
					fieldLabel:'购买数量(份)',
					allowBlank:false,
					minValue:0,
					anchor:'90%'
				},{
					xtype:'numberfield',
					id:'b',
					name:'b',
					fieldLabel:'单位价格(元)',
					allowBlank:false,
					minValue:0,
					anchor:'90%'
				}]
			}]
		}],
		buttons:[{
			text:'计算',
			style: {
				marginLeft:'200px'
			},
			handler:function(){
				if(!inputForm.getForm().isValid()){
					Ext.Msg.alert('提示','输入格式有误，请重新输入');
					return false;
				}
				var formValue = inputForm.getForm().getValues();
				var a = parseFloat(formValue.a);
				var b = parseFloat(formValue.b);
		        Ext.getCmp("result1").setValue((a*b).toFixed(2));
			}
		},{
			text:'重置',
			handler:function(){
				inputForm.getForm().reset();
				outputForm.getForm().reset();
			}
		}]
	});
	var outputForm = new Ext.form.FormPanel({
		labelWidth:200,
		frame:true,
		border: false,
		height:150,
		labelAlign:'right',
		buttonAlign:'center',
		region: 'south',
		split:true,
		items:[{
			layout:'column',
			items:[{
				columnWidth:.50,
				layout:'form',
				items:[
					{  xtype:'textfield',id:'result1',fieldLabel:'金额(元)',anchor:'90%',value:'计算得出',readOnly:true}
				]
			}]
		}]
	});
	
//	var view = new Ext.Viewport({
//		layout : 'fit',
//		items:[{
//			layout: 'border',
//			items:[titleForm,inputForm,outputForm]
//	    }]
//	});
	
	var p = new Ext.Panel({
	    renderTo:'viewDiv',
	    layout:'column',
	    width:840,
	    items: [{
			columnWidth:1,
			layout:'form',
			items:[titleForm,inputForm,outputForm]
		}]
	});



});