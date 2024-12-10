<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Grades Management</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 min-h-screen flex items-center justify-center">
<div class="container mx-auto px-4">
    <h1 class="text-2xl font-bold text-center text-gray-900 mb-6">Grades Overview</h1>

    <!-- Form for adding a new grade -->
    <form action="addGrade" method="post" class="mb-4">
        <label>
            <select multiple name="subjects" class="block w-full border bg-white rounded py-2 px-3 shadow leading-tight focus:outline-none focus:shadow-outline">
                <!-- Assuming the servlet prepares a list of enrolled students -->
                <c:forEach items="${subjects}" var="subject">
                    <option value="${subject.id}">${subject.subjectName}</option>
                </c:forEach>
            </select>
        </label>
        <label>
            <select multiple name="subjects" class="block w-full border bg-white rounded py-2 px-3 shadow leading-tight focus:outline-none focus:shadow-outline">
                <!-- Assuming the servlet prepares a list of enrolled students -->
                <c:forEach items="${students}" var="student">
                    <option value="${student.id}">${student.name}</option>
                </c:forEach>
            </select>
        </label>
        <input type="text" name="gradeValue" placeholder="Grade Value" required>
        <button type="submit" class="bg-green-500 hover:bg-green-700 text-white py-2 px-4 rounded">Add Grade</button>
        <a href="homeServlet" class="bg-gray-500 hover:bg-gray-700 text-white py-2 px-4 rounded">Cancel</a>
    </form>

    <div class="flex flex-col">
        <c:forEach items="${grades}" var="grade">
            <div class="bg-white p-4 shadow-md rounded-lg mb-4 flex justify-between items-center">
                <!-- Grade Information -->
                <span class="text-gray-800 text-lg">Grade: ${grade.gradeValue}  Subject Name: ${grade.studentSubject.subject.subjectName}  Student Name: ${grade.studentSubject.student.name}</span>

                <!-- Conditional rendering based on whether the grade is related to a subject taught by the logged-in teacher -->
                <c:if test="${grade.studentSubject.subject.teacher.id == teacherId}">
                    <!-- Links to dedicated Edit and Delete pages -->
                    <div>
                        <a href="manageGrade?action=edit&id=${grade.id}" class="bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded mr-2">Edit</a>
                        <a href="manageGrade?action=delete&id=${grade.id}" class="bg-red-500 hover:bg-red-700 text-white py-2 px-4 rounded">Delete</a>
                    </div>
                </c:if>
            </div>
        </c:forEach>
    </div>

</div>
</body>
</html>