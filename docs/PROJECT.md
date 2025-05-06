# README

# **Project Title: LoanLift – Crowdfunded Investment Platform**

### **Concept Overview**

**LoanLift** is a backend API for a crowdfunded loan platform. Users can create or support business loan campaigns, earning monthly repayments with interest. The system enforces campaign approval, fee deduction, and automatic repayment handling.

**LoanLift** is a backend API that enables **users with bank accounts to collectively fund campaigns** that are structured as **loan requests**, not donations.
Each campaign is an **investment opportunity**, where contributors (lenders/pledgers) receive **monthly repayments with interest** from the campaign account.

**A bank representative must approve each campaign**, and **fees are charged monthly** to campaigns for processing.

---

## **Core Features (6+)**

1. **User Registration & Login**
2. **Campaign Creation and Management**
3. **Pledging to a Campaign (Investing)**
4. **Account management**
5. **Transaction history for contributions and refunds**
6. **Campaign success/failure logic + automatic fund handling**
7. **Admin moderation and approval of campaigns (optional)**
8. **Portfolio tracking for users to view earnings and pledge status**

---

## **Core Data Models and Relationships**

### 1. `User`

* `id` (PK)
* `civil_id` (unique)
* `username` (unique)
* `email` (unique)
* `password` (string)
* `created_at` (datetime)
* `updated_at` (datetime)
* `is_active` (boolean)

#### Relationships:

* OneToOne with `Profile`
* OneToMany with `Campaign`
* OneToMany with `Pledge`
* OneToMany with `Accounts`
* OneToMany with `Comments`
* OneToMany with `User_Roles`

---

### 2. `Role`

* `id` (PK)
* `name` (unique)

#### Relationships:

* OneToMany with `User_Roles`

---

### 3. `User_Roles`

* `id` (PK)
* `user_id` (FK)
* `role_id` (Fk)


#### Relationships:

* ManyToOne with `User`
* ManyToOne with `Role`

---

### 4. `Profile`
* `id` (PK)
* `user_id` (FK)
* `first_name` (string)
* `last_name` (string)
* `date_of_birth` (datetime)
* `nationality` (datetime)
* `salary` (decimal)

#### Relationships:
* OneToOne with `User`

---

### 5. `Account`

* `id` (PK)
* `name` (string)
* `balance` (decimal)
* `type` (`user`, `campaign`)
* `user_id` (FK → User, nullable)
* `campaign_id` (FK → Campaign, nullable)
* `is_primary` (boolean)
* `is_active` (boolean)
* `created_at` (datetime)

#### Ownership Logic:
- If `type = personal` → `user_id` must be non-null.
- If `type = campaign` → `campaign_id` must be non-null.
- One account **must not** belong to both.

#### Relationships:

* ManyToOne with `User` (nullable)
* ManyToOne with `Campaign` (nullable)
* OneToMany with `Transaction`

#### **NOTES**
* One of user_id or campaign_id must be set, but not both.
* Type should match the non-null FK (user ↔️ user_id, campaign ↔️ campaign_id).


---

### 6. `Category`

* `id` (PK) 
* `name` (string)

#### Relationships:

* OneToMany with `Transaction`
* OneToMany with `Campaign`

--- 

### 7. `Campaign`

* `id` (PK)
* `business_category` (FK -> Category)
* `title`
* `description`
* `goal_amount`
* `amount_raised` (calculated)
* `interest_rate`
* `repayment_months`
* `monthly_installment` (calculated)
* `status` (`new`, `pending`, `active`, `funded`, `failed`, `completed`, `defaulted`, `rejected`, `deactivated`)
* `extra_docs` (JSON or file metadata — for bank rep to review)
* `submitted_at`
* `approved_by` (admin user FK)
* `deadline` (timestamp)

#### Relationships:

* ManyToOne with `User`
* ManyToOne with `Category`
* OneToMany with `Pledge`
* OneToOne with `Account`
* OneToMany with `Comment`
* OneToMany with `Repayment`
* OneToMany with `File`

#### Status Enums

| Status        | Description                                        |
| ------------- | -------------------------------------------------- |
| `new`         | Draft, editable by creator                         |
| `pending`     | Submitted, awaiting admin approval                 |
| `rejected`    | Rejected by admin                                  |
| `active`      | Live and accepting pledges                         |
| `funded`      | Goal reached or deadline hit with goal met         |
| `failed`      | Deadline hit, but goal not met — refunds triggered |
| `completed`   | All repayments finished                            |
| `defaulted`   | Missed 3+ repayments                               |
| `deactivated` | Manually deactivated                               |


