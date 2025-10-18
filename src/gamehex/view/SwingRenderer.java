package gamehex.view;

import gamehex.model.Snapshot;
import gamehex.model.obstacles.HexWall;
import gamehex.model.obstacles.Telegraph;
import gamehex.model.physics.HexMath;
import gamehex.model.physics.Phys;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.List;

public final class SwingRenderer implements Renderer {
    private static final long START_NANO = System.nanoTime();

    @Override
    public void draw(Graphics2D g, Snapshot s, int w, int h) {
        g.setColor(new Color(18,18,20));
        g.fillRect(0,0,w,h);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int cx = w/2, cy = h/2;

        float timeSec = (System.nanoTime() - START_NANO) / 1_000_000_000f;
        float hue = ((timeSec * 0.07f) % 1.0f);
        Color toneA = Color.getHSBColor(hue, 0.55f, 0.42f);
        Color toneB = Color.getHSBColor((hue+0.05f)%1f, 0.55f, 0.34f);

        AffineTransform old = g.getTransform();
        g.translate(cx, cy);
        g.rotate(Math.toRadians(s.bgAngleDeg()));

        int N = s.sectors();
        double step = 2*Math.PI / N;
        int rOuterFull = (int)Math.ceil(Math.hypot(w, h));
        for (int i=0;i<N;i++){
            double a0 = i*step, a1 = (i+1)*step;
            GeneralPath wedge = ringSectorPath(0, rOuterFull, a0, a1);
            g.setColor((i%2==0) ? toneA : toneB);
            g.fill(wedge);
        }
        g.setColor(new Color(0,0,0,90));
        g.setStroke(new BasicStroke(2f));
        for (int i=0;i<N;i++){
            double ang = i*step;
            int x2 = (int)(rOuterFull*Math.cos(ang));
            int y2 = (int)(rOuterFull*Math.sin(ang));
            g.drawLine(0,0,x2,y2);
        }

        Telegraph t = s.telegraph();
        int playRing = (int)s.playerRadius();
        if (t != null){
            float center = t.thetaCenterDeg;
            float width  = t.widthDeg;
            g.setColor(new Color(255,255,255,110));
            GeneralPath tele = ringSectorPath(
                    (int)(playRing*1.65), (int)(playRing*1.85),
                    Math.toRadians(center - width/2f),
                    Math.toRadians(center + width/2f)
            );
            g.fill(tele);
        }

        g.setColor(Color.getHSBColor((hue+0.1f)%1f, 0.8f, 0.95f));
        java.util.List<HexWall> hexWalls = s.hexWalls();
        double phase = 0.0;

        for (HexWall wseg : hexWalls){
            float[] O0 = HexMath.pointOnSide(wseg.rOuter, wseg.side, wseg.u0, phase);
            float[] O1 = HexMath.pointOnSide(wseg.rOuter, wseg.side, wseg.u1, phase);
            float  rIn = wseg.rInner - 0.5f;
            float[] I1 = HexMath.pointOnSide(rIn,         wseg.side, wseg.u1, phase);
            float[] I0 = HexMath.pointOnSide(rIn,         wseg.side, wseg.u0, phase);

            GeneralPath quad = new GeneralPath();
            quad.moveTo(O0[0], O0[1]); quad.lineTo(O1[0], O1[1]);
            quad.lineTo(I1[0], I1[1]); quad.lineTo(I0[0], I0[1]);
            quad.closePath();
            g.fill(quad);
        }

        g.setTransform(old);

        Color playerCol = Color.getHSBColor((hue+0.08f)%1f, 0.85f, 1f);

        drawRegularPolygon(
                g, cx, cy,
                (int)(playRing*0.52),
                6,
                -Math.PI/2 + Math.toRadians(s.bgAngleDeg()),
                playerCol,
                3f
        );

        drawCenteredTinyTriangle(g, cx, cy, s.playerThetaDeg(), s.playerRadius(), playerCol, s.gameOver());

        if (s.gameOver() || s.paused()){
            g.setColor(new Color(0,0,0,150));
            g.fillRect(0,0,w,h);
            g.setColor(Color.WHITE);
            g.setFont(g.getFont().deriveFont(Font.BOLD, 28f));
            drawCentered(g, w, h, s.gameOver() ? "GAME OVER" : "PAUSED");
        }
    }

    private static GeneralPath ringSectorPath(int rInner, int rOuter, double angStart, double angEnd){
        GeneralPath p = new GeneralPath();
        int steps = Math.max(12, (int)Math.ceil(Math.abs(angEnd-angStart) / Math.toRadians(6)));
        double d = (angEnd-angStart)/steps;
        for (int i=0;i<=steps;i++){
            double a = angStart + i*d;
            float x = (float)(rOuter*Math.cos(a));
            float y = (float)(rOuter*Math.sin(a));
            if (i==0) p.moveTo(x,y); else p.lineTo(x,y);
        }
        for (int i=steps;i>=0;i--){
            double a = angStart + i*d;
            float x = (float)(rInner*Math.cos(a));
            float y = (float)(rInner*Math.sin(a));
            p.lineTo(x,y);
        }
        p.closePath();
        return p;
    }

    private static void drawRegularPolygon(Graphics2D g, int cx, int cy, int r, int n, double phase, Color col, float stroke){
        GeneralPath path = new GeneralPath();
        for (int i=0;i<n;i++){
            double a = phase + i * (2*Math.PI/n);
            float x = (float)(cx + r*Math.cos(a));
            float y = (float)(cy + r*Math.sin(a));
            if (i==0) path.moveTo(x,y); else path.lineTo(x,y);
        }
        path.closePath();
        Stroke old = g.getStroke();
        g.setColor(col);
        g.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(path);
        g.setStroke(old);
    }

    private static void drawCenteredTinyTriangle(Graphics2D g, int cx, int cy,
                                                 float thetaDeg, float ringRadius,
                                                 Color col, boolean gameOver){
        final int side = 12;
        final float r  = ringRadius * 0.58f;

        double th = Math.toRadians(thetaDeg);
        double mx = cx + r * Math.cos(th);
        double my = cy + r * Math.sin(th);

        double a = th;
        double d = side / Math.sqrt(3.0);

        int x1 = (int)Math.round(mx + d * Math.cos(a));
        int y1 = (int)Math.round(my + d * Math.sin(a));
        int x2 = (int)Math.round(mx + d * Math.cos(a + 2*Math.PI/3));
        int y2 = (int)Math.round(my + d * Math.sin(a + 2*Math.PI/3));
        int x3 = (int)Math.round(mx + d * Math.cos(a - 2*Math.PI/3));
        int y3 = (int)Math.round(my + d * Math.sin(a - 2*Math.PI/3));

        Polygon tri = new Polygon(new int[]{x1,x2,x3}, new int[]{y1,y2,y3}, 3);
        g.setColor(gameOver ? new Color(200,50,50) : col);
        g.fillPolygon(tri);
    }

    private static void drawCentered(Graphics2D g, int w, int h, String text){
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(text)) / 2;
        int y = (h - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);
    }
}
