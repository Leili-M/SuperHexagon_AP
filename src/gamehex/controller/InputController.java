package gamehex.controller;

import gamehex.model.GameState;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public final class InputController extends KeyAdapter {
    private final GameState.Input in;

    public InputController(GameState state){
        this.in = state.input();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_LEFT  -> in.left  = true;
            case KeyEvent.VK_RIGHT -> in.right = true;
            case KeyEvent.VK_P     -> in.pauseEdge = true;
            case KeyEvent.VK_M -> {
                in.mouseEnabled = !in.mouseEnabled;
                if (!in.mouseEnabled) in.mouseActive = false;
            }
            default -> {}
        }
    }



    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_LEFT  -> in.left  = false;
            case KeyEvent.VK_RIGHT -> in.right = false;
            default -> {}
        }
    }
}
