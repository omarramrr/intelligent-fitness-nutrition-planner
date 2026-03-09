# 🎯 Intelligent Fitness & Nutrition Planner (JavaFX)

An advanced **Java desktop application** designed to function as an automated **personal trainer and nutrition planner**.

The system applies modern training science such as **progressive overload**, **volume management**, and **energy balance** to generate **hyper-personalized workout and nutrition plans**.

Built with a modern **JavaFX GUI** and powered by a **Microsoft SQL Server backend**.

---

# 🚀 Key Features

## 🏋️ Workout Generation Engine

The heart of the application is a sophisticated **PlanBuilder (Builder Pattern)** that dynamically generates workout plans based on user goals and fitness level.

### Smart Training Splits

**Full Body A–F Rotation**

* Generates up to **6 unique training templates**
* Alternates emphasis (Quad Bias, Upper Power, Posterior Focus)
* Designed for **high-frequency training with optimal recovery**

**Upper / Lower Split**

* Implements **A/B/C rotations**
* Balances **Strength, Hypertrophy, and Stability**

### Scientific Volume Control

* Muscles trained **2–3 times per week**
* **Maximum cap of 27 exercises per week**
* Prevents **overtraining and excessive fatigue**

### Equipment Awareness

The system automatically adapts workouts depending on available equipment:

* 🏠 Home Gym
* 🏋️ Commercial Gym

---

# 🥗 Adaptive Nutrition System

Unlike traditional static calorie calculators, this system dynamically adapts to the user's metabolism.

### TDEE Baseline

Calories calculated using the **Mifflin-St Jeor equation** adjusted for activity level.

### Metabolic Adaptation

* Tracks **weekly average weight**
* Filters out daily fluctuations
* Compares progress across **2-week trends**

### Automatic Plateau Correction

If weight loss stalls for more than **2 weeks**, the system automatically:

* Adjusts caloric intake
* Maintains progress without extreme dieting

---

# 📊 Progress Tracking System

### Relational Persistence

All user data is stored securely in **Microsoft SQL Server**.

### Rolling Averages

The system removes **water weight noise** and analyzes only meaningful trends for accurate metabolic feedback.

---

# 🧠 Software Architecture

## Core Technology Stack

| Component    | Technology           |
| ------------ | -------------------- |
| Language     | Java SE 21+          |
| UI Framework | JavaFX 25            |
| Database     | Microsoft SQL Server |
| Persistence  | JDBC                 |
| Architecture | Layered Architecture |

---

# 🧩 Design Patterns Used

The project follows professional software architecture principles:

* **Builder Pattern** → Dynamic WorkoutPlan construction
* **Strategy Pattern** → Swappable `IDietPlan` algorithms
* **Repository Pattern** → Decouples SQL from business logic
* **Singleton Pattern** → Centralized `DatabaseConnection`

---

# 📁 Project Structure

```
WholeProject
│
├── src
│   ├── app        # Main application & session management
│   ├── config     # Database configuration
│   ├── model      # Core domain logic
│   └── view       # JavaFX UI (FXML, controllers, styles)
│
├── lib            # External libraries (JavaFX, JDBC)
│
├── DATABASE_SETUP.sql
├── run_app.bat
└── setup_javafx.bat
```

# 📈 Future Roadmap

* 🔬 Machine Learning prediction of **1RM trends**
* 📱 Mobile version via **Gluon Mobile**
* 🌐 Social API for **leaderboards and workout sharing**
* ☁️ Cloud database support

---

# 👨‍💻 Author

Developed with a focus on **scientific training principles** and **professional software architecture**.


