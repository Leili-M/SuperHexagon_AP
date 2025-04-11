package model;

public class GameRecord {
    private String date;
    private String playerName;
    private long playDuration; // مدت زمان بازی به میلی‌ثانیه

    public GameRecord(String date, String playerName, long playDuration) {
        this.date = date;
        this.playerName = playerName;
        this.playDuration = playDuration;
    }

    public String getDate() {
        return date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public long getPlayDuration() {
        return playDuration;
    }

    @Override
    public String toString() {
        return "GameRecord{" +
                "date='" + date + '\'' +
                ", playerName='" + playerName + '\'' +
                ", playDuration=" + playDuration +
                '}';
    }
}
