package gamehex.model.obstacles;

public final class Wall {
    public final float thetaStartDeg;
    public final float thetaEndDeg;

    public float rOuter;
    public float rOuterPrev;

    public final float thickness;
    public final float speed;

    public Wall(float thetaStartDeg, float thetaEndDeg, float rOuter, float thickness, float speed) {
        this.thetaStartDeg = thetaStartDeg;
        this.thetaEndDeg   = thetaEndDeg;
        this.rOuter        = rOuter;
        this.rOuterPrev    = rOuter;
        this.thickness     = thickness;
        this.speed         = speed;
    }


}