---

### 8. `Pledge`

* `id` (PK)
* `user_id` (FK)
* `account_id` (FK)
* `campaign_id` (FK)
* `amount` (decimal)
* `created_at` (datetime)
* `updated_at` (datetime)
* `status` enum('pledged', 'withdrawn', 'refunded', 'committed')
* `committed_at`: datetime (optional)
* `withdrawn_at`: datetime (optional)


#### Relationships:

* ManyToOne with `User`
* ManyToOne with `Campaign`


#### Status Enums

| Status        | Description                                        |
| ------------- | -------------------------------------------------- |
| `pledged`     | Initial pledge status pre-deadline                 |
| `withdrawn`   | Submitted, pre-deadline                            |
| `refunded`    | Rejected by admin                                  |
| `committed`   | Funding amount sent to the campaign                |


---

### 9. `Transaction`

* `id` (PK)
* `from_account_id` (FK)
* `to_account_id` (FK)
* `amount` (decimal)
* `type` (`pledge`, `repayment`, `fee`, `topup`, etc.)
* `reference_id` 
* `reference_type` (e.g., pledge, repayment)
* `timestamp`
* `category_id` (FK -> Category)


#### Relationships:

* `from_account_id` and `to_account_id` are ManyToOne with `Account`
* ManyToOne with `Repayment`
* ManyToOne with `Pledge`
* ManyToOne with `Category`

---

### 12. `Portfolio`

* `id` (PK)
* `user_id` (FK → User, required)
* `linked_account_id` (FK → Account, optional)
* `total_pledged` (decimal, derived)
* `total_received` (decimal, derived)
* `expected_total` (decimal, derived)
* `created_at` (timestamp)
* `updated_at` (timestamp)

#### Purpose:
Represents the **investment portfolio of a single user**, optionally tied to a default account for receiving repayments.

#### Notes:
- `linked_account_id` is the account to which repayments are typically distributed, but transactions still refer to actual `to_account_id`.
- `total_pledged`, `total_received`, and `expected_total` can be denormalized and updated by background jobs or on new pledges/repayments.

---

### 13. `PortfolioEntry`

* `id` (PK)
* `portfolio_id` (FK → Portfolio)
* `campaign_id` (FK → Campaign)
* `pledge_id` (FK → Pledge)
* `total_pledged` (decimal)
* `total_received` (decimal)
* `expected_total` (decimal)
* `next_expected_payment` (decimal, optional)
* `status` (`active`, `completed`, `defaulted`, `refunded`, `pledged`)
* `created_at` (timestamp)
* `updated_at` (timestamp)

#### Relationships:

* ManyToOne with `Portfolio`
* ManyToOne with `Campaign`
* ManyToOne with `Pledge`

#### Purpose:
Tracks the **relationship between a user’s portfolio and a specific campaign pledge**. It is updated monthly after repayment distribution.

#### Notes:
- `status` mirrors the campaign lifecycle (`completed`, `defaulted`, etc.) from the user's perspective.
- `expected_total = total_pledged + (interest over time)` is calculated from campaign’s repayment plan.
- `next_expected_payment` updates based on future repayment schedule.
- When a campaign fails and is refunded, status becomes `refunded`.

--- 

### 10. `Repayment`

* `id`
* `campaign_id`
* `due_date`
* `status` (`pending`, `paid`, `late`)
* `total_amount_due`
* `bank_fee`
* `amount_distributed`
* `created_at`

Each repayment tracks **monthly distribution** of funds + **bank fee deduction**.

---

### 11. `File`

* `id`
* `campaign_id`
* `media_type`
* `url`
* `created_at`
* `is_public`
* `verified` (boolean) — For documents that require admin verification
* `uploaded_by` (FK → User)


#### Relationships:

* ManyToOne with `Campaign`

#### MediaType Enum Values

