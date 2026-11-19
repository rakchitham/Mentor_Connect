# 🚀 Mentor Connect
### A Java-Based Real-Time Mentorship & Collaboration Platform

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-Database-005C84?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/Java_Swing-GUI-blue?style=for-the-badge" />
  <img src="https://img.shields.io/badge/TCP-Socket_Programming-2496ED?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Maven-Build_Tool-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" />
</p>

## 📌 Project Overview

**Mentor Connect** is a Java-based desktop application designed to connect mentors and mentees through a centralized platform for professional guidance, knowledge sharing, and real-time communication.

The application follows a **Client-Server Architecture** and integrates Java Socket Programming, Multithreading, Java Swing, JDBC, and MySQL to provide an interactive and structured mentorship experience.

It enables users to discover mentors, manage mentorship requests, communicate through real-time messaging, and share feedback after completing sessions.

## 🎯 Project Objectives

- Build a centralized platform for mentor-mentee interaction.
- Simplify mentor discovery based on expertise and professional domains.
- Enable structured mentorship request management.
- Implement real-time communication using Java Socket Programming.
- Maintain persistent user and session information using MySQL.
- Provide role-based access and personalized dashboards.
- Improve mentorship quality through session ratings and feedback.

## 💡 Problem Statement

Students and early-career professionals often face difficulties finding suitable mentors and maintaining structured communication.

Traditional communication channels may lack organized session management, centralized mentor information, and systematic feedback mechanisms.

Mentor Connect addresses these challenges by providing an integrated desktop platform that combines mentor discovery, session management, real-time communication, and feedback tracking.

## ✨ Key Features

### 👥 1. Role-Based User Management

The application supports two primary user roles:

| Functionality | Mentor | Mentee |
|---|---|---|
| User Registration | ✓ | ✓ |
| Secure Login | ✓ | ✓ |
| Profile Management | ✓ | ✓ |
| Mentor Discovery | — | ✓ |
| Session Requests | Accept / Reject | Create / Track |
| Real-Time Chat | ✓ | ✓ |
| Notifications | ✓ | ✓ |
| Feedback | View Ratings | Submit Ratings |

### 🔍 2. Mentor Discovery

- Search for mentors based on professional expertise.
- Explore available mentorship profiles.
- Identify mentors based on technical domains.
- Access relevant mentor information before requesting sessions.

### 📅 3. Mentorship Session Management

- Allow mentees to submit mentorship requests.
- Enable mentors to accept or reject incoming requests.
- Track session progress through predefined statuses.
- Maintain session-related information in the database.

**Session Lifecycle:**

```text
     Session Request
           |
           v
        Pending
           |
       +---+---+
       |       |
       v       v
    Accepted Rejected
       |
       v
    Completed
       |
       v
    Feedback
```

### 💬 4. Real-Time Communication

The application implements one-to-one messaging using Java TCP Socket Programming.

Key capabilities:

- Client-server communication.
- Bidirectional message exchange.
- Concurrent client handling.
- Message delivery through socket connections.
- Multithreaded communication handling.

### 🔔 5. Notification Management

The notification module provides alerts for important mentorship activities.

Examples:

- New mentorship requests.
- Request acceptance.
- Request rejection.
- Session-related status updates.
- Other relevant system notifications.

### ⭐ 6. Feedback and Rating System

- Allow mentees to submit feedback after sessions.
- Record ratings and comments.
- Maintain feedback information in MySQL.
- Enable mentors to review received feedback.

## 🏗️ System Architecture

Mentor Connect follows a **multi-threaded Client-Server Architecture**.

```text
             +----------------------+
             |         USERS        |
             +----------+-----------+
                        |
             +----------v-----------+
             |   Java Swing GUI     |
             |                      |
             |  Mentor / Mentee     |
             +----------+-----------+
                        |
             +----------v-----------+
             |   Client Application |
             |                      |
             | Authentication      |
             | Session Management  |
             | Chat Interface      |
             +----------+-----------+
                        |
                TCP Socket
                        |
             +----------v-----------+
             |     Chat Server      |
             |                      |
             | Client Connections  |
             | Multithreading      |
             | Message Routing     |
             | Notifications       |
             +----------+-----------+
                        |
             +----------v-----------+
             |   JDBC Connectivity |
             +----------+-----------+
                        |
             +----------v-----------+
             |     MySQL Database  |
             |                      |
             | Users               |
             | Sessions            |
             | Messages            |
             | Feedback            |
             +----------------------+
```

### Architecture Components

| Component | Responsibility |
|---|---|
| Java Swing Client | Provides the graphical user interface |
| Client Application | Handles user interactions and application logic |
| Chat Server | Manages client socket connections |
| Multithreading | Supports concurrent client communication |
| TCP Sockets | Enables real-time bidirectional communication |
| JDBC | Connects Java application with MySQL |
| MySQL | Stores persistent application information |

## ⚙️ Technical Implementation

### 1. Java Socket Programming

TCP sockets establish communication between the client and server.

- `ServerSocket` listens for incoming connections.
- `Socket` establishes client-server communication.
- Input streams receive incoming messages.
- Output streams transmit messages.

### 2. Multithreading

Multithreading allows the server to handle multiple client connections concurrently.

Each connected client can be handled through an independent execution thread.

