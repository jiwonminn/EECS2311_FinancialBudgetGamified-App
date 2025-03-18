package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import controller.QuestController;

/**
 * Reusable panel for displaying level progress and XP
 */
public class LevelProgressPanel extends JPanel {
    // Colors
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213);
    private final Color XP_BAR_COLOR = new Color(46, 204, 113);
    private final Color XP_BAR_TRACK_COLOR = new Color(30, 60, 45);
    
    // Components
    private JLabel levelNameLabel;
    private JLabel levelSubtitleLabel;
    private JProgressBar xpProgressBar;
    private JLabel xpProgressLabel;
    
    // Data
    private int userId;
    private QuestController questController;
    private int level;
    private int currentXp;
    private int xpForNextLevel;
    
    // Layout modes
    public static final int LAYOUT_FULL = 1; // Shield icon + level name + progress bar
    public static final int LAYOUT_HEADER = 2; // Level name + progress bar as header
    public static final int LAYOUT_COMPACT = 3; // Small compact footer without shield icon
    
    private int layoutMode;
    
    /**
     * Constructor
     */
    public LevelProgressPanel(int userId, int layoutMode) {
        this.userId = userId;
        this.layoutMode = layoutMode;
        this.questController = new QuestController();
        
        initializeUI();
        updateLevelDisplay();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setOpaque(true);
        
        if (layoutMode == LAYOUT_HEADER) {
            initializeHeaderLayout();
        } else if (layoutMode == LAYOUT_COMPACT) {
            initializeCompactLayout();
        } else {
            // Default to full layout
            initializeFullLayout();
        }
    }
    
    /**
     * Initialize the full layout with shield icon, level name, and progress bar
     */
    private void initializeFullLayout() {
        setLayout(new BorderLayout(15, 0));
        setBackground(PANEL_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(50, 30, 80), 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        
        // Left side - level shield + text
        JPanel levelPanel = new JPanel(new BorderLayout(10, 0));
        levelPanel.setBackground(PANEL_COLOR);
        
        JLabel levelIconLabel = createLevelShield(1);
        
        JPanel levelTextPanel = new JPanel();
        levelTextPanel.setLayout(new BoxLayout(levelTextPanel, BoxLayout.Y_AXIS));
        levelTextPanel.setBackground(PANEL_COLOR);
        
        levelNameLabel = new JLabel("Level 1");
        levelNameLabel.setForeground(TEXT_COLOR);
        levelNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        levelNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        levelSubtitleLabel = new JLabel("0 XP total");
        levelSubtitleLabel.setForeground(new Color(180, 180, 180));
        levelSubtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        levelSubtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        levelTextPanel.add(levelNameLabel);
        levelTextPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        levelTextPanel.add(levelSubtitleLabel);
        
        levelPanel.add(levelIconLabel, BorderLayout.WEST);
        levelPanel.add(levelTextPanel, BorderLayout.CENTER);
        
        // Right side - Progress to next level bar
        JPanel progressPanel = new JPanel(new BorderLayout(0, 5));
        progressPanel.setBackground(PANEL_COLOR);
        
        JLabel progressTitleLabel = new JLabel("Progress to Next Level");
        progressTitleLabel.setForeground(TEXT_COLOR);
        progressTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Custom XP progress bar with rounded corners
        xpProgressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int width = getWidth();
                    int height = getHeight();
                    
                    // Draw background with rounded corners
                    g2d.setColor(XP_BAR_TRACK_COLOR);
                    g2d.fillRoundRect(0, 0, width, height, height, height);
                    
                    // Calculate filled width
                    int fillWidth = (int) (width * ((double) getValue() / getMaximum()));
                    
                    // Draw filled portion with rounded corners
                    g2d.setColor(XP_BAR_COLOR);
                    if (fillWidth > 0) {
                        g2d.fillRoundRect(0, 0, fillWidth, height, height, height);
                    }
                }
            }
        };
        
        xpProgressBar.setValue(0);
        xpProgressBar.setStringPainted(false);
        xpProgressBar.setOpaque(false);
        xpProgressBar.setBorderPainted(false);
        xpProgressBar.setBorder(null);
        xpProgressBar.setPreferredSize(new Dimension(100, 10));
        
        xpProgressLabel = new JLabel("0 / 100 XP to level 2");
        xpProgressLabel.setForeground(new Color(180, 180, 180));
        xpProgressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        progressPanel.add(progressTitleLabel, BorderLayout.NORTH);
        progressPanel.add(xpProgressBar, BorderLayout.CENTER);
        progressPanel.add(xpProgressLabel, BorderLayout.SOUTH);
        
        add(levelPanel, BorderLayout.WEST);
        add(progressPanel, BorderLayout.CENTER);
    }
    
    /**
     * Initialize the header layout with level name and progress bar
     */
    private void initializeHeaderLayout() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(0, 0, 5, 0));
        
        // Top panel with level name
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        
        levelNameLabel = new JLabel(getLevelTitle(0)); // Default to level 0
        levelNameLabel.setForeground(TEXT_COLOR);
        levelNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        topPanel.add(levelNameLabel, BorderLayout.CENTER);
        
        // Progress bar
        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(BACKGROUND_COLOR);
        progressPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        
        // Custom XP progress bar with rounded corners
        xpProgressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int width = getWidth();
                    int height = getHeight();
                    
                    // Draw background with rounded corners
                    g2d.setColor(XP_BAR_TRACK_COLOR);
                    g2d.fillRoundRect(0, 0, width, height, height, height);
                    
                    // Calculate filled width
                    int fillWidth = (int) (width * ((double) getValue() / getMaximum()));
                    
                    // Draw filled portion with rounded corners
                    g2d.setColor(XP_BAR_COLOR);
                    if (fillWidth > 0) {
                        g2d.fillRoundRect(0, 0, fillWidth, height, height, height);
                    }
                }
            }
        };
        
        xpProgressBar.setValue(0);
        xpProgressBar.setStringPainted(false);
        xpProgressBar.setOpaque(false);
        xpProgressBar.setBorderPainted(false);
        xpProgressBar.setBorder(null);
        xpProgressBar.setPreferredSize(new Dimension(100, 6));
        
        JPanel xpLabelWrapper = new JPanel(new BorderLayout());
        xpLabelWrapper.setBackground(BACKGROUND_COLOR);
        
        xpProgressLabel = new JLabel("0 / 100 XP");
        xpProgressLabel.setForeground(new Color(180, 180, 180));
        xpProgressLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        xpProgressLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        xpLabelWrapper.add(xpProgressLabel, BorderLayout.EAST);
        xpLabelWrapper.setBorder(new EmptyBorder(3, 0, 0, 0));
        
        progressPanel.add(xpProgressBar, BorderLayout.CENTER);
        progressPanel.add(xpLabelWrapper, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        add(progressPanel, BorderLayout.CENTER);
    }
    
    /**
     * Initialize compact layout for smaller screens or limited space
     */
    private void initializeCompactLayout() {
        setLayout(new BorderLayout(0, 0));
        setBackground(PANEL_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 30, 80)),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
        JPanel progressPanel = new JPanel(new BorderLayout(10, 0));
        progressPanel.setBackground(PANEL_COLOR);
        
        JLabel levelLabel = new JLabel("Progress to Level 3:");
        levelLabel.setForeground(TEXT_COLOR);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Custom XP progress bar with rounded corners
        xpProgressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                if (g instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int width = getWidth();
                    int height = getHeight();
                    
                    // Draw background with rounded corners
                    g2d.setColor(XP_BAR_TRACK_COLOR);
                    g2d.fillRoundRect(0, 0, width, height, height, height);
                    
                    // Calculate filled width
                    int fillWidth = (int) (width * ((double) getValue() / getMaximum()));
                    
                    // Draw filled portion with rounded corners
                    g2d.setColor(XP_BAR_COLOR);
                    if (fillWidth > 0) {
                        g2d.fillRoundRect(0, 0, fillWidth, height, height, height);
                    }
                }
            }
        };
        
        xpProgressBar.setValue(0);
        xpProgressBar.setStringPainted(false);
        xpProgressBar.setOpaque(false);
        xpProgressBar.setBorderPainted(false);
        xpProgressBar.setBorder(null);
        xpProgressBar.setPreferredSize(new Dimension(100, 8));
        
        xpProgressLabel = new JLabel("50 / 400 XP");
        xpProgressLabel.setForeground(new Color(180, 180, 180));
        xpProgressLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        xpProgressLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        progressPanel.add(levelLabel, BorderLayout.WEST);
        progressPanel.add(xpProgressBar, BorderLayout.CENTER);
        progressPanel.add(xpProgressLabel, BorderLayout.EAST);
        
        add(progressPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates a level shield icon with the specified level
     */
    private JLabel createLevelShield(int level) {
        final int finalLevel = level;
        
        JLabel shieldLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shield shape
                int w = getWidth();
                int h = getHeight();
                int shieldWidth = 40;
                int shieldHeight = 48;
                int x = (w - shieldWidth) / 2;
                int y = (h - shieldHeight) / 2;
                
                // Shield background
                g2d.setColor(ACCENT_COLOR);
                int[] xPoints = {x, x + shieldWidth, x + shieldWidth, x + shieldWidth/2, x};
                int[] yPoints = {y, y, y + shieldHeight * 2/3, y + shieldHeight, y + shieldHeight * 2/3};
                g2d.fillPolygon(xPoints, yPoints, 5);
                
                // Level text
                g2d.setColor(TEXT_COLOR);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                String levelText = String.valueOf(finalLevel);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(levelText);
                int textHeight = fm.getHeight();
                g2d.drawString(levelText, x + (shieldWidth - textWidth) / 2, y + (shieldHeight + textHeight / 2) / 2);
                
                g2d.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(60, 50);
            }
        };
        
        return shieldLabel;
    }
    
    /**
     * Returns the title for a given level
     */
    private String getLevelTitle(int level) {
        switch (level) {
            case 0:
                return "Level 0 Budget Novice";
            case 1:
                return "Level 1 Budget Apprentice";
            case 2:
                return "Level 2 Budget Explorer";
            case 3:
                return "Level 3 Budget Warrior";
            case 4:
                return "Level 4 Budget Veteran";
            case 5:
                return "Level 5 Budget Master";
            default:
                if (level > 5) {
                    return "Level " + level + " Budget Legend";
                } else {
                    return "Level " + level + " Budget Beginner";
                }
        }
    }
    
    /**
     * Updates the level display with user's current level and XP
     */
    public void updateLevelDisplay() {
        try {
            int[] userExp = questController.getUserExperience(userId);
            currentXp = userExp[0];
            level = userExp[1];
            
            // Calculate XP needed for next level
            xpForNextLevel = questController.getXpForNextLevel(level);
            int xpNeededForCurrentLevel = (level * level) * 100;
            int xpInCurrentLevel = currentXp - xpNeededForCurrentLevel;
            int xpNeededForNextLevel = xpForNextLevel - xpNeededForCurrentLevel;
            
            if (layoutMode == LAYOUT_HEADER) {
                // Update header style layout
                levelNameLabel.setText(getLevelTitle(level));
                xpProgressBar.setMaximum(xpNeededForNextLevel);
                xpProgressBar.setValue(xpInCurrentLevel);
                xpProgressLabel.setText(xpInCurrentLevel + " / " + xpNeededForNextLevel + " XP");
            } else if (layoutMode == LAYOUT_COMPACT) {
                // Update compact layout
                xpProgressBar.setMaximum(xpNeededForNextLevel);
                xpProgressBar.setValue(xpInCurrentLevel);
                xpProgressLabel.setText(xpInCurrentLevel + " / " + xpNeededForNextLevel + " XP");
            } else {
                // Update full layout
                levelNameLabel.setText("Level " + level);
                levelSubtitleLabel.setText(currentXp + " XP total");
                
                xpProgressBar.setMaximum(xpNeededForNextLevel);
                xpProgressBar.setValue(xpInCurrentLevel);
                xpProgressLabel.setText(xpInCurrentLevel + " / " + xpNeededForNextLevel + " XP to level " + (level + 1));
                
                // Update shield with current level
                Component comp = getComponent(0); // Level panel at WEST
                if (comp instanceof JPanel) {
                    Component shieldComp = ((JPanel)comp).getComponent(0);
                    if (shieldComp instanceof JLabel) {
                        // Replace with new shield showing current level
                        ((JPanel)comp).remove(0);
                        ((JPanel)comp).add(createLevelShield(level), BorderLayout.WEST);
                        ((JPanel)comp).revalidate();
                        ((JPanel)comp).repaint();
                    }
                }
            }
            
            revalidate();
            repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the current user level
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Get the current user XP
     */
    public int getCurrentXp() {
        return currentXp;
    }
} 