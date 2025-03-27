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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
        private String initials;

        public CircularLabel(String text, Color bgColor) {
            this.bgColor = bgColor;
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

            // Draw circle background
            g2.setColor(bgColor);
            g2.fill(new Ellipse2D.Double(0, 0, getWidth() - 1, getHeight() - 1));

            // Draw text
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(initials)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(initials, x, y);

            g2.dispose();
        }
    }

    private class PodiumPlatform extends JPanel {
        private int position;
        private Color platformColor;

        public PodiumPlatform(int position) {
            this.position = position;
            this.platformColor = new Color(90, 50, 168); // Base platform color
            setPreferredSize(new Dimension(120, position == 1 ? 100 : position == 2 ? 80 : 60));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw platform
            g2.setColor(platformColor);
            int width = getWidth();
            int height = getHeight();
            int arcSize = 15;

            // Top part of platform
            g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);

            // Add a subtle gradient effect
            g2.setColor(new Color(0, 0, 0, 50));
            g2.fillRoundRect(0, height/2, width, height/2, arcSize, arcSize);

            // Add position number
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            String posText = String.valueOf(position);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (width - fm.stringWidth(posText)) / 2;
            int textY = height - 10;
            g2.drawString(posText, textX, textY);

            g2.dispose();
        }
    }

    private JPanel createPodiumSpot(LeaderboardEntry entry, int position) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BACKGROUND_COLOR);
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create circular avatar
        Color avatarColor = position == 1 ? new Color(255, 87, 34) :
                           position == 2 ? new Color(76, 175, 80) :
                           new Color(33, 150, 243);
        CircularLabel avatar = new CircularLabel(entry.getUserName(), avatarColor);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create medal icon
        JLabel medalLabel = new JLabel(position == 1 ? "🏆" : position == 2 ? "🥈" : "🥉");
        medalLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        medalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create username label
        JLabel nameLabel = new JLabel(entry.getUserName());
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create level label
        JLabel levelLabel = new JLabel("Level " + entry.getLevel());
        levelLabel.setForeground(SUBTITLE_COLOR);
        levelLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create platform
        PodiumPlatform platform = new PodiumPlatform(position);
        platform.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with proper spacing
        container.add(medalLabel);
        container.add(Box.createVerticalStrut(5));
        container.add(avatar);
        container.add(Box.createVerticalStrut(5));
        container.add(nameLabel);
        container.add(Box.createVerticalStrut(2));
        container.add(levelLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(platform);

        return container;
    }

    private JPanel createEmptyPodiumSpot(int position) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BACKGROUND_COLOR);
        container.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create empty circular spot
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
                g2.dispose();
            }
        };
        emptySpot.setPreferredSize(new Dimension(60, 60));
        emptySpot.setBackground(BACKGROUND_COLOR);
        emptySpot.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create empty medal placeholder
        JLabel emptyMedalLabel = new JLabel("?");
        emptyMedalLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        emptyMedalLabel.setForeground(new Color(255, 255, 255, 50));
        emptyMedalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create position label
        JLabel positionLabel = new JLabel(position == 1 ? "1st Place" : position == 2 ? "2nd Place" : "3rd Place");
        positionLabel.setForeground(SUBTITLE_COLOR);
        positionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        positionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create platform
        PodiumPlatform platform = new PodiumPlatform(position);
        platform.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components with proper spacing
        container.add(emptyMedalLabel);
        container.add(Box.createVerticalStrut(5));
        container.add(emptySpot);
        container.add(Box.createVerticalStrut(5));
        container.add(positionLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(platform);

        return container;
    }

    private JPanel createContenderEntry(LeaderboardEntry entry) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(PANEL_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(10, 15, 10, 15),
            BorderFactory.createLineBorder(BACKGROUND_COLOR, 1, true)
        ));

        // Rank
        JLabel rankLabel = new JLabel("#" + entry.getRank());
        rankLabel.setForeground(TEXT_COLOR);
        rankLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(rankLabel, BorderLayout.WEST);

        // Avatar and name
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        userInfo.setBackground(PANEL_COLOR);
        
        CircularLabel avatar = new CircularLabel(entry.getUserName(), new Color(128, 90, 213));
        avatar.setPreferredSize(new Dimension(40, 40));
        
        JLabel nameLabel = new JLabel(entry.getUserName());
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        userInfo.add(avatar);
        userInfo.add(nameLabel);
        panel.add(userInfo, BorderLayout.CENTER);

        // Level and XP
        JLabel levelLabel = new JLabel("Level " + entry.getLevel());
        levelLabel.setForeground(TEXT_COLOR);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(levelLabel, BorderLayout.EAST);

        return panel;
    }

    private void loadLeaderboard() {
        try {
            List<LeaderboardEntry> entries = controller.getLeaderboard();
            
            // Clear panels
            podiumPanel.removeAll();
            contendersPanel.removeAll();

            // Create a panel for the podium spots
            JPanel spotsPanel = new JPanel(new GridBagLayout());
            spotsPanel.setBackground(BACKGROUND_COLOR);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 20, 0, 20);
            
            // Add podium spots (filled or empty)
            // Second place (left)
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.anchor = GridBagConstraints.SOUTH;
            if (entries.size() >= 2) {
                spotsPanel.add(createPodiumSpot(entries.get(1), 2), gbc);
            } else {
                spotsPanel.add(createEmptyPodiumSpot(2), gbc);
            }
            
            // First place (center, higher)
            gbc.gridx = 1;
            gbc.gridy = 0;
            if (entries.size() >= 1) {
                spotsPanel.add(createPodiumSpot(entries.get(0), 1), gbc);
            } else {
                spotsPanel.add(createEmptyPodiumSpot(1), gbc);
            }
            
            // Third place (right)
            gbc.gridx = 2;
            gbc.gridy = 2;
            if (entries.size() >= 3) {
                spotsPanel.add(createPodiumSpot(entries.get(2), 3), gbc);
            } else {
                spotsPanel.add(createEmptyPodiumSpot(3), gbc);
            }

            podiumPanel.add(spotsPanel);

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
