package gamehex.model;

public final class AppSettings {
    private boolean musicEnabled = true;
    private boolean saveHistory  = true;

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
    }

    public boolean isSaveHistory() {
        return saveHistory;
    }

    public void setSaveHistory(boolean enabled) {
        this.saveHistory = enabled;
    }
}
