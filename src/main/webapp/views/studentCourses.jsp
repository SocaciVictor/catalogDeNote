<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Courses</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 min-h-screen flex items-center justify-center">
<div class="container max-w-4xl mx-auto p-8 bg-white rounded-lg shadow">
    <h1 class="text-2xl font-bold text-center text-gray-900 mb-6">Enrolled Courses</h1>

    <table class="min-w-full bg-white border rounded-lg">
        <thead>
        <tr class="bg-gray-200 text-gray-700 uppercase text-sm">
            <th class="py-3 px-4 text-left">Course Name</th>
            <th class="py-3 px-4 text-left">Teacher Name</th>
            <th class="py-3 px-4 text-left">Student Name</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${studentsSubjects}" var="entry">
            <tr class="border-b">
                <td class="py-3 px-4">
                    <c:out value="${entry.subject.subjectName}" />
                </td>
                <td class="py-3 px-4">
                    <c:out value="${entry.subject.teacher.name}" />
                </td>
                <td class="py-3 px-4">
                    <c:out value="${entry.student.name}" />
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${empty studentsSubjects}">
        <p class="text-center text-gray-500 mt-4">No courses found.</p>
    </c:if>
</div>
</body>
</html>