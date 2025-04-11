package view;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    private CustomPlayButton btnNewGame, btnHistory, btnSettings, btnExit;
    private JLabel lblTotalRecord;

    public MainMenu() {
        super("Super Hexagon - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 1000);  // اندازه فریم؛ می‌توانید بسته به نیاز تغییر دهید
        setLocationRelativeTo(null);

        // ایجاد یک پنل پس‌زمینه سفارشی که تصویر پس‌زمینه رو رسم می‌کند
        BackgroundPanel backgroundPanel = new BackgroundPanel("/view/resources/background.jpg");
        backgroundPanel.setLayout(null); // استفاده از layout دستی جهت قرار دادن مطلق کامپوننت‌ها

        // افزودن عنوان برنامه
        JLabel lblTitle = new JLabel("Super Hexagon");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 40));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(50, 50, 400, 60);
        backgroundPanel.add(lblTitle);

        // نمایش رکورد کل (زمان زنده ماندن) در منو
        lblTotalRecord = new JLabel("Total Record: 0");
        lblTotalRecord.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalRecord.setForeground(Color.WHITE);
        lblTotalRecord.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalRecord.setBounds(50, 200, 400, 40);
        backgroundPanel.add(lblTotalRecord);

        // ایجاد دکمه‌ها با استفاده از CustomPlayButton
        btnNewGame = new CustomPlayButton("New Game");
        btnHistory  = new CustomPlayButton("History");
        btnSettings = new CustomPlayButton("Settings");
        btnExit     = new CustomPlayButton("Exit");

        // تنظیم اندازه استاندارد دکمه‌ها
        int btnWidth = 250, btnHeight = 80;
        int centerX = (500 - btnWidth) / 2; // مرکز کردن دکمه‌ها افقی (عرض فریم 500 پیکسل)
        int startY = 300;                 // محل شروع اولین دکمه از بالا
        int spacing = 20;                 // فاصله بین دکمه‌ها

        btnNewGame.setBounds(centerX, startY, btnWidth, btnHeight);
        btnHistory.setBounds(centerX, startY + btnHeight + spacing, btnWidth, btnHeight);
        btnSettings.setBounds(centerX, startY + 2 * (btnHeight + spacing), btnWidth, btnHeight);
        btnExit.setBounds(centerX, startY + 3 * (btnHeight + spacing), btnWidth, btnHeight);

        // افزودن دکمه‌ها به پنل پس‌زمینه
        backgroundPanel.add(btnNewGame);
        backgroundPanel.add(btnHistory);
        backgroundPanel.add(btnSettings);
        backgroundPanel.add(btnExit);

        // تنظیم پنل پس‌زمینه به عنوان content pane فریم
        setContentPane(backgroundPanel);
    }

    // متدهای getter جهت دسترسی به دکمه‌ها (برای هماهنگی با Controller)
    public CustomPlayButton getBtnNewGame() {
        return btnNewGame;
    }

    public CustomPlayButton getBtnHistory() {
        return btnHistory;
    }

    public CustomPlayButton getBtnSettings() {
        return btnSettings;
    }

    public CustomPlayButton getBtnExit() {
        return btnExit;
    }

    public JLabel getLblTotalRecord() {
        return lblTotalRecord;
    }

    // متد main جهت تست منوی اصلی
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }

    /**
     * کلاس داخلی BackgroundPanel برای رسم پس‌زمینه با استفاده از تصویر
     */
    private static class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(getClass().getResource(imagePath)).getImage();
            } catch (Exception e) {
                System.err.println("Could not load background image: " + imagePath);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // رسم تصویر پس‌زمینه در اندازه کامل پنل
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
