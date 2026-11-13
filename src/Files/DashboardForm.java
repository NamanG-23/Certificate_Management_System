package Files;
import Common.*;
import DatabaseConnectivity.dbConnect;
import LoginFiles.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardForm extends JFrame {
    private final Color navyColor = new Color(21, 33, 66);
    private final Color blueColor = new Color(41, 98, 255);
    private final Color blueHover = new Color(25, 75, 210);
    private final Color redColor = new Color(200, 50, 50);
    private final Color redHover = new Color(160, 35, 35);
    private final Color bgColor = new Color(245, 245, 245);
    private final String userName;
    public DashboardForm(String userName) {
        this.userName = userName;
        setTitle("Certificate Management System - Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setJMenuBar(createMenuBar());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(Header.getHeaderPanel(), BorderLayout.NORTH);
        topPanel.add(createTitleBar(), BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);
        add(Footer.getFooterPanel(), BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        JMenuItem deleteItem = new JMenuItem("Delete Account");
        JMenuItem exitItem = new JMenuItem("Exit");
        aboutItem.addActionListener(e -> showAbout());
        deleteItem.addActionListener(e -> handleDeleteAccount());
        exitItem.addActionListener(e -> System.exit(0));
        helpMenu.add(aboutItem);
        helpMenu.add(deleteItem);
        helpMenu.addSeparator();
        helpMenu.add(exitItem);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JPanel createTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(navyColor);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Certificate Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel user = new JLabel("Welcome, " + userName);
        user.setFont(new Font("SansSerif", Font.PLAIN, 14));
        user.setForeground(Color.WHITE);
        bar.add(title, BorderLayout.WEST);
        bar.add(user, BorderLayout.EAST);
        return bar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));
        sidebar.add(Box.createVerticalStrut(25));
        sidebar.add(createSidebarButton("Add Certificate", blueColor, blueHover, this::openAddCertificate));
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(createSidebarButton("View Certificates", blueColor, blueHover, this::openViewCertificates));
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(createSidebarButton("Edit Certificate", blueColor, blueHover, this::openViewCertificates));
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(createSidebarButton("Forgot Password", blueColor, blueHover, this::openForgotPassword));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createSidebarButton("Logout", redColor, redHover, this::handleLogout));
        sidebar.add(Box.createVerticalStrut(25));
        return sidebar;
    }

    private JButton createSidebarButton(String text, Color normal, Color hover, Runnable action) {
        JButton btn = new JButton(text);
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension size = new Dimension(190, 42);
        btn.setPreferredSize(size);
        btn.setMinimumSize(size);
        btn.setMaximumSize(size);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(normal);
            }
        });
        btn.addActionListener(e -> action.run());
        return btn;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(bgColor);
        main.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        JLabel welcome = new JLabel("Welcome, " + userName + "!");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 26));
        welcome.setForeground(navyColor);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub = new JLabel("Manage and organize your certificate records from one place.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(new Color(90, 90, 90));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(welcome);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(sub);

        JPanel cards = new JPanel(new GridLayout(2, 2, 15, 15));
        cards.setOpaque(false);
        cards.add(createCard("Add Certificate", "Upload and save the details of a new certificate.", "Add", this::openAddCertificate));
        cards.add(createCard("View Certificates", "See all your saved certificates in one table.", "View", this::openViewCertificates));
        cards.add(createCard("Edit Certificate", "Update the details of your saved certificates.", "Edit", this::openViewCertificates));
        cards.add(createCard("Forgot Password", "Reset your password or recover your username.", "Reset", this::openForgotPassword));

        JPanel cardWrap = new JPanel(new BorderLayout());
        cardWrap.setOpaque(false);
        cardWrap.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        cardWrap.add(cards, BorderLayout.NORTH);
        main.add(textPanel, BorderLayout.NORTH);
        main.add(cardWrap, BorderLayout.CENTER);
        return main;
    }

    private JPanel createCard(String title, String description, String buttonText, Runnable action) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(navyColor);
        JLabel descLabel = new JLabel("<html><div style='width:140px'>" + description + "</div></html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descLabel.setForeground(new Color(80, 80, 80));
        descLabel.setVerticalAlignment(SwingConstants.TOP);

        JButton actionBtn = new JButton(buttonText);
        actionBtn.setBackground(blueColor);
        actionBtn.setForeground(Color.WHITE);
        actionBtn.setFocusPainted(false);
        actionBtn.setBorderPainted(false);
        actionBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        actionBtn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                actionBtn.setBackground(blueHover);
            }
            @Override public void mouseExited(MouseEvent e) {
                actionBtn.setBackground(blueColor);
            }
        });
        actionBtn.addActionListener(e -> action.run());
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);
        card.add(actionBtn, BorderLayout.SOUTH);
        return card;
    }

    private void openAddCertificate() {
        dispose();
        new AddCertificates(userName).setVisible(true);
    }
    private void openViewCertificates() {
        dispose();
        new ViewCertificates(userName).setVisible(true);
    }
    private void openForgotPassword() {
        dispose();
        new ForgotPasswordForm().setVisible(true);
    }
    private void handleLogout() {
        dispose();
        new LoginForm().setVisible(true);
    }
    private void showAbout() {
        String message =
                "Certificate Management System (CMS)\n\n"
                        + "Welcome to the Certificate Management System.\n\n"
                        + "This desktop application is developed as a Mini Project "
                        + "to digitally manage and organize certificate records.\n\n"

                        + "Core Features:\n"
                        + "  - Secure User Registration and Login\n"
                        + "  - Forgot Username and Password Recovery\n"
                        + "  - Add and Store Certificate Records\n"
                        + "  - View Personal Certificate Details\n"
                        + "  - User-wise Certificate Management\n"
                        + "  - Input Validation on Every Form\n"
                        + "  - Secure Account Deletion Option\n\n"

                        + "Instructions:\n"
                        + "Use the dashboard buttons and sidebar menu "
                        + "to manage your certificate records efficiently.\n\n"

                        + "Developed By:\n"
                        + "Naman Gupta\n"
                        + "PIET25CS100\n"
                        + "B.Tech (CSE)";

        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleDeleteAccount() {
        int first = JOptionPane.showConfirmDialog(this, "Do you want to delete your account?", "Delete Account",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (first != JOptionPane.YES_OPTION) return;
        JPasswordField passField = new JPasswordField(15);
        JPanel passPanel = new JPanel(new BorderLayout(0, 8));
        passPanel.add(new JLabel("Enter your password to continue:"), BorderLayout.NORTH);
        passPanel.add(passField, BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, passPanel, "Delete Account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        String password = new String(passField.getPassword());
        try {
            if (!isPasswordCorrect(password)) {
                JOptionPane.showMessageDialog(this,
                        "Incorrect password. Your account was not deleted.",
                        "Delete Account", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int second = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete your account?\nThis action cannot be undone.", "Delete Account",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (second != JOptionPane.YES_OPTION) return;
            if (deleteAccountFromDatabase()) {
                JOptionPane.showMessageDialog(this, "Your account has been deleted successfully.", "Delete Account", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Account could not be deleted. Please try again.", "Delete Account", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Delete Account", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isPasswordCorrect(String password) throws SQLException {
        String sql = "SELECT password FROM login_info WHERE username = ?";
        try (Connection con = dbConnect.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && password.equals(rs.getString("password"));
            }
        }
    }

    private boolean deleteAccountFromDatabase() throws SQLException {
        String deleteCertificates = "DELETE FROM certificate_details WHERE user_id = ?";
        String deleteUser = "DELETE FROM login_info WHERE username = ?";
        try (Connection con = dbConnect.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps1 = con.prepareStatement(deleteCertificates);
                 PreparedStatement ps2 = con.prepareStatement(deleteUser)) {
                int userId = CertificateFormPanel.findUserId(con, userName);
                if (userId >= 0) {
                    ps1.setInt(1, userId);
                    ps1.executeUpdate();
                }
                ps2.setString(1, userName);
                int deleted = ps2.executeUpdate();
                con.commit();
                return deleted > 0;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        }
    }
}