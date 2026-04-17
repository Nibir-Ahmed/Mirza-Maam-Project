package com.medimate.view;

import com.medimate.service.AIService;
import com.medimate.util.SessionManager;
import com.medimate.util.StyleUtil;
import com.medimate.view.components.GradientButton;
import com.medimate.view.components.RoundedPanel;

import javax.swing.*;
import java.awt.*;

/**
 * AIChatPanel - AI symptom analysis panel matching the web AIChat page
 * Calls Groq API for medical advice
 */
public class AIChatPanel extends JPanel {

    private JTextArea symptomsArea;
    private JTextArea responseArea;
    private GradientButton analyzeBtn;
    private JPanel responsePanel;
    private final AIService aiService = new AIService();

    public AIChatPanel() {
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
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setAlignmentX(CENTER_ALIGNMENT);

        // Active badge
        RoundedPanel badge = new RoundedPanel(20, new Color(255, 255, 255, 25));
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        badge.setMaximumSize(new Dimension(200, 30));
        JLabel dot = new JLabel("●");
        dot.setForeground(StyleUtil.SUCCESS_GREEN);
        dot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel activeText = new JLabel("AI Assistant Active");
        activeText.setFont(StyleUtil.FONT_SMALL);
        activeText.setForeground(StyleUtil.TEXT_WHITE_70);
        badge.add(dot);
        badge.add(activeText);
        badge.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("✦ MediMate AI", SwingConstants.CENTER);
        title.setFont(StyleUtil.FONT_TITLE);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Enter your symptoms to receive instant AI advice", SwingConstants.CENTER);
        subtitle.setFont(StyleUtil.FONT_BODY);
        subtitle.setForeground(StyleUtil.TEXT_WHITE_60);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        header.add(badge);
        header.add(Box.createVerticalStrut(10));
        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitle);

        content.add(header);
        content.add(Box.createVerticalStrut(20));

        // How to use cards
        JPanel howToUse = new JPanel(new GridLayout(1, 3, 10, 0));
        howToUse.setOpaque(false);
        howToUse.setMaximumSize(new Dimension(700, 90));
        howToUse.setAlignmentX(CENTER_ALIGNMENT);

        String[][] steps = {
            {"✎", "Enter Symptoms", "Describe your symptoms below"},
            {"⟡", "AI Analysis", "Get instant diagnosis advice"},
            {"✜", "Medical Advice", "Know what to do before seeing a doctor"}
        };

        for (String[] step : steps) {
            RoundedPanel stepCard = new RoundedPanel(15);
            stepCard.setLayout(new BoxLayout(stepCard, BoxLayout.Y_AXIS));
            stepCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

            JLabel icon = new JLabel(step[0], SwingConstants.CENTER);
            icon.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            icon.setAlignmentX(CENTER_ALIGNMENT);
            JLabel stepTitle = new JLabel(step[1], SwingConstants.CENTER);
            stepTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            stepTitle.setForeground(Color.WHITE);
            stepTitle.setAlignmentX(CENTER_ALIGNMENT);
            JLabel stepDesc = new JLabel(step[2], SwingConstants.CENTER);
            stepDesc.setFont(StyleUtil.FONT_SMALL);
            stepDesc.setForeground(StyleUtil.TEXT_WHITE_50);
            stepDesc.setAlignmentX(CENTER_ALIGNMENT);

            stepCard.add(icon);
            stepCard.add(Box.createVerticalStrut(5));
            stepCard.add(stepTitle);
            stepCard.add(Box.createVerticalStrut(3));
            stepCard.add(stepDesc);
            howToUse.add(stepCard);
        }
        content.add(howToUse);
        content.add(Box.createVerticalStrut(20));

        // Input card
        RoundedPanel inputCard = new RoundedPanel(25);
        inputCard.setLayout(new BoxLayout(inputCard, BoxLayout.Y_AXIS));
        inputCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputCard.setMaximumSize(new Dimension(700, 250));
        inputCard.setAlignmentX(CENTER_ALIGNMENT);

        // Symptoms text area
        symptomsArea = new JTextArea(4, 40);
        symptomsArea.setFont(StyleUtil.FONT_BODY);
        symptomsArea.setForeground(Color.WHITE);
        symptomsArea.setCaretColor(Color.WHITE);
        symptomsArea.setBackground(new Color(255, 255, 255, 20));
        symptomsArea.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        symptomsArea.setLineWrap(true);
        symptomsArea.setWrapStyleWord(true);

