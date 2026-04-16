package com.medimate;

import com.medimate.view.LoginFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main - Entry point for MediMate Desktop Application
 * Sets up the look and feel and launches the login screen
 */
public class Main {

    public static void main(String[] args) {
        // Set system properties for better rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Run on Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> {
            try {
                // Try Nimbus look and feel for better aesthetics
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }

                // Customize Nimbus colors for dark theme
                UIManager.put("control", new Color(46, 16, 101));
                UIManager.put("nimbusBase", new Color(46, 16, 101));
                UIManager.put("nimbusFocus", new Color(124, 58, 237));
                UIManager.put("nimbusLightBackground", new Color(30, 27, 75));
                UIManager.put("info", new Color(46, 16, 101));
                UIManager.put("nimbusSelectionBackground", new Color(124, 58, 237));

                // Text field and area colors
                UIManager.put("TextField.background", new Color(30, 27, 75));
                UIManager.put("TextField.foreground", Color.WHITE);
                UIManager.put("TextArea.background", new Color(30, 27, 75));
                UIManager.put("TextArea.foreground", Color.WHITE);
                UIManager.put("PasswordField.background", new Color(30, 27, 75));
                UIManager.put("PasswordField.foreground", Color.WHITE);

                // Combo box
                UIManager.put("ComboBox.background", new Color(46, 16, 101));
                UIManager.put("ComboBox.foreground", Color.WHITE);

                // Option pane
                UIManager.put("OptionPane.background", new Color(46, 16, 101));
                UIManager.put("OptionPane.messageForeground", Color.WHITE);
                UIManager.put("Panel.background", new Color(46, 16, 101));

                // ScrollBar
                UIManager.put("ScrollBar.thumb", new Color(124, 58, 237, 100));
                UIManager.put("ScrollBar.track", new Color(30, 27, 75));

            } catch (Exception e) {
                // Fall back to default look and feel
                System.err.println("Could not set Nimbus L&F: " + e.getMessage());
            }

            // Launch login screen
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);

            System.out.println("🏥 MediMate Desktop Application started!");
        });
    }
}
