package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import controller.LeaderboardController;
import model.LeaderboardEntry;

public class LeaderboardUI extends JPanel {
    private LeaderboardController controller;
    private JPanel podiumPanel;
    private JPanel contendersPanel;

    // Colors consistent with the new design
    private final Color BACKGROUND_COLOR = new Color(48, 16, 107); // Deep purple background
    private final Color PANEL_COLOR = new Color(69, 39, 160); // Lighter purple
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color SUBTITLE_COLOR = new Color(200, 200, 200);
    private final Color GOLD_COLOR = new Color(255, 215, 0);
    private final Color SILVER_COLOR = new Color(192, 192, 192);
    private final Color BRONZE_COLOR = new Color(205, 127, 50);

    public LeaderboardUI(int userId) {
        try {
            controller = new LeaderboardController(userId);
            initializeUI();
            loadLeaderboard();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error initializing Leaderboard: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(BACKGROUND_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(30, 40, 20, 40));

        JLabel titleLabel = new JLabel("Global Leaderboard");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        
        JLabel subtitleLabel = new JLabel("Compare your financial journey with other adventurers across the realm.");
        subtitleLabel.setForeground(SUBTITLE_COLOR);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // Podium Panel
        podiumPanel = new JPanel(new GridBagLayout());
        podiumPanel.setBackground(BACKGROUND_COLOR);
        podiumPanel.setBorder(new EmptyBorder(20, 40, 40, 40));
        add(podiumPanel, BorderLayout.CENTER);

        // Contenders Panel
        contendersPanel = new JPanel();
        contendersPanel.setLayout(new BoxLayout(contendersPanel, BoxLayout.Y_AXIS));
        contendersPanel.setBackground(BACKGROUND_COLOR);
        contendersPanel.setBorder(new EmptyBorder(0, 40, 20, 40));

        JLabel contendersTitle = new JLabel("Other Contenders");
        contendersTitle.setForeground(TEXT_COLOR);
        contendersTitle.setFont(new Font("Arial", Font.BOLD, 20));
        contendersTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        contendersPanel.add(contendersTitle);

        add(contendersPanel, BorderLayout.SOUTH);
    }

    private class CircularLabel extends JLabel {
        private Color bgColor;
        private Color borderColor;
        private String initials;
        private static final int BORDER_THICKNESS = 3;

        public CircularLabel(String text, Color bgColor) {
            this.bgColor = bgColor;
            this.borderColor = Color.WHITE;
            this.initials = text.substring(0, Math.min(2, text.length())).toUpperCase();
            setPreferredSize(new Dimension(60, 60));
            setForeground(TEXT_COLOR);
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            setFont(new Font("Arial", Font.BOLD, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int diameter = Math.min(getWidth(), getHeight()) - (2 * BORDER_THICKNESS);
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            // Draw white border
            g2.setColor(borderColor);
            g2.fill(new Ellipse2D.Double(x - BORDER_THICKNESS, y - BORDER_THICKNESS, 
                diameter + (2 * BORDER_THICKNESS), diameter + (2 * BORDER_THICKNESS)));

            // Draw circle background
            g2.setColor(bgColor);
            g2.fill(new Ellipse2D.Double(x, y, diameter, diameter));

            // Draw text
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(initials)) / 2;
            int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(initials, textX, textY);

            g2.dispose();
        }
    }

    private class PodiumBlock extends JPanel {
        private Color blockColor;
        private int position;
        private static final int FIRST_HEIGHT = 100;
        private static final int SECOND_HEIGHT = 70;
        private static final int THIRD_HEIGHT = 40;

        public PodiumBlock(int position) {
            this.position = position;
            this.blockColor = position == 1 ? new Color(218, 165, 32) :  // Gold
                             position == 2 ? new Color(108, 108, 108) :  // Silver
                             new Color(205, 127, 50);                    // Bronze
            setOpaque(false);
            setPreferredSize(new Dimension(100, position == 1 ? 100 : position == 2 ? 70 : 40));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int arc = 10;

            // Draw main block with rounded top
            g2.setColor(blockColor);
            g2.fillRoundRect(0, 0, width, height, arc, arc);

            // Add subtle gradient for 3D effect
            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(0, 0, width, height/2, arc, arc);

            // Draw position number
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String number = String.valueOf(position);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(number, (width - fm.stringWidth(number))/2, height - 10);

            g2.dispose();
        }
    }

    private class PodiumPanel extends JPanel {
        private static final int PODIUM_WIDTH = 120;
        private static final int FIRST_HEIGHT = 100;
        private static final int SECOND_HEIGHT = 70;
        private static final int THIRD_HEIGHT = 40;
        private static final int PLATFORM_SPACING = 60;
        private static final int MIN_WIDTH = 300;

        public PodiumPanel() {
            setLayout(null);
            setBackground(BACKGROUND_COLOR);
            setPreferredSize(new Dimension(500, 280));  // Increased height
            setMinimumSize(new Dimension(MIN_WIDTH, 230));  // Increased minimum height
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculate center position dynamically
            int width = Math.max(getWidth(), MIN_WIDTH);
            int centerX = width / 2;
            int bottomY = getHeight() - 20;
            
            // Draw platforms in order (back to front)
            // Calculate spacing that adjusts with panel width
            int spacing = Math.min(PLATFORM_SPACING, width / 6);
            
            // Second place (left)
            g2.setColor(new Color(90, 50, 168));
            g2.fillRect(centerX - PODIUM_WIDTH - spacing, bottomY - SECOND_HEIGHT, PODIUM_WIDTH, SECOND_HEIGHT);
            
            // Third place (right)
            g2.fillRect(centerX + spacing, bottomY - THIRD_HEIGHT, PODIUM_WIDTH, THIRD_HEIGHT);
            
            // First place (center, drawn last to overlap)
            g2.fillRect(centerX - PODIUM_WIDTH/2, bottomY - FIRST_HEIGHT, PODIUM_WIDTH, FIRST_HEIGHT);

            g2.dispose();
        }
    }

    private JPanel createPodiumSpot(LeaderboardEntry entry, int position) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BACKGROUND_COLOR);
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create circular avatar with appropriate color
        Color avatarColor = position == 1 ? new Color(255, 87, 34) :    // Orange
                           position == 2 ? new Color(76, 175, 80) :      // Green
                           new Color(33, 150, 243);                      // Blue
        CircularLabel avatar = new CircularLabel(entry.getUserName(), avatarColor);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create username label
        JLabel nameLabel = new JLabel(entry.getUserName());
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create level label
        JLabel levelLabel = new JLabel("Level " + entry.getLevel());
        levelLabel.setForeground(TEXT_COLOR);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create XP label
        JLabel xpLabel = new JLabel(entry.getXp() + " XP");
        xpLabel.setForeground(SUBTITLE_COLOR);
        xpLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create podium block
        PodiumBlock block = new PodiumBlock(position);
        block.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with proper spacing
        container.add(avatar);
        container.add(Box.createVerticalStrut(5));
        container.add(nameLabel);
        container.add(levelLabel);
        container.add(xpLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(block);

        return container;
    }

    private JPanel createEmptyPodiumSpot(int position) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BACKGROUND_COLOR);
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create empty circular spot with question mark
        JPanel emptySpot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw dashed circle
                g2.setColor(new Color(255, 255, 255, 50));
                float[] dash = { 5.0f };
                g2.setStroke(new java.awt.BasicStroke(2.0f, java.awt.BasicStroke.CAP_BUTT,
                        java.awt.BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                g2.drawOval(1, 1, getWidth() - 3, getHeight() - 3);

                // Draw question mark
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                FontMetrics fm = g2.getFontMetrics();
                String text = "?";
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(text, textX, textY);

                g2.dispose();
            }
        };
        emptySpot.setPreferredSize(new Dimension(60, 60));
        emptySpot.setBackground(BACKGROUND_COLOR);
        emptySpot.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create position label
        JLabel positionLabel = new JLabel(position == 1 ? "1st Place" : position == 2 ? "2nd Place" : "3rd Place");
        positionLabel.setForeground(SUBTITLE_COLOR);
        positionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        positionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create placeholder for XP label to maintain consistent spacing
        JLabel placeholderXP = new JLabel(" ");
        placeholderXP.setFont(new Font("Arial", Font.PLAIN, 12));
        placeholderXP.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create podium block
        PodiumBlock block = new PodiumBlock(position);
        block.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with proper spacing
        container.add(emptySpot);
        container.add(Box.createVerticalStrut(5));
        container.add(positionLabel);
        container.add(placeholderXP);  // Added placeholder for consistent spacing
        container.add(Box.createVerticalStrut(10));
        container.add(block);

