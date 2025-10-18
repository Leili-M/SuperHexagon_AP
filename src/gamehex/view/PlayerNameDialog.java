package gamehex.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public final class PlayerNameDialog extends JDialog {
    private String result;

    private static final Font TITLE_FONT = new Font("Papyrus", Font.BOLD, 22);
    private static final Font TEXT_FONT  = new Font("Papyrus", Font.PLAIN, 16);
    private static final Font BTN_FONT   = new Font("Papyrus", Font.BOLD, 16);

    private PlayerNameDialog(Window owner, String defaultName){
        super(owner, "Player Name", ModalityType.APPLICATION_MODAL);

        JPanel root = Theme.gradientPanel(new GridBagLayout());
        setContentPane(root);

        JLabel title = new JLabel("What’s your name?");
        title.setForeground(Theme.TEXT);
        title.setFont(TITLE_FONT);

        JTextField name = new JTextField(defaultName == null ? "" : defaultName, 16);
        name.setFont(TEXT_FONT);
        name.setForeground(Theme.TEXT);
        name.setCaretColor(Theme.ACCENT);
        name.setOpaque(false);
        name.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.EDGE_OFF, 2, true),
                BorderFactory.createEmptyBorder(10,14,10,14)
        ));

        JButton ok = Theme.neonButton("Start");
        ok.setFont(BTN_FONT);
        JButton cancel = Theme.neonButton("Cancel");
        cancel.setFont(BTN_FONT);

        ok.addActionListener(e -> {
            String s = name.getText().trim();
            if (s.isEmpty()) {
                name.requestFocus();
                name.selectAll();
                return;
            }
            result = s;
            dispose();
        });
        cancel.addActionListener(e -> { result = null; dispose(); });


        name.registerKeyboardAction(e -> ok.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_FOCUSED);
        root.registerKeyboardAction(e -> cancel.doClick(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.gridx=0; c.gridy=0; c.gridwidth=2;
        root.add(title, c);
        c.gridy++; c.fill=GridBagConstraints.HORIZONTAL;
        root.add(name, c);
        c.gridy++; c.gridwidth=1; c.fill=GridBagConstraints.NONE;
        root.add(ok, c);
        c.gridx=1;
        root.add(cancel, c);

        pack();
        setSize(420, 250);
        setLocationRelativeTo(owner);
    }

    public static String ask(Window owner, String defaultName){
        PlayerNameDialog d = new PlayerNameDialog(owner, defaultName);
        d.setVisible(true);
        return d.result;
    }
}
