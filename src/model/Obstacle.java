package model;

public class Obstacle {
    // مشخصات پایه برای یک مانع: موقعیت، سرعت و سایر ویژگی‌ها.
    // در آینده می‌خواهیم الگوی ذوزنقه‌ای و مسیر حرکت از لبه به سمت مرکز رو محاسبه کنیم.

    // به عنوان نمونه اولیه
    private double currentAngle;  // زاویه فعلی مانع در تقسیم‌بندی زمین بازی
    private double currentRadius; // فاصله فعلی از مرکز چندضلعی
    private double speed;         // سرعت حرکت مانع به سمت مرکز

    public Obstacle(double currentAngle, double currentRadius, double speed) {
        this.currentAngle = currentAngle;
        this.currentRadius = currentRadius;
        this.speed = speed;
    }

    public double getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(double currentAngle) {
        this.currentAngle = currentAngle;
    }

    public double getCurrentRadius() {
        return currentRadius;
    }

    public void setCurrentRadius(double currentRadius) {
        this.currentRadius = currentRadius;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    // متد به‌روزرسانی موقعیت مانع (به عنوان نمونه)
    public void updatePosition(long deltaTime) {
        // در هر tick زمان، فاصله از مرکز کاهش می‌یابد
        currentRadius -= speed * deltaTime;
    }
}
