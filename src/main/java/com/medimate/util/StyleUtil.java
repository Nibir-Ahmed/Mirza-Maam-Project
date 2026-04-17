package com.medimate.util;

import java.awt.*;

/**
 * StyleUtil - Central place for all UI styling constants
 * Matches the web app's dark violet/purple glassmorphism theme
 */
public class StyleUtil {

    // === Colors (matching web theme) ===
    public static final Color BG_DARK_1 = new Color(46, 16, 101);       // #2e1065
    public static final Color BG_DARK_2 = new Color(88, 28, 135);       // #581c87
    public static final Color BG_DARK_3 = new Color(30, 27, 75);        // #1e1b4b

    public static final Color PRIMARY_VIOLET = new Color(124, 58, 237);  // #7c3aed
    public static final Color PRIMARY_PURPLE = new Color(147, 51, 234);  // #9333ea
    public static final Color LIGHT_VIOLET = new Color(167, 139, 250);   // #a78bfa

    public static final Color PANEL_BG = new Color(255, 255, 255, 25);   // white/10
    public static final Color PANEL_BG_HOVER = new Color(255, 255, 255, 38); // white/15
    public static final Color PANEL_BORDER = new Color(255, 255, 255, 51);   // white/20

    public static final Color TEXT_WHITE = new Color(255, 255, 255);
    public static final Color TEXT_WHITE_70 = new Color(255, 255, 255, 178);
    public static final Color TEXT_WHITE_60 = new Color(255, 255, 255, 153);
    public static final Color TEXT_WHITE_50 = new Color(255, 255, 255, 128);
    public static final Color TEXT_WHITE_40 = new Color(255, 255, 255, 102);

    public static final Color SUCCESS_GREEN = new Color(74, 222, 128);   // green-400
    public static final Color ERROR_RED = new Color(248, 113, 113);      // red-400
    public static final Color WARNING_YELLOW = new Color(250, 204, 21);  // yellow-400

    public static final Color SIDEBAR_BG = new Color(255, 255, 255, 25);
    public static final Color SIDEBAR_HOVER = new Color(255, 255, 255, 51);
    public static final Color SIDEBAR_ACTIVE = new Color(255, 255, 255, 51);

    // === Fonts ===
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 24);

    // === Dimensions ===
    public static final int SIDEBAR_WIDTH = 220;
    public static final int CORNER_RADIUS = 20;
    public static final int BUTTON_RADIUS = 15;
    public static final int FIELD_HEIGHT = 45;
    public static final int BUTTON_HEIGHT = 45;

    // === Insets ===
    public static final Insets PANEL_PADDING = new Insets(20, 20, 20, 20);
    public static final Insets FIELD_PADDING = new Insets(10, 15, 10, 15);

    /**
     * Enable anti-aliased text rendering
     */
    public static void enableAntiAliasing(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    /**
     * Paint a gradient background matching the web app
     */
    public static void paintGradientBackground(Graphics g, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        enableAntiAliasing(g);
        GradientPaint gp = new GradientPaint(0, 0, BG_DARK_1, width, height, BG_DARK_3);
        g2.setPaint(gp);
        g2.fillRect(0, 0, width, height);
    }

    /**
     * Paint a button gradient (violet to purple)
     */
    public static void paintButtonGradient(Graphics2D g2, int width, int height, int radius) {
        GradientPaint gp = new GradientPaint(0, 0, PRIMARY_VIOLET, width, 0, PRIMARY_PURPLE);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, width, height, radius, radius);
    }
}
