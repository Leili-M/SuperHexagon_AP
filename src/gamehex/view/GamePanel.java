package gamehex.view;

import gamehex.model.Snapshot;

import javax.swing.*;
import java.awt.*;

public final class GamePanel extends JPanel {
    private final Renderer renderer;
    private volatile Snapshot snapshot;

    public GamePanel(Renderer renderer){
        this.renderer = renderer;
        setBackground(new Color(20,20,24));
        setDoubleBuffered(true);
    }

    public void setSnapshot(Snapshot s){ this.snapshot = s; }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Snapshot s = snapshot;
        if (s != null) {
            renderer.draw((Graphics2D) g, s, getWidth(), getHeight());
        }
    }
}
