package gamehex.view;

import gamehex.model.AppSettings;

import javax.swing.*;
import java.awt.*;

public final class SettingsPanel extends JPanel {

    private static final Font TITLE_FONT = new Font("Papyrus", Font.BOLD, 22);
    private static final Font BTN_FONT   = new Font("Papyrus", Font.BOLD, 16);

    public SettingsPanel(AppSettings settings, Runnable onBack){
        super(new GridBagLayout());
        JPanel root = Theme.gradientPanel(new GridBagLayout());
        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);

        JLabel title = new JLabel("Settings");
        title.setForeground(Theme.TEXT);
        title.setFont(TITLE_FONT);

        JToggleButton music = Theme.neonToggle("Theme Music", settings.isMusicEnabled());
        music.setFont(BTN_FONT);
        JToggleButton save  = Theme.neonToggle("Save History", settings.isSaveHistory());
        save.setFont(BTN_FONT);
        JButton back        = Theme.neonButton("Back");
        back.setFont(BTN_FONT);

        music.addActionListener(e -> settings.setMusicEnabled(music.isSelected()));
        save.addActionListener(e -> settings.setSaveHistory(save.isSelected()));
        back.addActionListener(e -> onBack.run());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(12, 12, 12, 12);
        c.gridx = 0;
        c.gridy = 0;
        root.add(title, c);
        c.gridy++;
        root.add(music, c);
        c.gridy++;
        root.add(save, c);
        c.gridy++;
        root.add(back, c);
    }
}
