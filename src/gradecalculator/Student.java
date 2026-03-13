package gradecalculator;

public class Student {
    private int    id;
    private String studentId;
    private String fullName;
    private String course;
    private String yearLevel;

    public Student() {}

    public Student(int id, String studentId, String fullName, String course, String yearLevel) {
        this.id        = id;
        this.studentId = studentId;
        this.fullName  = fullName;
        this.course    = course;
        this.yearLevel = yearLevel;
    }

    // Getters & Setters
    public int    getId()         { return id; }
    public void   setId(int id)   { this.id = id; }

    public String getStudentId()               { return studentId; }
    public void   setStudentId(String s)       { this.studentId = s; }

    public String getFullName()                { return fullName; }
    public void   setFullName(String s)        { this.fullName = s; }

    public String getCourse()                  { return course; }
    public void   setCourse(String s)          { this.course = s; }

    public String getYearLevel()               { return yearLevel; }
    public void   setYearLevel(String s)       { this.yearLevel = s; }

    @Override
    public String toString() {
        return studentId + " - " + fullName;
    }
}