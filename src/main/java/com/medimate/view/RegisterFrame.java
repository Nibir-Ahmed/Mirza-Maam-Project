package com.medimate.view;

import com.medimate.model.User;
import com.medimate.service.AuthService;
import com.medimate.util.StyleUtil;
import com.medimate.view.components.GradientButton;
import com.medimate.view.components.GradientPanel;
import com.medimate.view.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;

/**
 * RegisterFrame - Registration screen matching the web app's Register page
 */
public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JLabel errorLabel;
    private final AuthService authService = new AuthService();

    public RegisterFrame() {
        setTitle("MediMate - Register");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.add(createRegisterCard());
        setContentPane(mainPanel);
    }

    private JPanel createRegisterCard() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setPreferredSize(new Dimension(380, 580));

        // Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        logoPanel.setOpaque(false);
        JLabel medi = new JLabel("Medi");
        medi.setFont(new Font("Segoe UI", Font.BOLD, 36));
        medi.setForeground(Color.WHITE);

        JLabel mate = new JLabel(" Mate ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                GradientPaint gp = new GradientPaint(0, 0, StyleUtil.LIGHT_VIOLET, getWidth(), 0, StyleUtil.PRIMARY_PURPLE);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mate.setFont(new Font("Segoe UI", Font.BOLD, 36));
        mate.setForeground(Color.WHITE);
        mate.setOpaque(false);
        logoPanel.add(medi);
        logoPanel.add(mate);

        JLabel subtitle = new JLabel("Your AI Health Assistant", SwingConstants.CENTER);
        subtitle.setFont(StyleUtil.FONT_BODY);
        subtitle.setForeground(StyleUtil.TEXT_WHITE_60);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        wrapper.add(logoPanel);
        wrapper.add(Box.createVerticalStrut(5));
        wrapper.add(subtitle);
        wrapper.add(Box.createVerticalStrut(20));

        // Card
        RoundedPanel card = new RoundedPanel(25, new Color(46, 16, 101, 240), new Color(15, 23, 42, 240));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        card.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Create an Account", SwingConstants.CENTER);
        title.setFont(StyleUtil.FONT_HEADING);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(15));

        // Error label
        errorLabel = new JLabel("");
        errorLabel.setFont(StyleUtil.FONT_SMALL);
        errorLabel.setForeground(StyleUtil.ERROR_RED);
        errorLabel.setAlignmentX(CENTER_ALIGNMENT);
        errorLabel.setVisible(false);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(5));

        // Name field
        card.add(createFieldLabel("Full Name"));
        nameField = createStyledTextField("Your name");
        card.add(nameField);
        card.add(Box.createVerticalStrut(10));

        // Email field
        card.add(createFieldLabel("Email"));
        emailField = createStyledTextField("email@example.com");
        card.add(emailField);
        card.add(Box.createVerticalStrut(10));

        // Password field
        card.add(createFieldLabel("Password"));
        passwordField = createStyledPasswordField();
        card.add(passwordField);
        card.add(Box.createVerticalStrut(10));

        // Role dropdown
        card.add(createFieldLabel("I am a"));
        roleCombo = new JComboBox<>(new String[]{"Patient", "Doctor"});
        roleCombo.setFont(StyleUtil.FONT_BODY);
        roleCombo.setBackground(new Color(46, 16, 101));
        roleCombo.setForeground(Color.WHITE);
        roleCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, StyleUtil.FIELD_HEIGHT));
        roleCombo.setPreferredSize(new Dimension(350, StyleUtil.FIELD_HEIGHT));
        roleCombo.setAlignmentX(LEFT_ALIGNMENT);
        card.add(roleCombo);
        card.add(Box.createVerticalStrut(15));

        // Register button
        GradientButton registerBtn = new GradientButton("Register Now");
        registerBtn.setPreferredSize(new Dimension(350, 45));
        registerBtn.setMaximumSize(new Dimension(350, 45));
        registerBtn.setAlignmentX(CENTER_ALIGNMENT);
        registerBtn.addActionListener(e -> handleRegister());
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(15));

        // Login link
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setOpaque(false);
        JLabel hasAccount = new JLabel("Already have an account? ");
        hasAccount.setFont(StyleUtil.FONT_SMALL);
        hasAccount.setForeground(StyleUtil.TEXT_WHITE_50);

        JLabel loginLink = new JLabel("Login here");
        loginLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        loginLink.setForeground(StyleUtil.LIGHT_VIOLET);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginLink.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginLink.setForeground(StyleUtil.LIGHT_VIOLET);
            }
        });

        linkPanel.add(hasAccount);
        linkPanel.add(loginLink);
        card.add(linkPanel);

        wrapper.add(card);
        return wrapper;
    }

    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String roleText = (String) roleCombo.getSelectedItem();
        String role = roleText.contains("Doctor") ? "doctor" : "patient";

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("[!] Please fill all the fields!");
            return;
        }

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return authService.register(name, email, password, role);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Registration successful! Please login.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new LoginFrame().setVisible(true);
                    } else {
                        showError("[!] Email already exists!");
                    }
                } catch (Exception ex) {
                    showError("[!] Something went wrong!");
                }
            }
        };
        worker.execute();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(StyleUtil.FONT_SMALL);
        label.setForeground(StyleUtil.TEXT_WHITE_70);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(StyleUtil.PANEL_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(StyleUtil.FONT_BODY);
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, StyleUtil.FIELD_HEIGHT));
        field.setPreferredSize(new Dimension(300, StyleUtil.FIELD_HEIGHT));
        field.setAlignmentX(LEFT_ALIGNMENT);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.setColor(StyleUtil.PANEL_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(StyleUtil.FONT_BODY);
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, StyleUtil.FIELD_HEIGHT));
        field.setPreferredSize(new Dimension(300, StyleUtil.FIELD_HEIGHT));
        field.setAlignmentX(LEFT_ALIGNMENT);
        return field;
    }
}
