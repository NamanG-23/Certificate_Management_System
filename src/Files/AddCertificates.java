package Files;
import Common.*;
import DatabaseConnectivity.dbConnect;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddCertificates extends JFrame {
    private final Color navyColor = new Color(21, 33, 66);
    private final Color blueColor = new Color(41, 98, 255);
    private final Color blueHover = new Color(25, 75, 210);
    private final Color greyColor = new Color(110, 110, 110);
    private final Color greyHover = new Color(80, 80, 80);
    private final Color bgColor = new Color(245, 245, 245);
    private final String userName;
    private final CertificateFormPanel form = new CertificateFormPanel();
    private JLabel errorLabel;

    public AddCertificates(String userName) {
        this.userName = userName;
        setTitle("Certificate Management System - Add Certificate");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(Header.getHeaderPanel(), BorderLayout.NORTH);
        topPanel.add(createTitleBar(), BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(Footer.getFooterPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(navyColor);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Add Certificate");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel user = new JLabel("Welcome, " + userName);
        user.setFont(new Font("SansSerif", Font.PLAIN, 14));
        user.setForeground(Color.WHITE);
        bar.add(title, BorderLayout.WEST);
        bar.add(user, BorderLayout.EAST);
        return bar;
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

        JButton saveBtn = CertificateFormPanel.createButton("Save Certificate", blueColor, blueHover, 170, 40);
        saveBtn.addActionListener(e -> handleSave());
        JButton backBtn = CertificateFormPanel.createButton("Back to Dashboard", greyColor, greyHover, 200, 40);
        backBtn.addActionListener(e -> {
            dispose();
            new DashboardForm(userName).setVisible(true);
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

    private void handleSave() {
        String error = form.validateForm();
        if (error != null) {
            errorLabel.setText(error);
            return;
        }
        errorLabel.setText(" ");
        String sql = "INSERT INTO certificate_details (certificate_name, issued_by, issue_date, achievement_type, category, mode, expiry_date, course_name, certificate_id, grade_score, remarks, certificate_path, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = dbConnect.getConnection()) {
            int userId = CertificateFormPanel.findUserId(con, userName);
            if (userId < 0) {
                JOptionPane.showMessageDialog(this,
                        "User account not found. Please login again.",
                        "Add Certificate", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                int next = form.bindValues(ps);
                ps.setInt(next, userId);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Add Certificate", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
        new ViewCertificates(userName).setVisible(true);
    }
}