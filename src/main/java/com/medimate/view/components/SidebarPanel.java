package com.medimate.view.components;

import com.medimate.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * SidebarPanel - Navigation sidebar matching the web app's sidebar
 * Contains navigation buttons for Home, AI Chat, Doctor Chat, Blog, Admin
 */
public class SidebarPanel extends JPanel {

    private JButton activeButton = null;
    private JPanel buttonContainer;

    public SidebarPanel() {
        setPreferredSize(new Dimension(StyleUtil.SIDEBAR_WIDTH, 0));
        setOpaque(false);
        setLayout(new BorderLayout());

        // Main container
        JPanel container = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                StyleUtil.enableAntiAliasing(g2);
                g2.setColor(StyleUtil.SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right border
                g2.setColor(StyleUtil.PANEL_BORDER);
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        container.setOpaque(false);
        container.setLayout(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Button container (top)
        buttonContainer = new JPanel();
        buttonContainer.setOpaque(false);
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));

        container.add(buttonContainer, BorderLayout.NORTH);

        // Bottom quick help card
        JPanel quickHelp = createQuickHelpCard();
        container.add(quickHelp, BorderLayout.SOUTH);

        add(container, BorderLayout.CENTER);
    }

    /**
     * Add a navigation button
     */
    public JButton addNavButton(String icon, String text, ActionListener action) {
        JButton button = new JButton(icon + "  " + text);
        button.setFont(StyleUtil.FONT_BODY);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeButton) {
                    button.setOpaque(true);
                    button.setBackground(StyleUtil.SIDEBAR_HOVER);
                    button.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    button.setOpaque(false);
                    button.repaint();
                }
            }
        });

        button.addActionListener(e -> {
            setActiveButton(button);
            action.actionPerformed(e);
        });

        buttonContainer.add(button);
        buttonContainer.add(Box.createVerticalStrut(3));
        return button;
    }

    /**
     * Set the active (highlighted) button
     */
    public void setActiveButton(JButton button) {
        // Reset previous
        if (activeButton != null) {
            activeButton.setOpaque(false);
            activeButton.setFont(StyleUtil.FONT_BODY);
            activeButton.repaint();
        }

        // Set new active
        activeButton = button;
        activeButton.setOpaque(true);
        activeButton.setBackground(StyleUtil.SIDEBAR_ACTIVE);
        activeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        activeButton.repaint();
    }

    /**
     * Create the "Quick Help" card at the bottom of sidebar
     */
    private JPanel createQuickHelpCard() {
        RoundedPanel card = new RoundedPanel(15, new Color(124, 58, 237, 50));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel helpText = new JLabel("<html>Emergency? AI সাথে<br>কথা বলুন এখনই!</html>");
        helpText.setFont(StyleUtil.FONT_SMALL);
        helpText.setForeground(StyleUtil.TEXT_WHITE_70);
        helpText.setAlignmentX(LEFT_ALIGNMENT);

        card.add(helpText);
        card.add(Box.createVerticalStrut(10));

        return card;
    }
}