**Benefits:**
- Concurrent client handling.
- Improved responsiveness.
- Independent communication sessions.
- Efficient server-side processing.

### 3. JDBC Database Connectivity

Java Database Connectivity (JDBC) is used to communicate with the MySQL database.

It supports:
- Database connections.
- SQL query execution.
- User authentication.
- Session data management.
- Feedback storage.
- Data retrieval and updates.

### 4. Object-Oriented Programming

The application follows fundamental OOP principles:

- Encapsulation
- Inheritance
- Polymorphism
- Abstraction
- Modular class design

## 🛠️ Technology Stack

| Layer | Technology | Purpose |
|---|---|---|
| Programming Language | Java 17 | Core application development |
| Frontend | Java Swing | Desktop user interface |
| Networking | TCP Socket Programming | Real-time communication |
| Concurrency | Java Multithreading | Concurrent client handling |
| Database | MySQL | Persistent data storage |
| Database Connectivity | JDBC | Java-MySQL integration |
| Build Management | Apache Maven | Dependency and project management |
| Development Environment | IntelliJ IDEA / Eclipse | Application development |

## 📂 Project Structure

```text
MENTOR_CONNECT/
│
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── example/
│                   │
│                   ├── Main.java
│                   ├── Login.java
│                   ├── Register.java
│                   ├── Dashboard.java
│                   ├── ChatServer.java
│                   ├── DatabaseConnection.java
│                   ├── FeedbackWindow.java
│                   └── NotificationWindow.java
│
├── pom.xml
├── README.md
└── .gitignore
```

### Important Modules

| File | Description |
|---|---|
| `Main.java` | Application entry point |
| `Login.java` | User authentication |
| `Register.java` | User registration and role selection |
| `Dashboard.java` | Main application navigation |
| `ChatServer.java` | Socket server and communication management |
| `DatabaseConnection.java` | JDBC database connectivity |
| `FeedbackWindow.java` | Feedback and rating interface |
| `NotificationWindow.java` | Notification interface |

## 🗄️ Database Design

MySQL is used to maintain application data.

The database can include the following logical entities:

| Entity | Description |
|---|---|
| Users | Stores mentor and mentee information |
| Sessions | Maintains mentorship requests and statuses |
| Messages | Stores communication records, if persistence is implemented |
| Feedback | Stores session ratings and comments |
| Notifications | Maintains relevant user notifications |

**Note:** Actual table names and relationships should match the SQL schema implemented in the project.

## 📋 Prerequisites

Before running the application, ensure that the following software is installed.

| Software | Requirement |
|---|---|
| Java JDK | 17 or later |
| MySQL Server | Installed and configured |
| Apache Maven | Installed |
| IDE | IntelliJ IDEA / Eclipse |
| Git | For version control |

## 🚀 Installation and Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/Mentor-Connect.git
```

### Step 2: Navigate to the Project

```bash
cd Mentor-Connect
```

### Step 3: Configure MySQL

1. Start your MySQL server.
2. Create the required project database.
3. Configure database credentials in `DatabaseConnection.java`.
4. Ensure the required tables are created.

Example:

```sql
CREATE DATABASE mentor_connect;
```

### Step 4: Configure Database Connectivity

Update the JDBC connection details according to your local MySQL configuration.

```java
String url = "jdbc:mysql://localhost:3306/mentor_connect";
String username = "root";
String password = "YOUR_PASSWORD";
```

Do not commit real database passwords or credentials to GitHub.

### Step 5: Build the Project

```bash
mvn clean install
```

### Step 6: Run the Application

Start the server first, followed by the client application.

Run the main application entry point:

```bash
mvn exec:java -Dexec.mainClass="org.example.Main"
```

The Maven command assumes the Exec Maven Plugin is configured in `pom.xml`. Otherwise, run `Main.java` directly from your IDE.

## 🔐 Security Considerations

- Validate user input before processing.
- Use parameterized SQL queries to reduce SQL injection risks.
- Protect database credentials.
- Apply appropriate authentication and authorization.
- Handle socket disconnections and communication errors.
- Avoid exposing sensitive information in application logs.

## 🔮 Future Enhancements

The following features can extend the platform:

- Video conferencing integration.
- Email notifications.
- Mentor availability calendar.
- Advanced mentor recommendation system.
- Secure password hashing.
- Session scheduling and reminders.
- Admin dashboard.
- Chat history and search.
- Cloud-based deployment.
- Mobile application integration.

## 🎓 Learning Outcomes

Developing Mentor Connect provides practical experience in:

- Java application development.
- Object-Oriented Programming.
- Client-Server Architecture.
- TCP Socket Programming.
- Java Multithreading.
- JDBC and MySQL integration.
- Desktop GUI development using Swing.
- Database management.
- Modular software design.
- Version control using Git and GitHub.

## 👨‍💻 Project Information

**Project Name:** Mentor Connect

**Project Type:** Java Desktop Application

**Architecture:** Client-Server Architecture

**Primary Language:** Java

**Database:** MySQL

**Development Approach:** Modular and Object-Oriented Design

## 📜 License

This project is developed for educational and learning purposes. Add an appropriate open-source license if you intend to distribute it publicly.

---

<p align="center">
  <b>Mentor Connect – Connecting Knowledge, Building Futures.</b>
  <br/>
  Designed to make mentorship more accessible, structured, and collaborative.
</p>
