package model;

public class Player {
    private double angle;  // زاویه فعلی نشانگر به رادیان یا درجه (بسته به پیاده‌سازی شما)
    private final double distanceFromCenter; // فاصله ثابت از مرکز چندضلعی

    public Player(double initialAngle, double distanceFromCenter) {
        this.angle = initialAngle;
        this.distanceFromCenter = distanceFromCenter;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getDistanceFromCenter() {
        return distanceFromCenter;
    }

    // متد جهت چرخش نشانگر (به چپ یا راست)
    public void rotate(double deltaAngle) {
        this.angle += deltaAngle;
    }
}
