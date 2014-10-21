	<header class="navbar navbar-static-top bs-docs-nav" id="top" role="banner">
		<div class="container">
			<div class="navbar-header">
				<h1>
					<a href="${ctx}/"><img alt="CodeRadar" src="${ctx}/static/img/logo.png" style="height:51px;"></a>
				</h1>
			</div>


			<form class="navbar-form navbar-left" role="search" id="top-search" action="${ctx}/find">
              <input type="hidden" name="search-options" id="search-options" value="${searchOptions}" />
              <input type="hidden" name="search-options-type" id="search-options-type" value="${selectedOptionType}"/>
              <input type="hidden" name="recordCount" id="recordCount" value="${count}" />
              <input type="hidden" name="keyword" id="keyword" value="${keyword}" /> 
              <input type="hidden" name="pageRecordCount" id="pageRecordCount" value="${pageRecordCount}" />
              <input type="hidden" name="page" id="page" value="${page}" />
              <input type="hidden" name="selected-filter-options" id="selected-filter-options" value="${selectedFilterOptions}"/>

              <div class="input-group">
                <input name="q" type="text" class="form-control" value="${queryText}" autofocus="autofocus"> 
                   <span class="input-group-btn">
						<button type="submit" class="btn btn-success">Search</button>
				  </span>
			  </div>
			</form>

			<ul class="nav nav-pills pull-right">
				<li class="active"><a href="#">Home</a></li>
				<li><a href="${ctx}/login">Login</a></li>
				<li><a href="${ctx}/management">Management</a></li>
			</ul>
		</div>
	</header>