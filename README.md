# 🚀 Mentor Connect
### *A High-Performance Java Ecosystem for Professional Mentorship*

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Networking](https://img.shields.io/badge/Sockets-Networking-blue?style=for-the-badge)](https://docs.oracle.com/javase/tutorial/networking/sockets/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)](https://maven.apache.org/)

**Mentor Connect** is a sophisticated **Java-based desktop application** designed to facilitate structured and real-time interaction between mentors and mentees. Built on a **Client-Server architecture**, it demonstrates the practical application of Socket Programming, Multithreading, and Database Management.

---

## 📖 Overview
The platform serves as a centralized digital ecosystem for academic and professional guidance. It replaces fragmented communication channels with a structured workflow that handles everything from **Mentor Discovery** to **Real-Time Collaboration** and **Post-Session Feedback**.

---

## 🎯 Mission
To bridge the mentorship gap in technical education by providing a secure, real-time platform that streamlines the knowledge-sharing process through role-based access and data-driven feedback.

---

## 🏗️ System Architecture



The application utilizes a **Multi-Threaded Client-Server Model**:
1.  **Server Side (`ChatServer.java`):** Manages concurrent socket connections, routes real-time messages, and handles background logic.
2.  **Client Side:** A modular **Java Swing GUI** that interacts with the server for chat and the **MySQL database** for persistent data.

---

## ✨ Key Features & User Roles

### 👥 User Roles
| Feature | 👨‍🏫 Mentor | 🎓 Mentee |
|:--- |:--- |:--- |
| **Discovery** | Manage professional profile | Search by domain/expertise |
| **Sessions** | Accept/Reject requests | Send session requests |
| **Communication** | Real-time chat with mentees | Real-time chat with mentors |
| **Feedback** | View ratings & performance | Rate sessions & submit feedback |

### 🔍 Core Technical Highlights
* **💬 Real-Time Chat Engine:** One-to-one low-latency messaging implemented via **Java Sockets**.
* **🔔 Live Notification System:** Instant socket-driven alerts for session requests and status updates.
* **📊 Persistent Data Storage:** Robust relational data management using **MySQL** and **JDBC**.
* **📂 Session Lifecycle Tracking:** Managed states including `Pending`, `Accepted`, `Rejected`, and `Completed`.
* **🎨 Intuitive UI:** Role-based dashboards built with the **Java Swing** framework.

---

## 🛠️ Technical Stack & Infrastructure

| Layer | Technology | Key Implementation Details |
|:--- |:--- |:--- |
| **Language** | ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) **Java 17** | Leverages multi-threading and robust OOP principles for core logic. |
| **Frontend** | ![Swing](https://img.shields.io/badge/UI-Java_Swing-blue?style=flat) **Java Swing** | Implements a responsive, role-based desktop GUI with custom layouts. |
| **Networking** | ![Sockets](https://img.shields.io/badge/Networking-TCP_Sockets-lightgrey?style=flat) **Java Sockets** | Handles 1-to-1 real-time bidirectional messaging via TCP/IP. |
| **Database** | ![MySQL](https://img.shields.io/badge/Database-MySQL-005C84?style=flat&logo=mysql&logoColor=white) **MySQL** | Manages persistent user data, session logs, and feedback metrics. |
| **Connectivity**| ![JDBC](https://img.shields.io/badge/Access-JDBC-orange?style=flat) **JDBC** | Ensures high-performance database transactions and connectivity. |
| **Build Tool** | ![Maven](https://img.shields.io/badge/Build-Maven-C71A36?style=flat&logo=apache-maven&logoColor=white) **Maven** | Streamlines dependency management and automated project lifecycles. |

---

## 📂 Project Structure

```text
MENTOR_CONNECT
├── src
│   └── main
│       └── java
│           └── org
│               └── example
│                   ├── Main.java                 # Entry point of the application
│                   ├── Login.java                # Authentication Module
│                   ├── Register.java             # User Role Onboarding
│                   ├── Dashboard.java            # Main Navigation Hub
│                   ├── ChatServer.java           # Socket Server Logic
│                   ├── DatabaseConnection.java   # JDBC Singleton Utility
│                   ├── FeedbackWindow.java       # Feedback & Rating Logic
│                   └── NotificationWindow.java   # Real-time System Alerts
├── pom.xml                                       # Maven Dependency Management
├── README.md                                     # Project Documentation
└── .gitignore                                    # Build/IDE Exclusion Rules

```
## Prerequisites


| Software         | Version / Requirement |
|-----------------|---------------------|
| Java JDK        | 17+                 |
| MySQL Server    | Latest stable       |
| Maven           | Latest stable       |

---

## Quick Environment Setup

| Step | Command | Description |
|------|---------|-------------|
| 1    | `sudo apt update` | Update your package repository |
| 2    | `sudo apt install openjdk-17-jdk` | Install OpenJDK 17 |
| 3    | `sudo apt install mysql-server` | Install MySQL Server |
| 4    | `sudo apt install maven` | Install Maven |
