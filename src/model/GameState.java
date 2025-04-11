package model;

public class GameState {
    private boolean isPaused;
    private long elapsedTime;   // زمان سپری شده به میلی‌ثانیه
    private long bestRecord;    // بهترین رکورد به میلی‌ثانیه

    public GameState() {
        this.isPaused = false;
        this.elapsedTime = 0;
        this.bestRecord = 0;
    }

    // متد به‌روزرسانی زمان سپری‌شده
    public void updateElapsedTime(long delta) {
        if (!isPaused) {
            elapsedTime += delta;
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public long getBestRecord() {
        return bestRecord;
    }

    public void setBestRecord(long bestRecord) {
        this.bestRecord = bestRecord;
    }

    // متد ریست برای زمانی که بازی جدید شروع می‌شود
    public void reset() {
        this.elapsedTime = 0;
        this.isPaused = false;
    }
}
