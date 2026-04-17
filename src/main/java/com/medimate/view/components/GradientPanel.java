package com.medimate.view.components;

import com.medimate.util.StyleUtil;

import javax.swing.*;
import java.awt.*;

/**
 * GradientPanel - Custom JPanel with gradient background
 * Replicates the web app's gradient body background
 */
public class GradientPanel extends JPanel {

    private Color color1;
    private Color color2;

    public GradientPanel() {
        this.color1 = StyleUtil.BG_DARK_1;
        this.color2 = StyleUtil.BG_DARK_3;
        setOpaque(false);
    }

    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        StyleUtil.enableAntiAliasing(g2);
        GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
