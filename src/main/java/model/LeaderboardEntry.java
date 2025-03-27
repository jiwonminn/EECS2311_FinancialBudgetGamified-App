package model;

public class LeaderboardEntry {
    private int rank;
    private String userName;
    private int level;
    private int xp;

    public LeaderboardEntry(int rank, String userName, int level, int xp) {
        this.rank = rank;
        this.userName = userName;
        this.level = level;
        this.xp = xp;
    }

    public int getRank() {
        return rank;
    }

    public String getUserName() {
        return userName;
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }
}
