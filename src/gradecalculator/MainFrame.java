package gradecalculator;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ╔══════════════════════════════════════════════════╗
 * ║        GRADE CALCULATOR — MAIN FRAME             ║
 * ║  Swing GUI  |  MySQL Backend  |  NetBeans Ready  ║
 * ╚══════════════════════════════════════════════════╝
 */
public class MainFrame extends JFrame {

    // ── Colors ────────────────────────────────────────
    private static final Color CLR_BG      = new Color(0, 0, 0);
    private static final Color CLR_SIDEBAR = new Color(30,  41,  59);
    private static final Color CLR_ACCENT  = new Color(99, 102, 241);
    private static final Color CLR_SUCCESS = new Color(34, 197,  94);
    private static final Color CLR_DANGER  = new Color(239,  68,  68);
    private static final Color CLR_WHITE   = Color.WHITE;
    private static final Color CLR_TEXT    = new Color(30,  41,  59);
    private static final Color CLR_MUTED   = new Color(100, 116, 139);
    private static final Color CLR_HEADER  = new Color(248, 250, 252);

    // ── Fonts ─────────────────────────────────────────
    private static final Font FNT_TITLE  = new Font("Segoe UI", Font.BOLD,   22);
    private static final Font FNT_LABEL  = new Font("Segoe UI", Font.PLAIN,  13);
    private static final Font FNT_BTN    = new Font("Segoe UI", Font.BOLD,   13);
    private static final Font FNT_SMALL  = new Font("Segoe UI", Font.PLAIN,  11);
    private static final Font FNT_NAV    = new Font("Segoe UI", Font.BOLD,   14);
    private static final Font FNT_MONO   = new Font("Consolas", Font.PLAIN,  13);

    private final GradeDAO dao = new GradeDAO();
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // ─── Shared state ─────────────────────────────────
    private Student selectedStudent = null;

    public MainFrame() {
        setTitle("Grade Calculator System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);
        setBackground(CLR_BG);
        setLayout(new BorderLayout());

        add(buildSidebar(),  BorderLayout.WEST);
        add(buildContent(),  BorderLayout.CENTER);

        setVisible(true);
    }

    // ══════════════════════════════════════════════════
    //  SIDEBAR
    // ══════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(CLR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Logo area — emoji in its own label using Segoe UI Emoji font
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(CLR_SIDEBAR);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(28, 10, 20, 10));
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.setMaximumSize(new Dimension(220, 110));

        // Emoji rendered separately using Segoe UI Emoji (Windows built-in)
        JLabel logoEmoji = new JLabel("\uD83C\uDF93");
        logoEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        logoEmoji.setForeground(CLR_WHITE);
        logoEmoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel logoText = new JLabel("GradeCalc");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoText.setForeground(CLR_WHITE);
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Grade Management System");
        sub.setFont(FNT_SMALL);
        sub.setForeground(new Color(180, 200, 255));
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoPanel.add(logoEmoji);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(logoText);
        logoPanel.add(Box.createVerticalStrut(4));
        logoPanel.add(sub);

        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(navDivider("MAIN MENU"));
        sidebar.add(navBtn("\uD83C\uDFE0", "Dashboard",   "DASHBOARD"));
        sidebar.add(navBtn("\uD83D\uDC65", "Students",    "STUDENTS"));
        sidebar.add(navBtn("\uD83D\uDCDA", "Subjects",    "SUBJECTS"));
        sidebar.add(navBtn("\uD83D\uDCDD", "Enter Grades","GRADES"));
        sidebar.add(navBtn("\uD83D\uDCCA", "Reports",     "REPORTS"));
        sidebar.add(Box.createVerticalGlue());

        JLabel ver = new JLabel("v1.0  |  MySQL + Swing");
        ver.setFont(FNT_SMALL);
        ver.setForeground(new Color(100, 116, 139));
        ver.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(ver);
        sidebar.add(Box.createVerticalStrut(16));

