package gamehex.controller;

import gamehex.model.GameState;
import gamehex.model.Snapshot;
import gamehex.view.GamePanel;

public final class GameLoop implements Runnable {
    private final GameState state;
    private final GamePanel panel;
    private volatile boolean running = false;

    public GameLoop(GameState state, GamePanel panel){
        this.state = state;
        this.panel = panel;
    }

    public void start(){
        if (running) return;
        running = true;
        Thread t = new Thread(this, "GameLoop");
        t.setDaemon(true);
        t.start();
    }

    public void stop(){ running = false; }

    @Override
    public void run() {
        final double targetFps = 60.0;
        final double frameTimeNs = 1_000_000_000.0 / targetFps;
        long last = System.nanoTime();

        while (running){
            long now = System.nanoTime();
            double dt = (now - last) / 1_000_000_000.0;
            if (dt > 0.25) dt = 0.25;
            last = now;

            if (state.input().pauseEdge) {
                state.setPaused(!state.paused());
                state.input().pauseEdge = false;
            }

            state.update((float) dt);

            panel.setSnapshot(Snapshot.from(state));
            panel.repaint();

            long sleepNs = (long)(frameTimeNs - (System.nanoTime() - now));
            if (sleepNs > 0) {
                try { Thread.sleep(sleepNs / 1_000_000L, (int)(sleepNs % 1_000_000L)); }
                catch (InterruptedException ignored) {}
            }
        }
    }
}
