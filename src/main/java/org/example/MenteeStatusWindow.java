package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;

public class MenteeStatusWindow extends JDialog {

    private final Dashboard parentDashboard;
    private final String menteeName;
    private final JPanel contentPanel;

    private static final Color BG_DARK_CARD = new Color(34, 40, 49);
    private static final Color TEXT_LIGHT_PRIMARY = Color.WHITE;
    private static final Color TEXT_LIGHT_SECONDARY = new Color(170, 170, 170);
    private static final Color STATUS_PENDING = new Color(255, 193, 7); // Yellow
    private static final Color STATUS_ACCEPTED = new Color(40, 167, 69); // Green
    private static final Color STATUS_REJECTED = new Color(220, 53, 69); // Red

    public MenteeStatusWindow(Dashboard parent, String menteeName) {
        super(parent, "My Request Statuses", true);
        this.parentDashboard = parent;
        this.menteeName = menteeName;

        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(25, 29, 36));

        JLabel title = new JLabel("Your Mentoring Requests", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_LIGHT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(25, 29, 36));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(new Color(25, 29, 36));
        add(scrollPane, BorderLayout.CENTER);

        loadMenteeRequests();

        // Refresh the parent dashboard's notification count when closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                parentDashboard.updateNotificationCount();
            }
        });
    }

    private void loadMenteeRequests() {
        contentPanel.removeAll();
        String sql = "SELECT mentor_name, status, session_start, session_end, mentee_is_read " +
                "FROM mentor_requests WHERE mentee_name = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, menteeName);
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasUpdates = false;
                while (rs.next()) {
                    String mentor = rs.getString("mentor_name");
                    String status = rs.getString("status");
                    boolean isRead = rs.getBoolean("mentee_is_read");
                    String start = rs.getString("session_start");
                    String end = rs.getString("session_end");

                    if (!isRead && !status.equals("Pending")) {
                        hasUpdates = true;
                    }

                    JPanel card = createRequestCard(mentor, status, start, end, isRead);
                    contentPanel.add(card);
                    contentPanel.add(Box.createVerticalStrut(15));
                }

                if (contentPanel.getComponentCount() == 0) {
                    JLabel msg = new JLabel("You have no mentorship requests yet.");
                    msg.setForeground(TEXT_LIGHT_SECONDARY);
                    msg.setFont(new Font("Segoe UI", Font.ITALIC, 16));
                    contentPanel.add(msg);
                }

                // If there were unread updates, mark them as read after loading
                if (hasUpdates) {
                    markRequestsAsRead();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading mentee requests: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load request data.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createRequestCard(String mentor, String status, String start, String end, boolean isRead) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isRead ? BG_DARK_CARD.brighter() : new Color(255, 100, 0), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(BG_DARK_CARD);
        card.setMaximumSize(new Dimension(650, 100)); // Limit height and width

        // Status Label
        JLabel statusLabel = new JLabel(status.toUpperCase());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setPreferredSize(new Dimension(100, 30));

        switch (status) {
            case "Pending":
                statusLabel.setBackground(STATUS_PENDING);
                statusLabel.setForeground(Color.BLACK);
                break;
            case "Accepted":
                statusLabel.setBackground(STATUS_ACCEPTED);
                statusLabel.setForeground(TEXT_LIGHT_PRIMARY);
                break;
            case "Rejected":
                statusLabel.setBackground(STATUS_REJECTED);
                statusLabel.setForeground(TEXT_LIGHT_PRIMARY);
                break;
        }

        // Content Panel
        JPanel content = new JPanel(new GridLayout(2, 1));
        content.setOpaque(false);

        JLabel mentorLabel = new JLabel("Mentor: " + mentor);
        mentorLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mentorLabel.setForeground(TEXT_LIGHT_PRIMARY);

        String timeInfo = "Time: N/A";
        if (status.equals("Accepted") && start != null && end != null) {
            timeInfo = "Scheduled: " + start + " - " + end;
        } else if (status.equals("Rejected")) {
            timeInfo = "Decision Finalized";
        }

        JLabel scheduleLabel = new JLabel(timeInfo);
        scheduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleLabel.setForeground(TEXT_LIGHT_SECONDARY);

        content.add(mentorLabel);
        content.add(scheduleLabel);

        card.add(statusLabel, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private void markRequestsAsRead() {

        String sql = "UPDATE mentor_requests SET mentee_is_read = TRUE WHERE mentee_name = ? AND status <> 'Pending' AND mentee_is_read = FALSE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, menteeName);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error marking mentee requests as read: " + e.getMessage());
        }
    }
}