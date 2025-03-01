package main.java.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LeaderboardPanel extends JPanel {
    
    public LeaderboardPanel() {
        initializeUI();
    }
    
    private void initializeUI() {
        setBackground(MainUI.DARK_PURPLE);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Leaderboard content
        JPanel leaderboardPanel = createLeaderboardContent();
        add(leaderboardPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainUI.MEDIUM_PURPLE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Leaderboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        
        JLabel subtitleLabel = new JLabel("Compete with others on financial management skills");
        subtitleLabel.setForeground(new Color(200, 200, 200));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.add(titleLabel, BorderLayout.NORTH);
        labelPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        panel.add(labelPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createLeaderboardContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MainUI.MEDIUM_PURPLE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // For now, we'll add a placeholder as this would typically
        // connect to a backend service to fetch other users' data
        JPanel topUsersPanel = new JPanel();
        topUsersPanel.setLayout(new BoxLayout(topUsersPanel, BoxLayout.Y_AXIS));
        topUsersPanel.setOpaque(false);
        
        // Add sample leaderboard entries
        topUsersPanel.add(createLeaderboardEntry(1, "SavvySaver", 7850, "Financial Wizard"));
        topUsersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topUsersPanel.add(createLeaderboardEntry(2, "BudgetMaster", 6340, "Budget Veteran"));
        topUsersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topUsersPanel.add(createLeaderboardEntry(3, "InvestorPro", 5920, "Investment Guru"));
        topUsersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topUsersPanel.add(createLeaderboardEntry(4, "MoneyWise", 4780, "Budget Warrior"));
        topUsersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        topUsersPanel.add(createLeaderboardEntry(5, "FrugalFinance", 3650, "Saving Specialist"));
        
        // Add a note about your ranking
        JPanel yourRankPanel = new JPanel(new BorderLayout());
        yourRankPanel.setBackground(new Color(60, 30, 90));
        yourRankPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainUI.LIGHT_PURPLE),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel yourRankLabel = new JLabel("You're currently not on the leaderboard. Keep tracking your finances to climb the ranks!");
        yourRankLabel.setForeground(Color.WHITE);
        yourRankLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        yourRankPanel.add(yourRankLabel, BorderLayout.CENTER);
        
        // Add components to main panel
        panel.add(new JScrollPane(topUsersPanel) {
            {
                setOpaque(false);
                getViewport().setOpaque(false);
                setBorder(null);
            }
        }, BorderLayout.CENTER);
        panel.add(yourRankPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLeaderboardEntry(int rank, String username, int xp, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 25, 65));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Rank
        JLabel rankLabel = new JLabel("#" + rank);
        rankLabel.setForeground(Color.WHITE);
        rankLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // User info
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1));
        userInfoPanel.setOpaque(false);
        
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(new Color(200, 200, 200));
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(titleLabel);
        
        // XP
        JLabel xpLabel = new JLabel(xp + " XP");
        xpLabel.setForeground(new Color(255, 215, 0)); // Gold color for XP
        xpLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Add all to panel
        panel.add(rankLabel, BorderLayout.WEST);
        panel.add(userInfoPanel, BorderLayout.CENTER);
        panel.add(xpLabel, BorderLayout.EAST);
        
        return panel;
    }
} 