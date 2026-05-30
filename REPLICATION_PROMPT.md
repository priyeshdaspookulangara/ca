# Prompt for Creating Chitty Collection Android App

## Objective
Create a native Android application using Kotlin and Jetpack Compose for managing Chitty (Chit Fund) collections. The app should allow agents to manage multiple chitty groups, track member payments, record collections, and handle dividends.

## Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Local Database:** Room Persistence Library
- **Architecture:** MVVM (Model-ViewModel-Intent/State)
- **Dependency Injection:** Manual or Hilt (Manual is currently used in the reference)
- **Data Parsing:** Gson
- **Navigation:** Navigation Compose
- **Concurrency:** Kotlin Coroutines and Flow

## Data Models
1.  **AgentDetails:** `agent_id`, `agent_name`, `contact_number`.
2.  **AgentCredential:** `agent_id`, `name`, `loginname`, `passwordHash`.
3.  **ChittyGroup:** `chitty_id`, `chitty_name`, `chitty_amount`, `installment_amount`, `total_members`.
4.  **Member:** `member_id`, `chitty_id`, `member_name`, `contact_number`, `due_date`.
5.  **Collection:** `transaction_id`, `chitty_id`, `member_id`, `collection_amount`, `payment_status`, `payment_method`, `timestamp`, `notes`.
6.  **Dividend:** `dividend_id`, `chitty_id`, `dividend_amount`, `term_date`, `defaulters_ineligible`.

## Key Features & Screen Requirements

### 1. Login Screen
- Local authentication using `loginname` and `password`.
- Passwords should be hashed using SHA-256.
- Redirect to Home Screen upon successful login.

### 2. Home Screen (Chitty Groups)
- Display a list of Chitty Groups.
- Search functionality to filter groups by name.
- Dropdown menu for:
    - **Settings:** For data management and configuration.
    - **View Defaulters:** To see a list of all members who missed payments.
- **Dividend Management Section:**
    - Select a Chitty Group.
    - Input Dividend Amount.
    - Toggle "Dividend Not Applicable to Defaulters".
    - Save dividend for the current term (YYYY-MM).

### 3. Chitty Details Screen
- Display members belonging to the selected Chitty Group.
- Show member details: Name, Contact, Due Date, and Pending Amount.
- Highlight "Defaulter" status if the member hasn't paid for the most recent due date.
- Clicking a member opens a dialog with options: "Record Collection" or "View History".

### 4. Record Collection Screen
- Automatically calculate the "Amount to Pay": `installment_amount - dividend_amount`.
- If "Defaulters Ineligible" is true and the member is a defaulter, the full `installment_amount` is charged.
- Fields: Amount (pre-filled), Payment Method (Cash/Bank Transfer), Payment Status (Paid/Pending), Notes.
- Save collection with a unique `transaction_id` and `timestamp`.

### 5. Payment History Screen
- List all collections recorded for a specific member.
- Display amount, status, method, date, and notes.

### 6. Defaulters Screen
- List all members identified as defaulters across all chitty groups.

### 7. Settings Screen
- **Upload Initial Data:** Import a JSON file containing `AgentDetails` and `ChittyGroups` (with members).
- **Upload Agent Data:** Import a JSON file containing agent login credentials.
- **Download Collections:** Export recorded collections within a selected date range to a JSON file in the Downloads folder.
- **API Sync Mode:** A toggle and URL field for future API integration.

## Logic Specifications
- **Defaulter Logic:** A member is a defaulter if there is no "Paid" collection matching the most recent due date (up to today).
- **Pending Amount:** Sum of all "Pending" collections for a member.
- **Data Import:** Use `ContentResolver` to read JSON files selected via file picker.
- **Data Export:** Use `MediaStore` to save the exported JSON to the `Downloads` directory.

## Implementation Details
- Use `Flow` for reactive UI updates from the Room database.
- Use `StateFlow` in ViewModels to manage UI state.
- Implement a `Repository` pattern to abstract data sources.
- Ensure proper foreign key constraints in the Room database (e.g., `Member` belongs to `ChittyGroup`).
- Use Material Design 3 components.
