package com.medimate.view;

import com.medimate.model.Post;
import com.medimate.service.PostService;
import com.medimate.util.SessionManager;
import com.medimate.util.StyleUtil;
import com.medimate.view.components.GradientButton;
import com.medimate.view.components.PostCardPanel;
import com.medimate.view.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * BlogPanel - Blog reading/writing panel matching the web Blog page
 * Doctors can create posts, everyone can read
 */
public class BlogPanel extends JPanel {

    private final PostService postService = new PostService();
    private JTextField titleField;
    private JTextArea contentArea;
    private JPanel postsContainer;

    public BlogPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(createContent());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createContent() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Header
        JLabel title = new JLabel(" Health Blog", SwingConstants.CENTER);
        title.setFont(StyleUtil.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Read health advice posts from doctors", SwingConstants.CENTER);
        subtitle.setFont(StyleUtil.FONT_BODY);
        subtitle.setForeground(StyleUtil.TEXT_WHITE_60);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(5));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(20));

        // Post form (Doctor only)
        if (SessionManager.getInstance().isDoctor()) {
            content.add(createPostForm());
            content.add(Box.createVerticalStrut(20));
        }

        // Posts container
        postsContainer = new JPanel();
        postsContainer.setOpaque(false);
        postsContainer.setLayout(new BoxLayout(postsContainer, BoxLayout.Y_AXIS));
        postsContainer.setAlignmentX(LEFT_ALIGNMENT);
        content.add(postsContainer);

        refreshPosts();
        return content;
    }

    private JPanel createPostForm() {
        RoundedPanel form = new RoundedPanel(25);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JLabel formTitle = new JLabel("✎ Write a New Post");
        formTitle.setFont(StyleUtil.FONT_SUBHEADING);
        formTitle.setForeground(Color.WHITE);
        formTitle.setAlignmentX(LEFT_ALIGNMENT);
        form.add(formTitle);
        form.add(Box.createVerticalStrut(12));

        // Title field
        titleField = new JTextField();
        titleField.setFont(StyleUtil.FONT_BODY);
        titleField.setForeground(Color.WHITE);
        titleField.setCaretColor(Color.WHITE);
        titleField.setBackground(new Color(255, 255, 255, 20));
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(StyleUtil.PANEL_BORDER, 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        titleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        titleField.setAlignmentX(LEFT_ALIGNMENT);
        form.add(titleField);
        form.add(Box.createVerticalStrut(10));

        // Content area
        contentArea = new JTextArea(4, 40);
        contentArea.setFont(StyleUtil.FONT_BODY);
        contentArea.setForeground(Color.WHITE);
        contentArea.setCaretColor(Color.WHITE);
        contentArea.setBackground(new Color(255, 255, 255, 20));
        contentArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(StyleUtil.PANEL_BORDER, 1, true));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);
        form.add(scrollPane);
        form.add(Box.createVerticalStrut(12));

        // Submit button
        GradientButton postBtn = new GradientButton("▸ Submit Post");
        postBtn.setPreferredSize(new Dimension(160, 40));
        postBtn.setAlignmentX(LEFT_ALIGNMENT);
        postBtn.addActionListener(e -> handleCreatePost());
        form.add(postBtn);

        return form;
    }

    private void handleCreatePost() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Title and Content!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        var user = SessionManager.getInstance().getCurrentUser();

        SwingWorker<Post, Void> worker = new SwingWorker<>() {
            @Override
            protected Post doInBackground() {
                return postService.createPost(title, content, user.getName(), user.getId());
            }

            @Override
            protected void done() {
                try {
                    Post post = get();
                    if (post != null) {
                        titleField.setText("");
                        contentArea.setText("");
                        refreshPosts();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void refreshPosts() {
        SwingWorker<List<Post>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Post> doInBackground() {
                return postService.getAllPosts();
            }

            @Override
            protected void done() {
                try {
                    List<Post> posts = get();
                    postsContainer.removeAll();

                    if (posts.isEmpty()) {
                        JLabel empty = new JLabel("No posts available", SwingConstants.CENTER);
                        empty.setFont(StyleUtil.FONT_BODY);
                        empty.setForeground(StyleUtil.TEXT_WHITE_40);
                        postsContainer.add(empty);
                    } else {
                        for (Post post : posts) {
                            postsContainer.add(new PostCardPanel(post));
                            postsContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150)); 
                            postsContainer.add(Box.createVerticalStrut(10));
                        }
                    }
                    postsContainer.revalidate();
                    postsContainer.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}
