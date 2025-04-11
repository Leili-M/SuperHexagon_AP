package view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class CustomPlayButton extends JButton {

    public CustomPlayButton(String text) {
        super(text);

        // حذف ظاهر پیش‌فرض دکمه در Swing
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        // می‌توانید اندازه دلخواه برای دکمه تعیین کنید
        setPreferredSize(new Dimension(200, 80));

        // ست کردن یک فونت درشت و مناسب
        setFont(new Font("Arial", Font.BOLD, 30));
        setForeground(Color.WHITE); // رنگ پیش‌فرض متن دکمه
    }

    @Override
    protected void paintComponent(Graphics g) {
        // رندر دو‌بعدی برای رسم شکل
        Graphics2D g2 = (Graphics2D) g.create();

        // آنتی‌الیاسینگ برای بهتر شدن کیفیت رسم
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // شکل چندضلعی شبیه دکمه (یک شش‌ضلعی کشیده)
        // مثلاً گوشه‌های بالا چپ و راست، کمی مورب شده‌اند
        Path2D hexShape = new Path2D.Double();
        // نقطه شروع: بالا چپ (مثلاً 20 پیکسل فاصله از گوشه)
        hexShape.moveTo(20, 0);
        // بالا راست
        hexShape.lineTo(w - 20, 0);
        // وسط راست
        hexShape.lineTo(w, h / 2.0);
        // پایین راست
        hexShape.lineTo(w - 20, h);
        // پایین چپ
        hexShape.lineTo(20, h);
        // وسط چپ
        hexShape.lineTo(0, h / 2.0);
        hexShape.closePath();

        // رسم گرادیان صورتی از بالا به پایین
        Color colorTop = new Color(255, 102, 178);   // صورتی روشن
        Color colorBottom = new Color(255, 0, 128);  // صورتی پررنگ‌تر

        GradientPaint gp = new GradientPaint(
                new Point2D.Double(0, 0),
                colorTop,
                new Point2D.Double(0, h),
                colorBottom
        );
        g2.setPaint(gp);
        g2.fill(hexShape);

        // اگر بخواهید یک حاشیه تیره‌تر دور دکمه بکشید:
        // g2.setColor(new Color(200, 0, 100)); // کمی تیره‌تر از رنگ پایین
        // g2.setStroke(new BasicStroke(3f));
        // g2.draw(hexShape);

        // رسم متن دکمه (PLAY) و فلش
        String text = getText(); // در سازنده، "PLAY" ست شده است
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent(); // فاصله از baseline تا بالای کاراکترها

        // مختصات مناسب برای مرکز کردن متن
        int textX = (w - textWidth) / 2;
        int textY = (h + textHeight) / 2 - 5; // کمی بالاتر از وسط واقعی

        // اول متن را رسم می‌کنیم
        g2.setColor(getForeground());
        g2.drawString(text, textX, textY);

        // رسم فلش (مثلث) در سمت راست متن
        // مثلاً سه نقطه که یک مثلث رو می‌سازن
        int arrowHeight = 14;
        int arrowWidth = 12;
        int arrowX = textX + textWidth + 10; // 10 پیکسل فاصله از انتهای متن
        int arrowY = (h - arrowHeight) / 2;

        Polygon arrow = new Polygon();
        arrow.addPoint(arrowX, arrowY);                 // بالا
        arrow.addPoint(arrowX + arrowWidth, arrowY + arrowHeight / 2); // وسط راست
        arrow.addPoint(arrowX, arrowY + arrowHeight);   // پایین

        g2.fillPolygon(arrow);

        // آزاد کردن منابع گرافیکی
        g2.dispose();

        // اگر موس روی دکمه کلیک شد و می‌خواهید افکت فشرده‌شدن را ببینید
        // بهتر است در انتهای کار، paintComponent اصلی JButton را نزنیم
        // چون دوباره یک زمینه پیش‌فرض می‌کشد.
        // ولی اگر می‌خواهید افکت فشرده شدن ببینید:
        // super.paintComponent(g);
    }
}
