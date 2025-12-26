package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.net.Socket;
public class Dashboard extends JFrame {
    public String loggedInUserName;
    public String loggedInUserRole;
    public JFrame loginFrame;
    public JPanel contentPanel;
    public JTextField searchField;
    public JButton notificationIcon;
    public JLabel notificationCountLabel;
    public void startSessionMonitoring(String menteeName) {
        System.out.println(" Session monitoring started for partner: " + menteeName);
    }

    public static class ThemeManager {
        public static final Color BG_DARK_MAIN = new Color(25, 29, 36);
        public static final Color BG_DARK_CARD = new Color(34, 40, 49);
        public static final Color BG_TOPBAR = new Color(34, 40, 49);
        public static final Color TEXT_LIGHT_PRIMARY = Color.WHITE;
        public static final Color TEXT_LIGHT_SECONDARY = new Color(170, 170, 170);
        public static final Color TEXT_AVAILABLE = new Color(0, 200, 100);
        public static final Color TEXT_BUSY = new Color(255, 70, 70);
        public static final Color ACCENT_PURPLE = new Color(130, 0, 255);
        public static final Color ACCENT_HOVER = new Color(170, 80, 255);
        public static final Color LOGOUT_RED = new Color(220, 50, 50);
        public static final Color LOGOUT_HOVER = new Color(255, 80, 80);
        public static final Color CARD_HOVER = new Color(45, 52, 64);
        public static final Color NOTIFICATION_ICON_COLOR = new Color(255, 100, 0);

