package com.medimate.view;

import com.medimate.model.ChatMessage;
import com.medimate.model.User;
import com.medimate.service.ChatService;
import com.medimate.util.SessionManager;
import com.medimate.util.StyleUtil;
import com.medimate.view.components.GradientButton;
import com.medimate.view.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * DoctorChatPanel - Messaging panel matching the web DoctorChat page
 * Uses database-backed messaging with auto-refresh (replaces WebSocket)
 */
public class DoctorChatPanel extends JPanel {

    private final ChatService chatService = new ChatService();
    private JPanel userListPanel;
    private JPanel chatArea;
    private JPanel messagesPanel;
    private JTextField messageInput;
    private JLabel chatHeader;
    private User selectedUser = null;
    private Timer refreshTimer;
    private int lastMessageId = 0;

    public DoctorChatPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 0));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(" Doctor Chat", SwingConstants.CENTER);
        title.setFont(StyleUtil.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel connected = new JLabel("● Connected", SwingConstants.CENTER);
        connected.setFont(StyleUtil.FONT_SMALL);
        connected.setForeground(StyleUtil.SUCCESS_GREEN);
        connected.setAlignmentX(CENTER_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(connected);

        add(header, BorderLayout.NORTH);

        // Main content: user list + chat
        JPanel mainContent = new JPanel(new BorderLayout(10, 0));
        mainContent.setOpaque(false);

        // Left: user list
        mainContent.add(createUserListPanel(), BorderLayout.WEST);

        // Right: chat area
        chatArea = createChatArea();
        mainContent.add(chatArea, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        // Bottom: current user info
        JPanel userInfo = createUserInfoBar();
        add(userInfo, BorderLayout.SOUTH);

        // Load users
        refreshUserList();

        // Auto-refresh timer (every 2 seconds)
        refreshTimer = new Timer(2000, e -> {
            if (selectedUser != null) {
                refreshNewMessages();
            }
        });
        refreshTimer.start();
    }

    private JPanel createUserListPanel() {
        RoundedPanel panel = new RoundedPanel(20);
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel header = new JLabel(" Users");
        header.setFont(StyleUtil.FONT_SUBHEADING);
        header.setForeground(Color.WHITE);
        panel.add(header, BorderLayout.NORTH);

        userListPanel = new JPanel();
        userListPanel.setOpaque(false);
        userListPanel.setLayout(new BoxLayout(userListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(userListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createChatArea() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        // Chat header
        chatHeader = new JLabel("Select a user from the left to start chatting", SwingConstants.CENTER);
        chatHeader.setFont(StyleUtil.FONT_SUBHEADING);
        chatHeader.setForeground(StyleUtil.TEXT_WHITE_40);

        RoundedPanel headerPanel = new RoundedPanel(15);
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        headerPanel.setPreferredSize(new Dimension(0, 50));
        headerPanel.add(chatHeader);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Messages area
        messagesPanel = new JPanel();
        messagesPanel.setOpaque(false);
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));

        JScrollPane msgScroll = new JScrollPane(messagesPanel);
        msgScroll.setOpaque(false);
        msgScroll.getViewport().setOpaque(false);
        msgScroll.setBorder(BorderFactory.createLineBorder(StyleUtil.PANEL_BORDER, 1, true));
        msgScroll.getVerticalScrollBar().setUnitIncrement(16);

        RoundedPanel msgContainer = new RoundedPanel(20);
        msgContainer.setLayout(new BorderLayout());
        msgContainer.add(msgScroll, BorderLayout.CENTER);
        panel.add(msgContainer, BorderLayout.CENTER);

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);

        messageInput = new JTextField();
        messageInput.setFont(StyleUtil.FONT_BODY);
        messageInput.setForeground(Color.WHITE);
        messageInput.setCaretColor(Color.WHITE);
        messageInput.setBackground(new Color(255, 255, 255, 20));
        messageInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(StyleUtil.PANEL_BORDER, 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        messageInput.setPreferredSize(new Dimension(0, 45));
        messageInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        GradientButton sendBtn = new GradientButton("▸");
        sendBtn.setPreferredSize(new Dimension(60, 45));
        sendBtn.addActionListener(e -> sendMessage());

        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUserInfoBar() {
        RoundedPanel infoBar = new RoundedPanel(15);
        infoBar.setPreferredSize(new Dimension(0, 45));
        infoBar.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));

        JLabel icon = new JLabel("");
        User current = SessionManager.getInstance().getCurrentUser();
        JLabel text = new JLabel("Logged in as " + (current != null ? current.getName() : ""));
        text.setFont(StyleUtil.FONT_SMALL);
        text.setForeground(StyleUtil.TEXT_WHITE_60);

        infoBar.add(icon);
        infoBar.add(text);
        return infoBar;
    }

    private void refreshUserList() {
        User current = SessionManager.getInstance().getCurrentUser();
        if (current == null) return;

        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() {
                return chatService.getAvailableUsers(current.getId());
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    userListPanel.removeAll();
                    userListPanel.add(Box.createVerticalStrut(10));

                    if (users.isEmpty()) {
                        JLabel empty = new JLabel("No users available", SwingConstants.CENTER);
                        empty.setFont(StyleUtil.FONT_SMALL);
                        empty.setForeground(StyleUtil.TEXT_WHITE_40);
                        userListPanel.add(empty);
                    } else {
                        for (User user : users) {
                            userListPanel.add(createUserButton(user));
                            userListPanel.add(Box.createVerticalStrut(4));
                        }
                    }
                    userListPanel.revalidate();
                    userListPanel.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JPanel createUserButton(User user) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                if (selectedUser != null && selectedUser.getId() == user.getId()) {
                    g2.setColor(new Color(124, 58, 237, 60));
                } else {
                    g2.setColor(new Color(0, 0, 0, 0));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Avatar
        JLabel avatar = new JLabel(String.valueOf(user.getName().charAt(0)).toUpperCase()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                GradientPaint gp = new GradientPaint(0, 0, StyleUtil.LIGHT_VIOLET, getWidth(), getHeight(), StyleUtil.PRIMARY_PURPLE);
                g2.setPaint(gp);
                g2.fillOval(0, 0, 28, 28);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(28, 28));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JLabel name = new JLabel(user.getName());
        name.setFont(StyleUtil.FONT_SMALL);
        name.setForeground(Color.WHITE);

        btn.add(avatar);
        btn.add(name);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectUser(user);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.repaint();
            }
        });

        return btn;
    }

    private void selectUser(User user) {
        selectedUser = user;
        lastMessageId = 0;
        chatHeader.setText(" " + user.getName() + " (" + user.getRole() + ")");
        chatHeader.setForeground(Color.WHITE);
        loadMessages();
        refreshUserList(); // Repaint to show selection
    }

    private void loadMessages() {
        if (selectedUser == null) return;
        User current = SessionManager.getInstance().getCurrentUser();

        SwingWorker<List<ChatMessage>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChatMessage> doInBackground() {
                return chatService.getMessages(current.getId(), selectedUser.getId());
            }

            @Override
            protected void done() {
                try {
                    List<ChatMessage> messages = get();
                    messagesPanel.removeAll();

                    for (ChatMessage msg : messages) {
                        messagesPanel.add(createMessageBubble(msg));
                        messagesPanel.add(Box.createVerticalStrut(5));
                        lastMessageId = Math.max(lastMessageId, msg.getId());
                    }

                    messagesPanel.revalidate();
                    messagesPanel.repaint();
                    scrollToBottom();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void refreshNewMessages() {
        if (selectedUser == null) return;
        User current = SessionManager.getInstance().getCurrentUser();

        SwingWorker<List<ChatMessage>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChatMessage> doInBackground() {
                return chatService.getNewMessages(current.getId(), selectedUser.getId(), lastMessageId);
            }

            @Override
            protected void done() {
                try {
                    List<ChatMessage> newMsgs = get();
                    if (!newMsgs.isEmpty()) {
                        for (ChatMessage msg : newMsgs) {
                            messagesPanel.add(createMessageBubble(msg));
                            messagesPanel.add(Box.createVerticalStrut(5));
                            lastMessageId = Math.max(lastMessageId, msg.getId());
                        }
                        messagesPanel.revalidate();
                        messagesPanel.repaint();
                        scrollToBottom();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JPanel createMessageBubble(ChatMessage msg) {
        User current = SessionManager.getInstance().getCurrentUser();
        boolean isMe = msg.getSenderId() == current.getId();

        JPanel row = new JPanel(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT, 8, 2));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Avatar
        if (!isMe) {
            JLabel avatar = new JLabel(String.valueOf(msg.getSenderName().charAt(0)).toUpperCase()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    StyleUtil.enableAntiAliasing(g2);
                    g2.setColor(StyleUtil.PRIMARY_VIOLET);
                    g2.fillOval(0, 0, 26, 26);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            avatar.setPreferredSize(new Dimension(26, 26));
            avatar.setHorizontalAlignment(SwingConstants.CENTER);
            avatar.setFont(new Font("Segoe UI", Font.BOLD, 10));
            avatar.setForeground(Color.WHITE);
            row.add(avatar);
        }

        // Message bubble
        JPanel bubble = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                if (isMe) {
                    GradientPaint gp = new GradientPaint(0, 0, StyleUtil.PRIMARY_VIOLET, getWidth(), 0, StyleUtil.PRIMARY_PURPLE);
                    g2.setPaint(gp);
                } else {
                    g2.setColor(new Color(255, 255, 255, 50));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubble.setOpaque(false);
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JLabel senderLabel = new JLabel(msg.getSenderName());
        senderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        senderLabel.setForeground(StyleUtil.TEXT_WHITE_40);

        JLabel msgLabel = new JLabel("<html><body style='width:200px'>" + msg.getMessage() + "</body></html>");
        msgLabel.setFont(StyleUtil.FONT_BODY);
        msgLabel.setForeground(Color.WHITE);

        bubble.add(senderLabel);
        bubble.add(msgLabel);
        row.add(bubble);

        return row;
    }

    private void sendMessage() {
        if (selectedUser == null || messageInput.getText().trim().isEmpty()) return;

        String text = messageInput.getText().trim();
        User current = SessionManager.getInstance().getCurrentUser();
        messageInput.setText("");

        SwingWorker<ChatMessage, Void> worker = new SwingWorker<>() {
            @Override
            protected ChatMessage doInBackground() {
                return chatService.sendMessage(current.getId(), selectedUser.getId(), text);
            }

            @Override
            protected void done() {
                try {
                    ChatMessage msg = get();
                    if (msg != null) {
                        // Create a display message with names
                        msg.setSenderName(current.getName());
                        msg.setReceiverName(selectedUser.getName());
                        messagesPanel.add(createMessageBubble(msg));
                        messagesPanel.add(Box.createVerticalStrut(5));
                        lastMessageId = Math.max(lastMessageId, msg.getId());
                        messagesPanel.revalidate();
                        messagesPanel.repaint();
                        scrollToBottom();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, messagesPanel);
            if (sp != null) {
                JScrollBar sb = sp.getVerticalScrollBar();
                sb.setValue(sb.getMaximum());
            }
        });
    }

    /**
     * Stop the refresh timer when panel is no longer visible
     */
    public void stopRefresh() {
        if (refreshTimer != null) refreshTimer.stop();
    }

    public void startRefresh() {
        if (refreshTimer != null) refreshTimer.start();
    }
}
