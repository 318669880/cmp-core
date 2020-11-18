/*
* @version: 1.0
* @Author:  zyl
* @Date:    2019-12-16
* @Last Modified by:   zyl
* @Last Modified time: 2019-12-16
*/
//树形多选下拉框
layui.define(['jquery', 'layer'], function(exports){
	var MOD_NAME = 'zTreeSelectM';
    var $ = jQuery = layui.$,
    	layer = layui.layer;
	var obj = function(config){
		this.disabledIndex =[];
		//当前选中的值名数据
		this.selected = [];
		//当前选中的值
		this.values =[];
		//当前选中的名称
		this.names =[];		
		//初始化设置参数
		this.config = {
			//选择器id或class
			elem: '',
			//候选项数据
			data: [],				
			//默认选中值
			selected: [],
			//空值项提示，支持将{max}替换为max			
			tips: '',
			//最多选中个数，默认5
			max : 15,
			//选择框宽度
			width:null,			
			//值验证，与lay-verify一致
			verify: '',	
			//input的name 不设置与选择器相同(去#.)
			name: '',			
			//值的分隔符
			delimiter: ',',			
			//候选项数据的键名 status=0为禁用状态
            field: { idName: 'id', titleName: 'name', statusName: 'status' },
            //ztree设置
            zTreeSetting: {},
			single: false ,
			checkBoxCondition : null
		};

		this.sourceData = [];

		this.config = $.extend(this.config,config);
		//创建选项元素
		this.createOption = function(){
			var o=this,c=o.config,f=c.field,d = c.data;
			var s = c.selected;
			$E = $(c.elem);
			var tips = c.tips.replace('{max}',c.max);
			//var inputName = c.name=='' ? c.elem.replace('#','').replace('.','') : c.name;
			var inputName = c.elem.replace('#','').replace('.','');
			var verify = c.verify=='' ? '' : 'lay-verify="'+c.verify+'" ';
            var html = '<link rel=\"stylesheet\" href=\"web-public/external/layui/extend/zTreeSelectM/zTreeSelectM.css\">\n';
	            html += '<link rel=\"stylesheet\" href=\"web-public/external/layui/extend/zTreeSelectM/zTree_v3/css/zTreeStyle/zTreeStyle.css\">\n';
	            html += '<script type=\"text/javascript\" src=\"web-public/external/layui/extend/zTreeSelectM/zTree_v3/js/jquery.ztree.all.min.js\"></script>\n';
				html +=	'<div class="layui-unselect layui-form-select">';
	            html +=		'<div class="layui-select-title" style="display:none;">';
				html +=			'<input '+verify+'name="'+inputName+'" type="text" readonly="" class="layui-input layui-unselect">';
				html +=		'</div>';
				html +=		'<div class="layui-input multiple fit2cloud-ts-multiple-input">';
				html +=		'</div>';
	            html +=     '<dl class="layui-anim layui-anim-upbit fit2cloud-ts-dl">';
                html +=         '<dd lay-value="" class="layui-select-tips">' + tips + '</dd>';
                html +=         '<dd><ul id="' + c.elem.replace("#","") + 'ztree" class="ztree"></ul></dd>';     
				html +=		'</dl>';
				html +=	'</div>';
			$E.html(html);
		}

		this.getNodeByDataId = function(dataId , nodes){
			let resultNode = null;
			let childs = new Array();
			if(nodes.some(function(node) {
				let temp = (node.id == dataId);
				resultNode = node;
				if (!!node.children){
					childs = childs.concat(node.children);
				}
				return temp;
			}.bind(this))){
				return resultNode;
			}
			return this.getNodeByDataId(dataId, childs);
		};

		this.formatTree2List = function(dataNodes, nodeLists){
			let _self = this;
			if (null == nodeLists){
				nodeLists = new Array();
			}
			dataNodes.forEach(node => {
				nodeLists.push(node);
				if (null != node.childNodes && node.childNodes.length > 0){
					_self.formatTree2List(node.childNodes, nodeLists);
				}
			})
		};

		this.setChkDisabled = function () {
			let _self = this;
			if (!this.config.checkBoxCondition || (typeof this.config.checkBoxCondition) != 'function') return;
			let conditionFunction = this.config.checkBoxCondition;
			let nodeDats = new Array();
			this.formatTree2List(this.sourceData, nodeDats);
			let nodes = this.zTree.getNodes();
			nodeDats.filter(nodeData => conditionFunction(nodeData)).forEach(cdata => {
				let dataId = cdata.nodeId;
				let node = _self.getNodeByDataId(dataId, nodes);
				!!node && _self.zTree.setChkDisabled(node, true);
			})
		}

		//设置选中值
		this.set = function(selected){
            var o = this, c = o.config;
            var s = typeof selected == 'undefined' ? c.selected : selected;
            //为默认选中值添加类名                       
            o.zTree = $.fn.zTree.getZTreeObj(c.elem.replace("#", "") + "ztree");
            o.zTree.checkAllNodes(false);
            o.zTree.expandAll(true);
            function filter(node) {
                return $.inArray(node.id, s) >= 0;
            }
            var nodes = o.zTree.getNodesByFilter(filter, false);
            var max = s.length < c.max ? (s.length < nodes.length ? s.length : nodes.length) : (c.max < nodes.length ? c.max : nodes.length);
            for (var i = 0; i < max; i++) {
                o.zTree.checkNode(nodes[i], true);
            }
            o.setSelected(selected);
		}
	
		//设置选中值 每次点击操作后执行
		this.setSelected = function(first){
			var o=this,c=o.config,f=c.field;
			$E = $(c.elem);
            var values = [], names = [], selected = [], spans = [];
            var checkedNodes = o.zTree.getCheckedNodes(true);                        
            if (checkedNodes.length==0){
				var tips = c.tips.replace('{max}',c.max);
				spans.push('<span class="tips">'+tips+'</span>');
            }            
            else {
                $.each(checkedNodes, function (index, node) {                   
                    var item = {};
                    var v = node.id;
                    var n = node.name;
                    item[f.idName] = v;
                    item[f.titleName] = n;
                    values.push(v);
                    names.push(n);
                    spans.push('<a href="javascript:;"><span lay-value="' + v + '">' + n + '</span><i class="layui-icon">&#x1006;</i></a>');
                    selected.push(item);
                })
			}
			spans.push('<i class="layui-edge" style="pointer-events: none;"></i>');
			$E.find('.multiple').html(spans.join(''));				
			$E.find('.layui-select-title').find('input').each(function(){
				if(typeof first=='undefined'){
					this.defaultValue = values.join(c.delimiter);
				}
				this.value = values.join(c.delimiter);
			});			
			o.values=values,o.names=names,o.selected = selected;
			o.config.changed && o.config.changed(values);
		}
		//ajax方式获取候选数据
		this.getData = function(url){
			let _self = this;
			var d;
			$.ajax({
				url:url,
				type: this.config.type,
				dataType:'json',
				contentType: (this.config.type=='POST' || this.config.type =='post')? 'application/json' : '',
				async:false,
				data: this.config.where,
				success:function(json){
					_self.sourceData = json.data;
					if(_self.config.parseData){
						json = _self.config.parseData(json);
					}
					d=json;
				},
				error: function(){
					console.error(MOD_NAME+' hint：候选数据ajax请求错误 ');
					d = false;
				}
			});
			return d;
		}
	};	
	//渲染一个实例
    obj.prototype.render = function(){
		var o=this,c=o.config,f=c.field;
		$E = $(c.elem);
		if($E.length==0){
			console.error(MOD_NAME+' hint：找不到容器 ' +c.elem);
			return false;
		}
		if(Object.prototype.toString.call(c.data)!='[object Array]'){
			var data = o.getData(c.data);
			if(data===false){
				console.error(MOD_NAME+' hint：缺少分类数据');
				return false;
			}
			o.config.data =  data;
		}

		//给容器添加一个类名
		$E.addClass('lay-ext-mulitsel');
		if(/^\d+$/.test(c.width)){
			$E.css('width',c.width+'px');
		}
		
		//创建选项
        o.createOption();
        c.zTreeSetting.callback = {
            onCheck: zTreeOnCheck
        };
        $.fn.zTree.init($(c.elem + "ztree"), c.zTreeSetting, c.data);
        function zTreeOnCheck(event, treeId, treeNode) {
            if (treeNode.checked && o.selected.length >= c.max) {
                treeNode.checked = false;
                $(c.elem + ' .multiple').addClass('danger');
                layer.tips('最多只能选择 ' + c.max + ' 个', c.elem + ' .multiple', {
                    tips: 3,
                    time: 1000,
                    end: function () {
                        $(c.elem + ' .multiple').removeClass('danger');
                    }
                });
                return false;
            }
            if(!!c.single){//单选模式
				o.zTree.cancelSelectedNode();
				o.zTree.checkAllNodes(false);
				o.zTree.checkNode(treeNode, true);
			}
            o.setSelected();
        };
		//设置选中值
		o.set();
		o.setChkDisabled();
		
		//展开/收起选项
        $E.on('click', '.layui-select-title,.multiple,.multiple.layui-edge', function (e) {
            //隐藏其他实例显示的弹层
			$('.lay-ext-mulitsel').not(c.elem).removeClass('layui-form-selected');
			if($(c.elem).is('.layui-form-selected')){
				$(c.elem).removeClass('layui-form-selected');				
				$(document).off('click',mEvent);
				c.end && c.end();
			}
            else {
				$(c.elem).addClass('layui-form-selected');
				c.start && c.start();
				$(document).on('click',mEvent=function(e){
					if(e.target.id!==c.elem && e.target.className!=='layui-input multiple fit2cloud-ts-multiple-input'){
						$(c.elem).removeClass('layui-form-selected');			
						$(document).off('click',mEvent);
						c.end && c.end();
					}
				});	
			}
        });	

        //点击选项
        $E.on('click', 'dd', function (e) {
            e.stopPropagation();
        });
        		
		//删除选项
		$E.on('click','a i',function(e){
			var _this = $(this).prev('span');
			var v = _this.attr('lay-value');
			if(v){
                function filter(node) {
                    return node.id == v;
                }
                var node = o.zTree.getNodesByFilter(filter, true);
                o.zTree.checkNode(node, false);
			}
			o.setSelected();
			_this.parent().remove();
			e.stopPropagation();			
		});
		
		//验证失败样式
		$E.find('input').focus(function(){
			$(c.elem+' .multiple').addClass('danger');
			setTimeout(function(){
				$(c.elem+' .multiple').removeClass('danger');
			},3000);
		});
	}
	
	//输出模块
	exports(MOD_NAME, function (config) {
		var _this = new obj(config);
		_this.render();
		return _this;
  });
});