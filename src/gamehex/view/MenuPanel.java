package gamehex.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/** Neon menu with rotating hex grid background (Super Hexagon vibe) */
public final class MenuPanel extends JPanel {
    private final Timer anim;
    private float t = 0f;
    private float rot = 0f;


    private static final Font FANCY_TITLE = new Font("Papyrus", Font.BOLD, 36);
    private static final Font FANCY_BTN   = new Font("Papyrus", Font.BOLD, 18);

    public MenuPanel(ActionListener onStart,
                     ActionListener onHistory,
                     ActionListener onSettings) {
        setOpaque(true);
        setDoubleBuffered(true);
        setLayout(new GridBagLayout());

        JLabel title = new JLabel("SUPER HEXAGON");
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(FANCY_TITLE);

        JButton btnStart    = mkButton("Start Game");
        JButton btnHistory  = mkButton("History");
        JButton btnSettings = mkButton("Settings");

        btnStart.addActionListener(onStart);
        btnHistory.addActionListener(onHistory);
        btnSettings.addActionListener(onSettings);


        btnStart.registerKeyboardAction(onStart, KeyStroke.getKeyStroke("ENTER"), JComponent.WHEN_IN_FOCUSED_WINDOW);


        JPanel stack = new JPanel(new GridBagLayout());
        stack.setOpaque(false);
        GridBagConstraints s = new GridBagConstraints();
        s.gridx=0; s.gridy=0; s.insets=new Insets(10,10,10,10); s.fill=GridBagConstraints.NONE;
        stack.add(title, s); s.gridy++;
        stack.add(btnStart, s); s.gridy++;
        stack.add(btnHistory, s); s.gridy++;
        stack.add(btnSettings, s);

        add(stack, new GridBagConstraints());


        anim = new Timer(16, e -> {
            t += 0.016f;
            rot = (rot + 25f * 0.016f) % 360f;
            repaint();
        });
        anim.start();
    }

    private JButton mkButton(String text){
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setForeground(new Color(240, 240, 255));
        b.setFont(FANCY_BTN);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140,160,255,180), 2, true),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)
        ));

        b.getModel().addChangeListener(e -> {
            boolean armed = b.getModel().isRollover() || b.getModel().isArmed();
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(armed ? new Color(170,200,255,220)
                            : new Color(140,160,255,180), 2, true),
                    BorderFactory.createEmptyBorder(10, 24, 10, 24)
            ));
        });
        return b;
    }

    @Override protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        int w = getWidth(), h = getHeight();
        int cx = w/2, cy = h/2;


        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);


        Color cTop = new Color(18,18,28);
        Color cBot = new Color(10,10,18);
        g.setPaint(new GradientPaint(0,0, cTop, 0,h, cBot));
        g.fillRect(0,0,w,h);


        float baseHue = ( (t*0.07f) % 1f );
        Color ringC1 = Color.getHSBColor((baseHue+0.02f)%1f, 0.6f, 0.9f);
        for (int i=0; i<4; i++){
            float r = (float)(Math.hypot(w,h) * (0.08 + 0.12*i + 0.02*Math.sin(t*2 + i)));
            g.setColor(new Color(ringC1.getRed(), ringC1.getGreen(), ringC1.getBlue(), 55 - i*8));
            g.setStroke(new BasicStroke(2.5f));
            g.drawOval((int)(cx-r),(int)(cy-r),(int)(2*r),(int)(2*r));
        }

        g.translate(cx, cy);
        g.rotate(Math.toRadians(rot));
        drawHexGrid(g, Math.max(w, h));
        g.rotate(Math.toRadians(-rot));
        g.translate(-cx, -cy);

        float hr = Math.min(w, h) * 0.14f;
        Color coreC = Color.getHSBColor((baseHue+0.08f)%1f, 0.85f, 1f);
        Stroke old = g.getStroke();
        g.setColor(new Color(0,0,0,90));
        g.fillPolygon(hexPolygon(cx, cy, (int)(hr*1.05f), -Math.PI/2));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(coreC);
        g.drawPolygon(hexPolygon(cx, cy, (int)hr, -Math.PI/2));
        g.setStroke(old);

        g.dispose();
    }

    private void drawHexGrid(Graphics2D g, int size){
        int rings = 6;
        float rStep = size * 0.08f;
        for (int k=1; k<=rings; k++){
            float radius = k * rStep;
            g.setColor(new Color(255,255,255, 28 - k*3));
            g.setStroke(new BasicStroke(1.5f));
            g.drawPolygon(hexPolygon(0, 0, (int)radius, -Math.PI/2));
            for (int i=0; i<6; i++){
                double a = -Math.PI/2 + i * (2*Math.PI/6.0);
                int x2 = (int)(radius * Math.cos(a));
                int y2 = (int)(radius * Math.sin(a));
                g.drawLine(0,0,x2,y2);
            }
        }
    }

    private Polygon hexPolygon(int cx, int cy, int r, double phase){
        Polygon p = new Polygon();
        for (int i=0;i<6;i++){
            double a = phase + i*(2*Math.PI/6.0);
            int x = (int)Math.round(cx + r*Math.cos(a));
            int y = (int)Math.round(cy + r*Math.sin(a));
            p.addPoint(x,y);
        }
        return p;
    }
}
