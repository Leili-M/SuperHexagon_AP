package gamehex.model;

import gamehex.model.obstacles.ObstacleSystem;


public final class GameState {
    private final World world;
    private final Player player;
    private final ObstacleSystem obstacles;
    private Difficulty difficulty;

    private float bgAngleDeg = 0f;
    private boolean paused = false;
    private boolean gameOver = false;
    private long elapsedMs = 0L;

    private final float playerSizePx = 8f;

    private float hitAccumSec = 0f;


    private static final float BASE_SPEED_PX_S   = 60f;   // سرعت اولیه
    private static final float BASE_GAP_DEG      = 200f;  // عرض گپ (ثابت)
    private static final float BASE_LAMBDA       = 0.30f; // نرخ موج (ثابت)
    private static final float BASE_THICKNESS    = 28f;   // ضخامت (ثابت)
    private static final float BASE_TELEGRAPH    = 0.60f; // تلگراف (ثابت)


    private static final float SPEED_GAIN_PER_SEC = 3f;   // px/s^2
    private static final float SPEED_CAP          = 220f;    // سقف سرعت

    public static final class Input {
        public volatile boolean left = false, right = false, pauseEdge = false;

        public volatile boolean mouseActive = false;
        public volatile boolean mouseEnabled = true;
        public volatile float mouseThetaDeg = 0f;
    }

    private final Input input = new Input();

    public GameState(World world, Difficulty difficulty){
        this.world = world;
        this.difficulty = difficulty;
        this.player = new Player(
                0f,
                world.playerRadius(),
                difficulty.omegaMaxDegPerSec,
                difficulty.alphaDegPerSec2,
                difficulty.betaDegPerSec2
        );

        float outer = Math.min((float)Math.min(800, 600) * 0.45f, world.playerRadius() * 1.6f);
        this.obstacles = new ObstacleSystem(world.sectors(), outer, world.playerRadius());

        obstacles.setDifficultyParams(
                BASE_SPEED_PX_S,
                BASE_GAP_DEG,
                BASE_LAMBDA,
                BASE_THICKNESS,
                BASE_TELEGRAPH
        );
    }

    public void update(float dtSec){
        bgAngleDeg = (bgAngleDeg + 50f * dtSec) % 360f;

        if (gameOver) return;

        if (paused) return;

        if (input.mouseActive) {
            player.setThetaDeg(input.mouseThetaDeg);
        }
        player.control(input.left, input.right, dtSec);

        float t = elapsedMs / 1000f;
        float v = Math.min(SPEED_CAP, BASE_SPEED_PX_S + SPEED_GAIN_PER_SEC * t);
        obstacles.setDifficultyParams(v, BASE_GAP_DEG, BASE_LAMBDA, BASE_THICKNESS, BASE_TELEGRAPH);
        obstacles.update(dtSec);

        if (obstacles.triangleHitsWallsHexSimple(player.thetaDeg(), player.radius(), bgAngleDeg)) {
            gameOver = true;
            return;
        }

        elapsedMs += (long)(dtSec * 1000f);
    }

    public World world(){ return world; }
    public Player player(){ return player; }
    public Difficulty difficulty(){ return difficulty; }
    public float bgAngleDeg(){ return bgAngleDeg; }
    public boolean paused(){ return paused; }
    public boolean gameOver(){ return gameOver; }
    public long elapsedMs(){ return elapsedMs; }
    public float playerSizePx(){ return playerSizePx; }
    public Input input(){ return input; }
    public ObstacleSystem obstacles(){ return obstacles; }

    public void setPaused(boolean p){ this.paused = p; }
    public void setDifficulty(Difficulty d){ this.difficulty = d; player.applyDifficulty(d); }
}
