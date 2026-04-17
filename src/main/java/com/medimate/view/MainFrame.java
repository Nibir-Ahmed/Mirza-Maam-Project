package com.medimate.view;

import com.medimate.service.AuthService;
import com.medimate.util.SessionManager;
import com.medimate.util.StyleUtil;
import com.medimate.view.components.GradientPanel;
import com.medimate.view.components.SidebarPanel;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame - Main application window with navbar, sidebar, and content area
 * Matches the web app's layout: Navbar (top) + Sidebar (left) + Content (center)
 */
public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private SidebarPanel sidebar;
    private HomePanel homePanel;
    private AIChatPanel aiChatPanel;
    private DoctorChatPanel doctorChatPanel;
    private BlogPanel blogPanel;
    private AdminPanel adminPanel;

    public MainFrame() {
        setTitle("MediMate - Healthcare Desktop App");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // Main gradient background
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // Top navbar
        mainPanel.add(createNavbar(), BorderLayout.NORTH);

        // Body: sidebar + content
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);

        // Sidebar
        sidebar = new SidebarPanel();
        body.add(sidebar, BorderLayout.WEST);

        // Content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        homePanel = new HomePanel();
        aiChatPanel = new AIChatPanel();
        doctorChatPanel = new DoctorChatPanel();
        blogPanel = new BlogPanel();

        contentPanel.add(homePanel, "home");
        contentPanel.add(aiChatPanel, "ai-chat");
        contentPanel.add(doctorChatPanel, "doctor-chat");
        contentPanel.add(blogPanel, "blog");

        // Admin panel (only if admin)
        if (SessionManager.getInstance().isAdmin()) {
            adminPanel = new AdminPanel();
            contentPanel.add(adminPanel, "admin");
        }

        body.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(body, BorderLayout.CENTER);

        // Setup sidebar navigation
        setupSidebarNav();

        setContentPane(mainPanel);
    }

    private JPanel createNavbar() {
        JPanel navbar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                g2.setColor(StyleUtil.PANEL_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(StyleUtil.PANEL_BORDER);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        navbar.setOpaque(false);
        navbar.setPreferredSize(new Dimension(0, 55));
        navbar.setLayout(new BorderLayout());
        navbar.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Left: Logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        logoPanel.setOpaque(false);

        JLabel medi = new JLabel("Medi");
        medi.setFont(StyleUtil.FONT_LOGO);
        medi.setForeground(Color.WHITE);

        JLabel mate = new JLabel(" Mate ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                GradientPaint gp = new GradientPaint(0, 0, StyleUtil.LIGHT_VIOLET, getWidth(), 0, StyleUtil.PRIMARY_PURPLE);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        mate.setFont(StyleUtil.FONT_LOGO);
        mate.setForeground(Color.WHITE);
        mate.setOpaque(false);

        logoPanel.add(medi);
        logoPanel.add(mate);
        navbar.add(logoPanel, BorderLayout.WEST);

        // Center: Nav links
        JPanel navLinks = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        navLinks.setOpaque(false);

        navLinks.add(createNavLink("AI Chat", "ai-chat"));
        navLinks.add(createNavLink("Blog", "blog"));
        navLinks.add(createNavLink("Doctor Chat", "doctor-chat"));

        if (SessionManager.getInstance().isAdmin()) {
            navLinks.add(createNavLink("Admin", "admin"));
        }

        navbar.add(navLinks, BorderLayout.CENTER);

        // Right: User info + Logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 10));
        rightPanel.setOpaque(false);

        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            // Avatar + name
            JLabel avatar = new JLabel(String.valueOf(user.getName().charAt(0)).toUpperCase()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    StyleUtil.enableAntiAliasing(g2);
                    GradientPaint gp = new GradientPaint(0, 0, StyleUtil.LIGHT_VIOLET, getWidth(), getHeight(), StyleUtil.PRIMARY_PURPLE);
                    g2.setPaint(gp);
                    g2.fillOval(0, 0, 30, 30);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            avatar.setPreferredSize(new Dimension(30, 30));
            avatar.setHorizontalAlignment(SwingConstants.CENTER);
            avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            avatar.setForeground(Color.WHITE);

            JLabel userName = new JLabel(user.getName());
            userName.setFont(new Font("Segoe UI", Font.BOLD, 13));
            userName.setForeground(Color.WHITE);

            rightPanel.add(avatar);
            rightPanel.add(userName);

            // Logout button
            JButton logoutBtn = new JButton("Log Out");
            logoutBtn.setFont(StyleUtil.FONT_SMALL);
            logoutBtn.setForeground(Color.WHITE);
            logoutBtn.setBackground(new Color(255, 255, 255, 25));
            logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(StyleUtil.PANEL_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            logoutBtn.setFocusPainted(false);
            logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logoutBtn.addActionListener(e -> handleLogout());

            logoutBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    logoutBtn.setBackground(new Color(239, 68, 68, 60));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    logoutBtn.setBackground(new Color(255, 255, 255, 25));
                }
            });

            rightPanel.add(logoutBtn);
        }

        navbar.add(rightPanel, BorderLayout.EAST);
        return navbar;
    }

    private JButton createNavLink(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(new Color(255, 255, 255, 25));
                btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setOpaque(false);
                btn.repaint();
            }
        });

        btn.addActionListener(e -> navigateTo(cardName));
        return btn;
    }

    private void setupSidebarNav() {
        JButton homeBtn = sidebar.addNavButton("", "Home", e -> navigateTo("home"));
        sidebar.addNavButton("", "AI Chat", e -> navigateTo("ai-chat"));
        sidebar.addNavButton("", "Doctor Chat", e -> navigateTo("doctor-chat"));
        sidebar.addNavButton("", "Blog", e -> navigateTo("blog"));

        if (SessionManager.getInstance().isAdmin()) {
            sidebar.addNavButton("", "Admin", e -> navigateTo("admin"));
        }

        // Set home as default active
        sidebar.setActiveButton(homeBtn);
    }

    private void navigateTo(String cardName) {
        cardLayout.show(contentPanel, cardName);

        // Refresh content on navigation
        switch (cardName) {
            case "home" -> homePanel.refreshPosts();
            case "blog" -> blogPanel.refreshPosts();
            case "doctor-chat" -> doctorChatPanel.startRefresh();
        }
    }

    private void handleLogout() {
        doctorChatPanel.stopRefresh();
        new AuthService().logout();
        dispose();
        new LoginFrame().setVisible(true);
    }
}
