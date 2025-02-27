# Automated Testing Framework

## 📌 Project Overview
This project aims to develop a comprehensive **Automated Testing Framework** for web applications. The framework integrates multiple testing tools to perform **UI, API, functional, and performance testing**, ensuring software reliability and efficiency.

### 🔗 Test Website
We will be testing the following website: [DemoBlaze](https://www.demoblaze.com/index.html)
This project aims to develop a comprehensive **Automated Testing Framework** for web applications. The framework integrates multiple testing tools to perform **UI, API, functional, and performance testing**, ensuring software reliability and efficiency.

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
- **API Testing** using Postman.
- **Performance Testing** with JMeter/Gatling.
- **Test Case Management** via JIRA.
- **Automated Reporting** using TestNG/JUnit.
- **Continuous Integration** with Jenkins.

## 🏧 Project Structure
```
📂 AutomatedTestingFramework
│── 📂 tests
│   ├── 📂 ui_tests
│   ├── 📂 api_tests
│   ├── 📂 performance_tests
│── 📂 framework
│   ├── 📂 pages    # Page Object Model (POM) classes
│   ├── 📂 utils    # Helper functions, configurations
│── 📂 reports
│   ├── 📂 html_reports
│   ├── 📂 logs
│   ├── 📂 screenshots
│── 📂 test_data
│── 📂 docs
│── requirements.txt
│── README.md
│── run_tests.java
│── .gitignore
```

## 📅 Project Timeline
| Phase          | Tasks                                            | Duration |
|----------------|--------------------------------------------------|----------|
| Planning       | Define goals, set up tools, assign roles         | Week 1   |
| Design         | Define framework architecture, create test cases | Week 1   |
| Implementation | Develop automated UI, API, and performance tests | Weeks 2-3|
| Testing & Eval | Run tests, log issues, optimize framework        | Week 4   |

## 🛠️ Technologies Used
- **Programming Language:** Java
- **Testing Frameworks:** Selenium, TestNG, JUnit, Postman, JMeter, Gatling
- **Test Management:** JIRA
- **CI/CD:** Jenkins

## ⚙️ Setup Instructions
### Prerequisites
Ensure you have the following installed:
- Java Development Kit (JDK)
- Selenium WebDriver
- Postman
- JMeter or Gatling
- JIRA API Access (if applicable)

### Installation
```bash
# Clone the repository
git clone https://github.com/your-repo-name.git
cd AutomatedTestingFramework
```

## 🏃 Running Tests
- **UI Tests:**
  ```bash
  java -cp tests/ui_tests TestLogin
  ```
- **API Tests:**
  Run tests using Postman or Newman CLI.
- **Performance Tests:**
  ```bash
  jmeter -n -t tests/performance_tests/LoadTest.jmx -l report.jtl
  ```

## 📝 Documentation
Refer to the [project documentation](docs/) for detailed instructions on usage and customization.

## 👨‍💻 Contributors
- **[Mohamed Ahmed Galal]** -
- **[Esraa Ahmed Ramdan]** -
- **[Jana Fouad Mohamed]** -
- **[Mohamed Ahmed Fathy]** -

## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

