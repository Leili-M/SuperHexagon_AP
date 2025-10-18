package gamehex.model;

public final class World {
    private final int sectors;
    private final float playerRadius;

    public World(int sectors, float playerRadius){
        if (sectors < 3) throw new IllegalArgumentException("sectors >= 3");
        this.sectors = sectors;
        this.playerRadius = playerRadius;
    }

    public int sectors(){ return sectors; }
    public float playerRadius(){ return playerRadius; }
}
