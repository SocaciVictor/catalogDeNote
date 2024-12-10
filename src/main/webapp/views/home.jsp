<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Home Page</title>
    <script src="https://cdn.tailwindcss.com"></script> <!-- Tailwind CSS -->
</head>
<body class="bg-gray-100 flex flex-col items-center justify-center min-h-screen">
<div class="bg-white p-8 rounded-lg shadow-lg">
    <h1 class="text-2xl font-bold text-gray-900 mb-4">Welcome, <%= request.getAttribute("username") %>!</h1>
    <p>This is your home page.</p>

    <% if ("teacher".equals(session.getAttribute("userType"))) { %>
    <div class="flex space-x-4 mt-6">
        <button class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
            <a href="courses" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">All Courses</a>
        </button>
        <button class="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
            Your Courses
        </button>
        <button class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">
            Grades
        </button>
    </div>
    <% } else { %>
    <div class="flex space-x-4 mt-6">
        <button class="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
            My Courses
        </button>
        <button class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">
            My Grades
        </button>
    </div>
    <% } %>

    <br/>
    <a href="${pageContext.request.contextPath}/logout" class="text-blue-500 hover:underline">Logout</a> <!-- Optional logout link -->
</div>
</body>
</html>
