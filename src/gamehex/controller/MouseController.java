package gamehex.controller;

import gamehex.model.GameState;
import gamehex.view.GamePanel;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class MouseController extends MouseAdapter {
    private final GameState state;
    private final GamePanel panel;

    public MouseController(GameState state, GamePanel panel) {
        this.state = state;
        this.panel = panel;
    }

    @Override
    public void mouseMoved(MouseEvent e) { updateMouse(e); }

    @Override
    public void mouseDragged(MouseEvent e) { updateMouse(e); }

    private void updateMouse(MouseEvent e){
        if (!state.input().mouseEnabled) return;

        Point p = e.getPoint();
        int cx = panel.getWidth()/2;
        int cy = panel.getHeight()/2;
        double dx = p.x - cx;
        double dy = p.y - cy;
        double ang = Math.toDegrees(Math.atan2(dy, dx));
        if (ang < 0) ang += 360.0;

        state.input().mouseThetaDeg = (float) ang;
        state.input().mouseActive = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        state.input().mouseActive = false;
    }

}
