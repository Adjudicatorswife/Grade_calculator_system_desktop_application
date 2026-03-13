package gradecalculator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for all database operations.
 */
public class GradeDAO {

    // ══════════════════════════════════════
    //  STUDENT OPERATIONS
    // ══════════════════════════════════════

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY full_name";
        Connection c = DatabaseConnection.getConnection();
        if (c == null) return list;
        try (c; Statement s = c.createStatement(); ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new Student(
                    r.getInt("id"), r.getString("student_id"),
                    r.getString("full_name"), r.getString("course"),
                    r.getString("year_level")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addStudent(Student st) {
        String sql = "INSERT INTO students (student_id, full_name, course, year_level) VALUES (?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, st.getStudentId());
            ps.setString(2, st.getFullName());
            ps.setString(3, st.getCourse());
            ps.setString(4, st.getYearLevel());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateStudent(Student st) {
        String sql = "UPDATE students SET student_id=?, full_name=?, course=?, year_level=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, st.getStudentId());
            ps.setString(2, st.getFullName());
            ps.setString(3, st.getCourse());
            ps.setString(4, st.getYearLevel());
            ps.setInt(5, st.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ══════════════════════════════════════
    //  SUBJECT OPERATIONS
    // ══════════════════════════════════════

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY subject_code";
        try (Connection c = DatabaseConnection.getConnection();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new Subject(
                    r.getInt("id"), r.getString("subject_code"),
                    r.getString("subject_name"), r.getInt("units")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addSubject(Subject sub) {
        String sql = "INSERT INTO subjects (subject_code, subject_name, units) VALUES (?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sub.getSubjectCode());
            ps.setString(2, sub.getSubjectName());
            ps.setInt(3, sub.getUnits());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteSubject(int id) {
        String sql = "DELETE FROM subjects WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ══════════════════════════════════════
    //  GRADE OPERATIONS
    // ══════════════════════════════════════

    public boolean saveGrade(int studentDbId, int subjectId,
                             double prelim, double midterm,
                             double prefinal, double finalExam) {
        String sql = "INSERT INTO grades (student_id, subject_id, prelim, midterm, prefinal, final_exam) " +
                     "VALUES (?,?,?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "prelim=VALUES(prelim), midterm=VALUES(midterm), " +
                     "prefinal=VALUES(prefinal), final_exam=VALUES(final_exam)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentDbId);
            ps.setInt(2, subjectId);
            ps.setDouble(3, prelim);
            ps.setDouble(4, midterm);
            ps.setDouble(5, prefinal);
            ps.setDouble(6, finalExam);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /** Returns all grades for a student as Object[][] for JTable. */
    public Object[][] getGradesByStudent(int studentDbId) {
        String sql = "SELECT sub.subject_code, sub.subject_name, sub.units, " +
                     "g.prelim, g.midterm, g.prefinal, g.final_exam, " +
                     "g.final_grade, g.remarks " +
                     "FROM grades g " +
                     "JOIN subjects sub ON g.subject_id = sub.id " +
                     "WHERE g.student_id = ? ORDER BY sub.subject_code";
        List<Object[]> rows = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentDbId);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                rows.add(new Object[]{
                    r.getString("subject_code"),
                    r.getString("subject_name"),
                    r.getInt("units"),
                    String.format("%.2f", r.getDouble("prelim")),
                    String.format("%.2f", r.getDouble("midterm")),
                    String.format("%.2f", r.getDouble("prefinal")),
                    String.format("%.2f", r.getDouble("final_exam")),
                    String.format("%.2f", r.getDouble("final_grade")),
                    r.getString("remarks")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows.toArray(new Object[0][]);
    }

    /** Calculates GWA (General Weighted Average) for a student. */
    public double getGWA(int studentDbId) {
        String sql = "SELECT SUM(g.final_grade * sub.units) / SUM(sub.units) AS gwa " +
                     "FROM grades g JOIN subjects sub ON g.subject_id = sub.id " +
                     "WHERE g.student_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentDbId);
            ResultSet r = ps.executeQuery();
            if (r.next()) return r.getDouble("gwa");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    /** Returns full grade report for ALL students (for Reports panel). */
    public Object[][] getFullReport() {
        String sql = "SELECT st.student_id, st.full_name, st.course, " +
                     "sub.subject_code, sub.subject_name, " +
                     "g.prelim, g.midterm, g.prefinal, g.final_exam, " +
                     "g.final_grade, g.remarks " +
                     "FROM grades g " +
                     "JOIN students st  ON g.student_id  = st.id " +
                     "JOIN subjects sub ON g.subject_id = sub.id " +
                     "ORDER BY st.full_name, sub.subject_code";
        List<Object[]> rows = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             Statement  s = c.createStatement();
             ResultSet  r = s.executeQuery(sql)) {
            while (r.next()) {
                rows.add(new Object[]{
                    r.getString("student_id"),
                    r.getString("full_name"),
                    r.getString("course"),
                    r.getString("subject_code"),
                    r.getString("subject_name"),
                    String.format("%.2f", r.getDouble("prelim")),
                    String.format("%.2f", r.getDouble("midterm")),
                    String.format("%.2f", r.getDouble("prefinal")),
                    String.format("%.2f", r.getDouble("final_exam")),
                    String.format("%.2f", r.getDouble("final_grade")),
                    r.getString("remarks")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows.toArray(new Object[0][]);
        
    }
    
}
