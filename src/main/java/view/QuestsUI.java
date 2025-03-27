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
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import controller.QuestController;
import model.Quest;

/**
 * UI for displaying and managing quests
 */
public class QuestsUI extends JPanel {
    // Colors
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color SECONDARY_COLOR = new Color(70, 50, 110);
    private final Color TAB_ACTIVE_COLOR = new Color(30, 20, 50);
    private final Color TAB_INACTIVE_COLOR = new Color(20, 12, 35);
    
    // Quest type colors
    private final Color DAILY_COLOR = new Color(52, 152, 219);
    private final Color WEEKLY_COLOR = new Color(155, 89, 182);
    private final Color MONTHLY_COLOR = new Color(211, 84, 0);
    
    // Components
    private JPanel dailyQuestsPanel;
    private JPanel weeklyQuestsPanel;
    private JPanel monthlyQuestsPanel;
    private JPanel specialQuestsPanel;
    private JProgressBar xpProgressBar;
    private JLabel levelLabel;
    private JLabel xpLabel;
    private JPanel tabsPanel;
    private JPanel contentPanel;
    private JPanel[] tabs;
    private String currentTab = "Daily Quests";
    
    // Controller
    private QuestController questController;
    private int userId;
    private String userName;
    private String userEmail;
    private javax.swing.Timer autoCheckTimer;
    
