<%@ page language="java" contentType="text/html" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description"
	content="coderadar is a free source code and documentation search engine. API documentation, code snippets and open source (free sofware) repositories are indexed and searchable.">
<meta name="author" content="Jimmy Cheng">
<link rel="shortcut icon" href="${ctx}/static/img/favicon.ico" type="image/x-icon">
<title>coderadar | Enterprise Repository Search Engine</title>
<link href="static/css/bootstrap.min.css" rel="stylesheet">
<link href="static/css/docs.min.css" rel="stylesheet">
<link href="static/css/coderadar.css" rel="stylesheet">
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
</head>

<body>
	<!--Part1 Header  -->
    <%@include file="inc/navbar-main.jsp" %>

	<!--Part2 Content  -->
	<div class="container">
		<!--Search Form -->
		<div class="jumbotron">
			<div class="row">
				<div class="col-md-12">
					<form method="get" action="${ctx}/find">
						<input id="main-search" name="q" type="text" placeholder="Type a code snippet or function. e.g. InstanceManager" autofocus="autofocus" />
						<input id="main-search-button" class="btn btn-success" type="submit" value="Search" />
					</form>
				</div>
			</div>
		</div>

		<!--Start of the coderadar features -->
		<div class="row">
			<div class="col-lg-12">
				<h2>Welcome to CodeRadar</h2>
			</div>
		</div>

		<div class="features">
			<div class="row">
				<!--Feature Set 1 -->
				<div class="row">
					<div class="col-lg-6 gmail-feature-set">
						<img alt="" src="${ctx}/static/img/coderadar-svn.png">
						<div>
							<h4>SVN Repository Support</h4>
							<p>
								CodeRadar can be integrated with SVN. CodeRadar monitors SVN changes and update the Solr Database periodically.  
							</p>
						</div>
					</div>
					<div class="col-lg-6 gmail-feature-set">
						<img alt="" class="hi-dpi" src="${ctx}/static/img/coderadar-git.png">
						<div>
							<h4>GIT Repository Support</h4>
							<p>
                                 CodeRadar can be integrated with GIT. CodeRadar monitors Git changes and update the Solr Database periodically.							
                            </p>
						</div>
					</div>
				</div>

				<div class="row">
					<div class="col-lg-6 gmail-feature-set">
						<img alt="" class="hi-dpi" src="${ctx}/static/img/coderadar-fulltext.png">
						<div>
							<h4>Full Text Search</h4>
							<p>
                                Powered by Solr, CodeRadar supports fulltext search, which can be very useful for developers to locate the code snippet.
							</p>
						</div>
					</div>
					<div class="col-lg-6 gmail-feature-set">
						<img alt="" class="hi-dpi" src="${ctx}/static/img/coderadar-deploy.png">
						<div>
							<h4>Easy Deployment</h4>
							<p>
                                Coderadar is composed by pure Java. It supports mainstream Java Servers like Tomcat, Jetty and JBoss, etc.							
                            </p>
						</div>
					</div>
				</div>
                <br>				
			</div>
		</div>
		<!--end of features-->
	</div>
	<!--container-->

    <!--Part3 Footer  -->
    <%@include file="inc/footer.jsp" %>

</body>
</html>
