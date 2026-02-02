# Workflow Management System

A Role-Based Approval Workflow Engine built using Spring Boot, Spring Security, JWT, and JPA.
This system enables configurable business approval flows such as Leave Requests, Expense Approvals, or Access Requests.

## 📌 Overview

Organizations often require structured approval flows where:

A Requester submits a request

One or more Approvers review it

The system enforces valid state transitions

All actions are logged in approval history

This project implements a secure, extensible, and production-style workflow engine.

## 🏗 Architecture

The project follows a layered architecture:

Layer	Responsibility
Controller	REST APIs
Service	Business logic & workflow transitions
Repository	Database access (JPA)
Security	JWT authentication & role authorization
Exception Layer	Centralized error handling

## 🔄 Workflow Lifecycle
```
REQUEST CREATED → IN_PROGRESS → APPROVED
                          ↘ REJECTED
```
Roles
Role	Capabilities
REQUESTER	Create & view requests
APPROVER	Approve / Reject
ADMIN	Override approvals
---
## ✨ Features

JWT Authentication

Role-Based Authorization

Dynamic Approval Flow

Request Status Transitions

Approval History Tracking

Global Exception Handling

H2 Database for development

Unit & Controller Tests
---
## 🧪 Initial ApprovalStep configuration 
```
INSERT INTO approval_steps (request_type,step_order,role) VALUES ('LEAVE',1,'ROLE_APPROVER');
INSERT INTO approval_steps (request_type,step_order,role) VALUES ('LEAVE',2,'ROLE_ADMIN');
INSERT INTO approval_steps (request_type,step_order,role) VALUES ('EXPENSE',1,'ROLE_APPROVER');
INSERT INTO approval_steps (request_type,step_order,role) VALUES ('EXPENSE',2,'ROLE_APPROVER');
INSERT INTO approval_steps (request_type,step_order,role) VALUES ('EXPENSE',3,'ROLE_ADMIN');
```
---
## 🔐 Authentication APIs
Register
```
POST /auth/register

{
  "user": "john",
  "pass": "123",
  "role": "ROLE_REQUESTER"
}
```
Login
```
POST /auth/login
```

Returns a JWT token.

Use token in requests:

Authorization: Bearer <token>

## 📮 Request APIs
Method	Endpoint	Description	Role
```
POST	/requests	Create request	REQUESTER
POST	/requests/{id}/approve	Approve request	APPROVER / ADMIN
POST	/requests/{id}/reject	Reject request	APPROVER
GET	/requests/{id}	Get request details	All roles
GET	/requests/history/{id}	Get approval history	All roles
```

## ⚙️ Run Instructions
1. Clone Repository
```   
git clone https://github.com/YOUR_USERNAME/workflow.git
cd workflow
```

2. Build
```
mvn clean install
```
3. Run
 ```
mvn spring-boot:run
```

Application runs at:
```
http://localhost:8080
``` 
## 🖥 H2 Console (Dev Only)
```
http://localhost:8080/h2-console
```
Setting	Value
```
JDBC URL	jdbc:h2:mem:test
User	sa
Password	(empty)
```
---

## 🔐 1. Register User

Endpoint
```
POST /auth/register
```

Request
```
{
  "user": "john",
  "pass": "pass123",
  "role": "ROLE_REQUESTER"
}
```

Response

Registered

## 🔐 2. Login

Endpoint
```
POST /auth/login

```
Request
```
{
  "user": "john",
  "pass": "pass123"
}
```

Response
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZSI6IlJPTEVfUkVRVUVTVEVSIiwiaWF0IjoxNzAwMDAwLCJleHAiOjE3MDAwMDB9.signature
```
## 📝 3. Create Request

Role: REQUESTER
Endpoint
```
POST /requests
```

Headers
```
Authorization: Bearer <TOKEN_OF_REQUESTER>
Content-Type: application/json
```

Request
```
{
  "type": "LEAVE"
}
```

Response
```
{
  "id": 1,
  "type": "LEAVE",
  "status": "IN_PROGRESS",
  "currentStep": 1,
  "createdBy": "john",
  "createdAt": "2026-02-02T10:30:00"
}
```
##  4. Get Request Details

Endpoint
```
GET /requests/1
```

Response
```
{
  "id": 1,
  "type": "LEAVE",
  "status": "IN_PROGRESS",
  "currentStep": 1,
  "createdBy": "john",
  "createdAt": "2026-02-02T10:30:00",
  "nextApproverRole": "ROLE_APPROVER"
}
```
## ✅ 5. Approve Request

Role: APPROVER or ADMIN
Endpoint
```
POST /requests/1/approve
```

Response
```
{
  "message": "Request approved successfully"
}
```
After Final Approval
```
{
  "id": 1,
  "type": "LEAVE",
  "status": "APPROVED",
  "currentStep": 2,
  "createdBy": "john",
  "createdAt": "2026-02-02T10:30:00"
}
```
## ❌ 6. Reject Request

Role: APPROVER
Endpoint
```
POST /requests/1/reject
```

Response
```
{
  "message": "Request rejected successfully"
}
```
## 📜 7. Approval History

Endpoint
```
GET /requests/history/1
```

Response
```
[
  {
    "id": 1,
    "requestId": 1,
    "action": "CREATED",
    "actionBy": "john",
    "actionAt": "2026-02-02T10:30:00"
  },
  {
    "id": 2,
    "requestId": 1,
    "action": "APPROVED_STEP_1",
    "actionBy": "manager",
    "actionAt": "2026-02-02T10:35:00"
  },
  {
    "id": 3,
    "requestId": 1,
    "action": "FINAL_APPROVAL",
    "actionBy": "admin",
    "actionAt": "2026-02-02T10:40:00"
  }
]
```
## 🚫 Error Example — Invalid Transition

Trying to approve already approved request:
```
{
  "message": "Request already approved. No further approval allowed."
}
```
## 🧪 Run Tests
mvn test

## 🔒 Security Model
Component	Purpose
JWT	Stateless authentication
PasswordEncoder	Password hashing
Spring Security	Role-based access
JwtFilter	Token validation
❗ Error Handling

All workflow errors return structured JSON:
```
{
  "message": "Invalid state transition"
}
```

Handled via global exception handler.
