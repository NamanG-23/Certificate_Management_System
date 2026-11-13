package Files;
import Common.*;
import DatabaseConnectivity.dbConnect;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class EditCertificates extends JFrame {
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
    private final Color navyColor = new Color(21, 33, 66);
    private final Color blueColor = new Color(41, 98, 255);
    private final Color blueHover = new Color(25, 75, 210);
    private final Color greyColor = new Color(110, 110, 110);
    private final Color greyHover = new Color(80, 80, 80);
    private final Color bgColor = new Color(245, 245, 245);
    private final String userName;
    private final int scid;
    private final CertificateFormPanel form = new CertificateFormPanel();
    private final JLabel scidValue = new JLabel("-");
    private final JLabel createdValue = new JLabel("-");
    private final JLabel updatedValue = new JLabel("-");
    private JLabel errorLabel;
    private JButton saveBtn;
    public EditCertificates(String userName, int scid) {
        this.userName = userName;
        this.scid = scid;
        setTitle("Certificate Management System - Edit Certificate");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(Header.getHeaderPanel(), BorderLayout.NORTH);
        JPanel barAndInfo = new JPanel(new BorderLayout());
        barAndInfo.add(createTitleBar(), BorderLayout.NORTH);
        barAndInfo.add(createInfoStrip(), BorderLayout.SOUTH);
        topPanel.add(barAndInfo, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(Footer.getFooterPanel(), BorderLayout.SOUTH);
        loadCertificate();
    }
    private JPanel createTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(navyColor);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Edit Certificate");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel user = new JLabel("Welcome, " + userName);
        user.setFont(new Font("SansSerif", Font.PLAIN, 14));
        user.setForeground(Color.WHITE);
        bar.add(title, BorderLayout.WEST);
        bar.add(user, BorderLayout.EAST);
        return bar;
    }
    private JPanel createInfoStrip() {
        JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 8));
        strip.setBackground(Color.WHITE);
        strip.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        strip.add(createInfoItem("Certificate Ref ID (SCID):", scidValue));
        strip.add(createInfoItem("Added On:", createdValue));
        strip.add(createInfoItem("Last Updated:", updatedValue));
        return strip;
    }
    private JPanel createInfoItem(String title, JLabel value) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setForeground(new Color(90, 90, 90));
        value.setFont(new Font("SansSerif", Font.PLAIN, 12));
        value.setForeground(navyColor);
        item.add(t);
        item.add(value);
        return item;
    }
    private JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(bgColor);
        content.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(bgColor);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        content.add(scroll, BorderLayout.CENTER);
        content.add(createButtonBar(), BorderLayout.SOUTH);
        return content;
    }
    private JPanel createButtonBar() {
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setOpaque(false);
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn = CertificateFormPanel.createButton("Save Changes", blueColor, blueHover, 170, 40);
        saveBtn.addActionListener(e -> handleSave());
        JButton backBtn = CertificateFormPanel.createButton("Back to Certificates", greyColor, greyHover, 200, 40);
        backBtn.addActionListener(e -> {
            dispose();
            new ViewCertificates(userName).setVisible(true);
        });
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttons.add(saveBtn);
        buttons.add(backBtn);
        bar.add(Box.createVerticalStrut(6));
        bar.add(errorLabel);
        bar.add(Box.createVerticalStrut(6));
        bar.add(buttons);
        return bar;
    }

    private void loadCertificate() {
        String sql = "SELECT * FROM certificate_details WHERE scid = ? AND user_id = ?";
        try (Connection con = dbConnect.getConnection()) {
            int userId = CertificateFormPanel.findUserId(con, userName);
            if (userId < 0) {
                saveBtn.setEnabled(false);
                JOptionPane.showMessageDialog(this, "User session invalid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, scid);
                ps.setInt(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        saveBtn.setEnabled(false);
                        JOptionPane.showMessageDialog(this,
                                "Certificate not found or access denied.",
                                "Edit Certificate", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    scidValue.setText(String.valueOf(rs.getInt("scid")));
                    Timestamp created = rs.getTimestamp("created_date");
                    Timestamp updated = rs.getTimestamp("updated_date");
                    createdValue.setText(created == null ? "-" : created.toLocalDateTime().format(DATE_TIME_FMT));
                    updatedValue.setText(updated == null ? "-" : updated.toLocalDateTime().format(DATE_TIME_FMT));

                    form.nameField.setText(rs.getString("certificate_name"));
                    form.issuedByField.setText(rs.getString("issued_by"));
                    form.setIssueDate(rs.getDate("issue_date").toLocalDate());
                    form.typeBox.setSelectedItem(rs.getString("achievement_type"));
                    form.categoryBox.setSelectedItem(rs.getString("category"));
                    form.modeBox.setSelectedItem(rs.getString("mode"));

                    java.sql.Date expiry = rs.getDate("expiry_date");
                    form.setExpiryDate(expiry == null ? null : expiry.toLocalDate());
                    form.courseField.setText(nullToEmpty(rs.getString("course_name")));
                    form.certIdField.setText(nullToEmpty(rs.getString("certificate_id")));
                    form.gradeField.setText(nullToEmpty(rs.getString("grade_score")));
                    form.remarksArea.setText(nullToEmpty(rs.getString("remarks")));
                    form.imageField.setText(nullToEmpty(rs.getString("certificate_path")));
                }
            }
        } catch (SQLException ex) {
            saveBtn.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Edit Certificate", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSave() {
        String error = form.validateForm();
        if (error != null) {
            errorLabel.setText(error);
            return;
        }
        errorLabel.setText(" ");
        String sql = "UPDATE certificate_details SET certificate_name = ?, issued_by = ?, issue_date = ?, achievement_type = ?, category = ?, mode = ?, expiry_date = ?, course_name = ?, certificate_id = ?, grade_score = ?, remarks = ?, certificate_path = ? WHERE scid = ? AND user_id = ?";
        try (Connection con = dbConnect.getConnection()) {
            int userId = CertificateFormPanel.findUserId(con, userName);
            if(userId < 0) return;
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int next = form.bindValues(ps);
                ps.setInt(next, scid);
                ps.setInt(next + 1, userId);
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Certificate updated successfully.",
                            "Edit Certificate", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new ViewCertificates(userName).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Certificate could not be updated. Please try again.",
                            "Edit Certificate", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Edit Certificate", JOptionPane.ERROR_MESSAGE);
        }
    }
    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}