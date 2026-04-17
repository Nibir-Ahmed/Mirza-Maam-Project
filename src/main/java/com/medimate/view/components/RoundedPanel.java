package com.medimate.view.components;

import com.medimate.util.StyleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * RoundedPanel - Custom JPanel with rounded corners and semi-transparent background
 * Replicates the glassmorphism card look from the web app
 */
public class RoundedPanel extends JPanel {

    private int cornerRadius;
    private Color backgroundColor;
    private Color borderColor;

    public RoundedPanel(int radius) {
        this.cornerRadius = radius;
        this.backgroundColor = StyleUtil.PANEL_BG;
        this.borderColor = StyleUtil.PANEL_BORDER;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bgColor) {
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = StyleUtil.PANEL_BORDER;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bgColor, Color borderColor) {
        this.cornerRadius = radius;
        this.backgroundColor = bgColor;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        StyleUtil.enableAntiAliasing(g2);

        // Fill rounded rect background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Draw border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
