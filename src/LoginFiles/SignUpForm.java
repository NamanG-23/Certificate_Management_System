package LoginFiles;
import Common.*;
import DatabaseConnectivity.dbConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class SignUpForm extends JFrame {
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleDropdown;
    private JLabel errorLabel;
    private JButton registerButton;
    private final Color normalColor = new Color(41, 98, 255);
    private final Color hoverColor = new Color(25, 75, 210);
    private final Color disabledColor = new Color(170, 170, 170);

    // Username error messages
    private static final String USERNAME_LEN_ERROR = "Username must be less than 20 characters.";
    private static final String USERNAME_CHAR_ERROR = "Username can only contain letters, numbers, and underscores.";
    public SignUpForm() {
        setTitle("Certificate Management System - Sign Up");
        setSize(775, 577);
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
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. Email Field
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Email:") {{ setFont(new Font("SansSerif", Font.BOLD, 14)); }}, gbc);
        emailField = new JTextField(18);
        emailField.setPreferredSize(new Dimension(170, 30));
        gbc.gridx = 1; formPanel.add(emailField, gbc);

        // 2. Username Field
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Username:") {{ setFont(new Font("SansSerif", Font.BOLD, 14)); }}, gbc);
        usernameField = new JTextField(18);
        usernameField.setPreferredSize(new Dimension(170, 30));
        gbc.gridx = 1; formPanel.add(usernameField, gbc);

        // 3. Password Field
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Password:") {{ setFont(new Font("SansSerif", Font.BOLD, 14)); }}, gbc);
        passwordField = new JPasswordField(18);
        gbc.gridx = 1; formPanel.add(createPasswordPanel(passwordField), gbc);

        // 4. Confirm Password Field
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Confirm Password:") {{ setFont(new Font("SansSerif", Font.BOLD, 14)); }}, gbc);
        confirmPasswordField = new JPasswordField(18);
        gbc.gridx = 1; formPanel.add(createPasswordPanel(confirmPasswordField), gbc);

        // 5. Role Dropdown
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Profession:") {{ setFont(new Font("SansSerif", Font.BOLD, 14)); }}, gbc);
        String[] roles = {"Student", "Working Professional", "Other"};
        roleDropdown = new JComboBox<>(roles);
        roleDropdown.setPreferredSize(new Dimension(170, 30));
        roleDropdown.setBackground(Color.WHITE);
        gbc.gridx = 1; formPanel.add(roleDropdown, gbc);

        // Hidden Error Label (Red Text)
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        errorPanel.setOpaque(false);
        errorPanel.add(errorLabel);

        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linksPanel.setOpaque(false);
        JLabel loginLabel = new JLabel("Have an account? Login");
        loginLabel.setForeground(Color.BLUE);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });
        linksPanel.add(loginLabel);

        registerButton = new JButton("Register");
        registerButton.setBackground(disabledColor); // Shuru me grey color
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setPreferredSize(new Dimension(140, 42));
        registerButton.setEnabled(false);
        registerButton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (registerButton.isEnabled()) {
                    registerButton.setBackground(hoverColor);
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (registerButton.isEnabled()) {
                    registerButton.setBackground(normalColor);
                }
            }
        });
        registerButton.addActionListener(e -> handleRegistration());
        addRealTimeValidations();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(formPanel);
        centerPanel.add(errorPanel);
        centerPanel.add(linksPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalStrut(30));
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createPasswordPanel(JPasswordField passField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        passField.setPreferredSize(new Dimension(140, 30));
        passField.setEchoChar('•');
        JButton eyeBtn = new JButton("👁");
        eyeBtn.setFocusable(false);
        eyeBtn.setMargin(new Insets(0, 5, 0, 5));
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.addActionListener(e -> {
            if (passField.getEchoChar() == (char) 0) {
                passField.setEchoChar('•');
                eyeBtn.setText("👁");
            } else {
                passField.setEchoChar((char) 0);
                eyeBtn.setText("🙈");
            }
        });
        panel.add(passField, BorderLayout.CENTER);
        panel.add(eyeBtn, BorderLayout.EAST);
        return panel;
    }

    // REAL-TIME VALIDATION
    private void addRealTimeValidations() {
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String email = emailField.getText().trim();
                if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
                    showError("Please enter a valid Gmail address (e.g., name@gmail.com).");
                } else clearError();
            }
        });
        usernameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { liveUsernameCheck(); }
            @Override public void removeUpdate(DocumentEvent e) { liveUsernameCheck(); }
            @Override public void changedUpdate(DocumentEvent e) { liveUsernameCheck(); }
        });
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String password = new String(passwordField.getPassword());
                // NAYA PASSWORD REGEX: 6+ chars, 1 Upper, 1 Lower, 1 Num
                if (!password.isEmpty() && !password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$")) {
                    showError("Password needs 6+ chars, 1 Upper, 1 Lower, 1 Num.");
                } else clearError();
            }
        });
        confirmPasswordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String password = new String(passwordField.getPassword());
                String confirmPass = new String(confirmPasswordField.getPassword());
                if (!confirmPass.isEmpty() && !password.equals(confirmPass)) {
                    showError("Passwords do not match!");
                } else clearError();
            }
        });
        DocumentListener docListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { checkButtonState(); }
            @Override public void removeUpdate(DocumentEvent e) { checkButtonState(); }
            @Override public void changedUpdate(DocumentEvent e) { checkButtonState(); }
        };
        emailField.getDocument().addDocumentListener(docListener);
        usernameField.getDocument().addDocumentListener(docListener);
        passwordField.getDocument().addDocumentListener(docListener);
        confirmPasswordField.getDocument().addDocumentListener(docListener);
    }

    // CHECK METHOD: Button Enable/Disable Logic
    private void checkButtonState() {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        boolean isEmailValid = email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
        boolean isUsernameValid = username.matches("^[a-zA-Z0-9_]{1,19}$");
        boolean isPasswordValid = password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$");
        boolean isConfirmPassMatch = password.equals(confirmPass);

        if (isEmailValid && isUsernameValid && isPasswordValid && isConfirmPassMatch) {
            registerButton.setEnabled(true);
            registerButton.setBackground(normalColor);
            registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            clearError();
        } else {
            registerButton.setEnabled(false);
            registerButton.setBackground(disabledColor);
            registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void handleRegistration() {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleDropdown.getSelectedItem().toString();
        try {
            Connection con = dbConnect.getConnection();
            String query = "INSERT INTO login_info(username,gmail,password,profession) VALUES(?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                errorLabel.setForeground(new Color(0, 150, 0));
                errorLabel.setText(
                        "Registration Successful! Redirecting to Login..."
                );
                Timer timer = new Timer(1500, evt -> {
                    dispose();
                    new LoginForm().setVisible(true);
                });
                timer.setRepeats(false);
                timer.start();
            }
            con.close();
        }catch (Exception e) {
            errorLabel.setForeground(Color.RED);
            errorLabel.setText(
                    "Username or Gmail already exists!"
            );
        }
    }

    private void showError(String message) {
        errorLabel.setForeground(Color.RED);
        errorLabel.setText(message);
    }

    private void clearError() {
        String username = usernameField.getText().trim();
        if (username.length() >= 20) {
            showError(USERNAME_LEN_ERROR);
        } else if (!username.isEmpty() && !username.matches("^[a-zA-Z0-9_]+$")) {
            showError(USERNAME_CHAR_ERROR);
        } else {
            errorLabel.setText(" ");
        }
    }

    private void liveUsernameCheck() {
        String username = usernameField.getText().trim();
        if (username.length() >= 20) {
            showError(USERNAME_LEN_ERROR);
        } else if (!username.isEmpty() && !username.matches("^[a-zA-Z0-9_]+$")) {
            showError(USERNAME_CHAR_ERROR);
        } else {
            String current = errorLabel.getText();
            if (USERNAME_LEN_ERROR.equals(current) || USERNAME_CHAR_ERROR.equals(current)) {
                errorLabel.setText(" ");
            }
        }
    }
}
