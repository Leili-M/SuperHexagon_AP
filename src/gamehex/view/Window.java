package gamehex.view;

import gamehex.controller.GameLoop;
import gamehex.controller.InputController;
import gamehex.controller.MouseController;
import gamehex.model.*;
import gamehex.audio.MusicPlayer;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.net.URL;

public final class Window extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private final AppSettings settings = new AppSettings();
    private final ScoreBoard  board    = new ScoreBoard();

    private MenuPanel     menuPanel;
    private JPanel        gameContainer;
    private HistoryPanel  historyPanel;
    private SettingsPanel settingsPanel;
    private HUDPanel      hud;

    private GameLoop   loop;
    private GamePanel  gamePanel;
    private GameState  state;
    private String     currentPlayer = "Player";

    private Timer uiTimer;

    // Audio
    private final MusicPlayer bgMusic   = new MusicPlayer();
    private final MusicPlayer overMusic = new MusicPlayer();
    private boolean lastGameOver = false;
    private boolean lastMusicEnabled = settings.isMusicEnabled();


    private final Path scorePath = Paths.get("src/main/resources/scores.json");

    public Window(){
        super("SuperHexagon");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setContentPane(root);

        try {
            Image icon = tryLoadIcon();
            if (icon != null) setIconImage(icon);
        } catch (Exception e) {
            System.out.println("[Icon] setIconImage failed: " + e);
        }
    }

    public void showUI(){
        board.loadFromJson(scorePath);
        bgMusic.loadFromResource("/gamehex/resources/game-music-loop.wav", true);
        bgMusic.setVolumeDb(-8f);

        overMusic.loadFromResource("/gamehex/resources/game-over.wav", false);
        overMusic.setVolumeDb(-6f);

        buildUIIfNeeded();
        setVisible(true);

        updateMusicForState();

        if (uiTimer == null) {
            uiTimer = new Timer(50, e -> onTickUI());
            uiTimer.start();
        }

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                bgMusic.close();
                overMusic.close();
                board.saveToJson(scorePath);
            }
        });
    }

    private void buildUIIfNeeded(){
        if (root.getComponentCount() > 0) return;

        menuPanel = new MenuPanel(
                e -> startGameFlow(),
                e -> showHistory(),
                e -> showSettings()
        );

        gameContainer = new JPanel(new BorderLayout());
        gameContainer.setBackground(Color.BLACK);

        historyPanel  = new HistoryPanel(board, this::showMenu);
        settingsPanel = new SettingsPanel(settings, this::showMenu);

        root.add(menuPanel,    "menu");
        root.add(gameContainer,"game");
        root.add(historyPanel, "history");
        root.add(settingsPanel,"settings");

        showMenu();
    }

    private void showMenu(){
        cards.show(root, "menu");
        updateMusicForState();
    }

    private void showGame(){
        cards.show(root, "game");
        updateMusicForState();
    }

    private void showHistory(){
        historyPanel.refresh(board);
        cards.show(root, "history");
        updateMusicForState();
    }

    private void showSettings(){
        cards.show(root, "settings");
    }

    private void startGameFlow(){
        String name = PlayerNameDialog.ask(this, currentPlayer);
        if (name == null || name.isBlank()) return;
        currentPlayer = name.trim();

        World world = new World(6, 180f);
        state = new GameState(world, Difficulty.NORMAL);

        SwingRenderer renderer = new SwingRenderer();
        gamePanel = new GamePanel(renderer);

        InputController keys  = new InputController(state);
        MouseController mouse = new MouseController(state, gamePanel);

        hud = new HUDPanel(state::elapsedMs, board);

        gameContainer.removeAll();
        gameContainer.add(hud, BorderLayout.NORTH);
        gameContainer.add(gamePanel, BorderLayout.CENTER);
        gameContainer.revalidate();
        gameContainer.repaint();

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
        gamePanel.addKeyListener(keys);
        gamePanel.addMouseMotionListener(mouse);
        gamePanel.addMouseListener(mouse);

        installEscExitBinding();

        loop = new GameLoop(state, gamePanel);
        loop.start();

        showGame();
    }

    private void installEscExitBinding(){
        InputMap im = gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = gamePanel.getActionMap();

        im.put(KeyStroke.getKeyStroke("ESCAPE"), "exitOrMenu");
        am.put("exitOrMenu", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                if (state == null) { showMenu(); return; }

                if (state.gameOver()) {
                    if (settings.isSaveHistory()) {
                        long elapsed = state.elapsedMs();
                        board.add(new ScoreEntry(currentPlayer, elapsed, Instant.now().toEpochMilli()));
                        board.saveToJson(scorePath);
                    }
                    if (loop != null) { loop.stop(); loop = null; }
                    state = null;
                    showMenu();
                    return;
                }

                state.setPaused(true);
                if (loop != null) { loop.stop(); loop = null; }
                state = null;
                showMenu();
            }
        });
    }

    private void onTickUI(){
        if (hud != null) hud.repaint();

        boolean nowOver = (state != null && state.gameOver());
        if (nowOver != lastGameOver){
            lastGameOver = nowOver;
            updateMusicForState();
        }

        boolean nowMusicEnabled = settings.isMusicEnabled();
        if (nowMusicEnabled != lastMusicEnabled){
            lastMusicEnabled = nowMusicEnabled;
            updateMusicForState();
        }
    }

    private void updateMusicForState(){
        boolean musicOn = settings.isMusicEnabled();

        if (!musicOn) {
            bgMusic.stop();
            overMusic.stop();
            return;
        }
        if (state == null || !state.gameOver()) {
            if (overMusic.isRunning()) overMusic.stop();
            bgMusic.playLoop();
            return;
        }
        bgMusic.stop();
        overMusic.playOnce();
    }
    private Image tryLoadIcon() {

        final String[] candidates = new String[]{
                "/gamehex/resources/icon.png",
                "/icon.png",
                "/gamehex/icon.png",
                "/gamehex/view/icon.png"
        };

        for (String p : candidates) {
            try {
                java.net.URL u1 = Window.class.getResource(p);
                if (u1 != null) return Toolkit.getDefaultToolkit().getImage(u1);

                java.net.URL u2 = Thread.currentThread().getContextClassLoader().getResource(p.startsWith("/") ? p.substring(1) : p);
                if (u2 != null) return Toolkit.getDefaultToolkit().getImage(u2);
            } catch (Exception ignore) {}
        }

        try {
            java.io.File f = new java.io.File("src/main/resources/gamehex/resources/icon.png");
            if (f.exists()) return Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath());
        } catch (Exception ignore) {}

        for (String p : candidates) {
            try {
                java.io.InputStream in = Window.class.getResourceAsStream(p);
                if (in == null) {
                    in = Thread.currentThread().getContextClassLoader().getResourceAsStream(p.startsWith("/") ? p.substring(1) : p);
                }
                if (in != null) {
                    java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(in);
                    if (img != null) return img;
                }
            } catch (Exception ignore) {}
        }

        java.awt.image.BufferedImage fallback = new java.awt.image.BufferedImage(82, 82, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = fallback.createGraphics();
        g.setColor(new Color(25, 25, 35));
        g.fillRect(0,0,32,32);
        g.setColor(new Color(120, 180, 255));
        g.setStroke(new BasicStroke(2f));

        Polygon hex = new Polygon();
        for (int i=0;i<6;i++){
            double a = -Math.PI/2 + i*(2*Math.PI/6.0);
            int x = (int)Math.round(16 + 12*Math.cos(a));
            int y = (int)Math.round(16 + 12*Math.sin(a));
            hex.addPoint(x,y);
        }
        g.drawPolygon(hex);
        g.dispose();
        System.out.println("[Icon] Resource not found. Using fallback icon.");
        return fallback;
    }

}