        return container;
    }

    private JPanel createContenderEntry(LeaderboardEntry entry) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 15, 10, 15),
            BorderFactory.createLineBorder(new Color(90, 50, 168), 1, true)
        ));

        // Rank
        JLabel rankLabel = new JLabel("#" + entry.getRank());
        rankLabel.setForeground(TEXT_COLOR);
        rankLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(rankLabel, BorderLayout.WEST);

        // Avatar and name in center
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userInfo.setBackground(PANEL_COLOR);
        
        CircularLabel avatar = new CircularLabel(entry.getUserName(), new Color(128, 90, 213));
        avatar.setPreferredSize(new Dimension(40, 40));
        
        JPanel nameAndRole = new JPanel();
        nameAndRole.setLayout(new BoxLayout(nameAndRole, BoxLayout.Y_AXIS));
        nameAndRole.setBackground(PANEL_COLOR);
        
        JLabel nameLabel = new JLabel(entry.getUserName());
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel roleLabel = new JLabel("Adventurer");  // You can customize this based on user role
        roleLabel.setForeground(SUBTITLE_COLOR);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        nameAndRole.add(nameLabel);
        nameAndRole.add(roleLabel);
        
        userInfo.add(avatar);
        userInfo.add(nameAndRole);
        panel.add(userInfo, BorderLayout.CENTER);

        // Level and XP on right
        JPanel rightInfo = new JPanel();
        rightInfo.setLayout(new BoxLayout(rightInfo, BoxLayout.Y_AXIS));
        rightInfo.setBackground(PANEL_COLOR);
        
        JLabel levelLabel = new JLabel("Level " + entry.getLevel());
        levelLabel.setForeground(TEXT_COLOR);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel xpLabel = new JLabel(entry.getXp() + " XP");
        xpLabel.setForeground(SUBTITLE_COLOR);
        xpLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        rightInfo.add(levelLabel);
        rightInfo.add(xpLabel);
        panel.add(rightInfo, BorderLayout.EAST);

        return panel;
    }

    private void loadLeaderboard() {
        try {
            List<LeaderboardEntry> entries = controller.getLeaderboard();
            
            // Clear panels
            podiumPanel.removeAll();
            contendersPanel.removeAll();

            // Create a main container with BorderLayout for centering
            JPanel mainContainer = new JPanel(new BorderLayout());
            mainContainer.setBackground(BACKGROUND_COLOR);
            
            // Create the podium base panel
            final PodiumPanel podiumBase = new PodiumPanel();
            
            // Create a panel for the podium spots with absolute positioning
            final JPanel spotsPanel = new JPanel(null);
            spotsPanel.setOpaque(false);
            spotsPanel.setBounds(0, 0, 500, 280);  // Match the podiumBase height

            // Add podium spots (filled or empty)
            JPanel firstPlace;
            if (entries.size() >= 1) {
                firstPlace = createPodiumSpot(entries.get(0), 1);
            } else {
                firstPlace = createEmptyPodiumSpot(1);
            }
            
            JPanel secondPlace;
            if (entries.size() >= 2) {
                secondPlace = createPodiumSpot(entries.get(1), 2);
            } else {
                secondPlace = createEmptyPodiumSpot(2);
            }
            
            JPanel thirdPlace;
            if (entries.size() >= 3) {
                thirdPlace = createPodiumSpot(entries.get(2), 3);
            } else {
                thirdPlace = createEmptyPodiumSpot(3);
            }
            
            // Calculate positions based on panel size
            int centerX = spotsPanel.getPreferredSize().width / 2;
            int spacing = Math.min(PodiumPanel.PLATFORM_SPACING, centerX / 3);
            int bottomY = spotsPanel.getPreferredSize().height - 20;
            
            // Position the spots - adjust Y positions to prevent cutting off
            firstPlace.setBounds(
                centerX - 60, 
                bottomY - PodiumPanel.FIRST_HEIGHT - 150,  // Moved up 
                120, 150);
                
            secondPlace.setBounds(
                centerX - PodiumPanel.PODIUM_WIDTH - spacing, 
                bottomY - PodiumPanel.SECOND_HEIGHT - 150,  // Moved up
                120, 150);
                
            thirdPlace.setBounds(
                centerX + spacing/2, 
                bottomY - PodiumPanel.THIRD_HEIGHT - 150,  // Moved up
                120, 150);

            // Add spots in correct order for proper layering
            spotsPanel.add(secondPlace);
            spotsPanel.add(thirdPlace);
            spotsPanel.add(firstPlace);  // First place added last to be on top
            
            podiumBase.add(spotsPanel);
            mainContainer.add(podiumBase, BorderLayout.CENTER);
            
            // Add the centered container to the podium panel
            podiumPanel.add(mainContainer);

            // Add "Other Contenders" title
            JLabel contendersTitle = new JLabel("Other Contenders");
            contendersTitle.setForeground(TEXT_COLOR);
            contendersTitle.setFont(new Font("Arial", Font.BOLD, 20));
            contendersTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            contendersPanel.add(contendersTitle);
            contendersPanel.add(Box.createVerticalStrut(10));

            // Add remaining entries to contenders panel
            if (entries.size() > 3) {
                for (int i = 3; i < entries.size(); i++) {
                    JPanel contenderEntry = createContenderEntry(entries.get(i));
                    contenderEntry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                    contendersPanel.add(contenderEntry);
                    contendersPanel.add(Box.createVerticalStrut(5));
                }
            } else {
                // Add a message when there are no contenders
                JLabel noContendersLabel = new JLabel("No other contenders yet");
                noContendersLabel.setForeground(SUBTITLE_COLOR);
                noContendersLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                noContendersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                contendersPanel.add(Box.createVerticalStrut(10));
                contendersPanel.add(noContendersLabel);
            }
            
            // Refresh the UI
            revalidate();
            repaint();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading leaderboard: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Call this method to refresh the leaderboard data
    public void refreshLeaderboard() {
        loadLeaderboard();
    }
}
