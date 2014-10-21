<%@ page contentType="text/html;charset=utf-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE HTML>
<html lang="en">
<head>
<title>CodeRadar</title>
<link rel="icon" href="favicon.ico" type="${ctx}/static/images/x-icon" />
<link rel="shortcut icon" href="favicon.ico" type="${ctx}/static/images/x-icon" />
<title>CodeRadar Search Result| Enterprise Repository Search Engine</title>
<link href="static/css/bootstrap.min.css" rel="stylesheet">
<link href="static/css/docs.min.css" rel="stylesheet">
<link href="static/css/coderadar.css" rel="stylesheet">
<link href="static/css/new.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="${ctx}/static/js/jquery.min.js"></script>
<script language="javascript" type="text/javascript" src="${ctx}/static/js/xregexp_1.5.1.js"></script>
<script type="text/javascript" src="${ctx}/static/js/highlighter/shCore.js"></script>

<c:choose>
	<c:when test="${fileType eq 'java'}">
		<script type="text/javascript" src="${ctx}/static/js/highlighter/shBrushJava.js"></script>
	</c:when>
	<c:when test="${fileType eq 'xml'}">
		<script type="text/javascript" src="${ctx}/static/js/highlighter/shBrushXml.js"></script>
	</c:when>
	<c:when test="${fileType eq 'sql'}">
		<script type="text/javascript" src="${ctx}/static/js/highlighter/shBrushSql.js"></script>
	</c:when>
	<c:when test="${fileType eq 'cpp'}">
		<script type="text/javascript" src="${ctx}/static/js/highlighter/shBrushCpp.js"></script>
	</c:when>
	<c:when test="${fileType eq 'php'}">
		<script type="text/javascript" src="${ctx}/static/js/highlighter/shBrushPhp.js"></script>
	</c:when>
	<c:when test="${fileType eq 'js'}">
		<script type="text/javascript" src="${ctx}/static/js/highlighter/shBrushJscript.js"></script>
	</c:when>
	<c:otherwise>
		<script type="text/javascript" src="${ctx}/static/js/highlighter/shBrushPlain.js"></script>
	</c:otherwise>
</c:choose>

<link type="text/css" rel="stylesheet" href="${ctx}/static/css/highlighter/shCoreRDark.css" />

<script type="text/javascript">
	SyntaxHighlighter.all();
</script>

<style type="text/css">
.syntaxhighlighter {
	overflow-y: hidden !important;
	overflow-x: auto !important;
}
</style>

</head>

<body style="background: white; font-family: Helvetica">
	<!--Part1 Header  -->
    <%@include file="inc/navbar-inner.jsp" %>    

	<!--Part2 Result  -->
	<div class="container">
		<h1>${sourceInfo.name }</h1>
		<h5><a href="${sourceInfo.viewVc}" target="_blank">${sourceInfo.svnUrl}</a></h5>
		<pre id="code-block" class="brush:${fileType};highlight:${matchLines};">
			${sourceInfo.content}
	    </pre>
	</div>
  
    <!--Part3 Footer  -->
    <%@include file="inc/footer.jsp" %>
</body>
</html>
