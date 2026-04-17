package com.medimate.view;

import com.medimate.model.Post;
import com.medimate.model.User;
import com.medimate.service.AdminService;
import com.medimate.util.StyleUtil;
import com.medimate.view.components.RoundedPanel;
import com.medimate.view.components.StatsCardPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

/**
 * AdminPanel - Admin dashboard matching the web AdminPanel page
 * Stats cards, user management table, post management table
 */
public class AdminPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private JPanel contentArea;
    private CardLayout cardLayout;
    private JPanel statsContainer;
    private DefaultTableModel usersTableModel;
    private DefaultTableModel postsTableModel;

    public AdminPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());

        // Admin sidebar
        JPanel sidebar = createAdminSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content with CardLayout
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setOpaque(false);
        contentArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        contentArea.add(createDashboardTab(), "dashboard");
        contentArea.add(createUsersTab(), "users");
        contentArea.add(createPostsTab(), "posts");

        add(contentArea, BorderLayout.CENTER);

        refreshStats();
        refreshUsers();
        refreshPosts();
    }

    private JPanel createAdminSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                g2.setColor(StyleUtil.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(StyleUtil.PANEL_BORDER);
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        // Header
        JLabel header = new JLabel("Admin Panel");
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(Color.WHITE);
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(5, 8, 15, 0));
        sidebar.add(header);

        sidebar.add(createAdminNavButton("", "Dashboard", "dashboard"));
        sidebar.add(Box.createVerticalStrut(3));
        sidebar.add(createAdminNavButton("", "Users", "users"));
        sidebar.add(Box.createVerticalStrut(3));
        sidebar.add(createAdminNavButton("", "Posts", "posts"));

        return sidebar;
    }

    private JButton createAdminNavButton(String icon, String text, String cardName) {
        JButton btn = new JButton(icon + "  " + text);
        btn.setFont(StyleUtil.FONT_BODY);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(StyleUtil.SIDEBAR_HOVER);
                btn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setOpaque(false);
                btn.repaint();
            }
        });

        btn.addActionListener(e -> cardLayout.show(contentArea, cardName));
        return btn;
    }

    // === Dashboard Tab ===
    private JPanel createDashboardTab() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Overview");
        title.setFont(StyleUtil.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        statsContainer = new JPanel(new GridLayout(1, 4, 12, 0));
        statsContainer.setOpaque(false);
        statsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        statsContainer.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(statsContainer);

        return panel;
    }

    // === Users Tab ===
    private JPanel createUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel title = new JLabel("All Users");
        title.setFont(StyleUtil.FONT_TITLE);
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Email", "Role", "Delete"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4;
            }
        };

        JTable table = createStyledTable(usersTableModel);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 4 && row >= 0) {
                    String role = (String) usersTableModel.getValueAt(row, 3);
                    if (!"admin".equals(role)) {
                        int id = (int) usersTableModel.getValueAt(row, 0);
                        int option = JOptionPane.showConfirmDialog(AdminPanel.this, 
                            "Are you sure you want to delete this user?", 
                            "Confirm Deletion", 
                            JOptionPane.YES_NO_OPTION);
                        if (option == JOptionPane.YES_OPTION) {
                            adminService.deleteUser(id);
                            refreshUsers();
                            refreshStats();
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(StyleUtil.PANEL_BORDER, 1, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // === Posts Tab ===
    private JPanel createPostsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel title = new JLabel("All Posts");
        title.setFont(StyleUtil.FONT_TITLE);
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.NORTH);

        String[] columns = {"ID", "Title", "Author", "Date", "Delete"};
        postsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4;
            }
        };

        JTable table = createStyledTable(postsTableModel);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == 4 && row >= 0) {
                    int id = (int) postsTableModel.getValueAt(row, 0);
                    int option = JOptionPane.showConfirmDialog(AdminPanel.this, 
                        "Are you sure you want to delete this post?", 
                        "Confirm Deletion", 
                        JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        adminService.deletePost(id);
                        refreshPosts();
                        refreshStats();
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(StyleUtil.PANEL_BORDER, 1, true));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(StyleUtil.FONT_BODY);
        table.setForeground(Color.WHITE);
        table.setBackground(new Color(46, 16, 101));
        table.setGridColor(new Color(255, 255, 255, 30));
        table.setRowHeight(40);
        table.setSelectionBackground(new Color(124, 58, 237, 60));
        table.setSelectionForeground(Color.WHITE);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(Color.WHITE);
        header.setBackground(new Color(88, 28, 135));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, StyleUtil.PANEL_BORDER));

        // Center alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setForeground(Color.WHITE);
        centerRenderer.setBackground(new Color(46, 16, 101));
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Delete column with red text
        DefaultTableCellRenderer deleteRenderer = new DefaultTableCellRenderer();
        deleteRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        deleteRenderer.setForeground(StyleUtil.ERROR_RED);
        deleteRenderer.setBackground(new Color(46, 16, 101));
        table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(deleteRenderer);

        return table;
    }

    // === Data Refresh Methods ===
    private void refreshStats() {
        SwingWorker<Map<String, Long>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Long> doInBackground() {
                return adminService.getStats();
            }

            @Override
            protected void done() {
                try {
                    Map<String, Long> stats = get();
                    statsContainer.removeAll();

                    statsContainer.add(new StatsCardPanel("Total Users",
                        String.valueOf(stats.getOrDefault("totalUsers", 0L)), "",
                        new Color(124, 58, 237), new Color(109, 40, 217)));

                    statsContainer.add(new StatsCardPanel("Doctors",
                        String.valueOf(stats.getOrDefault("totalDoctors", 0L)), "",
                        new Color(147, 51, 234), new Color(79, 70, 229)));

                    statsContainer.add(new StatsCardPanel("Patients",
                        String.valueOf(stats.getOrDefault("totalPatients", 0L)), "",
                        new Color(79, 70, 229), new Color(124, 58, 237)));

                    statsContainer.add(new StatsCardPanel("Total Posts",
                        String.valueOf(stats.getOrDefault("totalPosts", 0L)), "",
                        new Color(109, 40, 217), new Color(147, 51, 234)));

                    statsContainer.revalidate();
                    statsContainer.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void refreshUsers() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() {
                return adminService.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    usersTableModel.setRowCount(0);
                    for (User u : users) {
                        usersTableModel.addRow(new Object[]{
                            u.getId(), u.getName(), u.getEmail(), u.getRole(),
                            "admin".equals(u.getRole()) ? "" : " Delete"
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void refreshPosts() {
        SwingWorker<List<Post>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Post> doInBackground() {
                return adminService.getAllPosts();
            }

            @Override
            protected void done() {
                try {
                    List<Post> posts = get();
                    postsTableModel.setRowCount(0);
                    for (Post p : posts) {
                        postsTableModel.addRow(new Object[]{
                            p.getId(), p.getTitle(), p.getAuthorName(),
                            p.getCreatedAt() != null ? p.getCreatedAt().toString().substring(0, 10) : "",
                            " Delete"
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
