# 🚀 Workforce Management System - Backend API

A Spring Boot application designed for managing workforce tasks with support for task creation, assignment, updates, priorities, comments, and activity tracking.

## 📦 Tech Stack

- Java 17
- Spring Boot
- Gradle
- In-memory storage (or H2/MySQL ready)
- MapStruct
- Lombok
- Postman (for testing)

The backend will start at: http://localhost:8080




🐞 Bug Fixes Implemented:



✅ Bug 1: Task Reassignment Creates Duplicates

Old tasks are now marked as CANCELLED when reassigned.
Only one active task exists per task type.

✅ Bug 2: Cancelled Tasks Clutter the View
Cancelled tasks are excluded from all fetch-by-date APIs.

✨ New Features:

1. ✅ Smart Task Views
/task-mgmt/fetch-by-date/v2:
Returns tasks within the date range AND earlier uncompleted tasks.



3. ✅ Task Priority
Priority field: HIGH, MEDIUM, LOW


Endpoints:
PUT /task-mgmt/priority – Update task priority
GET /task-mgmt/priority/{priority} – View tasks by priority



3. ✅ Task Comments & Activity Logs
Add comment: POST /task-mgmt/{taskId}/comment
View comments: GET /task-mgmt/{taskId}/comments
View activity: GET /task-mgmt/{taskId}/activities



📬 API Endpoints

🔹 Get Task by ID
             GET /task-mgmt/{id}
🔹 Create Task(s)
             POST /task-mgmt/create
🔹 Update Task Status / Description
              POST /task-mgmt/update
🔹 Assign by Reference
              POST /task-mgmt/assign-by-ref
🔹 Fetch Tasks by Date (Smart View)
              POST /task-mgmt/fetch-by-date/v2
🔹 Update Priority
             PUT /task-mgmt/priority
🔹 Get Tasks by Priority
             GET /task-mgmt/priority/HIGH
🔹 Add Comment
             POST /task-mgmt/{taskId}/comment
🔹 Get Comments
             GET /task-mgmt/{taskId}/comments
🔹 Get Activity Log
            GET /task-mgmt/{taskId}/activities


            
📁 Project Structure

├── controller
├── dto
├── model
│   └── enums
├── repository
├── service
│   └── impl
├── mapper
├── common
│   └── exception
│   └── response
└── Application.java
