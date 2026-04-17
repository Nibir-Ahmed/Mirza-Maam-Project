package com.medimate.view.components;

import com.medimate.model.Post;
import com.medimate.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * PostCardPanel - Individual blog post card widget
 * Matches the web app's PostCard component
 */
public class PostCardPanel extends RoundedPanel {

    public PostCardPanel(Post post) {
        super(20);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Title with icon
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel bookIcon = new JLabel("📖");
        bookIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        titleRow.add(bookIcon);

        JLabel titleLabel = new JLabel(post.getTitle());
        titleLabel.setFont(StyleUtil.FONT_SUBHEADING);
        titleLabel.setForeground(Color.WHITE);
        titleRow.add(titleLabel);

        content.add(titleRow);
        content.add(Box.createVerticalStrut(8));

        // Content preview (truncated)
        String preview = post.getContent();
        if (preview.length() > 120) {
            preview = preview.substring(0, 120) + "...";
        }
        JLabel contentLabel = new JLabel("<html><body style='width: 280px'>" + preview + "</body></html>");
        contentLabel.setFont(StyleUtil.FONT_BODY);
        contentLabel.setForeground(StyleUtil.TEXT_WHITE_60);
        contentLabel.setAlignmentX(LEFT_ALIGNMENT);
        content.add(contentLabel);
        content.add(Box.createVerticalStrut(12));

        // Author row with separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 25));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        content.add(sep);
        content.add(Box.createVerticalStrut(8));

        JPanel authorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        authorRow.setOpaque(false);
        authorRow.setAlignmentX(LEFT_ALIGNMENT);

        // Avatar circle
        JLabel avatar = new JLabel(String.valueOf(post.getAuthorName().charAt(0)).toUpperCase()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                GradientPaint gp = new GradientPaint(0, 0, StyleUtil.LIGHT_VIOLET, getWidth(), getHeight(), StyleUtil.PRIMARY_PURPLE);
                g2.setPaint(gp);
                g2.fillOval(0, 0, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(24, 24));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 10));
        avatar.setForeground(Color.WHITE);
        authorRow.add(avatar);

        JPanel authorInfo = new JPanel();
        authorInfo.setOpaque(false);
        authorInfo.setLayout(new BoxLayout(authorInfo, BoxLayout.Y_AXIS));

        JLabel authorName = new JLabel(post.getAuthorName());
        authorName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        authorName.setForeground(StyleUtil.TEXT_WHITE_70);

        JLabel dateLabel = new JLabel("");
        if (post.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateLabel.setText(sdf.format(post.getCreatedAt()));
        }
        dateLabel.setFont(StyleUtil.FONT_SMALL);
        dateLabel.setForeground(StyleUtil.TEXT_WHITE_40);

        authorInfo.add(authorName);
        authorInfo.add(dateLabel);
        authorRow.add(authorInfo);

        content.add(authorRow);
        add(content, BorderLayout.CENTER);
    }
}
