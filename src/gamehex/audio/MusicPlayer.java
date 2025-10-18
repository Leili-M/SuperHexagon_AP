package gamehex.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MusicPlayer {
    private Clip clip;

    public boolean loadFromResource(String logicalPath, boolean loop){
        close();
        URL url = null;

        String p1 = logicalPath;
        String p2 = logicalPath.startsWith("/") ? logicalPath.substring(1) : logicalPath;
        try {
            url = MusicPlayer.class.getResource(p1);
            if (url == null) url = MusicPlayer.class.getResource(p2);
            if (url == null) url = Thread.currentThread().getContextClassLoader().getResource(p2);
        } catch (Exception ignored){}

        if (url != null) {
            return openFromUrl(url, loop);
        }

        try {
            Path devPath = Path.of(System.getProperty("user.dir"), "src", "main", "resources", p2);
            if (Files.exists(devPath)) {
                return openFromFile(devPath.toFile(), loop);
            }
        } catch (Exception ignored){}

        System.err.println("[MusicPlayer] Resource not found (classpath + dev fallback failed): " + logicalPath);
        return false;
    }

    private boolean openFromUrl(URL url, boolean loop){
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(url)) {
            clip = AudioSystem.getClip();
            clip.open(ais);
            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.stop();
            System.out.println("[MusicPlayer] loaded (classpath): " + url);
            return true;
        } catch (UnsupportedAudioFileException uex) {
            System.err.println("[MusicPlayer] Unsupported format (use WAV/AIFF/AU or MP3 SPI): " + url);
        } catch (Exception e) {
            System.err.println("[MusicPlayer] load failed from URL: " + e);
        }
        close();
        return false;
    }

    private boolean openFromFile(File file, boolean loop){
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            clip = AudioSystem.getClip();
            clip.open(ais);
            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.stop();
            System.out.println("[MusicPlayer] loaded (dev file): " + file.getAbsolutePath());
            return true;
        } catch (UnsupportedAudioFileException uex) {
            System.err.println("[MusicPlayer] Unsupported format (use WAV/AIFF/AU or MP3 SPI): " + file);
        } catch (Exception e) {
            System.err.println("[MusicPlayer] load failed from file: " + e);
        }
        close();
        return false;
    }

    public void playLoop(){
        if (clip == null) return;
        if (!clip.isRunning()) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
    }

    public void playOnce(){
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void stop(){
        if (clip != null) {
            try { clip.stop(); } catch (Exception ignored) {}
        }
    }

    public void close(){
        if (clip != null) {
            try { clip.stop(); } catch (Exception ignored) {}
            try { clip.close(); } catch (Exception ignored) {}
            clip = null;
        }
    }

    public void setVolumeDb(float db){
        if (clip == null) return;
        try {
            FloatControl c = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            db = Math.max(c.getMinimum(), Math.min(c.getMaximum(), db));
            c.setValue(db);
        } catch (Exception ignored) {}
    }

    public boolean isRunning(){ return clip != null && clip.isRunning(); }
}
