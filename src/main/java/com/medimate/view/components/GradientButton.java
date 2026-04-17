package com.medimate.view.components;

import com.medimate.util.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class GradientButton extends JButton {

    private boolean hovered = false;
    private Color gradientStart = StyleUtil.PRIMARY_VIOLET;
    private Color gradientEnd = StyleUtil.PRIMARY_PURPLE;

    public GradientButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(StyleUtil.FONT_BUTTON);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(200, StyleUtil.BUTTON_HEIGHT));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        StyleUtil.enableAntiAliasing(g2);

        Color start = hovered ? gradientStart.brighter() : gradientStart;
        Color end = hovered ? gradientEnd.brighter() : gradientEnd;

        GradientPaint gp = new GradientPaint(0, 0, start, getWidth(), 0, end);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), StyleUtil.BUTTON_RADIUS, StyleUtil.BUTTON_RADIUS);

        // Draw shadow
        if (!hovered) {
            g2.setColor(new Color(124, 58, 237, 80));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 2, StyleUtil.BUTTON_RADIUS, StyleUtil.BUTTON_RADIUS);
        }

        // Redraw button on top of shadow
        gp = new GradientPaint(0, 0, start, getWidth(), 0, end);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight() - 2, StyleUtil.BUTTON_RADIUS, StyleUtil.BUTTON_RADIUS);

        g2.dispose();
        super.paintComponent(g);
    }
}
