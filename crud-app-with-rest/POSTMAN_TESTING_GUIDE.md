# Postman Testing Guide for Student Management System REST API

## Base URL
All endpoints use the base URL: `http://localhost:8080`

## 1. STUDENT CONTROLLER ENDPOINTS (/api/students)

### 1.1 GET All Students
- **Method**: GET
- **URL**: `http://localhost:8080/api/students`
- **Headers**: None required
- **Expected Response**: 200 OK with array of students

### 1.2 GET Student by ID
- **Method**: GET
- **URL**: `http://localhost:8080/api/students/{id}`
- **Example**: `http://localhost:8080/api/students/1`
- **Headers**: None required
- **Expected Response**: 200 OK with student object or 404 Not Found

### 1.3 POST Create New Student
- **Method**: POST
- **URL**: `http://localhost:8080/api/students`
- **Headers**: 
  - Content-Type: application/json
- **Body (JSON)**:
```json
{
    "name": "John Doe",
    "email": "john.doe@university.edu",
    "age": 22
}
```
- **Expected Response**: 201 Created with created student object

### 1.4 PUT Update Student
- **Method**: PUT
- **URL**: `http://localhost:8080/api/students/{id}`
- **Example**: `http://localhost:8080/api/students/1`
- **Headers**: 
  - Content-Type: application/json
- **Body (JSON)**:
```json
{
    "name": "John Smith",
    "email": "john.smith@university.edu",
    "age": 23
}
```
- **Expected Response**: 200 OK with updated student or 404 Not Found

### 1.5 DELETE Student
- **Method**: DELETE
- **URL**: `http://localhost:8080/api/students/{id}`
- **Example**: `http://localhost:8080/api/students/1`
- **Headers**: None required
- **Expected Response**: 200 OK with success message or 404 Not Found

### 1.6 Advanced Student Endpoints

#### Search Students
- **Method**: GET
- **URL**: `http://localhost:8080/api/students/search?query=john`
- **Headers**: None required

#### Get Students Older Than Age
- **Method**: GET
- **URL**: `http://localhost:8080/api/students/older-than/20`
- **Headers**: None required

#### Get Students Without Courses
- **Method**: GET
- **URL**: `http://localhost:8080/api/students/without-courses`
- **Headers**: None required

#### Get Students with Courses (Older Than Age)
- **Method**: GET
- **URL**: `http://localhost:8080/api/students/with-courses-older-than/18`
- **Headers**: None required

#### Get Course Count per Student
- **Method**: GET
- **URL**: `http://localhost:8080/api/students/course-count`
- **Headers**: None required

#### Get Total Credits per Student
- **Method**: GET
- **URL**: `http://localhost:8080/api/students/total-credits`
- **Headers**: None required

## 2. COURSE CONTROLLER ENDPOINTS (/api/courses)

### 2.1 GET All Courses
- **Method**: GET
- **URL**: `http://localhost:8080/api/courses`
- **Headers**: None required
- **Expected Response**: 200 OK with array of courses

### 2.2 GET Course by ID
- **Method**: GET
- **URL**: `http://localhost:8080/api/courses/{id}`
- **Example**: `http://localhost:8080/api/courses/1`
- **Headers**: None required
- **Expected Response**: 200 OK with course object or 404 Not Found

### 2.3 POST Create New Course
- **Method**: POST
- **URL**: `http://localhost:8080/api/courses`
- **Headers**: 
  - Content-Type: application/json
- **Body (JSON)**:
```json
{
    "title": "Advanced Mathematics",
    "credits": 4
}
```
- **Expected Response**: 201 Created with created course object

### 2.4 POST Create Course for Student
- **Method**: POST
- **URL**: `http://localhost:8080/api/courses/student/{studentId}`
- **Example**: `http://localhost:8080/api/courses/student/1`
- **Headers**: 
  - Content-Type: application/json
- **Body (JSON)**:
```json
{
    "title": "Computer Science Fundamentals",
    "credits": 3
}
```
- **Expected Response**: 201 Created with created course assigned to student

### 2.5 PUT Update Course
- **Method**: PUT
- **URL**: `http://localhost:8080/api/courses/{id}`
- **Example**: `http://localhost:8080/api/courses/1`
- **Headers**: 
  - Content-Type: application/json
- **Body (JSON)**:
```json
{
    "title": "Advanced Calculus",
    "credits": 5
}
```
- **Expected Response**: 200 OK with updated course or 404 Not Found

### 2.6 DELETE Course
- **Method**: DELETE
- **URL**: `http://localhost:8080/api/courses/{id}`
- **Example**: `http://localhost:8080/api/courses/1`
- **Headers**: None required
- **Expected Response**: 200 OK with success message or 404 Not Found

