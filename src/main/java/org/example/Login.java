package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Login extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerLinkButton;
    public Login() {
        setTitle("Mentor Connect - Login");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
        }
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(70, 70, 100);
                Color color2 = new Color(20, 20, 40);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel);
        buildLoginForm(mainPanel);
    }
    private void buildLoginForm(JPanel mainPanel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Font titleFont = new Font("Arial", Font.BOLD, 30);
        Font labelFont = new Font("Arial", Font.BOLD, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);


        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(emailLabel, gbc);

        emailField = new JTextField(20);
        emailField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(emailField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(passwordField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 20));
        loginButton.setBackground(new Color(0, 150, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.ipady = 10;
        mainPanel.add(loginButton, gbc);
        gbc.ipady = 0;

        Color linkPink = new Color(221, 14, 100);
        String hexColor = "#E80F69";

        registerLinkButton = new JButton("<html><a href='' style='color: " + hexColor + ";'>New User? -> Register here</a></html>");

        registerLinkButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registerLinkButton.setHorizontalAlignment(SwingConstants.CENTER);
        registerLinkButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLinkButton.setBorderPainted(false);
        registerLinkButton.setContentAreaFilled(false);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        mainPanel.add(registerLinkButton, gbc);

        loginButton.addActionListener(e -> authenticateUser());
        passwordField.addActionListener(e -> authenticateUser()); // Allows pressing Enter

        registerLinkButton.addActionListener(e -> {
            this.dispose();
            new Register().setVisible(true);
        });
    }

    // --- AUTHENTICATION LOGIC ---
    private void authenticateUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()); // WARN: Passwords should be hashed!

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT name, role FROM register WHERE email = ? AND password = ?";
        String userName = null;
        String userRole = null;

        try (Connection conn = DatabaseConnection.getConnection(); // Use the central connection class
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userName = rs.getString("name");
                    userRole = rs.getString("role");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: Failed to connect or query.", "Connection Failure", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return;
        }

        if (userName != null && userRole != null) {
            JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + userName + " (" + userRole + ")", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.setVisible(false);


            Dashboard dashboard = new Dashboard(userName, userRole, this);
            dashboard.setVisible(true);

            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}

