Database Schema Documentation (MVP)
users

Stores authentication information for users.

Column	Type	Description
user_id	BIGSERIAL	Primary Key
username	VARCHAR(50)	Login username (Unique)
email	VARCHAR(100)	User email
password_hash	VARCHAR(255)	Encrypted password
role	VARCHAR(20)	USER or ADMIN
created_at	TIMESTAMP	Account creation time
patients

Stores personal details and insurance information of the patient.

Column	Type	Description
patient_id	BIGSERIAL	Primary Key
user_id	BIGINT	References users table
full_name	VARCHAR(100)	Patient name
dob	DATE	Date of birth
gender	VARCHAR(20)	Gender
phone	VARCHAR(15)	Phone number
email	VARCHAR(100)	Email
address	TEXT	Residential address
pan_number	VARCHAR(10)	PAN Card
insurance_company	VARCHAR(100)	LIC
policy_number	VARCHAR(50)	Policy Number
member_id	VARCHAR(50)	Member ID
created_at	TIMESTAMP	Record creation
medical_bills

Stores every uploaded hospital or pharmacy bill.

Column	Type	Description
bill_id	BIGSERIAL	Primary Key
patient_id	BIGINT	References patients
hospital_name	VARCHAR(150)	Hospital/Pharmacy
bill_number	VARCHAR(100)	Invoice Number
bill_date	DATE	Bill Date
subtotal	NUMERIC(10,2)	Subtotal
tax	NUMERIC(10,2)	GST/Tax
total_amount	NUMERIC(10,2)	Final Amount
ocr_status	VARCHAR(20)	OCR Status
original_file_path	TEXT	Uploaded file
uploaded_at	TIMESTAMP	Upload time
bill_items

Stores all items extracted from each bill.

Column	Type	Description
item_id	BIGSERIAL	Primary Key
bill_id	BIGINT	References medical_bills
description	VARCHAR(255)	Medicine/Service description
quantity	INTEGER	Quantity
price	NUMERIC(10,2)	Price
claims

Stores generated insurance claims.

Column	Type	Description
claim_id	BIGSERIAL	Primary Key
patient_id	BIGINT	References patients
claim_number	VARCHAR(50)	Claim Number
claim_status	VARCHAR(20)	Draft/Submitted
generated_pdf	TEXT	Generated PDF path
created_at	TIMESTAMP	Claim creation