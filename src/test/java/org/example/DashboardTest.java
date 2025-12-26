package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DashboardTest {
    private Dashboard dashboard;
    private JFrame mockLoginFrame;
    @BeforeEach
    void setUp() {
        System.setProperty("swing.defaultlaf", "javax.swing.plaf.metal.MetalLookAndFeel");
        mockLoginFrame = new JFrame();
        dashboard = new Dashboard("TestUser", "Mentee", mockLoginFrame);
    }
    @Test
    void startSessionMonitoring_ShouldNotThrowException() {
        assertDoesNotThrow(() -> dashboard.startSessionMonitoring("TestMentee"),
                "startSessionMonitoring should not throw exceptions");
    }
    @Test
    void testThemeManagerColors() {

        assertNotNull(Dashboard.ThemeManager.BG_DARK_MAIN);
        assertNotNull(Dashboard.ThemeManager.BG_DARK_CARD);
        assertNotNull(Dashboard.ThemeManager.TEXT_LIGHT_PRIMARY);
        assertNotNull(Dashboard.ThemeManager.ACCENT_PURPLE);
        assertNotNull(Dashboard.ThemeManager.LOGOUT_RED);
    }
    @Test
    void getIconBGColor_ShouldReturnColorForEachDomain() {

        assertNotNull(Dashboard.ThemeManager.getIconBGColor("Cybersecurity"));
        assertNotNull(Dashboard.ThemeManager.getIconBGColor("Cloud Computing"));
        assertNotNull(Dashboard.ThemeManager.getIconBGColor("Database Management"));
        assertNotNull(Dashboard.ThemeManager.getIconBGColor("Unknown Domain"));
    }
    @Test
    void styleSearchButton_ShouldStyleButtonCorrectly() {
        JButton button = new JButton("Test");
        dashboard.styleSearchButton(button);

        assertEquals(Dashboard.ThemeManager.ACCENT_PURPLE, button.getBackground());
        assertEquals(Color.WHITE, button.getForeground());
        assertFalse(button.isFocusPainted());
        assertNotNull(button.getMouseListeners());

    }
    @Test
    void styleLogoutButton_ShouldStyleButtonCorrectly() {
        JButton button = new JButton("Test");
        dashboard.styleLogoutButton(button);

        assertEquals(Dashboard.ThemeManager.LOGOUT_RED, button.getBackground());
        assertEquals(Color.WHITE, button.getForeground());
        assertFalse(button.isFocusPainted());
        assertNotNull(button.getMouseListeners());

    }
    @Test
    void getDummyMentors_ShouldReturnNonEmptyList() {
        List<Dashboard.MentorData> mentors = dashboard.getDummyMentors();
        assertNotNull(mentors, "Should return non-null list");
        assertFalse(mentors.isEmpty(), "Should return non-empty list");
        assertEquals(9, mentors.size(), "Should return 9 dummy mentors");
        Dashboard.MentorData firstMentor = mentors.get(0);
        assertEquals("Kavitha", firstMentor.name);
        assertEquals("Cybersecurity", firstMentor.domain);
        assertNotNull(firstMentor.description);
        assertNotNull(firstMentor.status);
        assertNotNull(firstMentor.schedule);
    }
    @Test
    void testMentorDataClass() {
        Dashboard.MentorData mentorData = new Dashboard.MentorData(
                "Test Name",
                "Test Domain",
                "Test Description",
                "Available",
                "9:00-17:00"
        );
        assertNotNull(mentorData, "MentorData should be created successfully");
        assertEquals("Test Name", mentorData.name);
        assertEquals("Test Domain", mentorData.domain);
        assertEquals("Test Description", mentorData.description);
        assertEquals("Available", mentorData.status);
        assertEquals("9:00-17:00", mentorData.schedule);
    }
    @Test
    void testConstructorSetsProperties() {
        assertEquals("Mentor Connect Dashboard", dashboard.getTitle());
        assertEquals(1300, dashboard.getWidth());
        assertEquals(850, dashboard.getHeight());
        assertEquals("TestUser", dashboard.loggedInUserName);
        assertEquals("Mentee", dashboard.loggedInUserRole);
        assertNotNull(dashboard.contentPanel);
        assertNotNull(dashboard.searchField);
    }
    @Test
    void testCreateMentorCard() {
        JPanel card = dashboard.createMentorCard(
                "Test Mentor",
                "Java Programming",
                "Expert in Java and J2EE",
                "Available",
                "9:00-17:00"
        );
        assertNotNull(card, "Created mentor card should not be null");
        assertEquals(new Dimension(400, 250), card.getPreferredSize());
        assertEquals(Dashboard.ThemeManager.BG_DARK_CARD, card.getBackground());
    }
}
