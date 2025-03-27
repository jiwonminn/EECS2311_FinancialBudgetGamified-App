package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import controller.LeaderboardController;
import model.LeaderboardEntry;

public class LeaderboardUI extends JPanel {
    private LeaderboardController controller;
    private JTable leaderboardTable;
    private JPanel podiumPanel;

    // Colors consistent with your existing pages
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213); // For header borders and selection
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
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Header with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("Leaderboard");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Create podium panel
        podiumPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        podiumPanel.setBackground(BACKGROUND_COLOR);
        add(podiumPanel, BorderLayout.CENTER);

        // Create table for remaining leaderboard entries
        leaderboardTable = new JTable();
        leaderboardTable.setFillsViewportHeight(true);
        leaderboardTable.setRowHeight(35);
        leaderboardTable.setBackground(PANEL_COLOR);
        leaderboardTable.setForeground(TEXT_COLOR);
        leaderboardTable.setGridColor(BACKGROUND_COLOR);
        leaderboardTable.setShowGrid(true);
        leaderboardTable.setIntercellSpacing(new Dimension(0, 0));

        // Customize table header appearance
        leaderboardTable.getTableHeader().setOpaque(false);
        leaderboardTable.getTableHeader().setBackground(PANEL_COLOR);
        leaderboardTable.getTableHeader().setForeground(TEXT_COLOR);
        leaderboardTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        ((DefaultTableCellRenderer) leaderboardTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        leaderboardTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_COLOR));

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private JPanel createPodiumSpot(LeaderboardEntry entry, int position) {
        JPanel spot = new JPanel();
        spot.setLayout(new BoxLayout(spot, BoxLayout.Y_AXIS));
        spot.setBackground(PANEL_COLOR);
        spot.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        spot.setPreferredSize(new Dimension(150, 200));
        
        // Set podium spot color based on position
        Color spotColor;
        switch (position) {
            case 1: // Gold
                spotColor = GOLD_COLOR;
                break;
            case 2: // Silver
                spotColor = SILVER_COLOR;
                break;
            case 3: // Bronze
                spotColor = BRONZE_COLOR;
                break;
            default:
                spotColor = PANEL_COLOR;
        }
        spot.setBackground(spotColor);

        // Add rank number
        JLabel rankLabel = new JLabel("#" + entry.getRank());
        rankLabel.setFont(new Font("Arial", Font.BOLD, 24));
        rankLabel.setForeground(TEXT_COLOR);
        rankLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spot.add(rankLabel);

        // Add trophy emoji
        JLabel trophyLabel = new JLabel(position == 1 ? "🏆" : position == 2 ? "🥈" : "🥉");
        trophyLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        trophyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spot.add(trophyLabel);

        // Add username
        JLabel nameLabel = new JLabel(entry.getUserName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spot.add(nameLabel);

        // Add level and XP
        JLabel levelLabel = new JLabel("Level " + entry.getLevel());
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        levelLabel.setForeground(TEXT_COLOR);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spot.add(levelLabel);

        JLabel xpLabel = new JLabel(entry.getXp() + " XP");
        xpLabel.setFont(new Font("Arial", Font.BOLD, 14));
        xpLabel.setForeground(TEXT_COLOR);
        xpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        spot.add(xpLabel);

        return spot;
    }

    private void loadLeaderboard() {
        try {
            List<LeaderboardEntry> entries = controller.getLeaderboard();
            
            // Clear podium panel
            podiumPanel.removeAll();
            
            // Add podium spots for top 3
            for (int i = 0; i < Math.min(3, entries.size()); i++) {
                LeaderboardEntry entry = entries.get(i);
                JPanel podiumSpot = createPodiumSpot(entry, i + 1);
                podiumPanel.add(podiumSpot);
            }
            
            // Create table for remaining entries (4th place and below)
            if (entries.size() > 3) {
                String[] columnNames = {"Rank", "User", "Level", "XP"};
                Object[][] data = new Object[entries.size() - 3][4];
                for (int i = 3; i < entries.size(); i++) {
                    LeaderboardEntry entry = entries.get(i);
                    data[i - 3][0] = entry.getRank();
                    data[i - 3][1] = entry.getUserName();
                    data[i - 3][2] = entry.getLevel();
                    data[i - 3][3] = entry.getXp();
                }
                
                DefaultTableModel model = new DefaultTableModel(data, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                leaderboardTable.setModel(model);

                // Set a custom renderer for centered text and zebra striping
                DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        setHorizontalAlignment(JLabel.CENTER);
                        c.setForeground(TEXT_COLOR);
                        if (isSelected) {
                            c.setBackground(ACCENT_COLOR);
                        } else {
                            // Zebra striping effect
                            c.setBackground((row % 2 == 0) ? PANEL_COLOR.darker() : PANEL_COLOR);
                        }
                        return c;
                    }
                };
                for (int iCol = 0; iCol < leaderboardTable.getColumnCount(); iCol++) {
                    leaderboardTable.getColumnModel().getColumn(iCol).setCellRenderer(renderer);
                }
            }
            
            // Refresh the UI
            podiumPanel.revalidate();
            podiumPanel.repaint();
            leaderboardTable.revalidate();
            leaderboardTable.repaint();
            
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
        revalidate();
        repaint();
    }
}
