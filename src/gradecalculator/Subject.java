package gradecalculator;

public class Subject {
    private int    id;
    private String subjectCode;
    private String subjectName;
    private int    units;

    public Subject() {}

    public Subject(int id, String subjectCode, String subjectName, int units) {
        this.id          = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.units       = units;
    }

    public int    getId()                    { return id; }
    public void   setId(int id)              { this.id = id; }

    public String getSubjectCode()           { return subjectCode; }
    public void   setSubjectCode(String s)   { this.subjectCode = s; }

    public String getSubjectName()           { return subjectName; }
    public void   setSubjectName(String s)   { this.subjectName = s; }

    public int    getUnits()                 { return units; }
    public void   setUnits(int u)            { this.units = u; }

    @Override
    public String toString() {
        return subjectCode + " - " + subjectName;
    }
}