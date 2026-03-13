# Grade_calculator_system_desktop_application
╔══════════════════════════════════════════════════════════════════════╗
║         GRADE CALCULATOR SYSTEM — SETUP GUIDE                       ║
║         Java Swing + MySQL  |  NetBeans IDE                         ║
╚══════════════════════════════════════════════════════════════════════╝

PROJECT FILES
─────────────────────────────────────────────────────────────────────
  src/gradecalculator/
    ├── MainFrame.java          ← Main GUI window (run this!)
    ├── DatabaseConnection.java ← MySQL connection utility
    ├── GradeDAO.java           ← All database queries
    ├── Student.java            ← Student model
    └── Subject.java            ← Subject model
  database_setup.sql            ← Run this in MySQL FIRST


STEP 1 — SET UP THE DATABASE
─────────────────────────────────────────────────────────────────────
  1. Open MySQL Workbench (or run: mysql -u root -p)
  2. Open and run: database_setup.sql
     • Creates: grade_calculator database
     • Creates: students, subjects, grades tables
     • Inserts:  sample students and subjects


STEP 2 — DOWNLOAD MYSQL JDBC DRIVER
─────────────────────────────────────────────────────────────────────
  Download: https://dev.mysql.com/downloads/connector/j/
  Choose:   Platform Independent → ZIP
  Extract:  mysql-connector-j-*.jar


STEP 3 — SET UP NETBEANS PROJECT
─────────────────────────────────────────────────────────────────────
  1. File → New Project → Java → Java Application
     Name: GradeCalculator
     Package: gradecalculator

  2. Copy these files into your src/gradecalculator/ folder:
     • MainFrame.java
     • DatabaseConnection.java
     • GradeDAO.java
     • Student.java
     • Subject.java

  3. Add MySQL JDBC Driver:
     • Right-click project → Properties → Libraries
     • Click "Add JAR/Folder"
     • Browse to mysql-connector-j-*.jar
     • Click OK

  4. Set Main Class:
     • Right-click project → Properties → Run
     • Main Class: gradecalculator.MainFrame


STEP 4 — CONFIGURE DATABASE CONNECTION
─────────────────────────────────────────────────────────────────────
  Open: DatabaseConnection.java
  Edit these 3 lines to match YOUR MySQL setup:

    private static final String DB_URL  = "jdbc:mysql://localhost:3306/grade_calculator?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";     ← your password here


STEP 5 — RUN!
─────────────────────────────────────────────────────────────────────
  Press F6 in NetBeans (or Run → Run Project)
  The main window will open with the Dashboard.


GRADE FORMULA USED
─────────────────────────────────────────────────────────────────────
  Final Grade = (Prelim × 20%) + (Midterm × 20%)
              + (Pre-Final × 20%) + (Final Exam × 40%)

  Passing Grade: 75.00
  Remarks:       PASSED (≥ 75) | FAILED (< 75)

  GWA (General Weighted Average):
    GWA = Σ(Final Grade × Units) / Σ(Units)


FEATURES
─────────────────────────────────────────────────────────────────────
  🏠 Dashboard    — System overview, stats, formula reference
  👥 Students     — Add / Edit / Delete students
  📖 Subjects     — Manage course subjects with units
  📝 Enter Grades — Enter grades per subject with live preview
  📊 Reports      — Full grade report with PASSED/FAILED coloring


TROUBLESHOOTING
─────────────────────────────────────────────────────────────────────
  ❌ "Driver not found"
     → Add mysql-connector-j-*.jar to project Libraries

  ❌ "Cannot connect to MySQL"
     → Check: Is MySQL service running?
     → Check: DB_URL, DB_USER, DB_PASS in DatabaseConnection.java

  ❌ "Unknown database"
     → Run database_setup.sql in MySQL first

  ❌ Blank grade table
     → Make sure you've added grades via "Enter Grades" tab first
