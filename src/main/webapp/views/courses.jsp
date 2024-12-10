<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Courses</title>
    <script src="https://cdn.tailwindcss.com"></script> <!-- Tailwind CSS included -->
</head>
<body class="bg-gray-50 flex justify-center">
<div class="w-full max-w-4xl p-5">
    <h1 class="text-3xl font-semibold text-gray-900 mb-4">List of Subjects</h1>
    <div class="bg-white shadow-md rounded-lg p-6">
        <ul class="list-disc list-inside">
            <%-- Assuming the attribute name for the list is 'subjects' --%>
            <%-- Iterate over subjects to display each one --%>
            <c:forEach items="${subjects}" var="subject">
                <li class="p-2 hover:bg-gray-100">
                    <strong>${subject.subjectName}</strong> - Teacher: <em>${subject.teacher.name}</em>
                </li>
            </c:forEach>
        </ul>
        <a href="homeServlet" class="bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded">Cancel</a>
    </div>
</div>
</body>
</html>
