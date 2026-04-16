# MediMate Desktop Application

**MediMate** is a comprehensive medical management desktop application built strictly using **Java Swing** and **Object-Oriented Programming (OOP) principles**. It was migrated from a web-based architecture into a standalone native application, adhering to stringent university Advanced Programming Lab guidelines.

## Features 
- **Custom UI:** Native dark-mode glassmorphism interface (Built without external UI libraries).
- **Authentication:** Secure Login/Registration with built-in Java SHA-256 password cryptography.
- **AI Diagnosis:** Integrated with the **Groq Llama-3 API** for instantaneous medical advice.
- **Real-Time Doctor Chat:** Database-backed polling system for real-time messaging between doctors and patients.
- **Health Blog:** Read and post articles dynamically.
- **Admin Dashboard:** Manage and delete users and posts.

## Tech Stack 
- **Language:** Java 17+
- **GUI:** Java Swing (Custom `paintComponent` rendering)
- **Database:** Aiven Cloud MySQL (JDBC `mysql-connector-j`)
- **Networking:** `HttpURLConnection` & `Gson`
- **Build System:** Maven

## How to Run 
1. Clone the repository.
2. Ensure you have Maven installed (`mvn -v`).
3. Run the following command in the project root:
```bash
mvn clean compile exec:java -Dexec.mainClass="com.medimate.Main"
```
