<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Scraped Data</title>
<link rel="stylesheet" type="text/css"
	href="<c:url value='/resources/CSS/style.css' />">
</head>
<body>
	<div class="container">
		<h1>Web Scraper</h1>
		<form action="scrape" method="post">
			<label for="urls">Enter URLs (CSV):</label><br>
			<textarea id="urls" name="urls" rows="10" cols="50"></textarea>
			<br> <input type="submit" value="Scrape">
		</form>
		<h1>Scraped Data</h1>

		<ul>
			<c:forEach var="result" items="${results}">
				<c:choose>
					<c:when test="${result.startsWith('Error')}">
						<li class="error">${result}</li>
					</c:when>
					<c:otherwise>
						<li>${result}</li>
					</c:otherwise>
				</c:choose>

			</c:forEach>
		</ul>
	</div>
</body>
</html>
