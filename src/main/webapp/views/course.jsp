<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Manage Course</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 min-h-screen flex items-center justify-center">
<div class="container mx-auto px-4">
    <h1 class="text-2xl font-bold text-center text-gray-900 mb-6">${action eq 'update' ? 'Update Course' : 'Delete Course'}</h1>
    <form action="manageCourse" method="post">
        <input type="hidden" name="id" value="${subject.id}">
        <input type="hidden" name="action" value="${action}">

        <c:if test="${action eq 'update'}">
            <div class="mb-4">
                <%--@declare id="subjectname"--%><label class="block text-gray-700 text-sm font-bold mb-2" for="subjectName">
                    Course Name:
                </label>
                <input type="text" name="subjectName" value="${subject.subjectName}" class="text-sm py-1 px-2 rounded border-gray-300">
            </div>
            <div class="mb-4">
                <%--@declare id="students"--%><label class="block text-gray-700 text-sm font-bold mb-2" for="students">
                    Enrolled Students:
                </label>
                <select multiple name="students" class="block w-full border bg-white rounded py-2 px-3 shadow leading-tight focus:outline-none focus:shadow-outline">
                    <!-- Assuming the servlet prepares a list of enrolled students -->
                    <c:forEach items="${enrolledStudents}" var="student">
                        <option value="${subject.id}">${subject.subjectName}</option>
                    </c:forEach>
                </select>
            </div>
            <button type="submit" class="bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded">Update</button>
            <a href="myCourses" class="bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded">Cancel</a>
        </c:if>

        <c:if test="${action eq 'delete'}">
            <p>Are you sure you want to delete this course?</p>
            <button type="submit" class="bg-red-500 hover:bg-red-700 text-white py-2 px-4 rounded">Delete</button>
            <a href="myCourses" class="bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded">Cancel</a>
        </c:if>
    </form>
</div>
</body>
</html>
