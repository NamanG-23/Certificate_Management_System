package LoginFiles;
import Files.*;
import Common.*;
import DatabaseConnectivity.dbConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    public LoginForm() {
        setTitle("Certificate Management System - Login");
        setSize(725, 577);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(Header.getHeaderPanel(), BorderLayout.NORTH);
        add(Footer.getFooterPanel(), BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(
                new JLabel("Username:") {{
                    setFont(new Font("SansSerif", Font.BOLD, 14));
                }},
                gbc
        );
        usernameField = new JTextField(18);
        usernameField.setPreferredSize(new Dimension(170, 30));
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);
        gbc.insets = new Insets(18, 6, 6, 6);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(
                new JLabel("Password:") {{
                    setFont(new Font("SansSerif", Font.BOLD, 14));
                }},
                gbc
        );
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.setOpaque(false);
        passwordField = new JPasswordField(18);
        passwordField.setEchoChar('•');
        JButton eyeButton = new JButton("👁");
        eyeButton.setFocusable(false);
        eyeButton.setMargin(new Insets(0, 5, 0, 5));
        eyeButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        eyeButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == (char) 0) {
                passwordField.setEchoChar('•');
                eyeButton.setText("👁");
            } else{
                passwordField.setEchoChar((char) 0);
                eyeButton.setText("🙈");
            }
        });
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(eyeButton, BorderLayout.EAST);
        gbc.gridx = 1;
        formPanel.add(passwordPanel, gbc);

        JPanel linksPanel = new JPanel(new BorderLayout());
        linksPanel.setOpaque(false);
        linksPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        0, 120, 0, 120
                )
        );
        JLabel signUpLabel = new JLabel("Don't have an account? Sign Up");
        signUpLabel.setForeground(Color.BLUE);
        signUpLabel.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        signUpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Login window ko band karega
                new SignUpForm().setVisible(true); // SignUp window khol dega
            }
        });
        JLabel forgotLabel = new JLabel("Forgot Username / Password?");
        forgotLabel.setForeground(Color.BLUE);
        forgotLabel.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Login window ko band karega
                new ForgotPasswordForm().setVisible(true); // Forgot window khol dega
            }
        });
        linksPanel.add(signUpLabel, BorderLayout.WEST);
        linksPanel.add(forgotLabel, BorderLayout.EAST);

        // Login Button
        JButton loginButton = new JButton("Login");
        Color normalColor = new Color(41, 98, 255);
        Color hoverColor = new Color(25, 75, 210);
        loginButton.setBackground(normalColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        loginButton.setPreferredSize(
                new Dimension(140, 42)
        );
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(normalColor);
            }
        });
        loginButton.addActionListener(e -> handleLogin());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        centerPanel.add(Box.createVerticalStrut(35));
        centerPanel.add(formPanel);
        centerPanel.add(Box.createVerticalStrut(3));
        centerPanel.add(linksPanel);
        centerPanel.add(Box.createVerticalStrut(22));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalStrut(70));
        add(centerPanel, BorderLayout.CENTER);
    }
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter Username and Password."
            );
            return;
        }
        try {
            Connection con = dbConnect.getConnection();
            String query = "SELECT * FROM login_info WHERE username=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No User Found ❌"
                );
                con.close();
                return;
            }
            String dbPassword = rs.getString("password");
            if (!dbPassword.equals(password)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password Doesn't Match ❌"
                );
                con.close();
                return;
            }
            JOptionPane.showMessageDialog(
                    this,
                    "Login Successful ✅"
            );
            con.close();
            dispose();
            DashboardForm dashboard = new DashboardForm(username);
            dashboard.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Database Error!"
            );
        }
    }
}