| Enum Value              | Description                                    |
| ----------------------- | ---------------------------------------------- |
| `BUSINESS_PLAN`         | Business plan PDF/doc for a campaign           |
| `ID_DOCUMENT`           | Identity document (e.g., civil ID, passport)   |
| `INCOME_STATEMENT`      | Uploaded income or salary proof                |
| `FINANCIAL_REPORT`      | Optional financial reports or forecasts        |
| `LEGAL_DOCUMENT`        | Contracts, terms, or legal declarations        |
| `PROFILE_IMAGE`         | User or campaign profile image                 |
| `CAMPAIGN_BANNER`       | Campaign cover/banner image for display        |
| `GALLERY_IMAGE`         | Additional campaign visuals (photo gallery)    |
| `PROMO_VIDEO`           | Video pitch or promotional content             |
| `TERMS_OF_USE`          | Static platform terms (could be shown in app)  |
| `LOAN_AGREEMENT`        | Agreement document between lender and borrower |
| `BANK_VERIFICATION_DOC` | Uploaded bank forms or verification docs       |
| `OTHER`                 | Any uncategorized or miscellaneous file        |

---
### 12. `Comment`

* `id`
* `created_by`
* `campaign_id`
* `created_at`
* `message`

--- 

### 13. `Reply`

* `id`
* `comment_id`
* `created_at`
* `message`

--- 

## **API Endpoints By Service**

### **Authentication Service**

* `POST /api/v1/auth/register` → Register new user
* `POST /api/v1/auth/login` → Authenticate and returns a token pair
* `POST /api/v1/auth/token/check-token` → Validates the token and returns user id
* `POST /api/v1/auth/token/refresh` → Validates the token and returns a new token pair

**DB:** User, Role, UserRoles
**Security:** Issues and validates JWTs, handles password hashing.

---

### **Account Service**

* `GET /api/v1/accounts/account/:accountId` → Get account details
* `POST /api/v1/accounts/transfer` → Transfer funds between accounts
* `GET /api/v1/accounts/clients/:userId/transactions` → View transaction history

---

### **Campaign Service**

* `POST /api/v1/campaigns` → Create new campaign request (with required data/docs)
* `GET /api/v1/campaigns` → List all approved campaigns
* `GET /api/v1/campaigns/campaign/:campaignId` → Campaign details
* `PUT /api/v1/campaigns/campaign/:campaignId` → Update (if before approval)
* `DELETE /api/v1/campaigns/:campaignId` → Delete own campaign (before approval)
* `GET /api/v1/campaigns/campaign/:campaignId/pledges` → View all pledges for campaign based on id
* `GET /api/v1/campaigns/campaign/:campaignId/repayments` → Repayment schedule
* `GET /api/v1/admin/campaigns/review` → List campaigns needing review (admin only)
* `POST /api/v1/admin/campaigns/:campaignId/decision` → Approve or reject campaign

---

### **Profile Service**
* `GET /api/v1/profile/:userId` → Get profile info for a user
* `PUT /api/v1/profile/:userId` → Update user profile (name, nationality, salary, etc.)
* `POST /api/v1/profile/:userId/verify` → Upload verification info (optional KYC)
* `GET /api/v1/profile/:userId/accounts` → Get user’s accounts linked to profile

---

### **Pledging**

* `GET /api/v1/pledge/` → View user’s pledges
* `DELETE /api/v1/pledge/pledge/:pledgeId` → Allow users to cancel pledges before campaign closes
* `POST /api/v1/pledge/campaign/:id/pledge` → Lender pledges from an account to campaign
* `POST /api/v1/pledge/campaign/:id/repayments/run` → Trigger monthly repayment (admin or cron) - might not have this
* `GET /api/v1/repayments/:campaignId/distributions` → Admin can view distributions to accounts by campaign

#### Notes:
* POST /api/v1/pledge/campaign/:id/pledge
Creates a pledge, immediately deducts funds from user account, and:

Creates a Transaction

Automatically creates or updates the user’s Portfolio and PortfolioEntry

Returns 400 if campaign is not active, or if deadline passed, or balance is insufficient

Withdrawals are only allowed if the campaign is still active and before its deadline

---

### **Portfolio Service**

* `GET /api/v1/portfolio/:userId` → View user’s portfolio overview
* `GET /api/v1/portfolio/:userId/campaign/:campaignId` → View campaign-specific performance
* `GET /api/v1/portfolio/:userId/earnings` → View total earnings across all campaigns
* `GET /api/v1/portfolio/earnings` → Return total repaid amounts to user across all pledges

---

### **File Service**

* `POST /api/v1/files/upload` → Upload one or more files tied to a campaign (e.g., business plan, documents)
* `GET /api/v1/files/:fileId` → Retrieve file metadata (and secure download URL, if applicable)
* `GET /api/v1/files/campaign/:campaignId` → List all files for a given campaign
* `PATCH /api/v1/files/:fileId/public` → Toggle file visibility (admin only or user-specific)
* `DELETE /api/v1/files/:fileId/delete` → Delete file (only if campaign is not yet approved)

