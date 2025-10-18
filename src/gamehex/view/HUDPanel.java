package gamehex.view;

import gamehex.model.ScoreBoard;

import javax.swing.*;
import java.awt.*;
import java.util.function.LongSupplier;

public final class HUDPanel extends JPanel {
    private final LongSupplier elapsedMillisSupplier;
    private final ScoreBoard board;

    public HUDPanel(LongSupplier elapsedMillisSupplier, ScoreBoard board){
        this.elapsedMillisSupplier = elapsedMillisSupplier;
        this.board = board;
        setOpaque(false);
        setPreferredSize(new Dimension(10, 36));
        setFont(getFont().deriveFont(Font.BOLD, 16f));
    }

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        long curMs   = elapsedMillisSupplier.getAsLong();
        long bestMs  = board.bestMillis();

        String curStr  = formatMillis(curMs);
        String bestStr = formatMillis(bestMs);

        Color curColor;
        if (bestMs <= 0) {
            curColor = new Color(230, 230, 250);
        } else if (curMs > bestMs) {
            curColor = new Color(90, 220, 140);
        } else {
            curColor = new Color(230, 230, 250);
        }

        Color bestColor = new Color(160, 180, 220);

        int w = getWidth(), h = getHeight();
        g.setColor(new Color(0,0,0,60));
        g.fillRoundRect(8, 6, w-16, h-12, 10, 10);

        g.setFont(getFont().deriveFont(Font.BOLD, 16f));
        int xPad = 16;
        int yBase = h/2 + g.getFontMetrics().getAscent()/2 - 3;

        g.setColor(new Color(190, 200, 255));
        g.drawString("Time:", xPad, yBase);
        int xCur = xPad + g.getFontMetrics().stringWidth("Time: ") + 6;

        g.setColor(curColor);
        g.drawString(curStr, xCur, yBase);

        String bestLabel = "Best: " + bestStr;
        int sw = g.getFontMetrics().stringWidth(bestLabel);
        g.setColor(bestColor);
        g.drawString(bestLabel, w - sw - xPad, yBase);

        g.dispose();
    }

    private static String formatMillis(long ms){
        if (ms < 0) ms = 0;
        long rounded = (ms + 5) / 10 * 10;
        long totalSeconds = rounded / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long centi   = (rounded % 1000) / 10;

        return String.format("%02d:%02d.%02d", minutes, seconds, centi);
    }
}
