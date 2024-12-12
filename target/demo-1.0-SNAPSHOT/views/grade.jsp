<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Manage Course</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 min-h-screen flex items-center justify-center">
<div class="container max-w-lg mx-auto p-8 bg-white rounded-lg shadow">
  <h1 class="text-2xl font-bold text-center text-gray-900 mb-6">
    ${action eq 'edit' ? 'Update Grade' : action eq 'add' ? 'Add Grade' : 'Delete Grade'}
  </h1>
  <form action="manageGrade" method="post">
    <input type="hidden" name="id" value="${grade.id}">
    <input type="hidden" name="action" value="${action}">

    <c:if test="${action eq 'edit'}">
      <div class="mb-4">
        <label class="block text-gray-700 text-sm font-bold mb-2" for="gradeValue">Grade Value:</label>
        <input type="text" name="gradeValue" value="${grade.gradeValue}" class="form-input mt-1 block w-full rounded-md border-gray-300 shadow-sm">
      </div>
      <div class="flex justify-between">
        <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Update</button>
        <a href="grades" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">Cancel</a>
      </div>
    </c:if>

    <c:if test="${action eq 'add'}">
      <div class="mb-4">
        <%--@declare id="gradevalue"--%><label class="block text-gray-700 text-sm font-bold mb-2" for="gradeValue">Grade Value:</label>
        <input type="text" name="gradeValue" class="form-input mt-1 block w-full rounded-md border-gray-300 shadow-sm">
      </div>

      <div class="mb-4">
       <%--@declare id="subjectid"--%><label class="block text-gray-700 text-sm font-bold mb-2" for="subjectId">Subjects</label>
        <select name="subjectId" class="form-select block w-full mt-1 border bg-white rounded-md shadow-sm">
          <c:forEach items="${subjects}" var="subject">
            <option value="${subject.id}">
                ${subject.subjectName}
            </option>
          </c:forEach>
        </select>
      </div>

      <div class="mb-4">
          <%--@declare id="studentid"--%><label class="block text-gray-700 text-sm font-bold mb-2" for="studentId">Students</label>
        <select name="studentId" class="form-select block w-full mt-1 border bg-white rounded-md shadow-sm">
          <c:forEach items="${students}" var="student">
            <option value="${student.id}">
                ${student.name}
            </option>
          </c:forEach>
        </select>
      </div>

      <div class="flex justify-between">
        <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Add</button>
        <a href="grades" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">Cancel</a>
      </div>
    </c:if>

    <c:if test="${action eq 'delete'}">
      <p class="mb-4 text-gray-700">Are you sure you want to delete this grade?</p>
      <div class="flex justify-between">
        <button type="submit" class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">Delete</button>
        <a href="grades" class="bg-gray-500 hover:bg-gray-700 text-white font-bold py-2 px-4 rounded">Cancel</a>
      </div>
    </c:if>
  </form>
</div>
</body>
</html>
