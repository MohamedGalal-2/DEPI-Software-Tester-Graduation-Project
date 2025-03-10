# Automated Testing Framework

## 📌 Project Overview

This is a Maven-based automated testing framework designed for UI, Functional, and API testing. The framework integrates a variety of testing tools, including Selenium for UI automation, TestNG for test execution and reporting, JMeter for performance testing, Postman for API request validation, and REST Assured for API testing.

The framework also supports JIRA for test management, ensuring smooth collaboration and tracking of test cases.

We will be testing the following website: [nopcommerce demo](https://demo.nopcommerce.com/)

## 💑 Table of Contents
- [📌 Project Overview](#-project-overview)
- [🚀 Features](#-features)
- [🏧 Project Structure](#-project-structure)
- [📅 Project Timeline](#-project-timeline)
- [🛠️ Technologies Used](#-technologies-used)
- [⚙️ Setup Instructions](#-setup-instructions)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [🏃 Running Tests](#-running-tests)
- [📝 Documentation](#-documentation)
- [👨‍💻 Contributors](#-contributors)
- [📝 License](#-license)

## 🚀 Features

- **UI Testing** using Selenium WebDriver.
- **API Testing** using Postman for manual and REST Assured for the automation.
- **Performance Testing** with JMeter/Gatling.
- **Test Case Management** via JIRA.
- **Automated Reporting** using TestNG.
- **Continuous Integration** with Jenkins.

## 🏧 Project Structure
```
📂 automation-framework              # Main framework directory
│── 📂 config                        # Configuration files (test data, environment variables)
│── 📂 docs                          # Documentation (test cases, guidelines, API references)
│── 📂 meetings                      # Meeting notes and discussions
│── 📂 logs                          # Execution logs for debugging
│── 📂 postman                       # Postman collections for API testing
│── 📂 reports                       # Test reports (TestNG, Allure, etc.)
│── 📂 src                           # Source code directory
│   ├── 📂 test
│   │   ├── 📂 java
│   │   │   ├── 📂 base              # Base classes (e.g., setup, teardown)
│   │   │   ├── 📂 tests
│   │   │   │   ├── 📂 ui           # UI tests (Selenium)
│   │   │   │   ├── 📂 api          # API tests (Rest Assured)
│   │   │   │   ├── 📂 performance  # Performance tests (JMeter)
│   │   │   ├── 📂 utils            # Utility classes (e.g., helpers, configurations)
│   ├── 📂 resources                 # Test resources (e.g., config files, test data)
│── 📂 target                        # Compiled test results and build artifacts
│── pom.xml                          # Maven project configuration
│── .gitignore                       # Files to ignore in Git
│── README.md                        # Project overview and setup guide
```

## 📅 Project Timeline
| Phase          | Tasks                                            | Duration  |
| -------------- | ------------------------------------------------ | --------- |
| Planning       | Define goals, set up tools, assign roles         | Week 1    |
| Design         | Define framework architecture, create test cases | Week 1    |
| Implementation | Develop automated UI, API, and performance tests | Weeks 2-3 |
| Testing & Eval | Run tests, log issues, optimize framework        | Week 4    |

## 🛠️ Technologies Used

- **Programming Language:** Java
- **Testing Frameworks:** Selenium WebDriver, TestNG, JMeter, Postman, Rest Assured.
- **Test Management:** JIRA
- **CI/CD:** Jenkins
- **Dependency:** Management: Maven

## ⚙️ Setup Instructions

### Prerequisites

Ensure you have the following installed:

- **Java 11+** (check with java -version)
- **Maven** (check with mvn -v)
- **Selenium WebDriver**
- **Postman**
- **JMeter**
- **IDE:** IntelliJ IDEA or VS Code

### Installation

```bash
# Clone the repository
git clone https://github.com/MohamedGalal-2/DEPI-Software-Tester-Graduation-Project
cd DEPI-Software-Tester-Graduation-Project
```

**Install dependencies**

```bash
mvn clean install
```

## 🏃 Running Tests

- **UI Tests:** Run **Selenium-based** UI tests:

```bash
  mvn test -Dtest=UITestClass
```

- **API Tests:** Run API tests using **REST Assured**:

```bash
 mvn test -Dtest=APITestClass
```

- **Performance Tests:** Run **JMeter** performance tests:

```bash
  jmeter -n -t tests/performance_tests/LoadTest.jmx -l results.jtl
```

- **Run All Tests:** Run all tests (UI, API, functional) together:

```bash
  mvn clean test
```

## 📝 Documentation

Refer to the [project documentation](docs/) for detailed instructions on usage and customization.

## 👨‍💻 Contributors

In this Scrum-based project, all team members contribute equally to testing efforts, ensuring comprehensive coverage across different testing areas. One member also takes on the role of Scrum Master and Team Lead to facilitate the process and maintain alignment.

- **[Mohamed Ahmed Galal]** - Scrum Master & Team Lead, responsible for coordination and removing blockers.
- **[Esraa Ahmed Ramdan]** - Tester, focusing on UI automation and functional testing.
- **[Jana Fouad Mohamed]** - Tester, specializing in API testing and integration validation.
- **[Mohamed Ahmed Fathy]** - Tester, handling performance testing and optimization.

All testers work collaboratively on bug tracking, test case execution, and continuous improvement of the testing framework.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
