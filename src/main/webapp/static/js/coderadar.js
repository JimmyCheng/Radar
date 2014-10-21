/** Search Options Manager * */
var PageInitializer = {
	/** Init all the form element * */
	init : function() {
		// initialize the tooltip for keyword input 
		$("#q").tipsy({fade: true, gravity: 'nw'});
		// initialize the FilterOption elements.
		FilterOptionsUtil.init();
	}
};

/**
 * paging util.
 */
var PagingUtil = {
	/**
	 * paging util for page control.
	 */
	init:function(){
		var prevElmt = $("#link-prev-page");
		var nextElmt = $("#link-next-page");
		var form = $("#search-form");
		var page = $("#page");
		
		prevElmt.click(function(){
			PagingUtil._doPaging(-1);
		});
		nextElmt.click(function(){
			PagingUtil._doPaging(1);
		});
	},
	/**
	 * Do paging.
	 */
	_doPaging:function(amount) {
		var form = $("#search-form");
		var page = $("#page");
		var pageNum = parseInt(page.val(),10)+amount;
		if(pageNum<=0) {
			pageNum = 1;
		}
		page.val(pageNum);
		form.submit();
	}
};

/** The FilterOptionsUtil */
var FilterOptionsUtil = {
	/**
	 * initialize the filter options.
	 */
	init:function(){
		FilterOptionsUtil._showSelectedFilters();
		FilterOptionsUtil._addSelectHandler();
		FilterOptionsUtil._bindClickOnFiltersTitle();

	},
	/**
	 * Bind the click on the filters title , so user can toggle the filters.
	 */
	_bindClickOnFiltersTitle:function(){
		// add the click action on the filter lists
		//FilterOptionsUtil._toggleFilterList("#options-title","#options-list");  //Jimmy: 2014-08-30 do it later.
		FilterOptionsUtil._toggleFilterList("#releases-title","#releases-list");
		FilterOptionsUtil._toggleFilterList("#filetypes-title","#filetype-list");
	},
	/**
	 * Toggle the filter.
	 * @Param titleElmtId - the html element ID which is the title of the filter.
	 * @Param listElmtId - the html element ID which is the list.
	 */
	_toggleFilterList:function(titleElmtId,listElmtId) {
		var titleElmt = $(titleElmtId);
		var text = titleElmt.attr("title");
		titleElmt.click(function(){
			// toggle the list
			$(listElmtId).slideToggle(300,"easeOutQuint");
		});
	},
	/**
	 * show the selected filters when page loaded.
	 */
	_showSelectedFilters:function() {
		var selecteFilterOptions = $("#selected-filter-options");
		var selopts = selecteFilterOptions.val().split(",");
		// add the selected class for the selected options.
		$.each(selopts,function(index,itemId){
			FilterOptionsUtil._select(itemId,true);
		});
	},
	/** 
	 * select or disselect the option
	 * @Param optid - the html id of this option element , it will be used as the filter value eighter.
	 * @Param select - if true then means this option is selected , 
	 *               - if false then means this option is removed.
	 */
	_select:function(optid,select){
		var id = RadarUtil.escapeId(optid);
		var text = $(id).attr("title");
		if(select){
			$(id).addClass("selected");
			$(id).html(text+" &radic;");
			FilterOptionsUtil._updateSelectedOptionValues(optid,true);
		}else{
			$(id).removeClass("selected");
			$(id).html(text);
			FilterOptionsUtil._updateSelectedOptionValues(optid,false);
		}
	},
	/**
	 * update the selected options
	 * @Param val - the value need add or remove from/to the selected option values
	 * @Param add - if true then add into selected values. 
	 *            - if false then remove.
	 */
	_updateSelectedOptionValues:function(val,add){
		var selectedFilterOptionElmt = $("#selected-filter-options");
		var selected = selectedFilterOptionElmt.val().split(",");
		if(add){
			var exist = $.inArray(val,selected);
			if(exist===-1){
				RadarUtil.addElement(selected, val);
			}
		}else{
			selected = RadarUtil.removeElement(selected, val);
		}
		selectedFilterOptionElmt.val(selected.join(","));
	},
	/** Add the select listener*/
	_addSelectHandler:function(){
		var options = $(".menu-text");
		$.each(options,function(index,item){
			$(this).click(function(){
				var id = $(this).attr("id");
				if($(this).hasClass("selected")){
					FilterOptionsUtil._select(id,false);
				}else{
					FilterOptionsUtil._select(id,true);
				}
				// submit the form
				$("#search-form").submit();
				return false;
			});
		});
	}
};

/** The util class*/
var RadarUtil = {
	/** replace the invalid JQuery id character */
	escapeId:function(id){
		return '#' + id.replace(/(:|\.)/g,'\\$1');
	},
	/**
	 * Remove a element from an array.
	 * @Param arr - the target array
	 * @Param elmt - the element need be removed.
	 */
	removeElement:function(arr,elmt){
		var res = $.grep(arr,function(value){
			return value != elmt;
		});
		return res;
	},
	/**
	 * Add a element into an array.
	 * @Param arr - the array need add element into.
	 * @Param elmt - the element need be added.
	 */
	addElement:function(arr,elmt) {
		arr.push(elmt);
	}
};

/** trigger the init functions when page load * */
$(document).ready(function() {
	PageInitializer.init();
});