        JScrollPane symptomsScroll = new JScrollPane(symptomsArea);
        symptomsScroll.setBorder(BorderFactory.createLineBorder(StyleUtil.PANEL_BORDER, 1, true));
        symptomsScroll.setOpaque(false);
        symptomsScroll.getViewport().setOpaque(false);
        symptomsScroll.setAlignmentX(LEFT_ALIGNMENT);
        inputCard.add(symptomsScroll);
        inputCard.add(Box.createVerticalStrut(12));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        analyzeBtn = new GradientButton("▸ Analyze with AI");
        analyzeBtn.setPreferredSize(new Dimension(250, 42));
        analyzeBtn.addActionListener(e -> handleAnalyze());
        btnRow.add(analyzeBtn);

        JButton resetBtn = new JButton("↺");
        resetBtn.setFont(StyleUtil.FONT_BODY);
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setBackground(new Color(255, 255, 255, 25));
        resetBtn.setBorderPainted(false);
        resetBtn.setFocusPainted(false);
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.setPreferredSize(new Dimension(50, 42));
        resetBtn.addActionListener(e -> handleReset());
        btnRow.add(resetBtn);

        inputCard.add(btnRow);
        content.add(inputCard);
        content.add(Box.createVerticalStrut(15));

        // Response panel (hidden by default)
        responsePanel = new JPanel();
        responsePanel.setOpaque(false);
        responsePanel.setLayout(new BoxLayout(responsePanel, BoxLayout.Y_AXIS));
        responsePanel.setMaximumSize(new Dimension(700, 600));
        responsePanel.setAlignmentX(CENTER_ALIGNMENT);
        responsePanel.setVisible(false);
        content.add(responsePanel);

        return content;
    }

    private void handleAnalyze() {
        String symptoms = symptomsArea.getText().trim();
        if (symptoms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your symptoms!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        analyzeBtn.setEnabled(false);
        analyzeBtn.setText("Analyzing...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return aiService.analyzeSymptoms(symptoms);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    showResponse(result);
                } catch (Exception ex) {
                    showResponse("[!] Something went wrong. Please try again.");
                }
                analyzeBtn.setEnabled(true);
                analyzeBtn.setText("▸ Analyze with AI");
            }
        };
        worker.execute();
    }

    private void showResponse(String result) {
        responsePanel.removeAll();
        responsePanel.setVisible(true);

        RoundedPanel responseCard = new RoundedPanel(25);
        responseCard.setLayout(new BoxLayout(responseCard, BoxLayout.Y_AXIS));
        responseCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // AI header
        JPanel aiHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        aiHeader.setOpaque(false);

        JLabel aiAvatar = new JLabel("✜");
        aiAvatar.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        JPanel aiInfo = new JPanel();
        aiInfo.setOpaque(false);
        aiInfo.setLayout(new BoxLayout(aiInfo, BoxLayout.Y_AXIS));
        JLabel aiName = new JLabel("MediMate AI");
        aiName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        aiName.setForeground(Color.WHITE);
        JLabel aiDesc = new JLabel("AI Medical Advice");
        aiDesc.setFont(StyleUtil.FONT_SMALL);
        aiDesc.setForeground(StyleUtil.TEXT_WHITE_50);
        aiInfo.add(aiName);
        aiInfo.add(aiDesc);

        aiHeader.add(aiAvatar);
        aiHeader.add(aiInfo);
        aiHeader.setAlignmentX(LEFT_ALIGNMENT);
        responseCard.add(aiHeader);
        responseCard.add(Box.createVerticalStrut(12));

        // Response text
        responseArea = new JTextArea(result);
        responseArea.setEditable(false);
        responseArea.setFont(StyleUtil.FONT_BODY);
        responseArea.setForeground(new Color(255, 255, 255, 230));
        responseArea.setBackground(new Color(0, 0, 0, 0));
        responseArea.setOpaque(false);
        responseArea.setLineWrap(true);
        responseArea.setWrapStyleWord(true);
        responseArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        responseArea.setAlignmentX(LEFT_ALIGNMENT);
        responseCard.add(responseArea);
        responseCard.add(Box.createVerticalStrut(12));

        // Warning box
        RoundedPanel warningBox = new RoundedPanel(15, new Color(250, 204, 21, 40), new Color(250, 204, 21, 60));
        warningBox.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        warningBox.setAlignmentX(LEFT_ALIGNMENT);
        JLabel warningIcon = new JLabel("⚠");
        warningIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JLabel warningText = new JLabel("<html>Note: This is strictly preliminary AI advice. Please consult a qualified doctor for actual medical treatment.</html>");
        warningText.setFont(StyleUtil.FONT_SMALL);
        warningText.setForeground(new Color(254, 240, 138));
        warningBox.add(warningIcon);
        warningBox.add(warningText);
        responseCard.add(warningBox);

        responsePanel.add(responseCard);
        responsePanel.revalidate();
        responsePanel.repaint();
    }

    private void handleReset() {
        symptomsArea.setText("");
        responsePanel.setVisible(false);
        responsePanel.removeAll();
    }
}