---

## Flows

### **Campaign Lifecycle Logic**

1. Authenticated user creates a campaign and uploads necessary 
information/documents (e.g., income, plan) → status: `new`
1. Campaign creator can update while campaign `status = new` 
1. A bank admin reviews campaign and `status = pending` 
1. Admin reviews:
   - If accepted → status: `active`
   - If rejected → status: `rejected`
1. While active:
   - Users can pledge until the `deadline`
   - Pledges are financially committed upon creation
   - Users may cancel their pledges before the campaign deadline
   - Campaign can become `funded` (if goal met before deadline)
1. On deadline:
   - If `amount_raised >= goal_amount` → status: `funded`, funds transferred to campaign account
   - Else → mark campaign as `failed`, refund pledges
1. During repayment period:
   - Campaign progresses toward `completed`
   - If 3+ repayments missed → status: `defaulted`
1. Admin or user may manually mark campaign as `deactivated` (no further activity)

You can run a **scheduled background job** (cron) to evaluate campaigns post-deadline.


##  **Business Logic & Repayment Mechanics**


### Monthly Repayment Cycle

Each month:

1. Campaign account is debited:
   `monthly_installment = (loan + interest) / repayment_months`
2. From this:
   * Bank takes a fixed fee (e.g., 2% of the installment)
   * Remaining funds are distributed proportionally to all pledgers
   * Each distribution updates the corresponding `PortfolioEntry` record:
     - `total_received` is incremented
     - `next_expected_payment` is recalculated


---

### Example Distribution

* Campaign owes \$1,000 this month
* Bank fee = \$50
* Remaining \$950 is distributed:

  * If Alice pledged 10% of total funds, she receives \$95
> Portfolio Impact: Alice’s `PortfolioEntry` for this campaign is updated:
> - `total_received += $95`
> - `next_expected_payment` updated based on remaining term

---


## **Business Logic & Repayment Mechanics**

### Monthly Repayment Cycle

Each month:

1. The system identifies campaigns with a `repayment` due and a `status` of `funded` or `active`.
2. The campaign's **linked account** is debited for the `monthly_installment`, calculated as:

