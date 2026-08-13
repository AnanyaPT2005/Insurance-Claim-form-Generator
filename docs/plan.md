Since this is a portfolio project, here's the order I'd follow. Each step builds on the previous one and gives you a working application at every stage.

---

# Phase 1 – Project Setup

### Step 1

Create a Spring Boot project.

Dependencies:

* Spring Web
* Spring Data JPA
* PostgreSQL Driver
* Spring Security
* Thymeleaf
* Validation
* Lombok (optional)
* DevTools (optional)

---

### Step 2

Create PostgreSQL database.

```sql
CREATE DATABASE insurance_db;
```

---

### Step 3

Connect Spring Boot to PostgreSQL.

Update `application.properties`.

---

### Step 4

Create all database tables.

Verify:

```sql
SELECT * FROM users;
```

---

# Phase 2 – Backend

### Step 5

Create Entity classes.

```
User
Patient
MedicalBill
BillItem
Claim
```

---

### Step 6

Create Repository interfaces.

```
UserRepository

PatientRepository

MedicalBillRepository

BillItemRepository

ClaimRepository
```

---

### Step 7

Create Services.

```
UserService

PatientService

BillService

ClaimService
```

---

### Step 8

Create Controllers.

```
AuthController

DashboardController

BillController

ClaimController
```

---

# Phase 3 – Authentication

### Step 9

Implement:

* Register
* Login
* Logout

Use Spring Security.

Passwords should be stored using BCrypt.

---

# Phase 4 – Frontend

### Step 10

Create pages.

```
Login

Register

Dashboard

Patient Profile

Medical Bills

Upload Bill

Claims

Generate PDF
```

---

### Step 11

Dashboard should contain:

```
Patient Details

↓

Insurance Details

↓

Medical Bill Repository

↓

Generate Claim

↓

Download PDF
```

---

# Phase 5 – OCR

### Step 12

Integrate Veryfi API.

Workflow:

```
Upload Bill

↓

Veryfi

↓

Extract JSON

↓

Save Medical Bill

↓

Save Bill Items
```

---

### Step 13

Display extracted bill.

Allow user to edit OCR mistakes before saving.

---

# Phase 6 – Claim Generation

### Step 14

Create claim.

User selects bills.

↓

Create Claim.

---

### Step 15

Generate PDF.

For now,

use Apache PDFBox

↓

hardcoded coordinates

↓

working LIC claim form

---

# Phase 7 – Option C

### Step 16

Build the visual PDF mapper.

Workflow:

```
Upload Blank LIC Form

↓

Display PDF

↓

Click a box

↓

Assign

Patient Name

↓

Save Position
```

---

### Step 17

Instead of hardcoded coordinates:

```
Read saved mapping

↓

Fill fields

↓

Generate PDF
```

---

# Phase 8 – Polish

### Step 18

Add:

* Search bills
* Delete bills
* Edit bills
* View uploaded image

---

### Step 19

Improve UI.

---

### Step 20

Deploy.

---

## Final Architecture

```
HTML/CSS/JS
        │
        ▼
Spring Boot Controllers
        │
        ▼
Service Layer
        │
        ▼
Repositories
        │
        ▼
PostgreSQL
        │
        ▼
Veryfi API
        │
        ▼
PDFBox
        │
        ▼
Generated LIC Claim Form
```

This order minimizes rework and lets you test each layer before adding the next feature.


## TO DO Later:

- Make checked status true by default for each bill item.
- generate the claim number.
- make pdf proper
