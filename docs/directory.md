insurance-claim-automation/
│
├── src/
│   ├── main/
│   │
│   ├── java/com/ananya/insurance/
│   │   │
│   │   ├── config/
│   │   │      SecurityConfig.java
│   │   │      PdfConfig.java
│   │   │      VeryfiConfig.java
│   │   │
│   │   ├── controller/
│   │   │      AuthController.java
│   │   │      DashboardController.java
│   │   │      BillController.java
│   │   │      ClaimController.java
│   │   │      PdfController.java
│   │   │      TemplateController.java
│   │   │
│   │   ├── service/
│   │   │      AuthService.java
│   │   │      VeryfiService.java
│   │   │      BillService.java
│   │   │      ClaimService.java
│   │   │      PdfService.java
│   │   │      TemplateMappingService.java
│   │   │
│   │   ├── repository/
│   │   │      UserRepository.java
│   │   │      PatientRepository.java
│   │   │      InsuranceRepository.java
│   │   │      BillRepository.java
│   │   │      ClaimRepository.java
│   │   │      PdfTemplateRepository.java
│   │   │      TemplateFieldRepository.java
│   │   │
│   │   ├── entity/
│   │   │      User.java
│   │   │      Patient.java
│   │   │      InsurancePolicy.java
│   │   │      MedicalBill.java
│   │   │      Claim.java
│   │   │      PdfTemplate.java
│   │   │      TemplateField.java
│   │   │
│   │   ├── dto/
│   │   │
│   │   ├── security/
│   │   │
│   │   ├── util/
│   │   │
│   │   └── InsuranceApplication.java
│   │
│   ├── resources/
│   │
│   │   ├── templates/
│   │   │      login.html
│   │   │      register.html
│   │   │      dashboard.html
│   │   │      upload.html
│   │   │      patient.html
│   │   │      insurance.html
│   │   │      template-mapper.html
│   │   │      preview.html
│   │   │
│   │   ├── static/
│   │   │      css/
│   │   │      js/
│   │   │      images/
│   │   │
│   │   ├── pdf/
│   │   │      lic_claim_form.pdf
│   │   │
│   │   └── application.properties
│
├── uploads/
│      originalBills/
│      generatedClaims/
│
└── pom.xml