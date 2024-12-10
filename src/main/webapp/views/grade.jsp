<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Manage Course</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 min-h-screen flex items-center justify-center">
<div class="container mx-auto px-4">
  <h1 class="text-2xl font-bold text-center text-gray-900 mb-6">${action eq 'edit' ? 'Update Grade' : 'Delete Grade'}</h1>
  <form action="manageGrade" method="post">
    <input type="hidden" name="id" value="${grade.id}">
    <input type="hidden" name="action" value="${action}">

    <c:if test="${action eq 'edit'}">
      <div class="mb-4">
          <%--@declare id="gradeValue"--%><label class="block text-gray-700 text-sm font-bold mb-2" for="gradeValue">
        Grade Value:
      </label>
        <input type="text" name="gradeValue" value="${grade.gradeValue}" class="text-sm py-1 px-2 rounded border-gray-300">
      </div>
      <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded">Update</button>
      <a href="grades" class="bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded">Cancel</a>
    </c:if>

    <c:if test="${action eq 'delete'}">
      <p>Are you sure you want to delete this grade?</p>
      <button type="submit" class="bg-red-500 hover:bg-red-700 text-white py-2 px-4 rounded">Delete</button>
      <a href="grades" class="bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded">Cancel</a>
    </c:if>
  </form>
</div>
</body>
</html>
