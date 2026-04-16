package com.medimate.view;

import com.medimate.model.Post;
import com.medimate.service.PostService;
import com.medimate.util.StyleUtil;
import com.medimate.view.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * HomePanel - Dashboard/home content matching the web Home page
 * Shows feature banner + latest blog posts grid
 */
public class HomePanel extends JPanel {

    private final PostService postService = new PostService();
    private JPanel postsGrid;

    public HomePanel() {
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

        // Feature Banner
        content.add(createFeatureBanner());
        content.add(Box.createVerticalStrut(25));

        // Latest Posts header
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel postsTitle = new JLabel("📰 Latest Posts");
        postsTitle.setFont(StyleUtil.FONT_HEADING);
        postsTitle.setForeground(Color.WHITE);
        headerRow.add(postsTitle, BorderLayout.WEST);

        content.add(headerRow);
        content.add(Box.createVerticalStrut(15));

        // Posts grid
        postsGrid = new JPanel(new GridLayout(0, 3, 12, 12));
        postsGrid.setOpaque(false);
        postsGrid.setAlignmentX(LEFT_ALIGNMENT);
        content.add(postsGrid);

        // Load posts
        refreshPosts();

        return content;
    }

    private JPanel createFeatureBanner() {
        // Feature cards row (replaces web slider)
        JPanel featuresRow = new JPanel(new GridLayout(1, 4, 10, 0));
        featuresRow.setOpaque(false);
        featuresRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        String[][] features = {
            {"", "AI Diagnosis", "Get suggestions from AI by entering symptoms", "#7c3aed", "#6d28d9"},
            {"", "Doctor Chat", "Talk to a doctor in real-time", "#9333ea", "#4f46e5"},
            {"", "Emergency Help", "Get rapid support in any emergency", "#4f46e5", "#7c3aed"},
            {"", "Health Blog", "Read health advice posts from doctors", "#6d28d9", "#9333ea"}
        };

        for (String[] f : features) {
            Color c1 = Color.decode(f[3]);
            Color c2 = Color.decode(f[4]);
            JPanel card = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    StyleUtil.enableAntiAliasing(g2);
                    GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            card.setOpaque(false);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

            JLabel icon = new JLabel(f[0]);
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 28));
            icon.setAlignmentX(LEFT_ALIGNMENT);

            JLabel title = new JLabel(f[1]);
            title.setFont(new Font("Segoe UI", Font.BOLD, 16));
            title.setForeground(Color.WHITE);
            title.setAlignmentX(LEFT_ALIGNMENT);

            JLabel desc = new JLabel("<html>" + f[2] + "</html>");
            desc.setFont(StyleUtil.FONT_SMALL);
            desc.setForeground(new Color(255, 255, 255, 200));
            desc.setAlignmentX(LEFT_ALIGNMENT);

            card.add(icon);
            card.add(Box.createVerticalStrut(8));
            card.add(title);
            card.add(Box.createVerticalStrut(4));
            card.add(desc);

            featuresRow.add(card);
        }

        return featuresRow;
    }

    public void refreshPosts() {
        SwingWorker<List<Post>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Post> doInBackground() {
                return postService.getRecentPosts(6);
            }

            @Override
            protected void done() {
                try {
                    List<Post> posts = get();
                    postsGrid.removeAll();

                    if (posts.isEmpty()) {
                        JLabel empty = new JLabel("No posts available", SwingConstants.CENTER);
                        empty.setFont(StyleUtil.FONT_BODY);
                        empty.setForeground(StyleUtil.TEXT_WHITE_40);
                        postsGrid.setLayout(new BorderLayout());
                        postsGrid.add(empty, BorderLayout.CENTER);
                    } else {
                        postsGrid.setLayout(new GridLayout(0, 3, 12, 12));
                        for (Post post : posts) {
                            postsGrid.add(createPostCard(post));
                        }
                    }
                    postsGrid.revalidate();
                    postsGrid.repaint();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JPanel createPostCard(Post post) {
        RoundedPanel card = new RoundedPanel(20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel(post.getTitle());
        title.setFont(StyleUtil.FONT_SUBHEADING);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(LEFT_ALIGNMENT);

        String preview = post.getContent();
        if (preview.length() > 80) preview = preview.substring(0, 80) + "...";
        JLabel content = new JLabel("<html><body style='width:160px'>" + preview + "</body></html>");
        content.setFont(StyleUtil.FONT_SMALL);
        content.setForeground(StyleUtil.TEXT_WHITE_60);
        content.setAlignmentX(LEFT_ALIGNMENT);

        JLabel author = new JLabel("👤 " + post.getAuthorName());
        author.setFont(StyleUtil.FONT_SMALL);
        author.setForeground(StyleUtil.TEXT_WHITE_40);
        author.setAlignmentX(LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(content);
        card.add(Box.createVerticalGlue());
        card.add(Box.createVerticalStrut(10));
        card.add(author);

        return card;
    }
}