        return sidebar;
    }

    private JLabel navDivider(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(71, 85, 105));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(220, 24));
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        return lbl;
    }

    private JButton navBtn(String emoji, String text, String card) {
        // Build label manually: emoji in Segoe UI Emoji + text in Segoe UI
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();

                // Draw emoji
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                g2.setColor(getForeground());
                FontMetrics fmE = g2.getFontMetrics();
                int emojiW = fmE.stringWidth(emoji);

                // Draw text
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fmT = g2.getFontMetrics();
                int textW = fmT.stringWidth(text);

                int totalW = emojiW + 8 + textW;
                int startX = (w - totalW) / 2;
                int baseY  = (h + fmT.getAscent() - fmT.getDescent()) / 2;

                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                g2.drawString(emoji, startX, baseY);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString(text, startX + emojiW + 8, baseY);
            }
        };

        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setPreferredSize(new Dimension(220, 44));

        btn.setForeground(Color.WHITE);
        btn.setBackground(CLR_SIDEBAR);
        btn.setBorder(BorderFactory.createEmptyBorder(11, 20, 11, 20));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(51, 65, 85));
                btn.setForeground(new Color(250, 250, 255));
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(99, 102, 241)),
                    BorderFactory.createEmptyBorder(11, 17, 11, 20)
                ));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(CLR_SIDEBAR);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createEmptyBorder(11, 20, 11, 20));
            }
            public void mousePressed(MouseEvent e) {
                btn.setBackground(new Color(99, 102, 241));
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(165, 180, 252)),
                    BorderFactory.createEmptyBorder(11, 16, 11, 20)
                ));
            }
            public void mouseReleased(MouseEvent e) {
                btn.setBackground(new Color(129, 140, 248));
                SwingUtilities.invokeLater(() -> {
                    javax.swing.Timer t = new javax.swing.Timer(120, ev -> {
                        btn.setBackground(new Color(51, 65, 85));
                        btn.setForeground(new Color(250, 250, 255));
                        btn.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(99, 102, 241)),
                            BorderFactory.createEmptyBorder(11, 17, 11, 20)
                        ));
                    });
                    t.setRepeats(false);
                    t.start();
                });
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentPanel, card));
        return btn;
    }

    // ══════════════════════════════════════════════════
    //  CARD CONTENT
    // ══════════════════════════════════════════════════
    private JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CLR_BG);
        contentPanel.add(buildDashboard(), "DASHBOARD");
        contentPanel.add(buildStudentsPanel(), "STUDENTS");
        contentPanel.add(buildSubjectsPanel(), "SUBJECTS");
        contentPanel.add(buildGradesPanel(),   "GRADES");
        contentPanel.add(buildReportsPanel(),  "REPORTS");
        return contentPanel;
    }

    // ──────────────────────────────────────────────────
    //  PAGE HEADER HELPER
    // ──────────────────────────────────────────────────
    private JPanel pageHeader(String icon, String title, String subtitle) {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(CLR_WHITE);
        hdr.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(20, 28, 20, 28)
        ));

        // Row 1: emoji icon + title side by side
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setBackground(CLR_WHITE);

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));  // emoji font
        ico.setForeground(CLR_TEXT);

        JLabel t = new JLabel(title);
        t.setFont(FNT_TITLE);
        t.setForeground(CLR_TEXT);

        titleRow.add(ico);
        titleRow.add(t);

        JLabel s = new JLabel(subtitle);
        s.setFont(FNT_LABEL);
        s.setForeground(CLR_MUTED);

        JPanel left = new JPanel();
        left.setBackground(CLR_WHITE);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(titleRow);
        left.add(Box.createVerticalStrut(3));
        left.add(s);

        hdr.add(left, BorderLayout.WEST);
        return hdr;
    }

    // ══════════════════════════════════════════════════
    //  DASHBOARD
    // ══════════════════════════════════════════════════
    private JPanel buildDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CLR_BG);
        panel.add(pageHeader("\uD83C\uDFE0", "Dashboard", "Overview of the Grade Calculator System"), BorderLayout.NORTH);

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(CLR_BG);
        body.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill   = GridBagConstraints.BOTH;

        // Stat cards
        int students = dao.getAllStudents().size();
        int subjects = dao.getAllSubjects().size();

        g.gridx=0; g.gridy=0; g.weightx=1; g.weighty=0.3;
        body.add(statCard("\uD83D\uDC65", "Total Students", String.valueOf(students), new Color(99,102,241)), g);
        g.gridx=1;
        body.add(statCard("\uD83D\uDCD6", "Total Subjects", String.valueOf(subjects), new Color(16,185,129)), g);
        g.gridx=2;
        body.add(statCard("\uD83D\uDCDD", "Grade Formula",  "20|20|20|40",            new Color(245,158,11)), g);
        g.gridx=3;
        body.add(statCard("\u2705",       "Passing Grade",  "75.00",                  new Color(239,68,68)),  g);

        // Formula card
        g.gridx=0; g.gridy=1; g.gridwidth=4; g.weighty=0.7;
        body.add(buildFormulaCard(), g);

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel statCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CLR_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(226,232,240), 1, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        top.setBackground(CLR_WHITE);
        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        top.add(ico);
        JLabel lbl = new JLabel("  " + label);
        lbl.setFont(FNT_LABEL);
        lbl.setForeground(CLR_MUTED);
        top.add(lbl);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 32));
        val.setForeground(color);
        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFormulaCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CLR_WHITE);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(226,232,240), 1, true),
            BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setBackground(CLR_WHITE);
        JLabel fIco = new JLabel("\uD83D\uDCD0");
        fIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        fIco.setForeground(CLR_TEXT);
        JLabel title = new JLabel("Grade Computation Formula");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(CLR_TEXT);
        titleRow.add(fIco);
        titleRow.add(title);

        String html = "<html><body style='font-family:Segoe UI;'>" +
            "<br><b>Final Grade = (Prelim × 20%) + (Midterm × 20%) + (Pre-Final × 20%) + (Final Exam × 40%)</b><br><br>" +
            "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse;width:80%;'>" +
            "<tr style='background:#f8fafc;'><th>Period</th><th>Weight</th><th>Description</th></tr>" +
            "<tr><td>Prelim</td><td><b>20%</b></td><td>First period examination</td></tr>" +
            "<tr><td>Midterm</td><td><b>20%</b></td><td>Second period examination</td></tr>" +
            "<tr><td>Pre-Final</td><td><b>20%</b></td><td>Third period examination</td></tr>" +
            "<tr><td>Final Exam</td><td><b>40%</b></td><td>Final examination (highest weight)</td></tr>" +
            "</table><br>" +
            "<span style='color:#dc2626;'><b>Passing Mark: 75.00 — Remarks: PASSED / FAILED</b></span>" +
            "</body></html>";

        JLabel body = new JLabel(html);
        body.setFont(FNT_LABEL);

        card.add(titleRow, BorderLayout.NORTH);
        card.add(body,  BorderLayout.CENTER);
        return card;
    }

    // ══════════════════════════════════════════════════
    //  STUDENTS PANEL
    // ══════════════════════════════════════════════════
    private JPanel buildStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CLR_BG);
        panel.add(pageHeader("\uD83D\uDC65", "Student Management", "Add, edit, or remove students"), BorderLayout.NORTH);

        // ── Form ─────────────────────────────────────
        JTextField txtId    = styledField("e.g. 2024-0001");
        JTextField txtName  = styledField("Full name");
        JTextField txtCourse= styledField("e.g. BSCS");
        JTextField txtYear  = styledField("e.g. 1st Year");

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(CLR_WHITE);
        formCard.setBorder(new CompoundBorder(
            new LineBorder(new Color(226,232,240), 1),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8); g.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formCard, g, 0, "Student ID*", txtId);
        addFormRow(formCard, g, 1, "Full Name*",  txtName);
        addFormRow(formCard, g, 2, "Course",      txtCourse);
        addFormRow(formCard, g, 3, "Year Level",  txtYear);

        // ── Table ────────────────────────────────────
        String[] cols = {"ID","Student No.","Full Name","Course","Year Level"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshStudentTable(model);

        // ── Buttons ───────────────────────────────────
        JButton btnAdd    = accentBtn("\u2795 Add Student", CLR_ACCENT);
        JButton btnUpdate = accentBtn("\u270F Update",      new Color(245,158,11));
        JButton btnDelete = accentBtn("\uD83D\uDDD1 Delete", CLR_DANGER);
        JButton btnClear  = accentBtn("\u2716 Clear",       CLR_MUTED);

        btnAdd.addActionListener(e -> {
            if (txtId.getText().isBlank() || txtName.getText().isBlank()) {
                showWarn("Student ID and Full Name are required."); return;
            }
            Student s = new Student(0, txtId.getText().trim(),
                txtName.getText().trim(), txtCourse.getText().trim(), txtYear.getText().trim());
            if (dao.addStudent(s)) {
                showSuccess("Student added!"); refreshStudentTable(model);
                clearFields(txtId, txtName, txtCourse, txtYear);
            } else showError("Could not add student. ID may already exist.");
        });

        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showWarn("Select a student to update."); return; }
            int dbId = (int) model.getValueAt(row, 0);
            Student s = new Student(dbId, txtId.getText().trim(),
                txtName.getText().trim(), txtCourse.getText().trim(), txtYear.getText().trim());
            if (dao.updateStudent(s)) {
                showSuccess("Student updated!"); refreshStudentTable(model);
                clearFields(txtId, txtName, txtCourse, txtYear);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showWarn("Select a student to delete."); return; }
            int dbId = (int) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this,
                    "Delete this student and all their grades?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dao.deleteStudent(dbId); refreshStudentTable(model);
                clearFields(txtId, txtName, txtCourse, txtYear);
            }
        });

        btnClear.addActionListener(e -> clearFields(txtId, txtName, txtCourse, txtYear));

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtId    .setText((String) model.getValueAt(row, 1));
                txtName  .setText((String) model.getValueAt(row, 2));
                txtCourse.setText((String) model.getValueAt(row, 3));
                txtYear  .setText((String) model.getValueAt(row, 4));
            }
        });

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnBar.setBackground(CLR_WHITE);
        btnBar.add(btnAdd); btnBar.add(btnUpdate);
        btnBar.add(btnDelete); btnBar.add(btnClear);

        g.gridx=0; g.gridy=4; g.gridwidth=4;
        formCard.add(btnBar, g);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(CLR_BG);
        left.setBorder(BorderFactory.createEmptyBorder(16,16,16,8));
        left.setPreferredSize(new Dimension(420, 0));
        left.add(formCard, BorderLayout.NORTH);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(CLR_BG);
        right.setBorder(BorderFactory.createEmptyBorder(16,8,16,16));
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(CLR_BG);
        body.add(left,  BorderLayout.WEST);
        body.add(right, BorderLayout.CENTER);

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════
    //  SUBJECTS PANEL
    // ══════════════════════════════════════════════════
    private JPanel buildSubjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CLR_BG);
        panel.add(pageHeader("\uD83D\uDCD6", "Subject Management", "Manage course subjects"), BorderLayout.NORTH);

        JTextField txtCode  = styledField("e.g. MATH101");
        JTextField txtName  = styledField("e.g. Mathematics 1");
        JSpinner   spnUnits = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
        spnUnits.setFont(FNT_LABEL);

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(CLR_WHITE);
        formCard.setBorder(new CompoundBorder(
            new LineBorder(new Color(226,232,240), 1),
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,8,6,8); g.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(formCard, g, 0, "Subject Code*", txtCode);
        addFormRow(formCard, g, 1, "Subject Name*", txtName);

        g.gridx=0; g.gridy=2; g.weightx=0;
        formCard.add(styledLbl("Units"), g);
        g.gridx=1; g.weightx=1;
        formCard.add(spnUnits, g);

        String[] cols = {"ID","Code","Subject Name","Units"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);
        refreshSubjectTable(model);

        JButton btnAdd     = accentBtn("\u2795 Add Subject",  CLR_ACCENT);
        JButton btnDelete  = accentBtn("\uD83D\uDDD1 Delete",  CLR_DANGER);
        JButton btnRefresh = accentBtn("\uD83D\uDD04 Refresh", new Color(16, 185, 129));
        JButton btnClear   = accentBtn("\u2716 Clear",         CLR_MUTED);

        btnAdd.addActionListener(e -> {
            if (txtCode.getText().isBlank() || txtName.getText().isBlank()) {
                showWarn("Code and Name are required."); return;
            }
            Subject s = new Subject(0, txtCode.getText().trim(),
                txtName.getText().trim(), (int) spnUnits.getValue());
            if (dao.addSubject(s)) {
                showSuccess("Subject added!"); refreshSubjectTable(model);
                clearFields(txtCode, txtName);
            } else {
                showError("Could not add subject. Code may already exist.");
            }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { showWarn("Select a subject to delete."); return; }
            int id = (int) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete this subject?",
                    "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dao.deleteSubject(id); refreshSubjectTable(model);
            }
        });

        btnRefresh.addActionListener(e -> {
            refreshSubjectTable(model);
            showSuccess("Subject list refreshed!");
        });

        btnClear.addActionListener(e -> {
            clearFields(txtCode, txtName);
            spnUnits.setValue(3);
            table.clearSelection();
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                txtCode.setText((String) model.getValueAt(row, 1));
                txtName.setText((String) model.getValueAt(row, 2));
                spnUnits.setValue(model.getValueAt(row, 3));
            }
        });

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnBar.setBackground(CLR_WHITE);
        btnBar.add(btnAdd); btnBar.add(btnDelete);
        btnBar.add(btnRefresh); btnBar.add(btnClear);
        g.gridx=0; g.gridy=3; g.gridwidth=4;
        formCard.add(btnBar, g);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(CLR_BG);
        left.setBorder(BorderFactory.createEmptyBorder(16,16,16,8));
        left.setPreferredSize(new Dimension(400, 0));
        left.add(formCard, BorderLayout.NORTH);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(CLR_BG);
        right.setBorder(BorderFactory.createEmptyBorder(16,8,16,16));
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.add(left, BorderLayout.WEST);
        body.add(right, BorderLayout.CENTER);
        body.setBackground(CLR_BG);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════
    //  GRADES PANEL
    // ══════════════════════════════════════════════════
    private JPanel buildGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CLR_BG);
        panel.add(pageHeader("\uD83D\uDCDD", "Enter Grades", "Record student grades per subject"), BorderLayout.NORTH);

        // Student selector
        JComboBox<Student>  cbStudent = new JComboBox<>();
        JComboBox<Subject>  cbSubject = new JComboBox<>();
        JTextField txtPrelim   = styledField("0 - 100");
        JTextField txtMid      = styledField("0 - 100");
        JTextField txtPreFinal = styledField("0 - 100");
        JTextField txtFinal    = styledField("0 - 100");
        JLabel     lblComputed = new JLabel("—");
        lblComputed.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblComputed.setForeground(CLR_ACCENT);
        JLabel lblRemarks = new JLabel("");
        lblRemarks.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Populate combos
        for (Student s : dao.getAllStudents()) cbStudent.addItem(s);
        for (Subject s : dao.getAllSubjects()) cbSubject.addItem(s);
        cbStudent.setFont(FNT_LABEL); cbSubject.setFont(FNT_LABEL);

        // Grade table
        String[] cols = {"Subject Code","Subject Name","Units","Prelim","Midterm","Pre-Final","Final","Grade","Remarks"};
        DefaultTableModel gradeModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable gradeTable = styledTable(gradeModel);

        JLabel lblGWA = new JLabel("GWA: —");
        lblGWA.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGWA.setForeground(CLR_ACCENT);

        // Live compute
        ActionListener compute = e -> {
            try {
                double p  = Double.parseDouble(txtPrelim  .getText().trim());
                double m  = Double.parseDouble(txtMid     .getText().trim());
                double pf = Double.parseDouble(txtPreFinal.getText().trim());
                double f  = Double.parseDouble(txtFinal   .getText().trim());
                if (p<0||p>100||m<0||m>100||pf<0||pf>100||f<0||f>100) throw new NumberFormatException();
                double grade = p*0.20 + m*0.20 + pf*0.20 + f*0.40;
                lblComputed.setText(String.format("%.2f", grade));
                lblComputed.setForeground(grade >= 75 ? CLR_SUCCESS : CLR_DANGER);
                lblRemarks.setText(grade >= 75 ? "\u2705 PASSED" : "\u274C FAILED");
                lblRemarks.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
                lblRemarks.setForeground(grade >= 75 ? CLR_SUCCESS : CLR_DANGER);
            } catch (NumberFormatException ex) {
                lblComputed.setText("—");
                lblComputed.setForeground(CLR_ACCENT);
                lblRemarks.setText("");
            }
        };
        txtPrelim.addKeyListener(kl(compute));
        txtMid.addKeyListener(kl(compute));
        txtPreFinal.addKeyListener(kl(compute));
        txtFinal.addKeyListener(kl(compute));

        // Reload table when student changes
        cbStudent.addActionListener(e -> {
            Student sel = (Student) cbStudent.getSelectedItem();
            if (sel != null) {
                Object[][] rows = dao.getGradesByStudent(sel.getId());
                gradeModel.setRowCount(0);
                for (Object[] r : rows) gradeModel.addRow(r);
                double gwa = dao.getGWA(sel.getId());
                lblGWA.setText(gwa > 0 ? String.format("GWA: %.2f", gwa) : "GWA: —");
                colorizeRemarks(gradeTable, gradeModel);
            }
        });

        JButton btnSave  = accentBtn("\uD83D\uDCBE Save Grade", CLR_ACCENT);
        JButton btnClear = accentBtn("\u2716 Clear",            CLR_MUTED);

        btnSave.addActionListener(e -> {
            Student sel = (Student) cbStudent.getSelectedItem();
            Subject sub = (Subject) cbSubject.getSelectedItem();
            if (sel == null || sub == null) { showWarn("Select student and subject."); return; }
            try {
                double p  = Double.parseDouble(txtPrelim  .getText().trim());
                double m  = Double.parseDouble(txtMid     .getText().trim());
                double pf = Double.parseDouble(txtPreFinal.getText().trim());
                double f  = Double.parseDouble(txtFinal   .getText().trim());
                if (p<0||p>100||m<0||m>100||pf<0||pf>100||f<0||f>100)
                    throw new NumberFormatException();
                if (dao.saveGrade(sel.getId(), sub.getId(), p, m, pf, f)) {
                    showSuccess("Grade saved!");
                    Object[][] rows = dao.getGradesByStudent(sel.getId());
                    gradeModel.setRowCount(0);
                    for (Object[] r : rows) gradeModel.addRow(r);
                    double gwa = dao.getGWA(sel.getId());
                    lblGWA.setText(String.format("GWA: %.2f", gwa));
                    colorizeRemarks(gradeTable, gradeModel);
                    clearFields(txtPrelim, txtMid, txtPreFinal, txtFinal);
                    lblComputed.setText("—"); lblRemarks.setText("");
                }
            } catch (NumberFormatException ex) {
                showWarn("Enter valid scores (0–100) for all periods.");
            }
        });
        btnClear.addActionListener(e -> {
            clearFields(txtPrelim, txtMid, txtPreFinal, txtFinal);
            lblComputed.setText("—"); lblRemarks.setText("");
        });

        // ── Build form card ────────────────────────
        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(CLR_WHITE);
        formCard.setBorder(new CompoundBorder(
            new LineBorder(new Color(226,232,240), 1),
            BorderFactory.createEmptyBorder(20,24,20,24)
        ));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,8,6,8); gc.fill = GridBagConstraints.HORIZONTAL;

        addFormRowComp(formCard, gc, 0, "Student",   cbStudent);
        addFormRowComp(formCard, gc, 1, "Subject",   cbSubject);
        addFormRow(formCard, gc, 2, "Prelim",    txtPrelim);
        addFormRow(formCard, gc, 3, "Midterm",   txtMid);
        addFormRow(formCard, gc, 4, "Pre-Final", txtPreFinal);
        addFormRow(formCard, gc, 5, "Final Exam",txtFinal);

        // Computed preview
        JPanel preview = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        preview.setBackground(CLR_WHITE);
        preview.add(styledLbl("Computed:")); preview.add(lblComputed); preview.add(lblRemarks);
        gc.gridx=0; gc.gridy=6; gc.gridwidth=4; formCard.add(preview, gc);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnBar.setBackground(CLR_WHITE);
        btnBar.add(btnSave); btnBar.add(btnClear);
        gc.gridy=7; formCard.add(btnBar, gc);

        // ── Right side: grade table + GWA ─────────
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(CLR_BG);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        JPanel gwaBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        gwaBar.setBackground(CLR_BG);
        gwaBar.add(lblGWA);

        tablePanel.add(new JScrollPane(gradeTable), BorderLayout.CENTER);
        tablePanel.add(gwaBar, BorderLayout.SOUTH);

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(CLR_BG);
        left.setBorder(BorderFactory.createEmptyBorder(16,16,16,8));
        left.setPreferredSize(new Dimension(400, 0));
        left.add(formCard, BorderLayout.NORTH);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(CLR_BG);
        right.setBorder(BorderFactory.createEmptyBorder(16,8,16,16));
        right.add(tablePanel, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(CLR_BG);
        body.add(left, BorderLayout.WEST);
        body.add(right, BorderLayout.CENTER);

        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════
    //  REPORTS PANEL
    // ══════════════════════════════════════════════════
    private JPanel buildReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CLR_BG);
        panel.add(pageHeader("\uD83D\uDCCA", "Grade Reports", "Full grade report for all students"), BorderLayout.NORTH);

        String[] cols = {"Stud. No.","Name","Course","Code","Subject",
                         "Prelim","Midterm","Pre-Final","Final Exam","Grade","Remarks"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = styledTable(model);

        JButton btnRefresh = accentBtn("\uD83D\uDD04 Refresh Report", CLR_ACCENT);
        btnRefresh.addActionListener(e -> {
            model.setRowCount(0);
            for (Object[] row : dao.getFullReport()) model.addRow(row);
            colorizeRemarks(table, model);
        });

        // Initial load
        for (Object[] row : dao.getFullReport()) model.addRow(row);
        colorizeRemarks(table, model);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        toolbar.setBackground(CLR_BG);
        toolbar.setBorder(BorderFactory.createEmptyBorder(4, 16, 0, 16));
        toolbar.add(btnRefresh);

        panel.add(toolbar, BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(CLR_BG);
        body.setBorder(BorderFactory.createEmptyBorder(0,16,16,16));
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    // ══════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════
    private void refreshStudentTable(DefaultTableModel m) {
        m.setRowCount(0);
        for (Student s : dao.getAllStudents())
            m.addRow(new Object[]{s.getId(), s.getStudentId(),
                s.getFullName(), s.getCourse(), s.getYearLevel()});
    }

    private void refreshSubjectTable(DefaultTableModel m) {
        m.setRowCount(0);
        for (Subject s : dao.getAllSubjects())
            m.addRow(new Object[]{s.getId(), s.getSubjectCode(),
                s.getSubjectName(), s.getUnits()});
    }

    private void colorizeRemarks(JTable table, DefaultTableModel model) {
        int remarksCol = model.getColumnCount() - 1;
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                String remarks = (String) model.getValueAt(row, remarksCol);
                if (!sel) {
                    setBackground(row % 2 == 0 ? CLR_WHITE : new Color(248,250,252));
                    if (col == remarksCol) {
                        setForeground("PASSED".equals(remarks) ? CLR_SUCCESS : CLR_DANGER);
                        setFont(FNT_BTN);
                    } else {
                        setForeground(CLR_TEXT);
                        setFont(FNT_LABEL);
                    }
                } else {
                    setBackground(new Color(224,231,255));
                    setForeground(CLR_TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        table.repaint();
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField(18);
        f.setFont(FNT_LABEL);
        f.setToolTipText(placeholder);
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(203,213,225), 1, true),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return f;
    }

    private JLabel styledLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FNT_LABEL);
        l.setForeground(CLR_MUTED);
        return l;
    }

    /**
     * Clean, stable accent button.
     * Uses two-JLabel panel trick for emoji + text so sizing is always correct.
     */
    private JButton accentBtn(String rawText, Color bg) {
        // Separate emoji from label text
        final String emoji;
        final String labelText;
        if (rawText.length() > 1 &&
            (Character.isHighSurrogate(rawText.charAt(0)) || rawText.charAt(0) > 0x2000)) {
            int end = (rawText.length() > 1 && Character.isSurrogatePair(rawText.charAt(0), rawText.charAt(1))) ? 2 : 1;
            emoji     = rawText.substring(0, end).trim();
            labelText = rawText.substring(end).trim();
        } else {
            emoji     = "";
            labelText = rawText.trim();
        }

        // Inner panel holds emoji + text labels
        JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        inner.setOpaque(false);

        if (!emoji.isEmpty()) {
            JLabel emojiLbl = new JLabel(emoji);
            emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            emojiLbl.setForeground(Color.WHITE);
            inner.add(emojiLbl);
        }

        JLabel textLbl = new JLabel(labelText);
        textLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        textLbl.setForeground(Color.WHITE);
        inner.add(textLbl);

        // Button with rounded painted background
        JButton b = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };

        b.setLayout(new BorderLayout());
        b.add(inner, BorderLayout.CENTER);

        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        // Fixed consistent height — width expands to fit text
        b.setPreferredSize(new Dimension(b.getPreferredSize().width, 36));
        b.setMinimumSize(new Dimension(80, 36));
        b.setMaximumSize(new Dimension(300, 36));

        b.addMouseListener(new MouseAdapter() {
            Color current = bg;
            public void mouseEntered(MouseEvent e) {
                current = bg.darker();
                b.setBackground(current);
                updateChildColors(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                current = bg;
                b.setBackground(bg);
                updateChildColors(Color.WHITE);
            }
            public void mousePressed(MouseEvent e) {
                b.setBackground(bg.darker().darker());
                // shrink effect
                b.setBorder(BorderFactory.createEmptyBorder(9, 15, 7, 15));
                b.revalidate();
            }
            public void mouseReleased(MouseEvent e) {
                b.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
                b.setBackground(bg.darker());
                javax.swing.Timer t = new javax.swing.Timer(120, ev -> {
                    b.setBackground(bg);
                    b.revalidate();
                });
                t.setRepeats(false);
                t.start();
            }
            private void updateChildColors(Color c) {
                for (Component comp : inner.getComponents()) {
                    if (comp instanceof JLabel) ((JLabel)comp).setForeground(c);
                }
            }
        });

        return b;
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(FNT_LABEL);
        t.setRowHeight(34);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,0));
        t.setSelectionBackground(new Color(224,231,255));
        t.setSelectionForeground(CLR_TEXT);
        t.setFillsViewportHeight(true);
        JTableHeader h = t.getTableHeader();
        h.setBackground(CLR_HEADER);
        h.setForeground(CLR_MUTED);
        h.setFont(new Font("Segoe UI", Font.BOLD, 12));
        h.setBorder(new MatteBorder(0,0,2,0, new Color(226,232,240)));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable tbl, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl,val,sel,foc,row,col);
                if (!sel) setBackground(row%2==0 ? CLR_WHITE : new Color(248,250,252));
                setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
                return this;
            }
        });
        return t;
    }

    private void addFormRow(JPanel p, GridBagConstraints g, int row, String label, JTextField field) {
        g.gridx=0; g.gridy=row; g.weightx=0; g.gridwidth=1;
        p.add(styledLbl(label), g);
        g.gridx=1; g.weightx=1; g.gridwidth=3;
        p.add(field, g);
    }

    private void addFormRowComp(JPanel p, GridBagConstraints g, int row, String label, JComponent comp) {
        g.gridx=0; g.gridy=row; g.weightx=0; g.gridwidth=1;
        p.add(styledLbl(label), g);
        g.gridx=1; g.weightx=1; g.gridwidth=3;
        p.add(comp, g);
    }

    private void clearFields(JTextField... fields) {
        for (JTextField f : fields) f.setText("");
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private KeyListener kl(ActionListener al) {
        return new KeyAdapter() {
            public void keyReleased(KeyEvent e) { al.actionPerformed(null); }
        };
    }

    // ══════════════════════════════════════════════════
    //  MAIN ENTRY POINT
    // ══════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Panel.background",    CLR_BG);
            UIManager.put("Button.arc",          10);
            UIManager.put("ScrollPane.border",   BorderFactory.createLineBorder(new Color(226,232,240)));
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(MainFrame::new);
    }
}