### 2.7 Advanced Course Endpoints

#### Get Courses by Minimum Credits
- **Method**: GET
- **URL**: `http://localhost:8080/api/courses/min-credits/3`
- **Headers**: None required

#### Get Unassigned Courses
- **Method**: GET
- **URL**: `http://localhost:8080/api/courses/unassigned`
- **Headers**: None required

#### Get Credit Distribution
- **Method**: GET
- **URL**: `http://localhost:8080/api/courses/credits-distribution`
- **Headers**: None required

#### Get Total Credits for Student
- **Method**: GET
- **URL**: `http://localhost:8080/api/courses/student/1/total-credits`
- **Headers**: None required

#### Get Average Credits by Age Group
- **Method**: GET
- **URL**: `http://localhost:8080/api/courses/avg-credits-by-age-group`
- **Headers**: None required

## 3. JPQL TEST CONTROLLER ENDPOINTS (/api/jpql)

### 3.1 Complete JPQL Demo
- **Method**: GET
- **URL**: `http://localhost:8080/api/jpql/demo`
- **Headers**: None required
- **Description**: Runs all complex JPQL queries and returns comprehensive results

### 3.2 Individual JPQL Query Endpoints

#### Students with Courses (Older Than Age)
- **Method**: GET
- **URL**: `http://localhost:8080/api/jpql/students/with-courses-older-than/20`
- **Headers**: None required

#### Student Course Count
- **Method**: GET
- **URL**: `http://localhost:8080/api/jpql/students/course-count`
- **Headers**: None required

#### Students with Minimum Courses
- **Method**: GET
- **URL**: `http://localhost:8080/api/jpql/students/minimum-courses/2`
- **Headers**: None required

#### Students by Credits and Age Range
- **Method**: GET
- **URL**: `http://localhost:8080/api/jpql/students/by-credits-and-age-range?minCredits=3&minAge=18&maxAge=25`
- **Headers**: None required

#### Search Students by Pattern
- **Method**: GET
- **URL**: `http://localhost:8080/api/jpql/students/search/john`
- **Headers**: None required

#### Query Explanations
- **Method**: GET
- **URL**: `http://localhost:8080/api/jpql/explanations`
- **Headers**: None required

## 4. TESTING SEQUENCE RECOMMENDATIONS

### Step 1: Create Test Data
1. POST `/api/students` - Create 3-5 students
2. POST `/api/courses/student/{id}` - Create courses for some students
3. POST `/api/courses` - Create some unassigned courses

### Step 2: Test Basic CRUD
1. GET `/api/students` - Verify students created
2. GET `/api/courses` - Verify courses created
3. PUT `/api/students/{id}` - Update a student
4. DELETE `/api/courses/{id}` - Delete a course

### Step 3: Test Advanced Queries
1. GET `/api/jpql/demo` - Run complete JPQL demo
2. GET `/api/students/without-courses` - Test complex queries
3. GET `/api/courses/credits-distribution` - Test aggregations

## 5. SAMPLE TEST DATA

### Students to Create:
```json
[
    {"name": "Alice Johnson", "email": "alice@university.edu", "age": 20},
    {"name": "Bob Smith", "email": "bob@university.edu", "age": 22},
    {"name": "Charlie Brown", "email": "charlie@university.edu", "age": 19},
    {"name": "Diana Prince", "email": "diana@university.edu", "age": 24},
    {"name": "Eve Wilson", "email": "eve@university.edu", "age": 21}
]
```

### Courses to Create:
```json
[
    {"title": "Mathematics I", "credits": 3},
    {"title": "Computer Science", "credits": 4},
    {"title": "Physics", "credits": 3},
    {"title": "Chemistry", "credits": 2},
    {"title": "Literature", "credits": 3}
]
```

## 6. EXPECTED HTTP STATUS CODES

- **200 OK**: Successful GET, PUT, DELETE operations
- **201 Created**: Successful POST operations
- **400 Bad Request**: Invalid request data (validation errors)
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server-side errors

## 7. VALIDATION RULES TO TEST

### Student Validation:
- Name: Required, not blank
- Email: Required, valid email format
- Age: Required, minimum 16

### Course Validation:
- Title: Required, not blank
- Credits: Required, minimum 1

## 8. POSTMAN COLLECTION SETUP

1. Create a new Collection in Postman called "Student Management API"
2. Add environment variable: `baseUrl` = `http://localhost:8080`
3. Use `{{baseUrl}}` in your requests
4. Create folders for: Students, Courses, JPQL, Advanced Queries

This comprehensive guide covers all your REST endpoints and provides you with everything needed to thoroughly test your Spring Boot application in Postman!
