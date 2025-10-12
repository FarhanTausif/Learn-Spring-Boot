# Simplified API Testing Guide

This guide explains how to use the updated `postman-testing.json` file to test the **simplified and consolidated** API endpoints in your Spring Boot CRUD app.

## 🎯 What Changed - API Simplification

### Before (Problems):
- **20+ redundant endpoints** for courses (separate endpoints for each filter)
- **15+ redundant endpoints** for students (multiple ways to achieve same results)
- Confusing API structure with overlapping functionality
- Hard to maintain and document

### After (Simplified):
- **10 core endpoints** for courses with smart filtering
- **10 core endpoints** for students with consolidated parameters  
- **2 demo/dashboard endpoints**
- Clean, RESTful design following Spring Boot best practices

## 🚀 Quick Start

### 1. Import the Collection
1. Open Postman
2. Click **Import** → **Upload Files** 
3. Select `postman-testing.json` from your project directory
4. Collection appears as "**Simplified CRUD App API Collection**"

### 2. Start Your Application
```bash
./mvnw spring-boot:run
```
Ensure it's running on `http://localhost:8080`

## 📋 API Structure Overview

### Course Management (`/api/courses`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/courses` | Get all courses with **optional filters** |
| GET | `/api/courses/dto` | Get courses as DTO |
| GET | `/api/courses/{id}` | Get course by ID |
| POST | `/api/courses` | Create course (optionally for student) |
| PUT | `/api/courses/{id}` | Update course |
| DELETE | `/api/courses/{id}` | Delete course |
| GET | `/api/courses/stats` | Get course statistics |

### Student Management (`/api/students`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/students` | Get all students with **optional filters** |
| GET | `/api/students/dto` | Get students as DTO with course info |
| GET | `/api/students/summary` | Get student summaries |
| GET | `/api/students/{id}` | Get student by ID |
| POST | `/api/students` | Create student |
| PUT | `/api/students/{id}` | Update student |
| DELETE | `/api/students/{id}` | Delete student |
| GET | `/api/students/stats` | Get student statistics |

## 🔍 Smart Filtering Examples

### Course Filtering (Single Endpoint)
```
GET /api/courses?studentId=1&minCredits=3&title=Java&unassigned=false
```

**Available Parameters:**
- `studentId` - Courses for specific student
- `minCredits` & `maxCredits` - Credit range filtering
- `title` - Search by course title
- `unassigned` - Show only unassigned courses

### Student Filtering (Single Endpoint)
```
GET /api/students?search=John&minAge=20&maxAge=30&email=john@example.com
```

**Available Parameters:**
- `search` - Search by name or email
- `minAge` & `maxAge` - Age range filtering  
- `email` - Find by exact email

### Advanced DTO Filtering
```
GET /api/students/dto?withoutCourses=true&minCourses=2
```

## 📝 Testing Workflow

### 1. **Basic CRUD Testing**
- Create Student → Create Course → Associate → Update → Delete

### 2. **Filter Testing**
- Test each filter parameter individually
- Test combinations of filters
- Verify empty results for non-existent data

### 3. **DTO Testing** 
- Compare full objects vs DTO responses
- Verify DTO contains expected fields only

### 4. **Statistics Testing**
- Check `/stats` endpoints return correct counts
- Verify dashboard data consistency

## 🛠️ Best Practices Implemented

### ✅ RESTful Design
- Resource-based URLs (`/courses`, `/students`)
- HTTP methods match operations (GET, POST, PUT, DELETE)
- Consistent response formats

### ✅ Query Parameter Filtering
- Single endpoint handles multiple filters
- Optional parameters for flexibility
- Clear parameter naming

### ✅ Proper HTTP Status Codes
- `200 OK` for successful GET/PUT
- `201 Created` for successful POST
- `404 Not Found` for missing resources
- `409 Conflict` for duplicate data

### ✅ Error Handling
- Consistent error response format
- Validation error messages
- Proper exception handling

## 🚨 Common Testing Scenarios

### Create Student with Course
1. `POST /api/students` - Create student
2. `POST /api/courses?studentId={id}` - Create course for student
3. `GET /api/students/{id}` - Verify association

### Filter and Search
1. `GET /api/courses?title=Java` - Search courses
2. `GET /api/students?minAge=20&maxAge=25` - Age range
3. `GET /api/students/dto?withoutCourses=true` - Students without courses

### Statistics and Analytics
1. `GET /api/courses/stats` - Course statistics
2. `GET /api/students/stats` - Student statistics  
3. `GET /api/demo/jpql` - JPQL demonstration

# Student-Course Relationship Management Guide

This guide explains how to manage student-course relationships efficiently using manual foreign keys (avoiding `@OneToMany` for performance).

## 🎯 What's New - Relationship Management

### **Performance-First Approach:**
- **Manual foreign key** (`student_id` in Course table) instead of JPA `@OneToMany`
- **No N+1 query problems** from lazy loading
- **Efficient queries** using repository methods
- **Full control** over relationship operations

