package view;

import controller.LeaderboardController;
import model.LeaderboardEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LeaderboardUI extends JPanel {
    private LeaderboardController controller;
    private JTable leaderboardTable;

    // Colors consistent with your existing pages
    private final Color BACKGROUND_COLOR = new Color(24, 15, 41);
    private final Color PANEL_COLOR = new Color(40, 24, 69);
    private final Color TEXT_COLOR = new Color(255, 255, 255);
    private final Color ACCENT_COLOR = new Color(128, 90, 213); // For header borders and selection

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

        // Create table for leaderboard entries
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
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLeaderboard() {
        try {
            List<LeaderboardEntry> entries = controller.getLeaderboard();
            String[] columnNames = {"Rank", "User", "Level", "XP"};
            Object[][] data = new Object[entries.size()][4];
            int i = 0;
            for (LeaderboardEntry entry : entries) {
                data[i][0] = entry.getRank();
                data[i][1] = entry.getUserName();
                data[i][2] = entry.getLevel();
                data[i][3] = entry.getXp();
                i++;
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
