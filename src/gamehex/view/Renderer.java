package gamehex.view;

import gamehex.model.Snapshot;
import java.awt.Graphics2D;

public interface Renderer {
    void draw(Graphics2D g, Snapshot snap, int width, int height);
}
