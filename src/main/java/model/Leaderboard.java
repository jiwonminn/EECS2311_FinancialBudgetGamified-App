package model;

import java.util.*;
import java.time.LocalDate;

public class Leaderboard {
    private List<LeaderboardEntry> entries;
    private static final int MAX_ENTRIES = 10;
    
    public Leaderboard() {
        this.entries = new ArrayList<>();
    }
    
    public void addEntry(String username, int points) {
        entries.add(new LeaderboardEntry(username, points));
        Collections.sort(entries);
        if (entries.size() > MAX_ENTRIES) {
            entries = entries.subList(0, MAX_ENTRIES);
        }
    }
    
    public List<LeaderboardEntry> getTopEntries() {
        return new ArrayList<>(entries);
    }
    
    public static class LeaderboardEntry implements Comparable<LeaderboardEntry> {
        private String username;
        private int points;
        private LocalDate date;
        
        public LeaderboardEntry(String username, int points) {
            this.username = username;
            this.points = points;
            this.date = LocalDate.now();
        }
        
        public String getUsername() { return username; }
        public int getPoints() { return points; }
        public LocalDate getDate() { return date; }
        
        @Override
        public int compareTo(LeaderboardEntry other) {
            return other.points - this.points; // Sort in descending order
        }
    }
}
