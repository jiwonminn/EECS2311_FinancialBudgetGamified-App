package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

    // Colors exactly matching the design
    private final Color BACKGROUND_COLOR = new Color(48, 16, 107); // Deep purple background
    private final Color GOLD_COLOR = new Color(247, 182, 24);      // Gold for 1st place
    private final Color SILVER_COLOR = new Color(158, 158, 158);   // Silver for 2nd place
    private final Color BRONZE_COLOR = new Color(224, 127, 61);    // Bronze for 3rd place
    private final Color HEADER_COLOR = new Color(255, 223, 0);     // Yellow for header
    private final Color TEXT_COLOR = new Color(255, 255, 255);     // White text
    private final Color DARKER_PURPLE = new Color(39, 20, 83);     // Darker purple for contenders panel

    public LeaderboardUI(int userId) {
        try {
            controller = new LeaderboardController(userId);
            initializeUI();
            loadLeaderboard();
            
            // Add component listener to refresh on resize
            addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent e) {
                    refresh();
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error initializing Leaderboard: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header Panel - Trophy and title
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Podium Panel - For top 3 players
        podiumPanel = new JPanel(new GridBagLayout());
        podiumPanel.setBackground(BACKGROUND_COLOR);
        add(podiumPanel, BorderLayout.CENTER);

        // Contenders Panel - For other players
        contendersPanel = createContendersPanel();
        add(contendersPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Trophy Icon - scaled based on panel width
        JLabel trophyLabel = new JLabel("🏆") {
            @Override
            public void paint(Graphics g) {
                Font scaledFont = new Font("Dialog", Font.BOLD, Math.max(24, getWidth() / 30));
                setFont(scaledFont);
                super.paint(g);
            }
        };
        trophyLabel.setForeground(HEADER_COLOR);
        trophyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Leaderboard Title - scaled based on panel width
        JLabel titleLabel = new JLabel("Leaderboard") {
            @Override
            public void paint(Graphics g) {
                Font scaledFont = new Font("Dialog", Font.BOLD, Math.max(20, getWidth() / 25));
                setFont(scaledFont);
                super.paint(g);
            }
        };
        titleLabel.setForeground(HEADER_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtitle - scaled based on panel width
        JLabel subtitleLabel = new JLabel("Top players this week") {
            @Override
            public void paint(Graphics g) {
                Font scaledFont = new Font("Dialog", Font.PLAIN, Math.max(12, getWidth() / 50));
                setFont(scaledFont);
                super.paint(g);
            }
        };
        subtitleLabel.setForeground(TEXT_COLOR);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        headerPanel.add(trophyLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createVerticalStrut(20));
        
        return headerPanel;
    }
    
    private JPanel createContendersPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(DARKER_PURPLE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(0, 120));
        
        // Other Contenders header with person icon
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(DARKER_PURPLE);
        
        JLabel personIcon = new JLabel("👥");
        personIcon.setFont(new Font("Dialog", Font.PLAIN, 20));
        personIcon.setForeground(TEXT_COLOR);
        
        JLabel contendersTitle = new JLabel(" Other Contenders");
        contendersTitle.setFont(new Font("Dialog", Font.BOLD, 18));
        contendersTitle.setForeground(TEXT_COLOR);
        
        headerPanel.add(personIcon);
        headerPanel.add(contendersTitle);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Content panel for the list of contenders
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(DARKER_PURPLE);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Default message
        JLabel defaultLabel = new JLabel("No other contenders yet");
        defaultLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        defaultLabel.setForeground(TEXT_COLOR);
        defaultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(defaultLabel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private void loadLeaderboard() {
        try {
            List<LeaderboardEntry> entries = controller.getLeaderboard();
            displayPodium(entries);
            displayContenders(entries);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading leaderboard data: " + e.getMessage(),
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void displayPodium(List<LeaderboardEntry> entries) {
        podiumPanel.removeAll();
        
        // Calculate avatar size based on panel width but with better limits
        int avatarSize = Math.max(40, Math.min(60, getWidth() / 20));
        
        // Create the podium players with their positions
        PodiumLayout podiumLayout = new PodiumLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Position 2 (Silver) - always create this position
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 20, 0, 20);
        PlayerPanel player2 = entries.size() >= 2 ? 
            createPlayerPanel(entries.get(1), 2, avatarSize) : 
            createPlaceholderPlayerPanel(2, avatarSize);
        podiumPanel.add(player2, gbc);
        
        // Position 1 (Gold) - always create this position
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 0, 20);
        PlayerPanel player1 = entries.size() >= 1 ? 
            createPlayerPanel(entries.get(0), 1, avatarSize) : 
            createPlaceholderPlayerPanel(1, avatarSize);
        podiumPanel.add(player1, gbc);
        
        // Position 3 (Bronze) - always create this position
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.insets = new Insets(60, 20, 0, 20);
        PlayerPanel player3 = entries.size() >= 3 ? 
            createPlayerPanel(entries.get(2), 3, avatarSize) : 
            createPlaceholderPlayerPanel(3, avatarSize);
        podiumPanel.add(player3, gbc);
        
        // Add the podium blocks
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(30, 0, 10, 0); // Reduced top padding
        gbc.fill = GridBagConstraints.HORIZONTAL;
        podiumPanel.add(podiumLayout, gbc);
        
        podiumPanel.revalidate();
        podiumPanel.repaint();
    }
    
    private PlayerPanel createPlayerPanel(LeaderboardEntry entry, int position, int avatarSize) {
        return new PlayerPanel(entry, position, false, avatarSize);
    }
    
    private PlayerPanel createPlaceholderPlayerPanel(int position, int avatarSize) {
        // Create a placeholder entry for empty positions
        LeaderboardEntry placeholder = new LeaderboardEntry(position, "No User", 0, 0);
        return new PlayerPanel(placeholder, position, true, avatarSize);
    }

    private void displayContenders(List<LeaderboardEntry> entries) {
        // Get the content panel (second component in contendersPanel)
        JPanel contentPanel = (JPanel) ((JPanel) contendersPanel.getComponent(0)).getComponent(1);
        contentPanel.removeAll();
        
        if (entries.size() <= 3) {
            // Show default message
            JLabel defaultLabel = new JLabel("No other contenders yet");
            defaultLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
            defaultLabel.setForeground(TEXT_COLOR);
            defaultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(defaultLabel);
        } else {
            // Display contenders beyond the top 3
            for (int i = 3; i < Math.min(entries.size(), 10); i++) {
                JPanel contenderRow = createContenderRow(entries.get(i));
                contentPanel.add(contenderRow);
                contentPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createContenderRow(LeaderboardEntry entry) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setBackground(DARKER_PURPLE);
        row.setBorder(new EmptyBorder(5, 0, 5, 0));
        
        // User avatar
        CircularAvatar avatar = new CircularAvatar(entry.getUserName(), new Color(103, 58, 183), 15);
        
        // Username
        JLabel nameLabel = new JLabel(" " + entry.getUserName());
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);
        
        // Create glue to push level and XP to the right
        Component glue = Box.createHorizontalGlue();
        
        // Level
        JLabel levelLabel = new JLabel("Level " + entry.getLevel() + "  ");
        levelLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        levelLabel.setForeground(new Color(179, 157, 219));
        
        // XP
        JLabel xpLabel = new JLabel(entry.getXp() + " XP");
        xpLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        xpLabel.setForeground(new Color(179, 157, 219));
        
        row.add(avatar);
        row.add(nameLabel);
        row.add(glue);
        row.add(levelLabel);
        row.add(xpLabel);
        
        return row;
    }
    
    // Custom component for player avatar
    private class CircularAvatar extends JPanel {
        private String initials;
        private Color bgColor;
        private int size;
        private Color borderColor;
        
        public CircularAvatar(String username, Color bgColor, int size) {
            this.bgColor = bgColor;
            this.size = size;
            this.borderColor = null; // No border by default
            
            // Get initials - special handling for "No User" placeholders
            if (username != null && !username.isEmpty()) {
                if (username.equals("No User")) {
                    this.initials = "NU";
                } else {
                    char first = Character.toUpperCase(username.charAt(0));
                    this.initials = String.valueOf(first);
                    if (username.contains(" ") && username.indexOf(" ") + 1 < username.length()) {
                        char second = Character.toUpperCase(username.charAt(username.indexOf(" ") + 1));
                        this.initials += String.valueOf(second);
                    }
                }
            } else {
                this.initials = "?";
            }
            
            // Make sure width and height are the same for a perfect circle
            int diameter = size * 2;
            setPreferredSize(new Dimension(diameter, diameter));
            setMinimumSize(new Dimension(diameter, diameter));
            setMaximumSize(new Dimension(diameter, diameter));
            setOpaque(false);
        }
        
        public CircularAvatar(String username, Color bgColor, int size, Color borderColor) {
            this(username, bgColor, size);
            this.borderColor = borderColor;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Ensure perfect circle by using the smaller dimension
            int diameter = Math.min(width, height);
            int x = (width - diameter) / 2;
            int y = (height - diameter) / 2;
            
            // Draw border if specified
            if (borderColor != null) {
                g2d.setColor(borderColor);
                g2d.fill(new Ellipse2D.Double(x, y, diameter, diameter));
                g2d.setColor(bgColor);
                g2d.fill(new Ellipse2D.Double(x + 2, y + 2, diameter - 4, diameter - 4));
            } else {
                g2d.setColor(bgColor);
                g2d.fill(new Ellipse2D.Double(x, y, diameter, diameter));
            }
            
            // Draw initials centered
            g2d.setColor(Color.WHITE);
            Font font = new Font("Dialog", Font.BOLD, size);
            g2d.setFont(font);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(initials);
            int textHeight = fm.getHeight();
            g2d.drawString(initials, x + (diameter - textWidth) / 2, 
                          y + (diameter - textHeight) / 2 + fm.getAscent());
            
            g2d.dispose();
        }
    }
    
    // Player panel for the podium
    private class PlayerPanel extends JPanel {
        private LeaderboardEntry entry;
        private int position;
        private boolean isPlaceholder;
        private int avatarSize;
        
        public PlayerPanel(LeaderboardEntry entry, int position, boolean isPlaceholder, int avatarSize) {
            this.entry = entry;
            this.position = position;
            this.isPlaceholder = isPlaceholder;
            this.avatarSize = avatarSize;
            
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(BACKGROUND_COLOR);
            
            // Create the avatar with appropriate decoration
            JPanel avatarPanel = new JPanel();
            avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
            avatarPanel.setBackground(BACKGROUND_COLOR);
            avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add crown/medal decoration
            JLabel decoration = new JLabel(position == 1 ? "👑" : position == 2 ? "🥈" : "🥉") {
                @Override
                public void paint(Graphics g) {
                    Font scaledFont = new Font("Dialog", Font.PLAIN, Math.max(16, avatarSize / 2));
                    setFont(scaledFont);
                    super.paint(g);
                }
            };
            decoration.setAlignmentX(Component.CENTER_ALIGNMENT);
            avatarPanel.add(decoration);
            
            // Avatar circle with border
            Color avatarColor = position == 1 ? new Color(255, 87, 34) :  // Orange for 1st
                               position == 2 ? new Color(76, 175, 80) :   // Green for 2nd
                               new Color(33, 150, 243);                   // Blue for 3rd
            
            // Use a gray color for placeholders
            if (isPlaceholder) {
                avatarColor = new Color(120, 120, 120);
            }
            
            Color borderColor = position == 1 ? GOLD_COLOR :
                               position == 2 ? SILVER_COLOR : 
                               BRONZE_COLOR;
            
            // Create scaled avatar
            CircularAvatar avatar = new CircularAvatar(entry.getUserName(), avatarColor, avatarSize, borderColor);
            avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
            avatarPanel.add(avatar);
            
            // Username - scaled based on avatar size
            JLabel nameLabel = new JLabel(entry.getUserName()) {
                @Override
                public void paint(Graphics g) {
                    Font scaledFont = new Font("Dialog", Font.BOLD, Math.max(10, avatarSize / 3));
                    setFont(scaledFont);
                    super.paint(g);
                }
            };
            nameLabel.setForeground(isPlaceholder ? new Color(180, 180, 180) : TEXT_COLOR);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Level - scaled based on avatar size
            JLabel levelLabel;
            if (isPlaceholder) {
                levelLabel = new JLabel("--");
            } else {
                levelLabel = new JLabel("Level " + entry.getLevel());
            }
            levelLabel.setFont(new Font("Dialog", Font.PLAIN, Math.max(8, avatarSize / 4)));
            levelLabel.setForeground(isPlaceholder ? new Color(180, 180, 180) : TEXT_COLOR);
            levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // XP - scaled based on avatar size
            JLabel xpLabel;
            if (isPlaceholder) {
                xpLabel = new JLabel("-- XP");
            } else {
                xpLabel = new JLabel(entry.getXp() + " XP");
            }
            xpLabel.setFont(new Font("Dialog", Font.PLAIN, Math.max(8, avatarSize / 4)));
            xpLabel.setForeground(isPlaceholder ? new Color(180, 180, 180) : TEXT_COLOR);
            xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            add(avatarPanel);
            add(Box.createVerticalStrut(5));
            add(nameLabel);
            add(levelLabel);
            add(xpLabel);
        }
    }
    
    // Podium layout (the colored blocks with numbers)
    private class PodiumLayout extends JPanel {
        public PodiumLayout() {
            // Fixed height for consistent appearance
            setPreferredSize(new Dimension(0, 120));
            setBackground(BACKGROUND_COLOR);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Fixed proportional widths
            int podiumWidth = Math.max(80, width / 6);
            
            // Calculate positions
            int firstX = width / 2 - podiumWidth / 2;
            int secondX = firstX - podiumWidth - 20;
            int thirdX = firstX + podiumWidth + 20;
            
            int bottomY = height - 10;
            
            // Fixed heights for predictable appearance
            int firstHeight = height - 20;
            int secondHeight = (int)(height * 0.75);
            int thirdHeight = (int)(height * 0.5);
            
            // Draw 2nd place podium (Silver)
            g2d.setColor(SILVER_COLOR);
            g2d.fillRect(secondX, bottomY - secondHeight, podiumWidth, secondHeight);
            
            // Draw 1st place podium (Gold)
            g2d.setColor(GOLD_COLOR);
            g2d.fillRect(firstX, bottomY - firstHeight, podiumWidth, firstHeight);
            
            // Draw 3rd place podium (Bronze)
            g2d.setColor(BRONZE_COLOR);
            g2d.fillRect(thirdX, bottomY - thirdHeight, podiumWidth, thirdHeight);
            
            // Draw position numbers
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Dialog", Font.BOLD, 36));
            
            drawCenteredString(g2d, "2", secondX, podiumWidth, bottomY - secondHeight / 2);
            drawCenteredString(g2d, "1", firstX, podiumWidth, bottomY - firstHeight / 2);
            drawCenteredString(g2d, "3", thirdX, podiumWidth, bottomY - thirdHeight / 2);
            
            g2d.dispose();
        }
        
        private void drawCenteredString(Graphics2D g2d, String text, int x, int width, int y) {
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            g2d.drawString(text, x + (width - textWidth) / 2, y + fm.getAscent() / 2);
        }
    }
    
    // Method to refresh the leaderboard
    public void refresh() {
        loadLeaderboard();
    }
}