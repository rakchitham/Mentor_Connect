package org.example;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FeedbackWindow extends JDialog {

    private final String menteeName;
    private final String mentorName;
    private final int requestId;


    private static final Color BG_DARK_MAIN = new Color(25, 29, 36);
    private static final Color TEXT_LIGHT_PRIMARY = Color.WHITE;
    private static final Color ACCENT_PURPLE = new Color(130, 0, 255);
    private static final Color ACCENT_HOVER = new Color(170, 80, 255);

    public FeedbackWindow(JFrame parent, String menteeName, String mentorName, int requestId) {
        super(parent, "Session Feedback & Rating", true);
        this.menteeName = menteeName;
        this.mentorName = mentorName;
        this.requestId = requestId;

        setTitle("Feedback for " + mentorName);
        setSize(450, 400);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(BG_DARK_MAIN);
        setLayout(new BorderLayout(10, 10));


        JLabel title = new JLabel("Rate Your Session with " + mentorName, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_LIGHT_PRIMARY);
        add(title, BorderLayout.NORTH);


        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(BG_DARK_MAIN);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));


        JLabel ratingLabel = new JLabel("Rating (1-5 Stars):");
        ratingLabel.setForeground(TEXT_LIGHT_PRIMARY);
        JComboBox<Integer> ratingBox = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        ratingPanel.setOpaque(false);
        ratingPanel.add(ratingLabel);
        ratingPanel.add(ratingBox);
        formPanel.add(ratingPanel);


        JLabel feedbackLabel = new JLabel("Comments:");
        feedbackLabel.setForeground(TEXT_LIGHT_PRIMARY);
        JTextArea feedbackArea = new JTextArea(5, 20);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_PURPLE));

        formPanel.add(feedbackLabel);
        formPanel.add(scrollPane);


        JButton submitButton = new JButton("Submit Feedback & Finish");
        submitButton.setBackground(ACCENT_PURPLE);
        submitButton.setForeground(TEXT_LIGHT_PRIMARY);
        submitButton.setFocusPainted(false);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        submitButton.addActionListener(e -> {
            int rating = (int) ratingBox.getSelectedItem();
            String comment = feedbackArea.getText().trim();


            saveFeedback(rating, comment);


            JOptionPane.showMessageDialog(this, "Thank you for your feedback! Session is archived.", "Complete", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BG_DARK_MAIN);
        buttonPanel.add(submitButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void saveFeedback(int rating, String comment) {

        String sql = "UPDATE mentor_requests SET status = 'Completed', mentee_rating = ?, mentee_feedback = ?, session_completed_at = NOW() WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, requestId);

            ps.executeUpdate();

        } catch (SQLException ex) {
            System.err.println("Error saving feedback: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database Error: Could not save feedback.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