## 🚀 Quick Start

### 1. Import the Updated Collection
1. Open Postman
2. Import the updated `postman-testing.json` 
3. Collection now includes **"Student-Course Relationships"** folder with 8 new endpoints

### 2. Relationship Management Workflow
```
Create Student → Create Course → Assign Course to Student → Manage Relationships
```

## 📋 New Relationship Endpoints

### Student → Course Operations (`/api/students/{studentId}/courses`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/students/{studentId}/courses/{courseId}` | Assign course to student |
| DELETE | `/api/students/{studentId}/courses/{courseId}` | Remove course from student |
| GET | `/api/students/{studentId}/courses` | Get all courses for student |
| GET | `/api/students/{studentId}/total-credits` | Get total credits for student |

### Course → Student Operations (`/api/courses/{courseId}/student`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/courses/{courseId}/student` | Get student taking the course |
| PUT | `/api/courses/{courseId}/student/{studentId}` | Reassign course to different student |
| DELETE | `/api/courses/{courseId}/student` | Unassign course from any student |
| GET | `/api/courses/students` | Get all students taking courses |

## 🔍 Relationship Management Examples

### **Assign Course to Student**
```
POST /api/students/1/courses/2
Response: {"message": "Course assigned to student successfully", "studentId": "1", "courseId": "2"}
```

### **Get Student's Courses**
```
GET /api/students/1/courses
Response: [
  {"courseId": 1, "title": "Java Basics", "credits": 3, "studentId": 1},
  {"courseId": 2, "title": "Spring Boot", "credits": 4, "studentId": 1}
]
```

### **Get Student's Total Credits**
```
GET /api/students/1/total-credits
Response: {"studentId": 1, "totalCredits": 7}
```

### **Reassign Course to Different Student**
```
PUT /api/courses/2/student/3
Response: {"message": "Course reassigned successfully", "courseId": "2", "newStudentId": "3"}
```

## 📝 Complete Testing Workflow

### 1. **Basic Setup**
```
1. POST /api/students - Create student (John)
2. POST /api/students - Create student (Jane) 
3. POST /api/courses - Create course (Java Basics)
4. POST /api/courses - Create course (Spring Boot)
```

### 2. **Relationship Management**
```
1. POST /api/students/1/courses/1 - Assign Java to John
2. POST /api/students/1/courses/2 - Assign Spring to John
3. GET /api/students/1/courses - View John's courses
4. GET /api/students/1/total-credits - Check John's credits
```

### 3. **Advanced Operations**
```
1. PUT /api/courses/2/student/2 - Reassign Spring Boot to Jane
2. GET /api/courses/1/student - Check who's taking Java
3. DELETE /api/courses/2/student - Unassign Spring Boot
4. GET /api/courses/students - List all students taking courses
```

## 🛠️ Benefits of This Approach

### ✅ **Performance Optimized**
- No lazy loading issues
- Direct foreign key queries
- Controlled relationship fetching
- No circular reference problems

### ✅ **Clean API Design**  
- RESTful resource relationships
- Clear endpoint naming
- Proper HTTP methods
- Consistent error handling

### ✅ **Flexible Operations**
- Assign/unassign courses
- Reassign to different students
- Calculate total credits
- Get relationships in both directions

## 🚨 Common Use Cases

### **Enrollment Management**
```
1. Student enrolls in course: POST /api/students/{id}/courses/{courseId}
2. Student drops course: DELETE /api/students/{id}/courses/{courseId}  
3. View student schedule: GET /api/students/{id}/courses
4. Check credit load: GET /api/students/{id}/total-credits
```

### **Course Administration**
```
1. Check course enrollment: GET /api/courses/{id}/student
2. Transfer course: PUT /api/courses/{id}/student/{newStudentId}
3. Cancel course: DELETE /api/courses/{id}/student
4. View active students: GET /api/courses/students
```

### **Reporting & Analytics**
```
1. Student statistics: GET /api/students/stats
2. Course statistics: GET /api/courses/stats
3. Students without courses: GET /api/students/dto?withoutCourses=true
4. Unassigned courses: GET /api/courses?unassigned=true
```

## 🔧 Error Handling

**Course Already Assigned:**
```
POST /api/students/1/courses/2 (when course 2 is already assigned)
Response: 400 Bad Request {"error": "Course is already assigned to another student"}
```

**Student/Course Not Found:**
```
POST /api/students/999/courses/1
Response: 400 Bad Request {"error": "Student not found with id: 999"}
```

**Course Not Assigned to Student:**
```
DELETE /api/students/1/courses/5 (when course 5 isn't assigned to student 1)
Response: 400 Bad Request {"error": "Course is not assigned to this student"}
```

---

**🎉 You now have a clean, maintainable API following Spring Boot best practices!**

**🎉 You now have complete student-course relationship management with optimal performance!**

The manual foreign key approach gives you:
- **Better performance** than `@OneToMany` annotations
- **Full control** over relationship operations  
- **Clean API design** following REST principles
- **Efficient queries** without N+1 problems
