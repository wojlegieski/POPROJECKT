package database;

import java.sql.Timestamp;

public class GameResult {
    private double timeSeconds;
    private int lapCount;
    private Timestamp completedAt;
    
    public GameResult(double timeSeconds, int lapCount, Timestamp completedAt) {
        this.timeSeconds = timeSeconds;
        this.lapCount = lapCount;
        this.completedAt = completedAt;
    }
    
    public double getTimeSeconds() {
        return timeSeconds;
    }
    
    public void setTimeSeconds(double timeSeconds) {
        this.timeSeconds = timeSeconds;
    }
    
    public int getLapCount() {
        return lapCount;
    }
    
    public void setLapCount(int lapCount) {
        this.lapCount = lapCount;
    }
    
    public Timestamp getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(Timestamp completedAt) {
        this.completedAt = completedAt;
    }
    
    public String getFormattedTime() {
        int minutes = (int) (timeSeconds / 60);
        int seconds = (int) (timeSeconds % 60);
        int milliseconds = (int) ((timeSeconds % 1) * 100);
        return String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
    }
} 