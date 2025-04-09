# Project Title: Android Expense Management App (Java + SQLite)

---

## Project Requirements

### 1. User Authentication
- Implement user registration and login using SQLite.
- Store user credentials securely (hashed passwords).
- Maintain user sessions for persistent login.

### 2. Transactions Management
- Users can add, edit, and delete transactions.
- Each transaction must include:
  - Amount (double, required)
  - Notes (text, optional)
  - Transaction category (linked to category table)
  - Date (timestamp, required)
  - Type (Expense or Income)

### 3. Budget Management
- Users can add, edit, and delete budgets.
- Each budget must include:
  - Budget category (same as transaction category)
  - Amount
  - Date range (fixed periods: weekly, monthly, yearly)
- Link transactions within the budget’s time range and category.

### 4. Category Management
- Users can add, edit, and delete categories.
- Each category must be classified as either Expense or Income.
- Predefined categories:
  - Expense: Food, Electronics, Health, etc.
  - Income: Salary, Bonus, Gift, etc.

### 5. Data Storage and Relations (SQLite)
- Users Table: Stores user information.
- Transactions Table: Stores all transactions.
- Budgets Table: Stores budget details.
- Categories Table: Stores predefined and user-created categories.
- Relationships:
  - A user can have multiple transactions and budgets.
  - A budget can include multiple transactions.
  - A transaction can belong to multiple budgets.

### 6. User Interface (UI/UX)
- Intuitive, easy-to-use UI.
- Main screens:
  - Login/Register Screen
  - Dashboard: Shows summary of transactions and budgets.
  - Transaction Management: Lists transactions grouped by date.
  - Budget Management: Lists budgets grouped by period (weekly, monthly, yearly).
  - Transaction Details: Displays transaction details.
  - Budget Details: Displays budget details, including:
    - Suggested daily spending.
    - Projected daily spending.
    - Actual daily spending.

### 7. Features & Functionalities
- Vietnamese Dong (VND) as currency.
- Transaction filtering by category and date.
- Transaction grouping by creation date.
- Budget details showing related transactions.
- Real-time budget tracking with progress indicators.

### 8. Development Stack
- Language: Java
- Database: SQLite (Android Room)
- UI Framework: Android Jetpack (ViewModel, LiveData)
- Authentication: SQLite-based authentication
- State Management: ViewModel + LiveData
- Navigation: Jetpack Navigation Component

---

## Next Steps
1. Database Schema Design – Define SQLite tables and relationships.
2. Authentication Implementation – Build user authentication with hashed passwords.
3. CRUD Operations – Implement transactions, budgets, and categories.
4. UI Design – Develop a simple, intuitive UI.
5. Data Binding & ViewModel – Implement real-time updates.
6. Testing & Optimization – Ensure smooth performance and bug-free experience.
