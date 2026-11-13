package Files;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class CertificateFormPanel extends JPanel implements Scrollable {
    static final String[] TYPES = {"Select Type", "Course", "Internship", "Workshop", "Hackathon", "Competition", "Other"};
    static final String[] CATEGORIES = {"Select Category", "Technical", "Academic", "Sports", "Cultural", "Other"};
    static final String[] MODES = {"Select Mode", "Online", "Offline", "Hybrid"};
    private static final int FIELD_HEIGHT = 34;
    private static final int LABEL_WIDTH = 150;
    private static final Color HINT_COLOR = new Color(150, 150, 150);
    private final Color navyColor = new Color(21, 33, 66);

    static class HintTextField extends JTextField {
        private final String hint;
        HintTextField(String hint) {
            this.hint = hint;
            setMargin(new Insets(2, 8, 2, 8));
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(HINT_COLOR);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets in = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(hint, in.left + 2, y);
                g2.dispose();
            }
        }
    }
    static class HintTextArea extends JTextArea {
        private final String hint;
        HintTextArea(String hint, int rows, int cols) {
            super(rows, cols);
            this.hint = hint;
            setMargin(new Insets(6, 8, 6, 8));
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(HINT_COLOR);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                Insets in = getInsets();
                g2.drawString(hint, in.left + 2, in.top + g2.getFontMetrics().getAscent());
                g2.dispose();
            }
        }
    }

    final JTextField nameField = new HintTextField("e.g. Java Programming Certification");
    final JTextField issuedByField = new HintTextField("e.g. NPTEL, Coursera, Google");
    final JSpinner issueDateSpinner = createDateSpinner();
    final JComboBox<String> typeBox = new JComboBox<>(TYPES);
    final JComboBox<String> categoryBox = new JComboBox<>(CATEGORIES);
    final JComboBox<String> modeBox = new JComboBox<>(MODES);
    final JSpinner expiryDateSpinner = createDateSpinner();
    final JCheckBox lifetimeCheck = new JCheckBox("No Expiry (Lifetime)");
    final JTextField courseField = new HintTextField("e.g. Python for Everybody");
    final JTextField certIdField = new HintTextField("e.g. ABC-12345");
    final JTextField gradeField = new HintTextField("e.g. A+ or 92%");
    final JTextField imageField = new HintTextField("No image selected (jpg, jpeg, png)");
    final JTextArea remarksArea = new HintTextArea("Any extra notes (max 255 characters)", 3, 20);

    public CertificateFormPanel() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        JComponent[] inputs = {nameField, issuedByField, issueDateSpinner, typeBox,
                categoryBox, modeBox, expiryDateSpinner, courseField, certIdField,
                gradeField, imageField};
        for (JComponent c : inputs) {
            c.setPreferredSize(new Dimension(220, FIELD_HEIGHT));
            c.setMinimumSize(new Dimension(140, FIELD_HEIGHT));
        }
        imageField.setEditable(false);
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        setSpinnerDate(expiryDateSpinner, LocalDate.now().plusYears(1));
        lifetimeCheck.setOpaque(false);
        lifetimeCheck.setFont(new Font("SansSerif", Font.BOLD, 13));
        lifetimeCheck.addActionListener(e -> setExpiryEnabled(!lifetimeCheck.isSelected()));

        JButton browseBtn = new JButton("Browse");
        browseBtn.setFocusPainted(false);
        browseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        browseBtn.setPreferredSize(new Dimension(100, FIELD_HEIGHT));
        browseBtn.addActionListener(e -> chooseImage());

        JPanel imagePanel = new JPanel(new BorderLayout(8, 0));
        imagePanel.setOpaque(false);
        imagePanel.add(imageField, BorderLayout.CENTER);
        imagePanel.add(browseBtn, BorderLayout.EAST);
        imagePanel.setMinimumSize(new Dimension(250, FIELD_HEIGHT));
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksScroll.setPreferredSize(new Dimension(220, 80));
        remarksScroll.setMinimumSize(new Dimension(250, 80));

        int y = 0;
        addSection("Required Details", y++);

        placeLabel(label("Certificate Name", true), 0, y);
        placeField(nameField, 1, y, 1);
        placeLabel(label("Issued By", true), 2, y);
        placeField(issuedByField, 3, y++, 1);
        placeLabel(label("Issue Date", true), 0, y);
        placeField(issueDateSpinner, 1, y, 1);
        placeLabel(label("Achievement Type", true), 2, y);
        placeField(typeBox, 3, y++, 1);
        placeLabel(label("Category", true), 0, y);
        placeField(categoryBox, 1, y, 1);
        placeLabel(label("Mode", true), 2, y);
        placeField(modeBox, 3, y++, 1);
        placeLabel(label("Expiry Date", true), 0, y);
        placeField(expiryDateSpinner, 1, y, 1);
        placeField(lifetimeCheck, 2, y++, 2);
        addSection("Optional Details", y++);
        placeLabel(label("Course Name", false), 0, y);
        placeField(courseField, 1, y, 1);
        placeLabel(label("Certificate ID", false), 2, y);
        placeField(certIdField, 3, y++, 1);
        placeLabel(label("Grade / Score", false), 0, y);
        placeField(gradeField, 1, y++, 1);
        placeLabel(label("Certificate Image", false), 0, y);
        placeField(imagePanel, 1, y++, 3);
        placeLabel(label("Remarks", false), 0, y);
        placeField(remarksScroll, 1, y, 3);
    }

    public static int findUserId(Connection con, String username) {
        String sql = "SELECT ID FROM login_info WHERE username = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void placeLabel(Component c, int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        add(c, g);
    }

    private void placeField(Component c, int x, int y, int width) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.gridwidth = width;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;
        add(c, g);
    }

    private void addSection(String text, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 15));
        l.setForeground(navyColor);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(0, 0, 4, 0)));
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = y;
        g.gridwidth = 4;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(y == 0 ? 0 : 18, 8, 4, 8);
        add(l, g);
    }

    private JLabel label(String text, boolean required) {
        JLabel l = new JLabel(required
                ? "<html><nobr>" + text + " <font color='red'>*</font></nobr></html>"
                : text);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        Dimension d = new Dimension(LABEL_WIDTH, FIELD_HEIGHT);
        l.setPreferredSize(d);
        l.setMinimumSize(d);
        return l;
    }

    private void chooseImage() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Image files (jpg, jpeg, png)", "jpg", "jpeg", "png"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            imageField.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private static JSpinner createDateSpinner() {
        JSpinner s = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        s.setEditor(new JSpinner.DateEditor(s, "dd-MM-yyyy"));
        return s;
    }

    private static LocalDate toLocalDate(JSpinner s) {
        return ((Date) s.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    private static void setSpinnerDate(JSpinner s, LocalDate d) {
        s.setValue(Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
    private static boolean commit(JSpinner s) {
        try {
            s.commitEdit();
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    private void setExpiryEnabled(boolean enabled) {
        expiryDateSpinner.setEnabled(enabled);
        ((JSpinner.DefaultEditor) expiryDateSpinner.getEditor()).getTextField().setEnabled(enabled);
    }
    public void setIssueDate(LocalDate d) {
        setSpinnerDate(issueDateSpinner, d);
    }
    public void setExpiryDate(LocalDate d) {
        if (d == null) {
            lifetimeCheck.setSelected(true);
            setExpiryEnabled(false);
        } else {
            lifetimeCheck.setSelected(false);
            setExpiryEnabled(true);
            setSpinnerDate(expiryDateSpinner, d);
        }
    }

    public String getCertName() { return nameField.getText().trim(); }
    public String getIssuedBy() { return issuedByField.getText().trim(); }
    public LocalDate getIssueDate() { return toLocalDate(issueDateSpinner); }
    public LocalDate getExpiryDate() {
        return lifetimeCheck.isSelected() ? null : toLocalDate(expiryDateSpinner);
    }
    public String getCourseName() { return emptyToNull(courseField.getText()); }
    public String getCertId() { return emptyToNull(certIdField.getText()); }
    public String getGrade() { return emptyToNull(gradeField.getText()); }
    public String getRemarks() { return emptyToNull(remarksArea.getText()); }
    public String getImagePath() { return emptyToNull(imageField.getText()); }

    private static String emptyToNull(String s) {
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // ----- Validation -----
    public String validateForm() {
        if (getCertName().isEmpty()) return "Certificate Name is required.";
        if (getCertName().length() > 100) return "Certificate Name must be 100 characters or less.";
        if (getIssuedBy().isEmpty()) return "Issued By is required.";
        if (getIssuedBy().length() > 100) return "Issued By must be 100 characters or less.";
        if (!commit(issueDateSpinner)) return "Please enter a valid Issue Date (dd-MM-yyyy).";
        if (getIssueDate().isAfter(LocalDate.now())) return "Issue Date cannot be in the future.";
        if (typeBox.getSelectedIndex() == 0) return "Please select an Achievement Type.";
        if (categoryBox.getSelectedIndex() == 0) return "Please select a Category.";
        if (modeBox.getSelectedIndex() == 0) return "Please select a Mode.";
        if (!lifetimeCheck.isSelected()) {
            if (!commit(expiryDateSpinner)) return "Please enter a valid Expiry Date (dd-MM-yyyy).";
            if (!getExpiryDate().isAfter(getIssueDate())) return "Expiry Date must be after the Issue Date.";
        }
        if (courseField.getText().trim().length() > 100) return "Course Name must be 100 characters or less.";
        if (certIdField.getText().trim().length() > 50) return "Certificate ID must be 50 characters or less.";
        if (gradeField.getText().trim().length() > 30) return "Grade / Score must be 30 characters or less.";
        if (remarksArea.getText().trim().length() > 255) return "Remarks must be 255 characters or less.";
        return null;
    }

    public int bindValues(PreparedStatement ps) throws SQLException {
        int i = 1;
        ps.setString(i++, getCertName());
        ps.setString(i++, getIssuedBy());
        ps.setDate(i++, java.sql.Date.valueOf(getIssueDate()));
        ps.setString(i++, (String) typeBox.getSelectedItem());
        ps.setString(i++, (String) categoryBox.getSelectedItem());
        ps.setString(i++, (String) modeBox.getSelectedItem());
        LocalDate expiry = getExpiryDate();
        if (expiry == null) {
            ps.setNull(i++, Types.DATE);
        } else {
            ps.setDate(i++, java.sql.Date.valueOf(expiry));
        }
        ps.setString(i++, getCourseName());
        ps.setString(i++, getCertId());
        ps.setString(i++, getGrade());
        ps.setString(i++, getRemarks());
        ps.setString(i++, getImagePath());
        return i;
    }

    static JButton createButton(String text, Color normal, Color hover, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(w, h));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hover);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(normal);
            }
        });
        return btn;
    }

    @Override public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }
    @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 16;
    }
    @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return orientation == SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
    }
    @Override public boolean getScrollableTracksViewportWidth() {
        return true;
    }
    @Override public boolean getScrollableTracksViewportHeight() {
        return getParent() instanceof JViewport
                && getParent().getHeight() > getPreferredSize().height;
    }
}