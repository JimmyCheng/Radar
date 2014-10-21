<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description"
	content="searchcode is a free source code and documentation search engine. API documentation, code snippets and open source (free sofware) repositories are indexed and searchable.">
<meta name="author" content="Jimmy Cheng">
<link rel="shortcut icon" href="${ctx}/static/images/favicon.ico" type="image/x-icon">
<title>CodeRadar Search Result| Enterprise Repository Search Engine</title>
<link href="${ctx}/static/css/bootstrap.min.css" rel="stylesheet">
<link href="${ctx}/static/css/docs.min.css" rel="stylesheet">
<link href="${ctx}/static/css/coderadar.css" rel="stylesheet">
<link href="${ctx}/static/css/new.css" rel="stylesheet" type="text/css">

     <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</head>
<body>
	<!--Part1 Header  -->
    <%@include file="inc/navbar-inner.jsp" %>   

	<!--Part2 Result  -->
	<div class="container">
		 <!-- Left Navigator, filter -->
         <div class="col-sm-3 col-md-2 sidebar">
			<div id="result-filter">
				<c:if test="${page>=1}">
					<div>
						<label class="subtitle">&lsaquo; Result Filters &rsaquo;</label>
						<ul class="ul_list">
							<li><a class="subtitle" id="releases-title" href="javascript:void(0);" title="Releases">- Releases</a>
								<ul class="ul_list pdl10 lh20" id="release-list">
									<c:forEach var="releaseTmp" items="${releases}" varStatus="status">
										<li><a href="javascript:void(0);" class="menu-text" id="release:${ releaseTmp }" title="${ releaseTmp }">${ releaseTmp }</a></li>
									</c:forEach>
								</ul></li>

							<li><a class="subtitle" id="filetypes-title" href="javascript:void(0);" title="File Type">- File Type</a>
								<ul class="ul_list pdl10 lh20" id="filetype-list">
									<c:forEach var="fileTypeTmp" items="${fileTypes}" varStatus="status">
										<li><a href="javascript:void(0);" class="menu-text" id="fileType:${ fileTypeTmp }"
											title="${ fileTypeTmp }">${ fileTypeTmp }</a></li>
									</c:forEach>
								</ul>
							</li>
						</ul>
					</div>
				</c:if>
			</div>
		</div>
        
        <!-- Right Pane, content -->
		<div class="col-sm-7 col-md-8 main">           
           <br>
           <br>
           <table class="table">
             <tbody>
               <tr>
                  <c:forEach var="item" items="${sourceInfoList}" varStatus="status">
                  	<div id="data-row-${status.index}">
                  		<ul class="ul_list">
                  			<li><span>
                                 <a target="_blank" class="item-name" href="${ctx}/view?query=${queryText}&path=${item.encodedPath}">${item.name}</a></span>
                            </li>
                  			<li>ViewVc &raquo; <a class="item-link" href="${item.viewVc}?view=markup" target="_blank">${item.path}</a></li>
                  			<li><div class="highlight-text">
                  					<c:out value="${item.match}" escapeXml="false" />
                  				</div>
                                          </li>
                  			<li>&nbsp;</li>
                  		</ul>
                  	</div>
                  </c:forEach>
               </tr>
             </tbody>
           </table>
            
           <div class="row">
              <div class="col-md-8 col-md-offset-4">
                <c:if test="${page>=1}">
                Total matches : ${count} records (Time used ${timeUsed} seconds).
                  <c:if test="${count>0}">
                    Page : ${page}. 
                    <a href="javascript:void(0);" id="link-prev-page">&laquo;Prev</a> | 
                    <a href="javascript:void(0);" id="link-next-page">Next&raquo;</a>
                  </c:if>
                </c:if>
              </div>
           </div>
           <br>      
		</div>
	</div>
    
    <%@include file="inc/footer.jsp" %>
    <script src="${ctx}/static/js/jquery.min.js"></script>
    <script src="${ctx}/static/js/jquery.tipsy.js"></script>
    <script src="${ctx}/static/js/coderadar.js"></script>
    
</body>

</html>