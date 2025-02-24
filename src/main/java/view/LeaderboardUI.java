package view;

import controller.LeaderboardController;
import model.Leaderboard;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class LeaderboardUI extends JFrame {
    private LeaderboardController leaderboardController;
    private JTable leaderboardTable;
    
    public LeaderboardUI() {
        leaderboardController = new LeaderboardController();
        setTitle("Leaderboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        setupUI();
        updateLeaderboard();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Top Performers", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);
        
        // Leaderboard Table
        String[] columnNames = {"Rank", "Username", "Points", "Date"};
        leaderboardTable = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Refresh Button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> updateLeaderboard());
        add(refreshButton, BorderLayout.SOUTH);
    }
    
    private void updateLeaderboard() {
        DefaultTableModel model = (DefaultTableModel) leaderboardTable.getModel();
        model.setRowCount(0);
        
        int rank = 1;
        for (Leaderboard.LeaderboardEntry entry : leaderboardController.getTopScores()) {
            model.addRow(new Object[]{
                rank++,
                entry.getUsername(),
                entry.getPoints(),
                entry.getDate()
            });
        }
    }
}
