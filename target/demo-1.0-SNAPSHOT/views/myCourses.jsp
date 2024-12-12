<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>My Courses</title>
    <script src="https://cdn.tailwindcss.com"></script> <!-- Tailwind CSS -->
</head>
<body class="bg-gray-100 min-h-screen flex flex-col items-center justify-center">
<div class="w-full max-w-2xl p-5">
    <h1 class="text-2xl font-bold text-center text-gray-900 mb-6">Course List</h1>

    <!-- Displaying the list of courses -->
    <div class="flex flex-col">
        <c:forEach items="${subjects}" var="subject">
            <div class="bg-white p-4 shadow-md rounded-lg mb-4 flex justify-between items-center">
                <span class="text-gray-800 text-lg">${subject.subjectName}</span>
                <div>
                    <a href="manageCourse?action=update&id=${subject.id}" class="bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded mr-2">Edit</a>
                    <a href="manageCourse?action=delete&id=${subject.id}" class="bg-red-500 hover:bg-red-700 text-white py-2 px-4 rounded">Delete</a>
                </div>
            </div>
        </c:forEach>
    </div>
    <a href="manageCourse?action=add" class="bg-green-500 hover:bg-green-700 text-white py-2 px-4 rounded w-full">Add Course</a>
    <a href="homeServlet" class="bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded mt-4">Back to Home</a>
</div>
</body>
</html>