```

monthly\_installment = (goal\_amount + total\_interest) / repayment\_months

````
3. The system performs:

* **Bank fee deduction** (e.g., 2%)
* **Proportional fund distribution** to all pledgers based on their pledge ratio
* **Transaction recording** for each transfer
* **Portfolio updates** for each pledger via their `PortfolioEntry`:

  * `total_received` is incremented
  * `next_expected_payment` is recalculated

---

### Example Distribution

* Campaign installment due: **\$1,000**
* Bank fee: **\$50**
* Amount available for pledgers: **\$950**

If Alice pledged **10%** of the total, she receives **\$95**

> **Portfolio Impact** for Alice:
>
> * `PortfolioEntry.total_received += $95`
> * `next_expected_payment` is updated using repayment schedule

---

### Repayment Flow Scenarios

Each month, the system processes repayment flows per campaign:
Portfolio entries are already available and “live” before the first repayment occurs, thanks to immediate pledge commitment.

---

#### 1. **Sufficient Funds**

* Campaign account holds full installment amount.
* System performs:

* Full deduction from campaign account
* Full distribution to pledgers
* Deduction of full bank fee
* All transactions are recorded
* `Repayment.status = paid`
* Each related `PortfolioEntry` is updated with:

 * `total_received += allocated_amount`
 * `next_expected_payment` recalculated

---

#### 2. **Partial Funds**

* Campaign account holds **some**, but **not all** of the installment.
* System performs:

* Partial deduction and proportional lender distribution
* Bank fee deducted proportionally or skipped based on config
* `Repayment.status = late`
* Shortfall logged, and `missed_repayment_count` incremented
* Each `PortfolioEntry` receives a proportional update:

 * `total_received += partial_amount`

---

#### 3. **No Funds**

* Campaign account has **zero** or negligible balance.
* System performs:

* No deductions or distributions
* `Repayment.status = late`
* `missed_repayment_count` incremented
* Campaign creator notified
* Portfolio is **not** updated

---

#### 4. **Three or More Missed Payments**

* If a campaign hits **3 missed repayments**:

* Campaign `status` is set to `defaulted`
* All pledgers notified
* Admin panel flagged for potential follow-up
* Related `PortfolioEntry.status` is updated to `defaulted`

---

#### 5. **Inactive Campaign Account**

* Campaign account is inactive (e.g., frozen, flagged).
* System performs:

* Repayment cycle skipped for the campaign
* Admins alerted and log entry created
* No updates made to `Repayment` or `Portfolio`

---

### Portfolio Tracking Logic

The `Portfolio` and `PortfolioEntry` models ensure that investment tracking is transparent and accurate:

* Each time a repayment is processed:

* `PortfolioEntry.total_received` increases
* `next_expected_payment` recalculates using future schedule
* On default:

* `PortfolioEntry.status` is updated to `defaulted`
* On full repayment:

* `PortfolioEntry.status` becomes `completed`
* These records are exposed via the **Portfolio API** so users can view:

* Total pledged and received
* Earnings per campaign
* Forecasted returns

---

## **Overview of Services**

| Service Name           | Responsibility Summary                               |
| ---------------------- | ---------------------------------------------------- |
| `auth-service`         | User & admin authentication, registration, JWTs      |
| `profile-service`      | User profile management and KYC data                 |
| `account-service`      | Account creation, balance management, transfers      |
| `campaign-service`     | Campaign creation, status tracking, admin approval   |
| `pledge-service`       | Pledge management, validation, linking to accounts   |
| `repayment-service`    | Monthly repayment processing, fund distribution      |
| `portfolio-service`    | Tracks user earnings per campaign/pledge             |
| `transaction-service`  | Records all financial activity and references        |
| `notification-service` | Email/SMS/in-app alerts, status updates, error flags |
| `log-service`          | System-wide structured logging and traceability      |
| `file-service`         | Campaign/user file uploads and metadata management   |
| `scheduler-service`    | Cronjob-triggered events like monthly repayment due  |


---


## **Event-Driven Communication (Sample Events)**

| Event                   | Emitted By        | Consumed By                             | Notes                                     |
| ----------------------- | ----------------- | --------------------------------------- | ----------------------------------------- |
| `campaign.created`      | campaign-service  | file-service                            | Attachments, approval routing             |
| `campaign.fully_funded` | pledge-service    | repayment-service                       | Starts repayment scheduling               |
| `repayment.due`         | scheduler-service | repayment-service                       | Triggers monthly repayment cycle          |
| `repayment.successful`  | repayment-service | portfolio-service, transaction-service  | Update portfolio and record transactions  |
| `repayment.failed`      | repayment-service | notification-service, log-service       | Trigger alert to campaign owner           |
| `campaign.defaulted`    | repayment-service | notification-service, portfolio-service | Notify lenders, flag entries              |
| `pledge.confirmed`      | pledge-service    | transaction-service, portfolio-service  | Tracks pledge and creates portfolio entry |
| `account.balance_low`   | account-service   | notification-service                    | Warn user of low balance                  |
| `file.uploaded`         | file-service      | campaign-service                        | Attach to campaign and verify             |
| `user.registered`       | auth-service      | profile-service                         | Auto-generate profile on signup           |
| `log.error`             | any service       | log-service                             | Captures traceable structured error logs  |


---

## What Data Should Be Sent to the Monitoring Service

### **Normal Operation Logs**

Captured at INFO or DEBUG level:

* **Service name** (e.g., `account-service`)
* **Timestamp**
* **Request ID / Trace ID** (for tracing across services)
* **User ID / Account ID** (if applicable)
* **HTTP Method & Endpoint** (e.g., `POST /accounts/topup`)
* **Status Code** (e.g., `200`)
* **Response time (ms)**
* **Payload summary** (not full data, for performance/security)
* **Message** (e.g., `Top-up request processed successfully`)

### **Error/Exception Logs**

Captured at ERROR or WARN level:

* All of the above, **plus**:
* **Error message**
* **Stack trace**
* **Service method/function name**
* **Database query or external API called**, if relevant
* **Exception type** (e.g., `ValidationError`, `TimeoutException`)

---

## Logging Format Recommendation

Use **structured logs** in JSON format for parsing and indexing. Example:

```json
{
"level": "ERROR",
"timestamp": "2025-05-01T12:04:32Z",
"service": "repayment-service",
"trace_id": "abc123",
"user_id": "u5678",
"endpoint": "POST /repayments/run",
"message": "Repayment execution failed",
"error": "InsufficientFundsException",
"stack_trace": "...",
"status_code": 500
}
````

Logs should include `trace_id` headers passed between services for distributed tracing (OpenTelemetry or similar recommended).