        public static Color getIconBGColor(String domain) {
            return switch (domain) {
                case "Cybersecurity" -> new Color(0, 150, 200);
                case "Cloud Computing" -> new Color(255, 100, 0);
                case "Database Management" -> new Color(150, 0, 255);
                case "DevOps Engineering" -> new Color(50, 200, 50);
                case "Machine Learning" -> new Color(255, 50, 100);
                case "AI" -> new Color(255, 200, 0);
                case "Data Analysis" -> new Color(0, 100, 255);
                case "Frontend Development" -> new Color(200, 0, 255);
                case "Backend Development" -> new Color(0, 200, 200);
                default -> new Color(100, 150, 255);
            };
        }
    }
    public Dashboard(String userName, String userRole, JFrame loginFrame) {
        this.loggedInUserName = userName;
        this.loggedInUserRole = userRole;
        this.loginFrame = loginFrame;
        setTitle("Mentor Connect Dashboard");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(ThemeManager.BG_DARK_MAIN);
        add(mainContainer);
        JPanel topbar = createTopBar();
        mainContainer.add(topbar, BorderLayout.NORTH);
        contentPanel = new JPanel();
        contentPanel.setBackground(ThemeManager.BG_DARK_MAIN);
        contentPanel.setLayout(new GridLayout(0, 3, 30, 30));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(ThemeManager.BG_DARK_MAIN);
        scrollPane.getViewport().setBackground(ThemeManager.BG_DARK_MAIN);
        scrollPane.setBorder(null);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainContainer.add(scrollPane, BorderLayout.CENTER);

        loadMentors(null);

        if ("Mentor".equalsIgnoreCase(loggedInUserRole)) {
            updateNotificationCount();
        }
    }
    public JPanel createTopBar() {
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(ThemeManager.BG_TOPBAR);
        topbar.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        JLabel title = new JLabel("Mentor Connect");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);

        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(searchField.getPreferredSize().width, 30));
        searchField.setBackground(new Color(50, 50, 70));
        searchField.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
        searchField.setCaretColor(ThemeManager.TEXT_LIGHT_PRIMARY);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JButton searchButton = new JButton("Search");
        styleSearchButton(searchButton);
        searchField.addActionListener(e -> loadMentors(searchField.getText().trim()));
        searchButton.addActionListener(e -> loadMentors(searchField.getText().trim()));
        rightPanel.add(searchField);
        rightPanel.add(searchButton);

        if ("Mentor".equalsIgnoreCase(loggedInUserRole)) {
            notificationIcon = new JButton("🔔");
            notificationIcon.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
            notificationIcon.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
            notificationIcon.setBackground(ThemeManager.BG_TOPBAR);
            notificationIcon.setFocusPainted(false);
            notificationIcon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            notificationIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            notificationCountLabel = new JLabel("0");
            notificationCountLabel.setForeground(Color.WHITE);
            notificationCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            JPanel notificationPanel = new JPanel(new BorderLayout());
            notificationPanel.setOpaque(false);
            notificationPanel.add(notificationIcon, BorderLayout.CENTER);
            notificationCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 8));
            notificationCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
            notificationCountLabel.setVerticalAlignment(SwingConstants.TOP);
            notificationCountLabel.setOpaque(true);
            notificationCountLabel.setBackground(ThemeManager.NOTIFICATION_ICON_COLOR);
            notificationCountLabel.setPreferredSize(new Dimension(20, 20));
            JPanel countWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            countWrapper.setOpaque(false);
            countWrapper.add(notificationCountLabel);

            countWrapper.setVisible(false);

            notificationPanel.setLayout(new OverlayLayout(notificationPanel));
            notificationPanel.add(countWrapper);
            notificationPanel.add(notificationIcon);
            notificationIcon.addActionListener(e -> showNotificationWindow());
            rightPanel.add(notificationPanel);

            JButton activeSessionsIcon = new JButton("🗓️");
            activeSessionsIcon.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
            activeSessionsIcon.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
            activeSessionsIcon.setBackground(ThemeManager.BG_TOPBAR);
            activeSessionsIcon.setFocusPainted(false);
            activeSessionsIcon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            activeSessionsIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            activeSessionsIcon.addActionListener(e -> showMentorActiveSessionsWindow());
            rightPanel.add(activeSessionsIcon);
        }
        else if ("Mentee".equalsIgnoreCase(loggedInUserRole)) {
            notificationIcon = new JButton("✉️");
            notificationIcon.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
            notificationIcon.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
            notificationIcon.setBackground(ThemeManager.BG_TOPBAR);
            notificationIcon.setFocusPainted(false);
            notificationIcon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            notificationIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            notificationCountLabel = new JLabel("0");
            notificationCountLabel.setForeground(Color.WHITE);
            notificationCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            JPanel notificationPanel = new JPanel(new BorderLayout());
            notificationPanel.setOpaque(false);
            notificationPanel.add(notificationIcon, BorderLayout.CENTER);

            notificationCountLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 8));
            notificationCountLabel.setHorizontalAlignment(SwingConstants.CENTER);
            notificationCountLabel.setVerticalAlignment(SwingConstants.TOP);
            notificationCountLabel.setOpaque(true);
            notificationCountLabel.setBackground(ThemeManager.NOTIFICATION_ICON_COLOR);
            notificationCountLabel.setPreferredSize(new Dimension(20, 20));
            JPanel countWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            countWrapper.setOpaque(false);
            countWrapper.add(notificationCountLabel);
            countWrapper.setVisible(false);
            notificationPanel.setLayout(new OverlayLayout(notificationPanel));
            notificationPanel.add(countWrapper);
            notificationPanel.add(notificationIcon);
            notificationIcon.addActionListener(e -> showMenteeStatusWindow());
            rightPanel.add(notificationPanel);
            updateNotificationCount();
        }

        JLabel userGreeting = new JLabel("Hi, " + loggedInUserName);
        userGreeting.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userGreeting.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
        rightPanel.add(userGreeting);
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        userIcon.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
        rightPanel.add(userIcon);
        JButton logoutBtn = new JButton("Logout");
        styleLogoutButton(logoutBtn);
        logoutBtn.addActionListener(e -> logoutAction());
        rightPanel.add(logoutBtn);
        topbar.add(title, BorderLayout.WEST);
        topbar.add(rightPanel, BorderLayout.EAST);
        return topbar;
    }
    public void showMenteeStatusWindow() {
        MenteeStatusWindow statusDialog = new MenteeStatusWindow(this, loggedInUserName);
        statusDialog.setVisible(true);
        updateNotificationCount();
    }
    public int getMenteeRequestCount() {
        if (!"Mentee".equalsIgnoreCase(loggedInUserRole)) return 0;
        String sql = "SELECT COUNT(*) FROM mentor_requests WHERE mentee_name = ? AND status = 'Pending'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loggedInUserName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching mentee request count: " + e.getMessage());
        }
        return 0;
    }
    public void updateNotificationCount() {
        if (notificationCountLabel != null) {
            int count = 0;
            if ("Mentor".equalsIgnoreCase(loggedInUserRole)) {
                count = getUnreadNotificationCount();
            } else if ("Mentee".equalsIgnoreCase(loggedInUserRole)) {
                count = getMenteeRequestCount();
            }
            notificationCountLabel.setText(String.valueOf(count));
            notificationCountLabel.getParent().setVisible(count > 0);
            notificationCountLabel.getParent().revalidate();
            notificationCountLabel.getParent().repaint();
        }
    }

    public class MenteeStatusWindow extends JDialog {
        private final String menteeName;
        private final Dashboard dashboard;
        public MenteeStatusWindow(Dashboard parent, String menteeName) {
            super(parent, "My Request Status", true);
            this.dashboard = parent;
            this.menteeName = menteeName;
            setSize(600, 450);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            getContentPane().setBackground(ThemeManager.BG_DARK_MAIN);

            JLabel titleLabel = new JLabel("Status of Requests Sent by " + menteeName, SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
            add(titleLabel, BorderLayout.NORTH);

            JPanel statusPanel = new JPanel();
            statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
            statusPanel.setBackground(ThemeManager.BG_DARK_MAIN);
            JScrollPane scrollPane = new JScrollPane(statusPanel);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(ThemeManager.BG_DARK_MAIN);
            add(scrollPane, BorderLayout.CENTER);
            loadMenteeRequests(statusPanel);

            JButton closeButton = new JButton("Close");
            styleLogoutButton(closeButton);
            closeButton.addActionListener(e -> dispose());
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            bottomPanel.setBackground(ThemeManager.BG_DARK_MAIN);
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            bottomPanel.add(closeButton);
            add(bottomPanel, BorderLayout.SOUTH);
        }
        public void loadMenteeRequests(JPanel panel) {
            String sql = "SELECT mentor_name, status, request_date, id FROM mentor_requests WHERE mentee_name = ? ORDER BY request_date DESC";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, menteeName);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        String mentor = rs.getString("mentor_name");
                        String status = rs.getString("status");
                        String date = rs.getTimestamp("request_date").toString().split("\\.")[0];
                        int requestId = rs.getInt("id");
                        panel.add(createRequestStatusCard(mentor, status, date, requestId));
                        panel.add(Box.createVerticalStrut(10));
                    }
                    if (!found) {
                        JLabel noRequests = new JLabel("You have not sent any mentor requests yet.", SwingConstants.CENTER);
                        noRequests.setForeground(ThemeManager.TEXT_LIGHT_SECONDARY);
                        noRequests.setAlignmentX(Component.CENTER_ALIGNMENT);
                        panel.add(Box.createVerticalGlue());
                        panel.add(noRequests);
                        panel.add(Box.createVerticalGlue());
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error loading mentee requests: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Failed to load request status.", "DB Error", JOptionPane.ERROR_MESSAGE);
            }
            panel.revalidate();
            panel.repaint();
        }
        public JPanel createRequestStatusCard(String mentorName, String status, String date, int requestId) {
            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setBackground(ThemeManager.BG_DARK_CARD);
            card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            card.setMaximumSize(new Dimension(550, 80));

            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setOpaque(false);
            JLabel mentorLabel = new JLabel("Mentor: " + mentorName);
            mentorLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            mentorLabel.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
            JLabel dateLabel = new JLabel("Sent On: " + date);
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dateLabel.setForeground(ThemeManager.TEXT_LIGHT_SECONDARY);
            infoPanel.add(mentorLabel);
            infoPanel.add(dateLabel);

            JLabel statusLabel = new JLabel(status, SwingConstants.CENTER);
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            Color statusColor = ThemeManager.TEXT_LIGHT_SECONDARY;
            if ("Pending".equalsIgnoreCase(status)) {
                statusColor = ThemeManager.NOTIFICATION_ICON_COLOR;
            } else if ("Accepted".equalsIgnoreCase(status)) {
                statusColor = ThemeManager.TEXT_AVAILABLE;
                JButton chatButton = new JButton("Start Chat");
                styleSearchButton(chatButton);
                chatButton.addActionListener(e -> {
                    ChatWindow chatWindow = new ChatWindow(dashboard, menteeName, mentorName, requestId);
                    chatWindow.setVisible(true);
                    dispose();
                });
                card.add(chatButton, BorderLayout.EAST);
            } else if ("Rejected".equalsIgnoreCase(status)) {
                statusColor = ThemeManager.TEXT_BUSY;
            } else if ("Completed".equalsIgnoreCase(status)) {
                statusColor = ThemeManager.TEXT_AVAILABLE;
                JButton feedbackButton = new JButton("Give Feedback");
                styleSearchButton(feedbackButton);
                feedbackButton.addActionListener(e -> {
                    FeedbackWindow feedbackWindow = new FeedbackWindow(dashboard, menteeName, mentorName, requestId);
                    feedbackWindow.setVisible(true);
                });
                card.add(feedbackButton, BorderLayout.EAST);
            }
            statusLabel.setForeground(statusColor);
            card.add(infoPanel, BorderLayout.WEST);
            card.add(statusLabel, BorderLayout.CENTER);
            return card;
        }
    }

    public void styleSearchButton(JButton btn) {
        btn.setBackground(ThemeManager.ACCENT_PURPLE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ThemeManager.ACCENT_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(ThemeManager.ACCENT_PURPLE);
            }
        });
    }

    public void styleLogoutButton(JButton btn) {
        btn.setBackground(ThemeManager.LOGOUT_RED);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ThemeManager.LOGOUT_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(ThemeManager.LOGOUT_RED);
            }
        });
    }

    public void logoutAction() {
        int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            this.dispose();
            if (loginFrame != null) {
                loginFrame.setVisible(true);
            } else {
                new Login().setVisible(true);
            }
        }
    }

    public int getUnreadNotificationCount() {
        if (!"Mentor".equalsIgnoreCase(loggedInUserRole)) return 0;
        String sql = "SELECT COUNT(*) FROM mentor_requests WHERE mentor_name = ? AND is_read = FALSE AND status = 'Pending'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loggedInUserName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching notification count: " + e.getMessage());
        }
        return 0;
    }

    public void showNotificationWindow() {
        NotificationWindow notificationDialog = new NotificationWindow(this, loggedInUserName, loggedInUserRole);
        notificationDialog.setVisible(true);
        updateNotificationCount();
    }

    public void showMentorActiveSessionsWindow() {
        MentorActiveSessionsWindow sessionsDialog = new MentorActiveSessionsWindow(this, loggedInUserName);
        sessionsDialog.setVisible(true);
    }

    public JPanel createMentorCard(String name, String domain, String description, String status, String schedule) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 250));
        card.setBackground(ThemeManager.BG_DARK_CARD);
        card.setLayout(new BorderLayout(20, 15));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel coreContent = new JPanel(new BorderLayout(0, 15));
        coreContent.setOpaque(false);
        // --- Top Section: Icon and Name/Domain ---
        JPanel topSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topSection.setOpaque(false);
        Color iconBG = ThemeManager.getIconBGColor(domain);
        JLabel domainIcon = new JLabel("💻", SwingConstants.CENTER);
        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(iconBG);
                g2d.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        iconCircle.setLayout(new GridBagLayout());
        iconCircle.setPreferredSize(new Dimension(55, 55));
        iconCircle.setOpaque(false);
        domainIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        domainIcon.setForeground(Color.WHITE);
        iconCircle.add(domainIcon);
        switch (domain) {
            case "Cybersecurity": domainIcon.setText("🔒"); break;
            case "Cloud Computing": domainIcon.setText("☁️"); break;
            case "Database Management": domainIcon.setText("💾"); break;
            case "DevOps Engineering": domainIcon.setText("⚙️"); break;
            case "Machine Learning": domainIcon.setText("🧠"); break;
            case "AI": domainIcon.setText("🤖"); break;
            case "Data Analysis": domainIcon.setText("📈"); break;
            case "Frontend Development": domainIcon.setText("🌐"); break;
            case "Backend Development": domainIcon.setText("🛠️"); break;
            default: domainIcon.setText("🧑‍💻"); break;
        }
        JPanel nameDomainPanel = new JPanel(new GridLayout(2, 1));
        nameDomainPanel.setOpaque(false);
        JLabel mentorNameLabel = new JLabel(name);
        mentorNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mentorNameLabel.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
        JLabel domainLabel = new JLabel(domain);
        domainLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        domainLabel.setForeground(ThemeManager.TEXT_LIGHT_SECONDARY);
        nameDomainPanel.add(mentorNameLabel);
        nameDomainPanel.add(domainLabel);
        topSection.add(iconCircle);
        topSection.add(nameDomainPanel);

        JLabel descriptionLabel = new JLabel("<html><p style='width: 350px;'>" + description + "</p></html>");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionLabel.setForeground(ThemeManager.TEXT_LIGHT_SECONDARY);
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
        descriptionLabel.setPreferredSize(new Dimension(350, 80));
        coreContent.add(topSection, BorderLayout.NORTH);
        coreContent.add(descriptionLabel, BorderLayout.CENTER);

        JPanel bottomWrapper = new JPanel(new BorderLayout(10, 0));
        bottomWrapper.setOpaque(false);
        JPanel statusSchedulePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        statusSchedulePanel.setOpaque(false);
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setForeground("Available".equalsIgnoreCase(status) ? ThemeManager.TEXT_AVAILABLE : ThemeManager.TEXT_BUSY);
        JLabel scheduleLabel = new JLabel("Time: " + schedule);
        scheduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        scheduleLabel.setForeground(ThemeManager.TEXT_LIGHT_SECONDARY);
        statusSchedulePanel.add(statusLabel);
        statusSchedulePanel.add(scheduleLabel);
        bottomWrapper.add(statusSchedulePanel, BorderLayout.WEST);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonPanel.setOpaque(false);
        if ("Mentee".equalsIgnoreCase(loggedInUserRole)) {
            JButton requestButton = new JButton("Send Request 🚀");
            styleRequestButton(requestButton);
            requestButton.addActionListener(e -> {
                sendRequest(mentorNameLabel.getText());
            });
            actionButtonPanel.add(requestButton);
        } else {
            JButton placeholderButton = new JButton("View Profile");
            placeholderButton.setBackground(ThemeManager.ACCENT_PURPLE.darker());
            placeholderButton.setForeground(Color.WHITE);
            placeholderButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            actionButtonPanel.add(placeholderButton);
        }
        bottomWrapper.add(actionButtonPanel, BorderLayout.EAST);

        MouseAdapter detailClickAdapter = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(ThemeManager.CARD_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(ThemeManager.BG_DARK_CARD);
            }
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == card) {
                    JOptionPane.showMessageDialog(card,
                            "Mentor: " + name +
                                    "\nDomain: " + domain +
                                    "\nDescription: " + description +
                                    "\nStatus: " + status +
                                    "\nSchedule: " + schedule,
                            "Mentor Details",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        };
        card.addMouseListener(detailClickAdapter);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.add(coreContent, BorderLayout.CENTER);
        card.add(bottomWrapper, BorderLayout.SOUTH);
        return card;
    }

    public void styleRequestButton(JButton btn) {
        btn.setBackground(ThemeManager.ACCENT_PURPLE);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ThemeManager.ACCENT_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(ThemeManager.ACCENT_PURPLE);
            }
        });
    }

    public void sendRequest(String mentorName) {
        String insertSql = "INSERT INTO mentor_requests (mentee_name, mentor_name, status, is_read) VALUES (?, ?, 'Pending', FALSE)";
        String checkSql = "SELECT COUNT(*) FROM mentor_requests WHERE mentee_name = ? AND mentor_name = ? AND status = 'Pending'";
        boolean pendingExists = false;
        try (Connection conn = DatabaseConnection.getConnection()) {
            // 1. Check for existing pending request
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, loggedInUserName);
                checkPs.setString(2, mentorName);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        pendingExists = true;
                    }
                }
            }
            if (pendingExists) {
                JOptionPane.showMessageDialog(this, "You already have a pending request with " + mentorName + ".", "Request Already Sent", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, loggedInUserName);
                ps.setString(2, mentorName);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Request successfully sent to " + mentorName + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Request Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadMentors(String query) {
        contentPanel.removeAll();
        List<MentorData> mentors = getMentorsFromDBWithFallback();
        if (query != null && !query.isEmpty()) {
            String q = query.toLowerCase();
            String[] tokens = q.split("\\s+");

            mentors.removeIf(m -> {
                String name = m.name.toLowerCase();
                String domain = m.domain.toLowerCase();
                String description = m.description.toLowerCase();
                String status = m.status.toLowerCase();
                boolean matchFound = false;
                for (String t : tokens) {
                    if (name.contains(t) || domain.contains(t) || description.contains(t) || status.contains(t)) {
                        matchFound = true;
                        break;
                    }
                }
                return !matchFound;
            });
        }
        if (mentors.isEmpty()) {
            JLabel msg = new JLabel("No mentors found matching your criteria.");
            msg.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            msg.setForeground(ThemeManager.TEXT_LIGHT_SECONDARY);
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.add(msg);
        } else {

            contentPanel.setLayout(new GridLayout(0, 3, 30, 30));
            for (MentorData m : mentors)
                contentPanel.add(createMentorCard(m.name, m.domain, m.description, m.status, m.schedule));
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static class MentorData {
        String name;
        String domain;
        String description;
        String status;
        String schedule;
        public MentorData(String name, String domain, String description, String status, String schedule) {
            this.name = name;
            this.domain = domain;
            this.description = description;
            this.status = status;
            this.schedule = schedule;
        }
    }

    public List<MentorData> getMentorsFromDBWithFallback() {
        List<MentorData> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT name, expertise, description, status, schedule FROM register WHERE role='Mentor'");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String expertise = rs.getString("expertise");
                if (expertise == null || expertise.isEmpty()) expertise = "General";

                String domain = expertise.split(",")[0].trim();
                list.add(new MentorData(
                        rs.getString("name"),
                        domain,
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getString("schedule")
                ));
            }
        } catch (Exception e) {
            System.err.println("DB Connection failed. Loading DUMMY data. Error: " + e.getMessage());

            return getDummyMentors();
        }

        return list;
    }

    public List<MentorData> getDummyMentors() {
        List<MentorData> list = new ArrayList<>();
        list.add(new MentorData("Kavitha", "Cybersecurity", "Specializes in network security and ethical hacking and defense systems.", "Available", "9:00-12:40 AM"));
        list.add(new MentorData("Karthik", "Cloud Computing", "Expert in AWS, Azure, and Google Cloud infrastructure and migration strategies.", "Busy", "1:30-2:15 PM"));
        list.add(new MentorData("Lakshmi", "Database Management", "Proficient in SQL, NoSQL, and database design/optimization techniques.", "Available", "8:45-9:50 PM"));
        list.add(new MentorData("Rajesh", "DevOps Engineering", "Skilled in CI/CD, automation, and infrastructure as code using Terraform.", "Available", "9:00-12:40 AM"));
        list.add(new MentorData("Deepa", "Machine Learning", "Experienced in supervised/unsupervised learning and deep learning models.", "Busy", "1:30-2:15 PM"));
        list.add(new MentorData("Ramkumar", "AI", "Focuses on natural language processing, LLMs, and computer vision applications.", "Available", "8:45-9:50 PM"));
        list.add(new MentorData("Suresh", "Data Analysis", "Strong in statistical analysis, data visualization, and Python/R programming.", "Available", "9:00-12:40 AM"));
        list.add(new MentorData("Priya", "Frontend Development", "Specializes in React, Angular, and modern web UI/UX design and development.", "Busy", "1:30-2:15 PM"));
        list.add(new MentorData("Anand", "Backend Development", "Proficient in Java Spring Boot, Node.js, and designing scalable RESTful APIs.", "Available", "8:45-9:50 PM"));
        return list;
    }


    public class ChatWindow extends JFrame {
        private final String sessionPartnerName;
        private final String loggedInUserName;
        private final Dashboard parentDashboard;
        private final int requestId;
        private SocketIOUtils socketIO;
        private JTextArea chatArea;
        private JTextField messageField;

        public ChatWindow(Dashboard parentDashboard, String loggedInUserName, String sessionPartnerName, int requestId) {
            this.parentDashboard = parentDashboard;
            this.loggedInUserName = loggedInUserName;
            this.sessionPartnerName = sessionPartnerName;
            this.requestId = requestId;

            setTitle("Live Session: " + loggedInUserName + " and " + sessionPartnerName);
            setSize(500, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    int choice = JOptionPane.showConfirmDialog(ChatWindow.this,
                            "Are you sure you want to end this session?",
                            "End Session",
                            JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        if ("Mentee".equalsIgnoreCase(loggedInUserRole)) {
                            // Show feedback form when mentee closes the chat
                            FeedbackWindow feedbackWindow = new FeedbackWindow(parentDashboard, loggedInUserName, sessionPartnerName, requestId);
                            feedbackWindow.setVisible(true);
                        }
                        closeChat();
                    }
                }
            });


            setupUI();


            connectToServer();


            loadPreviousMessages();
        }

        public void setupUI() {
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(34, 40, 49));


            chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setBackground(new Color(25, 29, 36));
            chatArea.setForeground(Color.WHITE);
            chatArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            JScrollPane chatScrollPane = new JScrollPane(chatArea);
            mainPanel.add(chatScrollPane, BorderLayout.CENTER);


            JPanel inputPanel = new JPanel(new BorderLayout());
            inputPanel.setBackground(new Color(34, 40, 49));

            messageField = new JTextField();
            messageField.setFont(new Font("Monospaced", Font.PLAIN, 14));
            messageField.setBackground(new Color(50, 50, 70));
            messageField.setForeground(Color.WHITE);

            JButton sendButton = new JButton("Send");
            sendButton.setBackground(new Color(130, 0, 255));
            sendButton.setForeground(Color.WHITE);
            sendButton.setFocusPainted(false);

            inputPanel.add(messageField, BorderLayout.CENTER);
            inputPanel.add(sendButton, BorderLayout.EAST);
            mainPanel.add(inputPanel, BorderLayout.SOUTH);


            add(mainPanel);


            sendButton.addActionListener(e -> sendMessage());

            messageField.addActionListener(e -> sendMessage());


            chatArea.append("--- Session with " + sessionPartnerName + " is OPEN ---\n");
        }

        public void connectToServer() {
            try {
                socketIO = new SocketIOUtils("localhost", 8080);
                socketIO.sendMessage(loggedInUserName);
                new Thread(this::listenForMessages).start();
            } catch (IOException e) {
                chatArea.append("Failed to connect: " + e.getMessage() + "\n");
            }
        }

        public void listenForMessages() {
            try {
                String message;
                while (socketIO.isConnected() && (message = socketIO.receiveMessage()) != null) {
                    final String msg = message;
                    SwingUtilities.invokeLater(() -> {
                        chatArea.append(msg + "\n");
                        saveMessageToDatabase(sessionPartnerName, msg);
                    });
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                        chatArea.append("Disconnected: " + e.getMessage() + "\n")
                );
            }
        }

        public void sendMessage() {
            String message = messageField.getText();
            if (!message.trim().isEmpty()) {
                socketIO.sendMessage(sessionPartnerName + ":" + message);
                saveMessageToDatabase(loggedInUserName, "[" + loggedInUserName + "]: " + message);
                messageField.setText("");
            }
        }

        public void saveMessageToDatabase(String sender, String message) {
            String sql = "INSERT INTO chat_messages (sender, receiver, message) VALUES (?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, sender);
                ps.setString(2, sender.equals(loggedInUserName) ? sessionPartnerName : loggedInUserName);
                ps.setString(3, message);
                ps.executeUpdate();

            } catch (SQLException ex) {
                System.err.println("Error saving message: " + ex.getMessage());
            }
        }

        public void loadPreviousMessages() {
            String sql = "SELECT sender, message FROM chat_messages " +
                    "WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?) " +
                    "ORDER BY timestamp";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, loggedInUserName);
                ps.setString(2, sessionPartnerName);
                ps.setString(3, sessionPartnerName);
                ps.setString(4, loggedInUserName);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String sender = rs.getString("sender");
                    String message = rs.getString("message");
                    chatArea.append(message + "\n");
                }

            } catch (SQLException ex) {
                System.err.println("Error loading messages: " + ex.getMessage());
                chatArea.append("⚠️ Could not load previous messages.\n");
            }
        }

        public void closeChat() {
            if (socketIO != null) {
                socketIO.close();
            }
            dispose();
        }
    }


    public class FeedbackWindow extends JDialog {
        private final String menteeName;
        private final String mentorName;
        private final int requestId;

        // Theme components
        private static final Color BG_DARK_MAIN = new Color(25, 29, 36);
        private static final Color TEXT_LIGHT_PRIMARY = Color.WHITE;
        private static final Color ACCENT_PURPLE = new Color(130, 0, 255);

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

            // Title
            JLabel title = new JLabel("Rate Your Session with " + mentorName, SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setForeground(TEXT_LIGHT_PRIMARY);
            add(title, BorderLayout.NORTH);

            // Form Panel
            JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
            formPanel.setBackground(BG_DARK_MAIN);
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            // Rating
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
                JOptionPane.showMessageDialog(this, "Thank you for your feedback!", "Complete", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            });

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBackground(BG_DARK_MAIN);
            buttonPanel.add(submitButton);

            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        public void saveFeedback(int rating, String comment) {

            String sql = "UPDATE mentor_requests SET status = 'Completed', mentee_rating = ?, mentee_feedback = ? WHERE id = ?";

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


    public class MentorActiveSessionsWindow extends JDialog {
        private final String mentorName;
        private final Dashboard parentDashboard;
        public MentorActiveSessionsWindow(Dashboard parent, String mentorName) {
            super(parent, "My Active Sessions", true);
            this.parentDashboard = parent;
            this.mentorName = mentorName;
            setSize(600, 450);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());
            getContentPane().setBackground(ThemeManager.BG_DARK_MAIN);

            JLabel titleLabel = new JLabel("Sessions with Accepted Requests", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titleLabel.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
            add(titleLabel, BorderLayout.NORTH);
            JPanel sessionsPanel = new JPanel();
            sessionsPanel.setLayout(new BoxLayout(sessionsPanel, BoxLayout.Y_AXIS));
            sessionsPanel.setBackground(ThemeManager.BG_DARK_MAIN);
            JScrollPane scrollPane = new JScrollPane(sessionsPanel);
            scrollPane.setBorder(null);
            scrollPane.getViewport().setBackground(ThemeManager.BG_DARK_MAIN);
            add(scrollPane, BorderLayout.CENTER);
            loadActiveSessions(sessionsPanel);

            JButton closeButton = new JButton("Close");
            styleLogoutButton(closeButton);
            closeButton.addActionListener(e -> dispose());
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            bottomPanel.setBackground(ThemeManager.BG_DARK_MAIN);
            bottomPanel.add(closeButton);
            add(bottomPanel, BorderLayout.SOUTH);
        }
        public void loadActiveSessions(JPanel panel) {

            String sql = "SELECT mentee_name, session_start, session_end, request_date, id FROM mentor_requests WHERE mentor_name = ? AND status = 'Accepted' ORDER BY request_date DESC";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, mentorName);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean found = false;
                    while (rs.next()) {
                        found = true;
                        String mentee = rs.getString("mentee_name");
                        String date = rs.getTimestamp("request_date").toString().split("\\.")[0];
                        String startTime = rs.getString("session_start");
                        String endTime = rs.getString("session_end");
                        int requestId = rs.getInt("id");
                        panel.add(createSessionCard(mentee, date, startTime, endTime, requestId));
                        panel.add(Box.createVerticalStrut(10));
                    }
                    if (!found) {
                        JLabel noSessions = new JLabel("You have no accepted sessions scheduled yet.", SwingConstants.CENTER);
                        noSessions.setForeground(ThemeManager.TEXT_LIGHT_SECONDARY);
                        noSessions.setAlignmentX(Component.CENTER_ALIGNMENT);
                        panel.add(Box.createVerticalGlue());
                        panel.add(noSessions);
                        panel.add(Box.createVerticalGlue());
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error loading active mentor sessions: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Failed to load active sessions.", "DB Error", JOptionPane.ERROR_MESSAGE);
            }
            panel.revalidate();
            panel.repaint();
        }
        public JPanel createSessionCard(String menteeName, String date, String startTime, String endTime, int requestId) {
            JPanel card = new JPanel(new BorderLayout(15, 0));
            card.setBackground(ThemeManager.BG_DARK_CARD);
            card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            card.setMaximumSize(new Dimension(550, 90));

            JPanel infoPanel = new JPanel(new GridLayout(3, 1));
            infoPanel.setOpaque(false);
            JLabel menteeLabel = new JLabel("Mentee: " + menteeName);
            menteeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            menteeLabel.setForeground(ThemeManager.TEXT_LIGHT_PRIMARY);
            JLabel scheduleLabel = new JLabel("Time: " + startTime + " - " + endTime);
            scheduleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            scheduleLabel.setForeground(ThemeManager.TEXT_AVAILABLE);
            JLabel dateLabel = new JLabel("Accepted On: " + date);
            dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            dateLabel.setForeground(ThemeManager.TEXT_LIGHT_SECONDARY);
            infoPanel.add(menteeLabel);
            infoPanel.add(scheduleLabel);
            infoPanel.add(dateLabel);

            JButton chatButton = new JButton("Start Chat");
            chatButton.setBackground(ThemeManager.ACCENT_PURPLE);
            chatButton.setForeground(Color.WHITE);
            chatButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            chatButton.addActionListener(e -> {

                parentDashboard.new ChatWindow(parentDashboard, mentorName, menteeName, requestId).setVisible(true);
                dispose();
            });
            card.add(infoPanel, BorderLayout.WEST);
            card.add(chatButton, BorderLayout.EAST);
            return card;
        }
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new Dashboard("TestMentee", "Mentee", null).setVisible(true));
    }
}
