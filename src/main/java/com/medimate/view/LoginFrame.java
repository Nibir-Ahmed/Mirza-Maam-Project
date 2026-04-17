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
 * LoginFrame - Login screen matching the web app's Login page
 * Dark violet theme with glassmorphism card
 */
public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("MediMate - Login");
        setSize(500, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Gradient background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());

        // Center card
        JPanel centerCard = createLoginCard();
        mainPanel.add(centerCard);

        setContentPane(mainPanel);
    }

    private JPanel createLoginCard() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setPreferredSize(new Dimension(380, 480));

        // Logo
        JLabel logoMedi = new JLabel("Medi", SwingConstants.CENTER);
        logoMedi.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoMedi.setForeground(Color.WHITE);
        logoMedi.setAlignmentX(CENTER_ALIGNMENT);

        JLabel logoMate = new JLabel("Mate", SwingConstants.CENTER);
        logoMate.setFont(new Font("Segoe UI", Font.BOLD, 36));
        logoMate.setForeground(Color.WHITE);
        logoMate.setAlignmentX(CENTER_ALIGNMENT);

        // Combined logo panel
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
        wrapper.add(Box.createVerticalStrut(25));

        // Central Card
        RoundedPanel card = new RoundedPanel(25, new Color(46, 16, 101, 240), new Color(15, 23, 42, 240));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        card.setAlignmentX(CENTER_ALIGNMENT);

        JLabel welcomeLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        welcomeLabel.setFont(StyleUtil.FONT_HEADING);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        card.add(welcomeLabel);
        card.add(Box.createVerticalStrut(20));

        // Error label
        errorLabel = new JLabel("");
        errorLabel.setFont(StyleUtil.FONT_SMALL);
        errorLabel.setForeground(StyleUtil.ERROR_RED);
        errorLabel.setAlignmentX(CENTER_ALIGNMENT);
        errorLabel.setVisible(false);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(5));

        // Email field
        card.add(createFieldLabel("Email Address"));
        emailField = createStyledTextField("example@domain.com");
        card.add(emailField);
        card.add(Box.createVerticalStrut(20));

        // Password field
        card.add(createFieldLabel("Password"));
        passwordField = createStyledPasswordField();
        card.add(passwordField);
        card.add(Box.createVerticalStrut(30));

        // Login Button
        GradientButton loginBtn = new GradientButton("Log In");
        loginBtn.setPreferredSize(new Dimension(350, 45));
        loginBtn.setMaximumSize(new Dimension(350, 45));
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(20));

        // Register link
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        linkPanel.setOpaque(false);
        JLabel noAccount = new JLabel("Don't have an account? ");
        noAccount.setFont(StyleUtil.FONT_SMALL);
        noAccount.setForeground(StyleUtil.TEXT_WHITE_50);

        JLabel registerLink = new JLabel("Register here");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setForeground(StyleUtil.LIGHT_VIOLET);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dispose();
                new RegisterFrame().setVisible(true);
            }
            public void mouseEntered(java.awt.event.MouseEvent e) {
                registerLink.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                registerLink.setForeground(StyleUtil.LIGHT_VIOLET);
            }
        });

        linkPanel.add(noAccount);
        linkPanel.add(registerLink);
        card.add(linkPanel);

        wrapper.add(card);
        return wrapper;
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showError("[!] Please provide both Email and Password!");
            return;
        }

        // Background thread for DB call
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return authService.login(email, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        dispose();
                        new MainFrame().setVisible(true);
                    } else {
                        showError("[!] Invalid email or password!");
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
