package gamehex.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** تم و سازنده‌های کامپوننت نئونی هماهنگ با بازی */
public final class Theme {
    private Theme(){}

    // رنگ‌ها
    public static final Color BG_TOP   = new Color(18,18,28);
    public static final Color BG_BOTTOM= new Color(10,10,18);
    public static final Color TEXT     = new Color(240, 240, 255);
    public static final Color SUBTEXT  = new Color(180, 180, 210);
    public static final Color EDGE_ON  = new Color(170,200,255,220);
    public static final Color EDGE_OFF = new Color(140,160,255,180);
    public static final Color ACCENT   = new Color(100,200,255);

    public static Font titleFont(Component c){ return c.getFont().deriveFont(Font.BOLD, 28f); }
    public static Font h2Font(Component c){ return c.getFont().deriveFont(Font.BOLD, 20f); }
    public static Font textFont(Component c){ return c.getFont().deriveFont(Font.PLAIN, 16f); }


    public static JPanel gradientPanel(LayoutManager lm){
        return new JPanel(lm){
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g.create();
                int w=getWidth(), h=getHeight();
                g2.setPaint(new GradientPaint(0,0, BG_TOP, 0,h, BG_BOTTOM));
                g2.fillRect(0,0,w,h);
                g2.dispose();
            }
        };
    }

    public static JButton neonButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setContentAreaFilled(true);
        b.setOpaque(true);
        b.setForeground(TEXT);
        b.setBackground(new Color(30, 30, 40, 120));
        b.setFont(new Font("Papyrus", Font.BOLD, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160,180,255,200), 2, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));

        return b;
    }

    public static JToggleButton neonToggle(String text, boolean initial){
        JToggleButton t = new JToggleButton(text, initial);
        t.setFocusPainted(false);
        t.setContentAreaFilled(false);
        t.setOpaque(false);
        t.setForeground(TEXT);
        t.setFont(t.getFont().deriveFont(Font.BOLD, 16f));
        t.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(initial ? EDGE_ON : EDGE_OFF, 2, true),
                new EmptyBorder(8,16,8,16)
        ));
        t.addChangeListener(e -> {
            boolean on = t.isSelected();
            t.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(on ? EDGE_ON : EDGE_OFF, 2, true),
                    new EmptyBorder(8,16,8,16)
            ));
        });
        return t;
    }

    public static JLabel sectionTitle(String text, Component c){
        JLabel l = new JLabel(text);
        l.setForeground(TEXT);
        l.setFont(h2Font(c));
        return l;
    }
}
