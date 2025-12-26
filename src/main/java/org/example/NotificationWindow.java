package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationWindow extends JDialog {

    private final Dashboard parentDashboard;
    private final String mentorName;
    private final JPanel contentPanel;


    public static final Color TEXT_LIGHT_SECONDARY = new Color(170, 170, 170);
    private static final Color BG_DARK_MAIN = new Color(25, 29, 36);
    private static final Color BG_DARK_CARD = new Color(34, 40, 49);
    private static final Color TEXT_LIGHT_PRIMARY = Color.WHITE;
    private static final Color STATUS_PENDING = new Color(255, 193, 7);
    private static final Color STATUS_REJECTED = new Color(220, 53, 69);

    public NotificationWindow(Dashboard parent, String mentorName, String userRole) {
        super(parent, "Notifications & Requests", true);
        this.parentDashboard = parent;
        this.mentorName = mentorName;

        setSize(700, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_DARK_MAIN);

        JLabel title = new JLabel("Incoming Mentee Requests:", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_LIGHT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG_DARK_MAIN);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getViewport().setBackground(BG_DARK_MAIN);
        add(scrollPane, BorderLayout.CENTER);

        loadPendingRequests();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {

                parentDashboard.updateNotificationCount();
            }
        });
    }
    private void loadPendingRequests() {
        contentPanel.removeAll();
        String sql = "SELECT id, mentee_name FROM mentor_requests WHERE mentor_name = ? AND status = 'Pending' ORDER BY request_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mentorName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    displayEmptyMessage();
                    return;
                }

                List<Integer> idsToMarkRead = new ArrayList<>();

                while (rs.next()) {
                    int requestId = rs.getInt("id");
                    String mentee = rs.getString("mentee_name");

                    JPanel card = createRequestCard(requestId, mentee);
                    contentPanel.add(card);
                    contentPanel.add(Box.createVerticalStrut(15));

                    idsToMarkRead.add(requestId);
                }

                if (!idsToMarkRead.isEmpty()) {
                    markRequestsAsRead(idsToMarkRead);
                }
            }
        } catch (SQLException e) {

            System.err.println("Error loading mentor requests: " + e.getMessage());
            displayEmptyMessage();
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createRequestCard(int requestId, String menteeName) {

        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(STATUS_PENDING, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(BG_DARK_CARD);
        card.setMaximumSize(new Dimension(650, 80));

        JLabel menteeLabel = new JLabel("New Request from: " + menteeName);
        menteeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        menteeLabel.setForeground(TEXT_LIGHT_PRIMARY);

        JLabel pendingLabel = new JLabel("PENDING");
        pendingLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pendingLabel.setForeground(Color.BLACK);
        pendingLabel.setBackground(STATUS_PENDING);
        pendingLabel.setOpaque(true);
        pendingLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel content = new JPanel(new BorderLayout(10, 0));
        content.setOpaque(false);
        content.add(menteeLabel, BorderLayout.WEST);
        content.add(pendingLabel, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton acceptButton = new JButton("Accept");
        acceptButton.setBackground(new Color(40, 167, 69)); // Green
        acceptButton.setForeground(Color.WHITE);

        JButton rejectButton = new JButton("Reject");
        rejectButton.setBackground(STATUS_REJECTED); // Red
        rejectButton.setForeground(Color.WHITE);

        acceptButton.addActionListener(e -> promptForSchedule(requestId, menteeName));
        rejectButton.addActionListener(e -> handleRequestDecision(requestId, "Rejected", null, null));

        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);

        card.add(content, BorderLayout.WEST);
        card.add(buttonPanel, BorderLayout.EAST);

        return card;
    }

    private void displayEmptyMessage() {
        JLabel msg = new JLabel("You have no new or pending mentee requests.");
        msg.setForeground(TEXT_LIGHT_SECONDARY);
        msg.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.add(msg);
    }

    private void promptForSchedule(int requestId, String menteeName) {
        JTextField startField = new JTextField(5);
        JTextField endField = new JTextField(5);

        JLabel hint = new JLabel("Format: HH:MM ");
        hint.setForeground(new Color(255, 193, 7)); // Yellow hint

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(hint);

        panel.add(new JLabel("Start Time:"));
        panel.add(startField);
        panel.add(new JLabel("End Time:"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Schedule Session with " + menteeName, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String start = startField.getText().trim();
            String end = endField.getText().trim();

            if (start.isEmpty() || end.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Session times cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                promptForSchedule(requestId, menteeName);
            } else {
                handleRequestDecision(requestId, "Accepted", start, end);
            }
        }
    }

    private String getMenteeNameForRequestId(int requestId) {
        String menteeName = null;
        String sql = "SELECT mentee_name FROM mentor_requests WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    menteeName = rs.getString("mentee_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching mentee name for request ID " + requestId + ": " + e.getMessage());
        }
        return menteeName;
    }


    private void handleRequestDecision(int requestId, String status, String sessionStart, String sessionEnd) {
        String sql;

        if ("Accepted".equals(status)) {

            sql = "UPDATE mentor_requests SET status = ?, session_start = ?, session_end = ?, is_read = TRUE, mentee_is_read = FALSE WHERE id = ?";
        } else {

            sql = "UPDATE mentor_requests SET status = ?, session_start = NULL, session_end = NULL, is_read = TRUE, mentee_is_read = FALSE WHERE id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String menteeName = getMenteeNameForRequestId(requestId);
            ps.setString(1, status);

            if ("Accepted".equals(status)) {
                ps.setString(2, sessionStart);
                ps.setString(3, sessionEnd);
                ps.setInt(4, requestId);
            } else {

                ps.setNull(2, java.sql.Types.VARCHAR);
                ps.setNull(3, java.sql.Types.VARCHAR);
                ps.setInt(4, requestId);
            }

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                String message = status.equals("Accepted")
                        ? "Request Accepted! Session scheduled from " + sessionStart + " to " + sessionEnd + ". Mentee notified."
                        : "Request Rejected. Mentee notified.";

                JOptionPane.showMessageDialog(this, message, "Decision Sent", JOptionPane.INFORMATION_MESSAGE);
                loadPendingRequests();


                if ("Accepted".equals(status) && menteeName != null) {

                    parentDashboard.startSessionMonitoring(menteeName);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: Could not update request status.", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public void startSessionMonitoring(String partnerName) {
        System.out.println(" Session monitoring started for partner: " + partnerName);

    }
    private void markRequestsAsRead(List<Integer> requestIds) {
        String idList = requestIds.toString().replace("[", "").replace("]", "");
        String sql = "UPDATE mentor_requests SET is_read = TRUE WHERE id IN (" + idList + ")";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.executeUpdate();
            parentDashboard.updateNotificationCount();

        } catch (SQLException e) {
            System.err.println("Error marking mentor request as read: " + e.getMessage());
        }
    }
}
