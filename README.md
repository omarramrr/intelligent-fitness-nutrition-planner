🎯 Intelligent Fitness & Nutrition Planner (JavaFX)
An advanced, scientifically-grounded Java application designed to function as an automated personal trainer and nutritionist. This system leverages principles of progressive overload, volume management, and energy balance to generate hyper-personalized fitness and diet plans. Built with a modern JavaFX GUI and a robust MS SQL Server backend.

✨ Key Features & Algorithms
🏋️‍♂️ 1. The Workout Generation Engine
The heart of the application is a sophisticated PlanBuilder (Builder Pattern) that constructs workouts dynamically based on user goals and fitness levels.

Smart Split Architecture (A-F Rotations):
Full Body: Generates up to 6 distinct templates (A-F) for high-frequency training, shifting emphasis (e.g., Quad Bias vs. Upper Power) to maximize recovery.
Upper/Lower: Implements A/B/C rotations to balance Strength, Hypertrophy, and Stability within a single week.
Scientific Volume Logic: Muscles are targeted 2-3x/week with strict volume caps (Max 27 exercises/week) to prevent overtraining.
Equipment Aware: Adapts plan generation based on available equipment (Home vs. Gym).
🥗 2. Adaptive Nutrition System
Unlike static calorie calculators, this system "learns" from the user's metabolism.

TDEE Baseline: Mifflin-St Jeor equation modulated by activity level.
Metabolic Adaptation: Tracks Weekly Average Weight (not daily swings) and compares trends over 2 weeks.
Automatic Plateaus Fix: Subtracts calories dynamically if weight loss stalls for >2 weeks.
📊 3. Robust Progress Tracking
Relational Persistence: All data is securely stored in MS SQL Server.
Rolling Averages: Removes "water-weight noise" from progress tracking to provide accurate metabolic feedback.
🛠️ Technical Architecture
Core Tech Stack
Language: Java SE 21+
UI Framework: JavaFX 25.0.1
Database: Microsoft SQL Server
Persistence: JDBC (Custom Repository Layer)
Professional Design Patterns
Builder Pattern: Complex WorkoutPlan construction.
Strategy Pattern: Swappable IDietPlan algorithms.
Repository Pattern: DAO layer decouples business logic from SQL.
Singleton Pattern: Global DatabaseConnection management.
Project Structure
WholeProject/
├── src/
│   ├── app/                # Main Application & Session Management
│   ├── config/             # DB Configuration & Properties
│   ├── model/              # Core Domain Logic (Nutrition, Workout, User)
│   └── view/               # JavaFX Controllers, FXML, and Stylesheets
├── lib/                    # All JAR dependencies (JavaFX, JDBC)
├── DATABASE_SETUP.sql       # SQL Schema Definition
├── run_app.bat             # Primary Build & Run Script
└── setup_javafx.bat        # Environment Setup Utility
⚙️ Installation & Setup
1. Database Setup
Open SQL Server Management Studio (SSMS).
Open and execute the DATABASE_SETUP.sql script.
This creates the FitnessApp database and all 12 required tables.
2. Configuration
Verify the connection details in src/config/db.properties.
Default: localhost:1433 with sa user.
3. Build & Run
The easiest way to run the project is using the provided automation scripts:

First Time: Run setup_javafx.bat to ensure libraries are configured.
Every Time: Run run_app.bat to compile and launch the application.
📈 Future Roadmap
 Machine Learning: Predict 1RM (One Rep Max) trends.
 Mobile Port: Android/iOS integration via Gluon Mobile.
 Social API: Leaderboards and workout sharing.
Created with focus on scientific training principles and professional software architecture.
