# Insurance Claim Management System

A web-based insurance claim management application built with **Java and
Spring Boot**. The system helps patients manage their insurance
information, upload medical bills, extract bill data using OCR, review
and select bill items, organize claims, maintain a medical bill
repository, and generate claim PDFs.

## Features

-   User authentication with Spring Security
-   Patient profile management
-   Medical bill upload
-   OCR-based bill data extraction using the Veryfi API
-   Automatic extraction of bill details and line items
-   Manual review and editing of extracted bill data
-   Individual bill-item selection for claims
-   Dynamic selected-amount calculation
-   Medical bill repository
-   Claim creation and claim history
-   Uploaded bill and generated PDF viewing
-   Custom claim PDF generation
-   Light/dark theme
-   REST endpoints for claims and bill items

## Technology Stack

  Layer             Technology
  ----------------- ----------------------------------
  Language          Java
  Backend           Spring Boot
  Web               Spring MVC
  Frontend          Thymeleaf, HTML, CSS, JavaScript
  Database          PostgreSQL
  ORM               Spring Data JPA / Hibernate
  Authentication    Spring Security
  OCR               Veryfi API
  Build Tool        Maven
  Version Control   Git / GitHub

## Application Workflow

``` text
User Login
    ↓
Patient Profile
    ↓
Upload Medical Bill
    ↓
Veryfi OCR Processing
    ↓
Structured Bill + Bill Items
    ↓
Review / Edit Extracted Data
    ↓
Select Claimable Items
    ↓
Save Bill
    ↓
Save Claim
    ↓
Claim History
    ↓
Generate Custom PDF
```

## Screenshots

### Login

![Login](screenshots/login.png)

### Dashboard

![Dashboard](screenshots/dashboard.png)

### Patient Profile

![Profile](screenshots/profile.png)

> Place the screenshots in a `screenshots/` directory at the project
> root using the filenames shown above.

Recommended project structure:

``` text
project-root/
├── screenshots/
│   ├── login.png
│   ├── dashboard.png
│   └── profile.png
├── src/
├── pom.xml
├── README.md
└── ...
```

## Architecture

The application follows a layered Spring architecture:

``` text
                Browser
                   │
                   ▼
          Thymeleaf / JavaScript
                   │
                   ▼
             Controllers
                   │
                   ▼
               Services
              /        \
             ▼          ▼
       Repositories   OCRService
             │          │
             ▼          ▼
        PostgreSQL    Veryfi API
```

### Main Layers

**Controller Layer**\
Handles HTTP requests, form submissions, redirects, and API requests.

**Service Layer**\
Coordinates application and business logic.

**Repository Layer**\
Provides database access using Spring Data JPA.

**Entity Layer**\
Represents persistent domain objects such as patients, bills, bill
items, and claims.

**DTO Layer**\
Represents data exchanged with the OCR integration.

**OCR Integration**\
Encapsulates communication with Veryfi so that the rest of the
application is independent of the external API response format.

## Main Domain Objects

### Patient

Stores personal and insurance information:

-   Full name
-   Date of birth
-   Gender
-   Phone
-   Email
-   Address
-   PAN number
-   Insurance company
-   Policy number
-   Member ID

### MedicalBill

Represents an uploaded medical bill and its extracted bill-level
information.

Typical data includes:

-   Hospital / pharmacy
-   Bill number
-   Bill date
-   Subtotal
-   Total amount
-   Original file reference
-   Upload timestamp
-   Bill status
-   Patient
-   Claim
-   Bill items

### BillItem

Represents an individual line item extracted from a medical bill.

-   Description
-   Quantity
-   Price
-   Selected state
-   Parent medical bill

### Claim

Represents a saved insurance claim.

-   Claim ID
-   Claim number
-   Claim status
-   Patient
-   Creation timestamp
-   Generated PDF reference
-   Associated medical bills

## Veryfi OCR Integration

When a medical bill is uploaded, the application sends the document to
Veryfi through `OCRService`.

The returned document information is mapped into application DTOs:

``` text
Uploaded File
      ↓
OCRService
      ↓
Veryfi API
      ↓
OCRBillResponse
      ↓
OCRBillItem[]
      ↓
MedicalBill + BillItem entities
      ↓
PostgreSQL
```

Veryfi credentials should be provided through environment variables and
must not be committed to the repository.

Example configuration:

``` properties
veryfi.client-id=${VERYFI_CLIENT_ID}
veryfi.username=${VERYFI_USERNAME}
veryfi.api-key=${VERYFI_API_KEY}
```

## Bill Selection

The dashboard allows the user to select individual medical bill items.

The selected amount is calculated as:

``` text
Selected Amount =
Σ (quantity × price)
for every selected item
```

The browser calculates the amount for immediate UI feedback. The backend
persists the selected state and edited item values when the bill is
saved.

For a production claim-submission workflow, the amount should also be
recalculated and validated on the server.

## Medical Bill Repository

The repository provides a persistent view of uploaded documents,
including:

-   Bill information
-   Upload date/time
-   Claim association
-   View Bill action

The application serves stored documents through backend-controlled
endpoints rather than exposing raw filesystem paths.

## PDF Generation

The application generates a custom claim PDF from persisted claim,
patient, bill, and selected-item information.

The current implementation uses a custom PDF layout. Insurer-specific
PDF mapping can be added later by mapping application fields to the
required insurer form fields or coordinates.

## Configuration

The application requires:

1.  Java
2.  Maven
3.  PostgreSQL
4.  Database configuration
5.  Veryfi API credentials

Example environment variables:

``` text
VERYFI_CLIENT_ID
VERYFI_USERNAME
VERYFI_API_KEY
```

Keep credentials outside Git and add local secret/configuration files to
`.gitignore`.

## Running the Application

Clone the repository and enter the project directory:

``` bash
git clone <repository-url>
cd <project-directory>
```

Configure the PostgreSQL connection and Veryfi environment variables.

Build the application:

``` bash
mvn clean install
```

Run the Spring Boot application:

``` bash
mvn spring-boot:run
```

Then open the application in a browser using the configured server
address.

## API Endpoints

The application also provides REST endpoints for domain resources.

### Claims

``` text
GET    /api/claims
GET    /api/claims/{id}
POST   /api/claims
DELETE /api/claims/{id}
```

### Bill Items

``` text
GET    /api/bill-items
GET    /api/bill-items/{id}
POST   /api/bill-items
DELETE /api/bill-items/{id}
```

## Security Considerations

The current application uses Spring Security for authentication.

For production deployment, additional protections should include:

-   Authorization checks ensuring users can access only their own
    records
-   File type and file size validation
-   Safe server-side upload filenames
-   Path traversal protection
-   Secure document storage
-   Server-side financial calculations
-   Secure secret management
-   CSRF protection for browser forms
-   Transactional handling of multi-entity operations

## Project Status

The application currently provides an end-to-end prototype workflow
covering:

-   Authentication
-   Patient profile management
-   Medical bill upload
-   OCR integration
-   Bill review and editing
-   Bill-item selection
-   Medical bill repository
-   Claim management
-   Claim history
-   Custom PDF generation

Potential future extensions include insurer-specific claim form mapping,
stronger authorization, cloud document storage, asynchronous OCR
processing, audit history, and a more detailed claim-status workflow.

## License

This project is intended for educational and project-development
purposes.
