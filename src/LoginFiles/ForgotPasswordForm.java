package LoginFiles;
import Common.*;
import DatabaseConnectivity.dbConnect;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class ForgotPasswordForm extends JFrame {
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;
    private JButton resetButton;
    private JRadioButton resetUsernameRadio;
    private JRadioButton resetPasswordRadio;
    private JPanel dynamicPanel;
    private CardLayout cardLayout;

    private final Color normalColor = new Color(41, 98, 255);
    private final Color disabledColor = new Color(170, 170, 170);

    public ForgotPasswordForm() {
        setTitle("Certificate Management System - Reset Account");
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

        JLabel titleLabel = new JLabel("Reset Account Details");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(21, 33, 66));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        selectionPanel.setOpaque(false);

        resetUsernameRadio = new JRadioButton("Forgot Username");
        resetPasswordRadio = new JRadioButton("Reset Password");
        resetPasswordRadio.setSelected(true); // Default Password reset par rahega
        resetUsernameRadio.setOpaque(false);
        resetPasswordRadio.setOpaque(false);
        resetUsernameRadio.setFont(new Font("SansSerif", Font.BOLD, 13));
        resetPasswordRadio.setFont(new Font("SansSerif", Font.BOLD, 13));

        ButtonGroup group = new ButtonGroup();
        group.add(resetUsernameRadio);
        group.add(resetPasswordRadio);
        selectionPanel.add(resetUsernameRadio);
        selectionPanel.add(resetPasswordRadio);

        // Layout Constraints setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Common Email Field
        JPanel emailPanel = new JPanel(new GridBagLayout());
        emailPanel.setOpaque(false);

        gbc.gridx = 0; gbc.gridy = 0;
        emailPanel.add(new JLabel("Registered Email:") {{ setFont(new Font("SansSerif", Font.BOLD, 14)); setPreferredSize(new Dimension(150, 30)); }}, gbc);

        emailField = new JTextField(18);
        emailField.setPreferredSize(new Dimension(170, 30));
        emailField.setMinimumSize(new Dimension(170, 30));
        gbc.gridx = 1; emailPanel.add(emailField, gbc);

        cardLayout = new CardLayout();
        dynamicPanel = new JPanel(cardLayout);
        dynamicPanel.setOpaque(false);

        // --- CARD 1: Password Fields ---
        JPanel passPanel = new JPanel(new GridBagLayout());
        passPanel.setOpaque(false);
        gbc.gridx = 0; gbc.gridy = 0;
        passPanel.add(new JLabel("New Password:") {{ setFont(new Font("SansSerif", Font.BOLD, 14)); setPreferredSize(new Dimension(150, 30)); }}, gbc);
        newPasswordField = new JPasswordField(18);
        gbc.gridx = 1; passPanel.add(createPasswordPanel(newPasswordField), gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        passPanel.add(new JLabel("Confirm Password:") {{ setFont(new Font("SansSerif", Font.BOLD, 14)); setPreferredSize(new Dimension(150, 30)); }}, gbc);
        confirmPasswordField = new JPasswordField(18);
        gbc.gridx = 1; passPanel.add(createPasswordPanel(confirmPasswordField), gbc);

        // --- CARD 2: Forgot Username ---
        JPanel userPanel = new JPanel();
        userPanel.setOpaque(false);
        dynamicPanel.add(passPanel, "PASSWORD_CARD");
        dynamicPanel.add(userPanel, "USERNAME_CARD");

        // Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        JPanel errorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        errorPanel.setOpaque(false);
        errorPanel.add(errorLabel);

        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linksPanel.setOpaque(false);
        JLabel backLabel = new JLabel("Back to Login");
        backLabel.setForeground(Color.BLUE);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });
        linksPanel.add(backLabel);

        // Reset Button
        resetButton = new JButton("Reset Password");
        resetButton.setBackground(disabledColor);
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setPreferredSize(new Dimension(160, 42));
        resetButton.setEnabled(false);
        resetButton.addActionListener(e -> handleReset());

        addRealTimeValidations();
        setupRadioListeners();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(resetButton);

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(selectionPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(emailPanel);
        centerPanel.add(dynamicPanel);
        centerPanel.add(errorPanel);
        centerPanel.add(linksPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalStrut(40));
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupRadioListeners() {
        ActionListener radioListener = e -> {
            clearError();
            if (resetUsernameRadio.isSelected()) {
                cardLayout.show(dynamicPanel, "USERNAME_CARD");
                resetButton.setText("Show Username");
            } else {
                cardLayout.show(dynamicPanel, "PASSWORD_CARD");
                resetButton.setText("Reset Password");
            }
            checkButtonState();
        };
        resetUsernameRadio.addActionListener(radioListener);
        resetPasswordRadio.addActionListener(radioListener);
    }

    private JPanel createPasswordPanel(JPasswordField passField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(170, 30));
        panel.setMinimumSize(new Dimension(170, 30));
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

    private void addRealTimeValidations() {
        emailField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String email = emailField.getText().trim();
                if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$"))
                    showError("Please enter a valid Gmail address.");
                else clearError();
            }
        });
        newPasswordField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String pass = new String(newPasswordField.getPassword());
                if (!pass.isEmpty() && !pass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$"))
                    showError("Password needs 6+ chars, 1 Upper, 1 Lower, 1 Num.");
                else clearError();
            }
        });
        confirmPasswordField.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                String pass = new String(newPasswordField.getPassword());
                String conf = new String(confirmPasswordField.getPassword());
                if (!conf.isEmpty() && !pass.equals(conf))
                    showError("Passwords do not match!");
                else clearError();
            }
        });
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { checkButtonState(); }
            public void removeUpdate(DocumentEvent e) { checkButtonState(); }
            public void changedUpdate(DocumentEvent e) { checkButtonState(); }
        };
        emailField.getDocument().addDocumentListener(docListener);
        newPasswordField.getDocument().addDocumentListener(docListener);
        confirmPasswordField.getDocument().addDocumentListener(docListener);
    }

    private void checkButtonState() {
        String email = emailField.getText().trim();
        boolean isEmailValid = email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$");
        boolean isValid;
        if (resetUsernameRadio.isSelected()) {
            isValid = isEmailValid;
        } else {
            String pass = new String(newPasswordField.getPassword());
            String conf = new String(confirmPasswordField.getPassword());
            boolean isPassValid = pass.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$");
            isValid = isEmailValid && isPassValid && pass.equals(conf);
        }
        if (isValid) {
            resetButton.setEnabled(true);
            resetButton.setBackground(normalColor);
            resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            clearError();
        } else {
            resetButton.setEnabled(false);
            resetButton.setBackground(disabledColor);
            resetButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private String fetchUsernameFromDatabase(String email) {
        try {
            Connection con = dbConnect.getConnection();
            String query = "SELECT username FROM login_info WHERE gmail=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                con.close();
                return username;
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleReset() {
        String email = emailField.getText().trim();
        errorLabel.setForeground(
                new Color(0,150,0)
        );
        try {
            Connection con = dbConnect.getConnection();
            if (resetUsernameRadio.isSelected()) {
                String username = fetchUsernameFromDatabase(email);
                if (username == null) {
                    showError("No account found with this Gmail.");
                    return;
                }
                errorLabel.setText("Your Username: " + username);
                return;
            }
            String newPassword = new String(newPasswordField.getPassword());
            String query = "UPDATE login_info SET password=? WHERE gmail=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, newPassword);
            ps.setString(2, email);

            int rows = ps.executeUpdate();
            if(rows > 0){
                errorLabel.setText("Password Reset Successfully! Redirecting...");
                Timer timer = new Timer(1500, evt -> {
                            dispose();
                            new LoginForm().setVisible(true);
                        });
                timer.setRepeats(false);
                timer.start();
            } else{
                showError("No account found with this Gmail.");
            }
            con.close();
        } catch (Exception e){
            e.printStackTrace();
            showError("Database Error!");
        }
    }

    private void showError(String message) {
        errorLabel.setForeground(Color.RED);
        errorLabel.setText(message);
    }
    private void clearError() {
        errorLabel.setText(" ");
    }
}