    /**
     * Constructor
     */
    public QuestsUI(int userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.questController = new QuestController();
        

        
        initializeUI();
        loadQuests();
        
        // Set up timer to periodically check for completed quests (every 5 seconds)
        autoCheckTimer = new javax.swing.Timer(5000, e -> {
            try {
                // Check for quest completions
                questController.checkAndCompleteQuests(userId);
                // Force quest controller tables creation
                questController.createQuestTablesIfNotExists();
                // Reload quests to reflect any automatic completions
                loadQuests();
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Silent failure - don't show error to user for background updates
            }
        });
        autoCheckTimer.start();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JLabel titleLabel = new JLabel("Quest Board");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel subtitleLabel = new JLabel("Quests are completed automatically as you use the app");
        subtitleLabel.setForeground(new Color(180, 180, 180));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Create tabs panel
        tabsPanel = new JPanel();
        tabsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabsPanel.setBackground(BACKGROUND_COLOR);
        
        // Tab names
        String[] tabNames = {"Daily Quests", "Weekly Quests", "Special Quests"};
        tabs = new JPanel[tabNames.length];
        
        // Create tabs
        for (int i = 0; i < tabNames.length; i++) {
            final String tabName = tabNames[i];
            tabs[i] = createTab(tabName, tabName.equals(currentTab));
            tabsPanel.add(tabs[i]);
            
            // Add click listener to tab
            tabs[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switchTab(tabName);
                }
            });
        }
        
        headerPanel.add(tabsPanel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);
        
        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Initialize quest panels
        dailyQuestsPanel = new JPanel();
        dailyQuestsPanel.setLayout(new BoxLayout(dailyQuestsPanel, BoxLayout.Y_AXIS));
        dailyQuestsPanel.setBackground(BACKGROUND_COLOR);
        
        weeklyQuestsPanel = new JPanel();
        weeklyQuestsPanel.setLayout(new BoxLayout(weeklyQuestsPanel, BoxLayout.Y_AXIS));
        weeklyQuestsPanel.setBackground(BACKGROUND_COLOR);
        
        monthlyQuestsPanel = new JPanel();
        monthlyQuestsPanel.setLayout(new BoxLayout(monthlyQuestsPanel, BoxLayout.Y_AXIS));
        monthlyQuestsPanel.setBackground(BACKGROUND_COLOR);
        
        specialQuestsPanel = new JPanel();
        specialQuestsPanel.setLayout(new BoxLayout(specialQuestsPanel, BoxLayout.Y_AXIS));
        specialQuestsPanel.setBackground(BACKGROUND_COLOR);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Show initial tab
        showTabContent(currentTab);
    }
    
    /**
     * Creates a tab for the tab panel
     */
    private JPanel createTab(String tabName, boolean isActive) {
        JPanel tab = new JPanel();
        tab.setLayout(new BorderLayout());
        tab.setBackground(isActive ? TAB_ACTIVE_COLOR : TAB_INACTIVE_COLOR);
        tab.setPreferredSize(new Dimension(120, 35));
        tab.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(isActive ? 2 : 0, 0, 0, 0, ACCENT_COLOR),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
        JLabel tabLabel = new JLabel(tabName);
        tabLabel.setForeground(isActive ? TEXT_COLOR : new Color(180, 180, 180));
        tabLabel.setFont(new Font("Arial", isActive ? Font.BOLD : Font.PLAIN, 13));
        tabLabel.setHorizontalAlignment(JLabel.CENTER);
        
        tab.add(tabLabel, BorderLayout.CENTER);
        tab.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        return tab;
    }
    
    /**
     * Switch to the selected tab
     */
    private void switchTab(String tabName) {
        if (tabName.equals(currentTab)) {
            return;
        }
        
        currentTab = tabName;
        
        // Update tabs
        String[] tabNames = {"Daily Quests", "Weekly Quests", "Special Quests"};
        for (int i = 0; i < tabNames.length; i++) {
            boolean isActive = tabNames[i].equals(currentTab);
            tabs[i].setBackground(isActive ? TAB_ACTIVE_COLOR : TAB_INACTIVE_COLOR);
            tabs[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(isActive ? 2 : 0, 0, 0, 0, ACCENT_COLOR),
                new EmptyBorder(8, 15, 8, 15)
            ));
            
            JLabel tabLabel = (JLabel) tabs[i].getComponent(0);
            tabLabel.setForeground(isActive ? TEXT_COLOR : new Color(180, 180, 180));
            tabLabel.setFont(new Font("Arial", isActive ? Font.BOLD : Font.PLAIN, 13));
        }
        
        showTabContent(tabName);
    }
    
    /**
     * Show content for the selected tab
     */
    private void showTabContent(String tabName) {
        contentPanel.removeAll();
        
        JPanel contentToShow = null;
        
        switch (tabName) {
            case "Daily Quests":
                contentToShow = dailyQuestsPanel;
                break;
            case "Weekly Quests":
                contentToShow = weeklyQuestsPanel;
                break;
            case "Special Quests":
                contentToShow = specialQuestsPanel;
                break;
        }
        
        if (contentToShow != null) {
            JScrollPane scrollPane = createScrollPane(contentToShow);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
        }
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Creates a scrollpane for a panel
     */
    private JScrollPane createScrollPane(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Custom scroll bar UI
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = ACCENT_COLOR;
                this.trackColor = PANEL_COLOR;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        
        return scrollPane;
    }
    
    /**
     * Creates a single quest card
     */
    private JPanel createQuestCard(Quest quest, Color accentColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_COLOR);
        // Add subtle gradient effect with rounded corners
        panel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 30, 80), 1),
            new EmptyBorder(18, 20, 18, 20)
        ));
        panel.setMaximumSize(new Dimension(2000, 160));
        panel.setPreferredSize(new Dimension(800, 160));
        
        // Left side - quest icon
        JLabel questIcon = createQuestIcon(quest.getQuestType());
        
        // Center - quest info
        JPanel questInfoPanel = new JPanel();
        questInfoPanel.setLayout(new BoxLayout(questInfoPanel, BoxLayout.Y_AXIS));
        questInfoPanel.setBackground(PANEL_COLOR);
        questInfoPanel.setBorder(new EmptyBorder(0, 15, 0, 15));
        
        // Create a title panel that supports text wrapping
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PANEL_COLOR);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("<html><div style='width: 400px;'>" + quest.getTitle() + "</div></html>");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Create a description panel that supports text wrapping
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(PANEL_COLOR);
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descriptionLabel = new JLabel("<html><div style='width: 400px;'>" + quest.getDescription() + "</div></html>");
        descriptionLabel.setForeground(new Color(200, 200, 200));
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descPanel.add(descriptionLabel, BorderLayout.WEST);
        
        questInfoPanel.add(titlePanel);
        questInfoPanel.add(Box.createRigidArea(new Dimension(0, 8))); // Space after title
        questInfoPanel.add(descPanel);
        questInfoPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Space after description
        
        // Progress bar - improved styling
        JProgressBar progressBar = new JProgressBar() {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int width = getWidth();
                    int height = getHeight();
                    
                    // Draw background with rounded corners
                    g2d.setColor(new Color(30, 18, 50));
                    g2d.fillRoundRect(0, 0, width, height, height, height);
                    
                    // Calculate filled width
                    int fillWidth = (int) (width * ((double) getValue() / getMaximum()));
                    
                    // Draw filled portion with rounded corners
                    g2d.setColor(accentColor);
                    if (fillWidth > 0) {
                        g2d.fillRoundRect(0, 0, fillWidth, height, height, height);
                    }
                }
            }
        };
        
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        
        // Calculate actual progress instead of using random values
        int progressValue = 0;
        try {
            // Use the controller to calculate quest progress
            progressValue = questController.calculateQuestProgress(quest, userId);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error calculating quest progress: " + e.getMessage());
            // Default value on error
            progressValue = quest.isCompleted() ? 100 : 0;
        }
        
        progressBar.setValue(progressValue);
        progressBar.setStringPainted(false);
        progressBar.setOpaque(false);
        progressBar.setBorderPainted(false);
        progressBar.setBackground(new Color(30, 18, 50));
        progressBar.setForeground(accentColor);
        progressBar.setPreferredSize(new Dimension(300, 12)); // Slightly taller for better visibility
        progressBar.setMaximumSize(new Dimension(500, 12));
        progressBar.setBorder(null);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Completion text with actual progress value
        JLabel progressLabel = new JLabel(progressValue + "% Complete");
        progressLabel.setForeground(new Color(180, 180, 180));
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        progressLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        questInfoPanel.add(progressBar);
        questInfoPanel.add(Box.createRigidArea(new Dimension(0, 8))); // Increased spacing
        questInfoPanel.add(progressLabel);
        
        // Right side - buttons and rewards
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(PANEL_COLOR);
        actionPanel.setPreferredSize(new Dimension(120, 60));
        
        // Info button
        JPanel buttonsPanel = new JPanel(new BorderLayout(10, 0));
        buttonsPanel.setBackground(PANEL_COLOR);
        
        JButton infoButton = createRoundButton("?", new Color(50, 50, 70));
        infoButton.setPreferredSize(new Dimension(35, 35)); // Slightly larger
        
        // Remove complete button and use only info button
        buttonsPanel.add(infoButton, BorderLayout.CENTER);
        
        // XP Reward with enhanced styling
        JLabel xpRewardLabel = new JLabel("+" + quest.getXpReward() + " XP") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Add a subtle glow behind the XP text
                    g2d.setColor(new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), 40));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
            }
        };
        
        xpRewardLabel.setForeground(ACCENT_COLOR);
        xpRewardLabel.setFont(new Font("Arial", Font.BOLD, 16));
        xpRewardLabel.setHorizontalAlignment(JLabel.RIGHT);
        xpRewardLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        actionPanel.add(buttonsPanel, BorderLayout.CENTER);
        actionPanel.add(xpRewardLabel, BorderLayout.SOUTH);
        
        // Add components to panel
        panel.add(questIcon, BorderLayout.WEST);
        panel.add(questInfoPanel, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.EAST);
        
        // Add margin between cards and slight shadow effect
        JPanel containerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint subtle shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 20, 10, 10);
            }
        };
        
        containerPanel.setOpaque(false);
        containerPanel.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        containerPanel.setBorder(new EmptyBorder(0, 0, 20, 0)); // Increased spacing between cards
        containerPanel.setMaximumSize(new Dimension(2000, 185)); // Increased to match the card size
        containerPanel.add(panel, BorderLayout.CENTER);
        
        // Add action listener to info button to show quest details
        infoButton.addActionListener(e -> {
            String questDetails = "Quest: " + quest.getTitle() + "\n\n" +
                                 "Description: " + quest.getDescription() + "\n\n" +
                                 "Type: " + quest.getQuestType() + "\n" +
                                 "XP Reward: " + quest.getXpReward() + "\n";
            
            if (quest.getDeadline() != null) {
                questDetails += "Deadline: " + quest.getDeadline() + "\n";
            }
            
            if (quest.isCompleted()) {
                questDetails += "\nStatus: Completed";
            } else {
                questDetails += "\nStatus: In Progress";
            }
            
            JOptionPane.showMessageDialog(
                this,
                questDetails,
                "Quest Details",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
        
        return containerPanel;
    }
    
    /**
     * Creates a quest icon based on quest type
     */
    private JLabel createQuestIcon(String questType) {
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 40;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                // Background circle
                Color iconColor;
                String iconText;
                
                switch (questType) {
                    case "DAILY":
                        iconColor = DAILY_COLOR;
                        iconText = "📋";
                        break;
                    case "WEEKLY":
                        iconColor = WEEKLY_COLOR;
                        iconText = "🛡️";
                        break;
                    case "MONTHLY":
                        iconColor = MONTHLY_COLOR;
                        iconText = "⭐";
                        break;
                    default:
                        iconColor = ACCENT_COLOR;
                        iconText = "📋";
                }
                
                g2d.setColor(iconColor);
                g2d.fillOval(x, y, size, size);
                
                // Icon text
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(iconText);
                int textHeight = fm.getHeight();
                g2d.drawString(iconText, x + (size - textWidth) / 2, y + size / 2 + textHeight / 4);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(80, 60);
            }
        };
        
        return iconLabel;
    }
    
    /**
     * Creates a round button
     */
    private JButton createRoundButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                
                g2d.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight / 3) / 2);
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    /**
     * Load quests from the database
     */
    private void loadQuests() {
        try {
            // Check if we need to generate sample quests
            List<Quest> existingQuests = questController.getQuestsByUserId(userId);
            if (existingQuests.isEmpty()) {
                questController.generateSampleQuests(userId);
            }
            
            // Load daily quests
            List<Quest> dailyQuests = questController.getDailyQuestsByUserId(userId);
            dailyQuestsPanel.removeAll();
            for (Quest quest : dailyQuests) {
                dailyQuestsPanel.add(createQuestCard(quest, DAILY_COLOR));
            }
            
            if (dailyQuests.isEmpty()) {
                dailyQuestsPanel.add(createEmptyQuestMessage("No daily quests available"));
            }
            
            // Load weekly quests
            List<Quest> weeklyQuests = questController.getWeeklyQuestsByUserId(userId);
            weeklyQuestsPanel.removeAll();
            for (Quest quest : weeklyQuests) {
                weeklyQuestsPanel.add(createQuestCard(quest, WEEKLY_COLOR));
            }
            
            if (weeklyQuests.isEmpty()) {
                weeklyQuestsPanel.add(createEmptyQuestMessage("No weekly quests available"));
            }
            
            // Load monthly quests - using these as "Special Quests"
            List<Quest> monthlyQuests = questController.getMonthlyQuestsByUserId(userId);
            specialQuestsPanel.removeAll();
            for (Quest quest : monthlyQuests) {
                specialQuestsPanel.add(createQuestCard(quest, MONTHLY_COLOR));
            }
            
            if (monthlyQuests.isEmpty()) {
                specialQuestsPanel.add(createEmptyQuestMessage("No special quests available"));
            }
            
            // Update level displays (both top and bottom)
            updateLevelPanels();
            
            // Refresh UI
            revalidate();
            repaint();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error loading quests: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Update all level progress panels in this UI
     */
    private void updateLevelPanels() {
        // Find and update all LevelProgressPanel instances
        for (Component comp : getComponents()) {
            if (comp instanceof LevelProgressPanel) {
                ((LevelProgressPanel) comp).updateLevelDisplay();
            } else if (comp instanceof JPanel) {
                for (Component inner : ((JPanel) comp).getComponents()) {
                    if (inner instanceof LevelProgressPanel) {
                        ((LevelProgressPanel) inner).updateLevelDisplay();
                    } else if (inner instanceof JPanel) {
                        for (Component innerInner : ((JPanel) inner).getComponents()) {
                            if (innerInner instanceof LevelProgressPanel) {
                                ((LevelProgressPanel) innerInner).updateLevelDisplay();
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Creates an empty message panel
     */
    private JPanel createEmptyQuestMessage(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(50, 20, 20, 20));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(new Color(150, 150, 150));
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        
        return panel;
    }
} 