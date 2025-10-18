package gamehex.model;

public enum Difficulty {
    EASY   (220f,  900f, 1200f),
    NORMAL (180f,  700f,  900f),
    HARD   (140f,  550f,  700f);

    public final float omegaMaxDegPerSec;
    public final float alphaDegPerSec2;
    public final float betaDegPerSec2;

    Difficulty(float omegaMax, float alpha, float beta) {
        this.omegaMaxDegPerSec = omegaMax;
        this.alphaDegPerSec2 = alpha;
        this.betaDegPerSec2  = beta;
    }
}
